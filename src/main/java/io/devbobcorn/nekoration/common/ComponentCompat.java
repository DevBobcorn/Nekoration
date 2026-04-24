package io.devbobcorn.nekoration.common;

import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * Compatibility helpers for translation components used by tooltip mods.
 */
public final class ComponentCompat {
    private ComponentCompat() {
    }

    /**
     * Returns a client-localized plain string on physical clients so third-party
     * tooltip serializers can consume interpolated values, while keeping a
     * translatable component on dedicated servers for per-client localization.
     */
    public static Object interpolationArg(String key) {
        Component translated = Component.translatable(key);
        return FMLEnvironment.dist.isClient() ? translated.getString() : translated;
    }
}
