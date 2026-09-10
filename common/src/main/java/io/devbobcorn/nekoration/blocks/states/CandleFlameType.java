package io.devbobcorn.nekoration.blocks.states;

import net.minecraft.util.StringRepresentable;

public enum CandleFlameType implements StringRepresentable {
    NONE("none"),
    FLAME("flame"),
    SOUL_FLAME("soul_flame"),
    FIREWORK("firework");

    private final String name;

    CandleFlameType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public boolean isLit() {
        return this != NONE;
    }
}
