package io.devbobcorn.nekoration.blocks.furniture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Simple wooden table block.
 */
public class TableBlock extends Block {
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 2.0D, 14.0D, 2.0D),
            Block.box(14.0D, 0.0D, 0.0D, 16.0D, 14.0D, 2.0D),
            Block.box(14.0D, 0.0D, 14.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 14.0D, 2.0D, 14.0D, 16.0D));

    public TableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
