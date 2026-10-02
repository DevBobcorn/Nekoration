package io.devbobcorn.nekoration.blocks.cement;

import io.devbobcorn.nekoration.blocks.DyeableHorizontalConnectedBlock;
import io.devbobcorn.nekoration.blocks.states.FrameAlignment;

public class DyeableFrameSillBlock extends DyeableHorizontalConnectedBlock {

    public DyeableFrameSillBlock(Properties settings) {
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
