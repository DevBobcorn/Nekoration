package io.devbobcorn.nekoration;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;

/**
 * Dye / accent colors used by dyeable blocks and items.
 */
public final class NekoColors {
    private NekoColors() {
    }

    /**
     * RGB tints for half-timber plaster.
     */
    public static final class HalfTimberColors {
        private HalfTimberColors() {
        }

        public static final int[] RGB_BY_ORDINAL = {
                0xf9fffe,
                0x9d9d97,
                0x474f52,
                0x2d2d31,
                0x835432,
                0xb02e26,
                0xf9801d,
                0xfed83d,
                0x80c71f,
                0x5ec316,
                0x169c9c,
                0x3ab3da,
                0x3c44aa,
                0x8932b8,
                0xc74ebd,
                0xf38baa,
        };
    }

    public enum NekoColorPalette {
        HALF_TIMBER(HalfTimberColors.RGB_BY_ORDINAL);

        private final int[] rgbByOrdinal;

        NekoColorPalette(int[] rgbByOrdinal) {
            this.rgbByOrdinal = rgbByOrdinal;
        }

        public int rgbFor(EnumNekoColor color) {
            return this.rgbByOrdinal[color.ordinal()];
        }
    }

    public enum EnumNekoColor implements StringRepresentable {
        WHITE((byte) 0, "white"),
        LIGHT_GRAY((byte) 1, "light_gray"),
        GRAY((byte) 2, "gray"),
        BLACK((byte) 3, "black"),
        BROWN((byte) 4, "brown"),
        RED((byte) 5, "red"),
        ORANGE((byte) 6, "orange"),
        YELLOW((byte) 7, "yellow"),
        LIME((byte) 8, "lime"),
        GREEN((byte) 9, "green"),
        CYAN((byte) 10, "cyan"),
        LIGHT_BLUE((byte) 11, "light_blue"),
        BLUE((byte) 12, "blue"),
        PURPLE((byte) 13, "purple"),
        MAGENTA((byte) 14, "magenta"),
        PINK((byte) 15, "pink");

        private final byte nbtId;
        private final String name;

        EnumNekoColor(byte nbtId, String name) {
            this.nbtId = nbtId;
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        /** Tint from the default {@link NekoColorPalette#HALF_TIMBER} palette. */
        public int getColor() {
            return getColor(NekoColorPalette.HALF_TIMBER);
        }

        public int getColor(NekoColorPalette palette) {
            return palette.rgbFor(this);
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
            return getColorValueFromId(id, NekoColorPalette.HALF_TIMBER);
        }

        public static int getColorValueFromId(byte id, NekoColorPalette palette) {
            return getColorEnumFromId(id).getColor(palette);
        }
    }
}
