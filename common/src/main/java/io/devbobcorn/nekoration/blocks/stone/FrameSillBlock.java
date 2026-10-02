package io.devbobcorn.nekoration.blocks.stone;

import io.devbobcorn.nekoration.blocks.HorizontalConnectedBlock;
import io.devbobcorn.nekoration.blocks.states.FrameAlignment;

public class FrameSillBlock extends HorizontalConnectedBlock {

    public FrameSillBlock(Properties settings) {
        super(settings, ConnectionType.BEAM, false, 4, 4, 12);
    }

    @Override
    public boolean hasFrameConnection() {
        return true;
    }

    @Override
    protected FrameAlignment defaultFrameConnection() {
        return FrameAlignment.TOP;
    }
}
