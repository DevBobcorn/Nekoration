package io.devbobcorn.nekoration.blocks.states;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class ModStateProperties {
    public static final EnumProperty<FramePart> FRAME_PART =
            EnumProperty.create("frame_part", FramePart.class);
    public static final EnumProperty<HorizontalConnection> HORIZONTAL_CONNECTION =
            EnumProperty.create("horizontal_connection", HorizontalConnection.class);
    public static final EnumProperty<VerticalConnection> VERTICAL_CONNECTION =
            EnumProperty.create("vertical_connection", VerticalConnection.class);
    public static final BooleanProperty LEFT = BooleanProperty.create("left");
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");

    private ModStateProperties() {
    }
}
