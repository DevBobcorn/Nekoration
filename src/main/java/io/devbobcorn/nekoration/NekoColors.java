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
        WHITE((byte) 0, "white", 0xf9fffe),
        LIGHT_GRAY((byte) 1, "light_gray", 0x9d9d97),
        GRAY((byte) 2, "gray", 0x474f52),
        BLACK((byte) 3, "black", 0x1d1d21),
        BROWN((byte) 4, "brown", 0x835432),
        RED((byte) 5, "red", 0xb02e26),
        ORANGE((byte) 6, "orange", 0xf9801d),
        YELLOW((byte) 7, "yellow", 0xfed83d),
        LIME((byte) 8, "lime", 0x80c71f),
        GREEN((byte) 9, "green", 0x5ec316),
        CYAN((byte) 10, "cyan", 0x169c9c),
        LIGHT_BLUE((byte) 11, "light_blue", 0x3ab3da),
        BLUE((byte) 12, "blue", 0x3c44aa),
        PURPLE((byte) 13, "purple", 0x8932b8),
        MAGENTA((byte) 14, "magenta", 0xc74ebd),
        PINK((byte) 15, "pink", 0xf38baa);

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
            return WHITE;
        }

        public static int getColorValueFromId(byte id) {
            for (EnumNekoColor c : values()) {
                if (c.nbtId == id) {
                    return c.color;
                }
            }
            return WHITE.color;
        }
    }
}
