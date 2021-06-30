package com.devbobcorn.nekoration.blocks.states;

import net.minecraft.util.IStringSerializable;

public enum LampPostType implements IStringSerializable {
   TOP("top"),
   SIDE_UP("side_up"),
   SIDE_DOWN("side_down"),
   POLE("pole"),
   BASE("base");

   private final String name;

   private LampPostType(String name) {
      this.name = name;
   }

   public String toString() {
      return this.getSerializedName();
   }

   public String getSerializedName() {
      return this.name;
   }
}
