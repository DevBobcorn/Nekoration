package com.devbobcorn.nekoration.blocks.states;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ModStateProperties {
    public static final EnumProperty<FramePart> FRAME_PART = EnumProperty.create("frame_part", FramePart.class);
    public static final EnumProperty<LampPostType> LAMP_POST_TYPE = EnumProperty.create("post_type", LampPostType.class);
    public static final EnumProperty<VerticalConnection> VERTICAL_CONNECTION = EnumProperty.create("vertical_connection", VerticalConnection.class);
    public static final EnumProperty<HorizontalConnection> HONRIZONTAL_CONNECTION = EnumProperty.create("horizontal_connection", HorizontalConnection.class);

    public static final BooleanProperty LEFT = BooleanProperty.create("left");
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");
}
