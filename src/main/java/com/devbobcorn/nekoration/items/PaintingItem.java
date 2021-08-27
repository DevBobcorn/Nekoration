package com.devbobcorn.nekoration.items;

import com.devbobcorn.nekoration.client.ClientHelper;
import com.devbobcorn.nekoration.entities.PaintingEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class PaintingItem extends Item {
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";

	public PaintingItem(Properties settings) {
		super(settings);
	}

	public InteractionResult useOn(UseOnContext ctx) {
		BlockPos blockpos = ctx.getClickedPos();
		Direction direction = ctx.getClickedFace();
		BlockPos blockpos1 = blockpos.relative(direction);
		Player player = ctx.getPlayer();
		ItemStack stack = ctx.getItemInHand();
		if (player != null && !this.mayPlace(player, direction, stack, blockpos1)) {
			return InteractionResult.FAIL;
		} else {
			Level world = ctx.getLevel();
			PaintingEntity painting;
			painting = new PaintingEntity(world, blockpos1, direction, (short)(PaintingItem.getWidth(stack) * 16), (short)(PaintingItem.getHeight(stack) * 16));
			painting.setPos(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());

			CompoundTag compoundnbt = stack.getTag();
			if (compoundnbt != null) {
				EntityType.updateCustomEntityTag(world, player, painting, compoundnbt);
			}

			if (painting.survives()) {
				if (!world.isClientSide) {
					painting.playPlacementSound();
					//hangingentity.recalculateBoundingBox();
					world.addFreshEntity(painting);
				}
				stack.shrink(1);
				return InteractionResult.sidedSuccess(world.isClientSide);
			} else {
				return InteractionResult.CONSUME;
			}
		}
	}

	@SuppressWarnings("deprecation")
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (world.isClientSide) {
			//System.out.println("Interacted with Painting!");
			DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> { 
				//Minecraft.getInstance().setScreen(new PaletteScreen(hand, a, col));
				ClientHelper.showPaintingSizeScreen(hand);
			});
		}
		return InteractionResultHolder.<ItemStack>success(stack);
	 }

	protected boolean mayPlace(Player player, Direction dir, ItemStack stack, BlockPos pos) {
		return !dir.getAxis().isVertical() && player.mayUseItemAt(pos, dir, stack);
	}

    public static int getWidth(ItemStack stack) {
		short w = stack.getOrCreateTag().getShort(WIDTH);
		return w <= 0 ? 1 : w;
	}

    public static void setWidth(ItemStack stack, short w) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putShort(WIDTH, w);
	}

	public static int getHeight(ItemStack stack) {
		short h = stack.getOrCreateTag().getShort(HEIGHT);
		return h <= 0 ? 1 : h;
	}

    public static void setHeight(ItemStack stack, short h) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putShort(HEIGHT, h);
	}

	@Override
	public Component getName(ItemStack stack) {
		return new TranslatableComponent(this.getDescriptionId(stack), getWidth(stack), getHeight(stack));
	}
}
