package io.devbobcorn.nekoration;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Gameplay settings (server-synced where applicable).
 */
public final class NekoConfig {
    public enum HorConnectionDir {
        LEFT2RIGHT,
        RIGHT2LEFT,
        BOTH,
        NEITHER
    }

    public enum VerConnectionDir {
        BOTTOM2TOP,
        TOP2BOTTOM,
        BOTH,
        NEITHER
    }

    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.EnumValue<VerConnectionDir> VER_CONNECTION_DIR = SERVER_BUILDER
            .comment("In what placement order vertical connect blocks merge with adjacent ones (default BOTTOM2TOP).")
            .defineEnum("verConnectionDir", VerConnectionDir.BOTTOM2TOP);
    public static final ModConfigSpec.EnumValue<HorConnectionDir> HOR_CONNECTION_DIR = SERVER_BUILDER
            .comment("In what placement order horizontal connect blocks merge with adjacent ones (default LEFT2RIGHT).")
            .defineEnum("horConnectionDir", HorConnectionDir.LEFT2RIGHT);

    public static final ModConfigSpec SERVER_SPEC = SERVER_BUILDER.build();

    private NekoConfig() {
    }
}
