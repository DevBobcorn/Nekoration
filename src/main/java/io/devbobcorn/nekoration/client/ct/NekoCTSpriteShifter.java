package io.devbobcorn.nekoration.client.ct;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.resources.ResourceLocation;

public final class NekoCTSpriteShifter {
    private static final Map<String, NekoCTSpriteShiftEntry> ENTRY_CACHE = new ConcurrentHashMap<>();

    private NekoCTSpriteShifter() {
    }

    public static NekoCTSpriteShiftEntry getCT(NekoCTType type, ResourceLocation blockTexture, ResourceLocation connectedTexture) {
        String key = blockTexture + "->" + connectedTexture + "+" + type.getId();
        return ENTRY_CACHE.computeIfAbsent(key, ignored -> new NekoCTSpriteShiftEntry(type, blockTexture, connectedTexture));
    }
}
