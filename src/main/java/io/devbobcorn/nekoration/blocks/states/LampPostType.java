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

    @Override
    public String getSerializedName() {
        return name;
    }
}
