package io.devbobcorn.nekoration.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;

/**
 * Dyeable stone pot/planter block with configurable radius.
 */
public class PotBlock extends DyeableBlock {
    private final VoxelShape shape;

    public PotBlock(Properties properties, double radius) {
        super(properties);
        this.shape = box(8.0D - radius, 0.0D, 8.0D - radius, 8.0D + radius, 16.0D, 8.0D + radius);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing,
            BlockState plant) {
        return plant.getFluidState().is(FluidTags.WATER) ? TriState.FALSE : TriState.TRUE;
    }
}
