package io.devbobcorn.nekoration.xplat;

import java.nio.file.Path;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import io.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;

/**
 * The platform-provided implementation behind {@link NekoPlatform}.
 */
public interface NekoPlatformImpl {
    boolean isClient();

    Path gameDir();

    NekoConfigData config();

    void sendToServer(CustomPacketPayload payload);

    void sendToClient(ServerPlayer player, CustomPacketPayload payload);

    void sendToAllPlayers(CustomPacketPayload payload);

    void openEaselMenu(ServerPlayer player, EaselMenuBlockEntity easel);
}
