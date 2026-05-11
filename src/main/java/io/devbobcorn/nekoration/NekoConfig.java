package io.devbobcorn.nekoration;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Gameplay settings (server-synced where applicable).
 */
public final class NekoConfig {

    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec SERVER_SPEC = SERVER_BUILDER.build();

    private NekoConfig() {
    }
}
