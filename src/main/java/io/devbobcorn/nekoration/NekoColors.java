package io.devbobcorn.nekoration;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;

/**
 * Dye / accent colors used by dyeable blocks and items (matches 1.16.5 NBT IDs 0–15).
 */
public final class NekoColors {
    private NekoColors() {
    }

    public enum EnumNekoColor implements StringRepresentable {
        BLACK((byte) 0, "black", 0x5c5c5c),
        BLUE((byte) 1, "blue", 0x2891ff),
        BROWN((byte) 2, "brown", 0x673400),
        CYAN((byte) 3, "cyan", 0x94e2ff),
        GRAY((byte) 4, "gray", 0x9f9f9f),
        GREEN((byte) 5, "green", 0x33b54c),
        LIGHT_BLUE((byte) 6, "light_blue", 0x75aaff),
        LIGHT_GRAY((byte) 7, "light_gray", 0xbebebe),
        LIME((byte) 8, "lime", 0x7aff8f),
        MAGENTA((byte) 9, "magenta", 0xf976ff),
        ORANGE((byte) 10, "orange", 0xff7700),
        PINK((byte) 11, "pink", 0xffa3e0),
        PURPLE((byte) 12, "purple", 0xbc61ff),
        RED((byte) 13, "red", 0xe03f3f),
        WHITE((byte) 14, "white", 0xffffff),
        YELLOW((byte) 15, "yellow", 0xffc80a);

        private final byte nbtId;
        private final String name;
        private final int color;

        EnumNekoColor(byte nbtId, String name, int color) {
            this.nbtId = nbtId;
            this.name = name;
            this.color = color;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public int getColor() {
            return color;
        }

        public int getNbtId() {
            return nbtId & 0xFF;
        }

        public void putIntoNbt(CompoundTag compound, String tagName) {
            compound.putByte(tagName, nbtId);
        }

        public static EnumNekoColor fromNbt(CompoundTag compound, String tagName) {
            byte id = 0;
            if (compound != null && compound.contains(tagName)) {
                id = compound.getByte(tagName);
            }
            return getColorEnumFromId(id);
        }

        public static EnumNekoColor getColorEnumFromId(byte id) {
            for (EnumNekoColor c : values()) {
                if (c.nbtId == id) {
                    return c;
                }
            }
            return LIGHT_GRAY;
        }

        public static int getColorValueFromId(byte id) {
            for (EnumNekoColor c : values()) {
                if (c.nbtId == id) {
                    return c.color;
                }
            }
            return LIGHT_GRAY.color;
        }
    }
}
