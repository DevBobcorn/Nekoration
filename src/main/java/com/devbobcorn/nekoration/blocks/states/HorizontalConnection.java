package com.devbobcorn.nekoration.blocks.states;

import net.minecraft.util.IStringSerializable;

public enum HorizontalConnection implements IStringSerializable {
    S0("s0"), //Single
    D0("d0"), //Double
    D1("d1"),
    T0("t0"), //Triple
    T1("t1"),
    T2("t2");
 
    private final String name;
 
    private HorizontalConnection(String name) {
       this.name = name;
    }
 
    public String toString() {
       return this.getSerializedName();
    }
 
    public String getSerializedName() {
       return this.name;
    }
}
