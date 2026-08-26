package io.devbobcorn.nekoration.blocks.states;

import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class ModStateProperties {
    public static final EnumProperty<FrameConnection> FRAME_CONNECTION =
            EnumProperty.create("frame_connection", FrameConnection.class);
    public static final EnumProperty<HorizontalConnection> HORIZONTAL_CONNECTION =
            EnumProperty.create("horizontal_connection", HorizontalConnection.class);
    public static final EnumProperty<VerticalConnection> VERTICAL_CONNECTION =
            EnumProperty.create("vertical_connection", VerticalConnection.class);
    public static final EnumProperty<LampPostType> LAMP_POST_TYPE =
            EnumProperty.create("post_type", LampPostType.class);

    private ModStateProperties() {
    }
}
