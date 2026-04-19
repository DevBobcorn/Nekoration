package io.devbobcorn.nekoration.blocks.states;

import net.minecraft.util.StringRepresentable;

public enum HorizontalConnection implements StringRepresentable {
    S0("s0"),
    D0("d0"),
    D1("d1"),
    T0("t0"),
    T1("t1"),
    T2("t2");

    private final String name;

    HorizontalConnection(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
