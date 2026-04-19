package io.devbobcorn.nekoration.blocks.states;

import net.minecraft.util.StringRepresentable;

public enum FramePart implements StringRepresentable {
    MIDDLE("middle"),
    TOP("top"),
    BOTTOM("bottom");

    private final String name;

    FramePart(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
