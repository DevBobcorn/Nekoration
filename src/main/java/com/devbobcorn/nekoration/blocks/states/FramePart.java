package com.devbobcorn.nekoration.blocks.states;

import net.minecraft.util.StringRepresentable;

public enum FramePart implements StringRepresentable {
    MIDDLE("middle"),
    TOP("top"),
    BOTTOM("bottom");
 
    private final String name;
 
    private FramePart(String name) {
       this.name = name;
    }
 
    public String toString() {
       return this.getSerializedName();
    }
 
    public String getSerializedName() {
       return this.name;
    }
}
