package com.devbobcorn.nekoration.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;

public class ChairBlock extends HorizontalBlock {
    protected ChairBlock(Properties settings) {
        super(settings);
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
        s.add(FACING);
    }

    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }
}
