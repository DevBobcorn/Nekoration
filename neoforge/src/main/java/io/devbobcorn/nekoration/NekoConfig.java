package io.devbobcorn.nekoration;

import java.util.Locale;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.TranslatableEnum;

import io.devbobcorn.nekoration.xplat.NekoConfigData;

/**
 * Gameplay settings (server-synced where applicable).
 */
public final class NekoConfig {

    /**
     * NeoForge-side mirror of {@link BopDisplayMode}; implements
     * {@link TranslatableEnum} so the built-in config screen shows the
     * translated option names.
     */
    public enum BopVariantDisplay implements TranslatableEnum {
        ALWAYS,
        WHEN_BOP_INSTALLED,
        NEVER;

        @Override
        public Component getTranslatedName() {
            return Component.translatable("nekoration.configuration.creative.bopVariants."
                    + name().toLowerCase(Locale.ROOT));
        }
    }

    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec SERVER_SPEC = SERVER_BUILDER.build();

    public static class Client {
        public final ModConfigSpec.BooleanValue useImageRendering;
        public final ModConfigSpec.BooleanValue simplifyRendering;
        public final ModConfigSpec.BooleanValue debugMode;
        public final ModConfigSpec.IntValue maxUndoLimit;
        public final ModConfigSpec.EnumValue<BopVariantDisplay> bopVariants;

        Client(ModConfigSpec.Builder builder) {
            builder.comment("Painting configuration settings").push("painting");
            this.useImageRendering = builder
                    .comment("Whether to cache paintings to this client, and use them for rendering.(Default to true)")
                    .define("useImageRendering", true);
            this.simplifyRendering = builder
                    .comment("Whether to simplify the lighting calculation when rendering paintings.(Default to true)")
                    .define("simplifyRendering", true);
            this.debugMode = builder
                    .comment("Whether to display debug information at the bottom of paintings.(Default to false)")
                    .define("debugMode", false);
            this.maxUndoLimit = builder
                    .comment("The maximum undo/redo steps allowed on this client.(Default to 15)")
                    .defineInRange("maxUndoLimit", 15, 2, 30);
            builder.pop();
            builder.comment("Creative inventory settings").push("creative");
            this.bopVariants = builder
                    .comment("When to show the Biomes O' Plenty wood variants (and their filter sub-tabs) in the creative inventory.(Default to WHEN_BOP_INSTALLED)")
                    .defineEnum("bopVariants", BopVariantDisplay.WHEN_BOP_INSTALLED);
            builder.pop();
        }
    }

    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec CLIENT_SPEC;

    public static final Client CLIENT;

    /** Loader-agnostic view of the client config for common code. */
    public static final NekoConfigData DATA = new NekoConfigData() {
        @Override
        public boolean useImageRendering() {
            return CLIENT.useImageRendering.get();
        }

        @Override
        public boolean simplifyRendering() {
            return CLIENT.simplifyRendering.get();
        }

        @Override
        public boolean debugMode() {
            return CLIENT.debugMode.get();
        }

        @Override
        public int maxUndoLimit() {
            return CLIENT.maxUndoLimit.get();
        }

        @Override
        public BopDisplayMode bopDisplayMode() {
            return BopDisplayMode.VALUES[CLIENT.bopVariants.get().ordinal()];
        }
    };

    static {
        CLIENT = new Client(CLIENT_BUILDER);
        CLIENT_SPEC = CLIENT_BUILDER.build();
    }

    private NekoConfig() {
    }
}
