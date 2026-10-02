package io.devbobcorn.nekoration.blocks.stone;

import io.devbobcorn.nekoration.blocks.HorizontalConnectedBlock;
import io.devbobcorn.nekoration.blocks.states.FrameAlignment;

public class FrameHeadBlock extends HorizontalConnectedBlock {

    public FrameHeadBlock(Properties settings) {
        super(settings, ConnectionType.BEAM, false, 2, 3, 0);
    }

    @Override
    public boolean hasFrameConnection() {
        return true;
    }

    @Override
    protected FrameAlignment defaultFrameConnection() {
        return FrameAlignment.BOTTOM;
    }
}
