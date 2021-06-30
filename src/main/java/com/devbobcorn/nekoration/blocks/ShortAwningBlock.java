package com.devbobcorn.nekoration.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class ShortAwningBlock extends DyeableHorizontalBlock {
	public static final VoxelShape SHAPE = Block.box(0.1D, 2.0D, 0.1D, 15.9D, 15.9D, 15.9D);

	public ShortAwningBlock(Properties settings) {
		super(settings);
	}

	public VoxelShape getInteractionShape(BlockState state, IBlockReader world, BlockPos pos) {
		return SHAPE;
	}

	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos) {
		return SHAPE;
	}

	public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_,
			ISelectionContext p_220053_4_) {
		return SHAPE;
	}
}
