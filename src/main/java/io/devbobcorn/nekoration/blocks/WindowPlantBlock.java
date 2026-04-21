package io.devbobcorn.nekoration.blocks;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Window plant variant that should not offer sturdy support on its back face,
 * so panes/bars do not visually connect into it.
 */
public class WindowPlantBlock extends DyeableHorizontalConnectBlock {
    private static final Map<Direction, VoxelShape> SHAPES = getAABBs(4.0D, 10.0D);

    public WindowPlantBlock(Properties settings) {
        super(settings, ConnectionType.BEAM, true);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }
}
