package io.devbobcorn.nekoration;

import java.awt.Color;

import org.joml.Vector3d;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

/**
 * Dye / accent colors used by dyeable blocks and items.
 */
public final class NekoColors {
    private NekoColors() {
    }

    // RGB color math helpers (used by painting & palette)...
    public static Color getRGBColor(Vec3i vec) {
        return new Color(Math.min(Math.max(vec.getX(), 0), 255), Math.min(Math.max(vec.getY(), 0), 255), Math.min(Math.max(vec.getZ(), 0), 255));
    }

    public static Color getRGBColor(Vector3d vec) {
        return new Color(Math.min(Math.max((int) vec.x, 0), 255), Math.min(Math.max((int) vec.y, 0), 255), Math.min(Math.max((int) vec.z, 0), 255));
    }

    public static Color getRGBColor(int col) {
        return new Color((col & 0xff0000) >> 16, (col & 0xff00) >> 8, col & 0xff);
    }

    public static int[] getRGBArray(int col) {
        return new int[] { ((col & 0xff0000) >> 16), ((col & 0xff00) >> 8), (col & 0xff) };
    }

    public static int getRGBColorBetween(double frac, int lc, int rc) {
        int red1 = (lc & 0xff0000) >> 16;
        int green1 = (lc & 0xff00) >> 8;
        int blue1 = lc & 0xff;

        int red2 = (rc & 0xff0000) >> 16;
        int green2 = (rc & 0xff00) >> 8;
        int blue2 = rc & 0xff;

        int red3 = (int) Mth.lerp(frac, red1, red2);
        int green3 = (int) Mth.lerp(frac, green1, green2);
        int blue3 = (int) Mth.lerp(frac, blue1, blue2);

        return (red3 << 16) + (green3 << 8) + blue3;
    }

    public static Color getRGBColorBetween(double frac, Color lc, Color rc) {
        return new Color((int) Mth.lerp(frac, lc.getRed(), rc.getRed()), (int) Mth.lerp(frac, lc.getGreen(), rc.getGreen()), (int) Mth.lerp(frac, lc.getBlue(), rc.getBlue()));
    }

    public static int getRed(int c) {
        return (c & 0xff0000) >> 16;
    }

    public static int getGreen(int c) {
        return (c & 0xff00) >> 8;
    }

    public static int getBlue(int c) {
        return c & 0xff;
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
                0xe03e16,
                0xf9801d,
                0xfec81d,
                0xc0f73f,
                0x4ee316,
                0x56dcac,
                0x5ad3fa,
                0x1ab3ea,
                0xc992f8,
                0xe75ecd,
                0xff8bca,
        };
    }

    /**
     * RGB tints for cement.
     */
    public static final class CementColors {
        private CementColors() {
        }

        public static final int[] RGB_BY_ORDINAL = {
                0xf9fffe,
                0x999999,
                0x787878,
                0x474f4f,
                0xab866c,
                0xb62b23,
                0xe8931a,
                0xffce2b,
                0xa2bf17,
                0x5fad3a,
                0x5dd19b,
                0x46ced1,
                0x3cb4ff,
                0xb9a7f9,
                0xd86cd3,
                0xff8cae,
        };
    }

    public enum NekoColorPalette {
        HALF_TIMBER(HalfTimberColors.RGB_BY_ORDINAL),
        CEMENT(CementColors.RGB_BY_ORDINAL);

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
