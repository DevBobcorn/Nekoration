package io.devbobcorn.nekoration;

import net.neoforged.neoforge.common.ModConfigSpec;
import io.devbobcorn.nekoration.xplat.NekoConfigData;

/**
 * Gameplay settings (server-synced where applicable).
 */
public final class NekoConfig {

    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec SERVER_SPEC = SERVER_BUILDER.build();

    public static class Client {
        public final ModConfigSpec.BooleanValue useImageRendering;
        public final ModConfigSpec.BooleanValue simplifyRendering;
        public final ModConfigSpec.BooleanValue debugMode;
        public final ModConfigSpec.IntValue maxUndoLimit;

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
    };

    static {
        CLIENT = new Client(CLIENT_BUILDER);
        CLIENT_SPEC = CLIENT_BUILDER.build();
    }

    private NekoConfig() {
    }
}
