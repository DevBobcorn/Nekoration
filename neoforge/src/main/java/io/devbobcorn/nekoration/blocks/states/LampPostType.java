package io.devbobcorn.nekoration.blocks.states;

import net.minecraft.util.StringRepresentable;

public enum LampPostType implements StringRepresentable {
    TOP("top"),
    SIDE_UP("side_up"),
    SIDE_DOWN("side_down"),
    POLE("pole"),
    BASE("base");

    private final String name;

    LampPostType(String name) {
        this.name = name;
    }

    /**
     * Whether this type is part of a free-standing vertical post (base, pole, or
     * capped top), as opposed to a wall-mounted bracket that extends sideways.
     */
    public boolean isPost() {
        return this == TOP || this == POLE || this == BASE;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
