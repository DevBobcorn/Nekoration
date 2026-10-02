package io.devbobcorn.nekoration.blocks.states;

import net.minecraft.util.StringRepresentable;

public enum FrameAlignment implements StringRepresentable {
    TOP("top"),
    BOTTOM("bottom");

    private final String name;

    FrameAlignment(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
