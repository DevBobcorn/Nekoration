package io.devbobcorn.nekoration.xplat;

import java.nio.file.Path;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import io.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;

/**
 * Static facade over the platform implementation. Each platform entrypoint
 * calls {@link #init} as its first statement, before any other common code
 * runs.
 */
public final class NekoPlatform {
    private static NekoPlatformImpl impl;

    private NekoPlatform() {
    }

    public static void init(NekoPlatformImpl platformImpl) {
        impl = platformImpl;
    }

    public static boolean isClient() {
        return impl.isClient();
    }

    public static Path gameDir() {
        return impl.gameDir();
    }

    public static NekoConfigData config() {
        return impl.config();
    }

    public static void sendToServer(CustomPacketPayload payload) {
        impl.sendToServer(payload);
    }

    public static void sendToClient(ServerPlayer player, CustomPacketPayload payload) {
        impl.sendToClient(player, payload);
    }

    public static void sendToAllPlayers(CustomPacketPayload payload) {
        impl.sendToAllPlayers(payload);
    }

    /** Opens the extended easel menu, carrying the block position as extra data. */
    public static void openEaselMenu(ServerPlayer player, EaselMenuBlockEntity easel) {
        impl.openEaselMenu(player, easel);
    }
}
