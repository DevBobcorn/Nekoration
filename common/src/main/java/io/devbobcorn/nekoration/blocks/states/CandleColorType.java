package io.devbobcorn.nekoration.blocks.states;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import net.minecraft.util.StringRepresentable;

/**
 * Candle color of candle holders: plain vanilla candles, or vanilla candles
 * matching one of the 16 mod dye colors.
 */
public enum CandleColorType implements StringRepresentable {
    UNCOLORED("uncolored", null),
    WHITE("white", EnumNekoColor.WHITE),
    LIGHT_GRAY("light_gray", EnumNekoColor.LIGHT_GRAY),
    GRAY("gray", EnumNekoColor.GRAY),
    BLACK("black", EnumNekoColor.BLACK),
    BROWN("brown", EnumNekoColor.BROWN),
    RED("red", EnumNekoColor.RED),
    ORANGE("orange", EnumNekoColor.ORANGE),
    YELLOW("yellow", EnumNekoColor.YELLOW),
    LIME("lime", EnumNekoColor.LIME),
    GREEN("green", EnumNekoColor.GREEN),
    CYAN("cyan", EnumNekoColor.CYAN),
    LIGHT_BLUE("light_blue", EnumNekoColor.LIGHT_BLUE),
    BLUE("blue", EnumNekoColor.BLUE),
    PURPLE("purple", EnumNekoColor.PURPLE),
    MAGENTA("magenta", EnumNekoColor.MAGENTA),
    PINK("pink", EnumNekoColor.PINK);

    private final String name;
    private final EnumNekoColor dyeColor;

    CandleColorType(String name, EnumNekoColor dyeColor) {
        this.name = name;
        this.dyeColor = dyeColor;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public boolean isUncolored() {
        return this.dyeColor == null;
    }

    /** Mod dye color matching this variant; null when uncolored. */
    public EnumNekoColor getDyeColor() {
        return this.dyeColor;
    }

    /** Variant for a mod dye color; uncolored dye colors (null) become {@link #UNCOLORED}. */
    public static CandleColorType of(EnumNekoColor dyeColor) {
        if (dyeColor == null) {
            return UNCOLORED;
        }
        for (CandleColorType color : values()) {
            if (color.dyeColor == dyeColor) {
                return color;
            }
        }
        return UNCOLORED;
    }
}
