package com.devbobcorn.nekoration.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;

public abstract class DyeableHorizontalBlock extends DyeableBlock {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    protected DyeableHorizontalBlock(Properties settings) {
        super(settings);
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
        s.add(COLOR, FACING);
    }

    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }
}