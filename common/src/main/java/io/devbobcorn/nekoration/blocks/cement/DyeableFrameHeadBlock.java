package io.devbobcorn.nekoration.blocks.cement;

import io.devbobcorn.nekoration.blocks.DyeableHorizontalConnectedBlock;
import io.devbobcorn.nekoration.blocks.states.FrameAlignment;

public class DyeableFrameHeadBlock extends DyeableHorizontalConnectedBlock {

    public DyeableFrameHeadBlock(Properties settings) {
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
