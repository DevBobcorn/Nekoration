package io.devbobcorn.nekoration.blocks.states;

import net.minecraft.util.StringRepresentable;

public enum FrameConnection implements StringRepresentable {
    LEFT("left"),
    RIGHT("right"),
    BOTH("both");

    private final String name;

    FrameConnection(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
