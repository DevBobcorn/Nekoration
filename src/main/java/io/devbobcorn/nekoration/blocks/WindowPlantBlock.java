package io.devbobcorn.nekoration.blocks;

public class WindowPlantBlock extends DyeableHorizontalConnectedBlock {
    public WindowPlantBlock(Properties settings) {
        super(settings, ConnectionType.BEAM, true, 4, 10, 0);
    }
}
