package io.devbobcorn.nekoration.xplat;

import net.minecraft.world.entity.player.Player;

/**
 * Minimal platform-agnostic view of a payload handling context.
 * Replaces the NeoForge {@code IPayloadContext}.
 */
public interface PayloadContext {
    Player player();

    /** Runs the work on the main thread (server or client, as appropriate). */
    void enqueue(Runnable work);
}
