package io.devbobcorn.nekoration.blocks;

import io.devbobcorn.nekoration.blocks.states.VerticalConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Non-dyeable vertical window; wood and shape are fixed per block id ({@code window_<wood>_<variant>}).
 */
public class WindowBlock extends VerticalConnectBlock {

    public WindowBlock(Properties settings) {
        super(settings);
        registerDefaultState(stateDefinition.any().setValue(CONNECTION, VerticalConnection.S0));
    }

    public WindowBlock(Properties settings, ConnectionType type, boolean connectOtherVariant) {
        super(settings, type, connectOtherVariant);
        registerDefaultState(stateDefinition.any().setValue(CONNECTION, VerticalConnection.S0));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
        return adjacentState.getBlock() == this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 0.5F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }
}
