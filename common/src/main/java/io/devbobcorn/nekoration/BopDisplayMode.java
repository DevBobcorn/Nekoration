package io.devbobcorn.nekoration;

import com.mojang.serialization.Codec;

import io.devbobcorn.nekoration.xplat.NekoPlatform;
import net.minecraft.util.StringRepresentable;

/**
 * Controls when the Biomes O' Plenty wood variants (and their creative
 * filter sub-tabs) appear in the creative inventory.
 */
public enum BopDisplayMode implements StringRepresentable {
    ALWAYS("always"),
    WHEN_BOP_INSTALLED("when_bop_installed"),
    NEVER("never");

    /** Mod id of Biomes O' Plenty. */
    public static final String BOP_MODID = "biomesoplenty";

    public static final Codec<BopDisplayMode> CODEC = StringRepresentable.fromEnum(BopDisplayMode::values);
    public static final BopDisplayMode[] VALUES = values();

    private final String serializedName;

    BopDisplayMode(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    /** Whether BOP wood variants should currently appear in the creative inventory. */
    public static boolean showBopVariants() {
        return switch (NekoPlatform.config().bopDisplayMode()) {
            case ALWAYS -> true;
            case WHEN_BOP_INSTALLED -> NekoPlatform.isModLoaded(BOP_MODID);
            case NEVER -> false;
        };
    }
}
