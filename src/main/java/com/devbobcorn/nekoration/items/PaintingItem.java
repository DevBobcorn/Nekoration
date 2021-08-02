package com.devbobcorn.nekoration.items;

import com.devbobcorn.nekoration.entities.PaintingEntity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PaintingItem extends Item {
	public PaintingItem(Properties settings) {
		super(settings);
	}

	public ActionResultType useOn(ItemUseContext ctx) {
		BlockPos blockpos = ctx.getClickedPos();
		Direction direction = ctx.getClickedFace();
		BlockPos blockpos1 = blockpos.relative(direction);
		PlayerEntity playerentity = ctx.getPlayer();
		ItemStack itemstack = ctx.getItemInHand();
		if (playerentity != null && !this.mayPlace(playerentity, direction, itemstack, blockpos1)) {
			return ActionResultType.FAIL;
		} else {
			World world = ctx.getLevel();
			PaintingEntity hangingentity;
			hangingentity = new PaintingEntity(world, blockpos1, direction, (byte)32, (byte)16);
			hangingentity.setPos(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
			//net.minecraft.entity.item.PaintingEntity hangingentity;
			//hangingentity = new net.minecraft.entity.item.PaintingEntity(world, blockpos1, direction);

			CompoundNBT compoundnbt = itemstack.getTag();
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

	protected boolean mayPlace(PlayerEntity player, Direction dir, ItemStack stack, BlockPos pos) {
		return !dir.getAxis().isVertical() && player.mayUseItemAt(pos, dir, stack);
	}
}
