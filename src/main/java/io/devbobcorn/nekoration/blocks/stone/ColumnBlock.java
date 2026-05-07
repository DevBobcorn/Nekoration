package io.devbobcorn.nekoration.blocks.stone;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import io.devbobcorn.nekoration.blocks.VerticalConnectedBlock;

public class ColumnBlock extends VerticalConnectedBlock {
    private final VoxelShape shapeMiddle;
    private final VoxelShape shapeBottom;
    private final VoxelShape shapeTopDoric;

    public ColumnBlock(Properties settings) {
        super(settings, ConnectionType.PILLAR, true);

        final int radius = 6;
        this.shapeMiddle = box(8.0D - radius, 0.0D, 8.0D - radius, 8.0D + radius, 16.0D, 8.0D + radius);

        VoxelShape shapePartBase = box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
        VoxelShape shapePartTop = box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        VoxelShape shapePartMiddle = box(1.0D, 6.0D, 1.0D, 15.0D, 13.0D, 15.0D);
        this.shapeBottom = Shapes.or(shapePartBase, shapePartMiddle, shapePartTop);

        this.shapeTopDoric = Shapes.or(this.shapeMiddle, shapePartTop);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(CONNECTION)) {
            case S0, D0, T0 -> this.shapeBottom;
            case T1 -> this.shapeMiddle;
            case D1, T2 -> this.shapeTopDoric;
        };
    }
}
