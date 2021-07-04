package com.devbobcorn.nekoration.blocks;

import net.minecraft.block.DoorBlock;

import com.devbobcorn.nekoration.common.VanillaCompat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class DyeableDoorBlock extends DoorBlock {
	public static final IntegerProperty COLOR = BlockStateProperties.LEVEL;

	public DyeableDoorBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
	}

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(HALF, FACING, OPEN, HINGE, POWERED, COLOR);
	}

	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (world.isClientSide) {
			return (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) ? ActionResultType.SUCCESS
					: super.use(state, world, pos, player, hand, hit);
		}
		
		if (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) {
			world.setBlock(pos, state.setValue(COLOR, VanillaCompat.COLOR_ITEMS.get(itemStack.getItem())), 3);
			return ActionResultType.CONSUME;
		}
		return super.use(state, world, pos, player, hand, hit);
	}
}