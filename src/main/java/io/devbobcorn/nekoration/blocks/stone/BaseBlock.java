package io.devbobcorn.nekoration.blocks.stone;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BaseBlock extends Block {
    private final VoxelShape shapeBase;

    public BaseBlock(Properties settings) {
        super(settings);
        VoxelShape shapePartBottom = box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
        VoxelShape shapePartTop = box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        VoxelShape shapePartMiddle = box(1.0D, 6.0D, 1.0D, 15.0D, 13.0D, 15.0D);
        this.shapeBase = Shapes.or(shapePartBottom, shapePartMiddle, shapePartTop);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shapeBase;
    }
}
