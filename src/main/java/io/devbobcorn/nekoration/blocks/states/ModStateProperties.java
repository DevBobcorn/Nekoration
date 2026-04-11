package io.devbobcorn.nekoration.blocks.states;

import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class ModStateProperties {
    public static final EnumProperty<VerticalConnection> VERTICAL_CONNECTION =
            EnumProperty.create("vertical_connection", VerticalConnection.class);

    private ModStateProperties() {
    }
}
