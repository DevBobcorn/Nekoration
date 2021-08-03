package com.devbobcorn.nekoration.items;

import com.devbobcorn.nekoration.entities.PaintingEntity;
import com.devbobcorn.nekoration.client.ClientHelper;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class PaintingItem extends Item {
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";

	public PaintingItem(Properties settings) {
		super(settings);
	}

	public ActionResultType useOn(ItemUseContext ctx) {
		BlockPos blockpos = ctx.getClickedPos();
		Direction direction = ctx.getClickedFace();
		BlockPos blockpos1 = blockpos.relative(direction);
		PlayerEntity playerentity = ctx.getPlayer();
		ItemStack stack = ctx.getItemInHand();
		if (playerentity != null && !this.mayPlace(playerentity, direction, stack, blockpos1)) {
			return ActionResultType.FAIL;
		} else {
			World world = ctx.getLevel();
			PaintingEntity hangingentity;
			hangingentity = new PaintingEntity(world, blockpos1, direction, (short)(PaintingItem.getWidth(stack) * 16), (short)(PaintingItem.getHeight(stack) * 16));
			hangingentity.setPos(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());

			CompoundNBT compoundnbt = stack.getTag();
			if (compoundnbt != null) {
				EntityType.updateCustomEntityTag(world, playerentity, hangingentity, compoundnbt);
			}

			if (hangingentity.survives()) {
				if (!world.isClientSide) {
					hangingentity.playPlacementSound();
					//hangingentity.recalculateBoundingBox();
					world.addFreshEntity(hangingentity);
				}
				return ActionResultType.sidedSuccess(world.isClientSide);
			} else {
				return ActionResultType.CONSUME;
			}
		}
	}

	@SuppressWarnings("deprecation")
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (world.isClientSide) {
			//System.out.println("Interacted with Painting!");
			DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> { 
				//Minecraft.getInstance().setScreen(new PaletteScreen(hand, a, col));
				ClientHelper.showPaintingSizeScreen(hand);
			});
		}
		return ActionResult.<ItemStack>success(stack);
	 }

	protected boolean mayPlace(PlayerEntity player, Direction dir, ItemStack stack, BlockPos pos) {
		return !dir.getAxis().isVertical() && player.mayUseItemAt(pos, dir, stack);
	}

    public static int getWidth(ItemStack stack) {
		short w = stack.getOrCreateTag().getShort(WIDTH);
		return w <= 0 ? 1 : w;
	}

    public static void setWidth(ItemStack stack, short w) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putShort(WIDTH, w);
	}

	public static int getHeight(ItemStack stack) {
		short h = stack.getOrCreateTag().getShort(HEIGHT);
		return h <= 0 ? 1 : h;
	}

    public static void setHeight(ItemStack stack, short h) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putShort(HEIGHT, h);
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		return new TranslationTextComponent(this.getDescriptionId(stack), getWidth(stack), getHeight(stack));
	}
}
