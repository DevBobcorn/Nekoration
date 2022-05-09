package com.devbobcorn.nekoration.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShortAwningBlock extends DyeableHorizontalBlock {
    public static final VoxelShape SHAPE = Block.box(0.1D, 2.0D, 0.1D, 15.9D, 15.9D, 15.9D);

    public ShortAwningBlock(Properties settings) {
        super(settings);
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
}
