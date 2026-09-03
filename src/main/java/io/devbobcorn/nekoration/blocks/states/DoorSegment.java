package io.devbobcorn.nekoration.blocks.states;

import net.minecraft.util.StringRepresentable;

/**
 * Vertical segment of a 3-block-tall door.
 */
public enum DoorSegment implements StringRepresentable {
    LOWER("lower"),
    MIDDLE("middle"),
    UPPER("upper");

    private final String name;

    DoorSegment(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
