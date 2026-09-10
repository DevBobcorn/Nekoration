package io.devbobcorn.nekoration.neoforge.xplat;

import java.nio.file.Path;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;
import io.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;
import io.devbobcorn.nekoration.NekoConfig;
import io.devbobcorn.nekoration.xplat.NekoConfigData;
import io.devbobcorn.nekoration.xplat.NekoPlatformImpl;

public final class NeoForgePlatform implements NekoPlatformImpl {
    public static final NeoForgePlatform INSTANCE = new NeoForgePlatform();

    private NeoForgePlatform() {
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }

    @Override
    public Path gameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public NekoConfigData config() {
        return NekoConfig.DATA;
    }

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    @Override
    public void sendToClient(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public void sendToAllPlayers(CustomPacketPayload payload) {
        PacketDistributor.sendToAllPlayers(payload);
    }

    @Override
    public void openEaselMenu(ServerPlayer player, EaselMenuBlockEntity easel) {
        player.openMenu(easel, easel.getBlockPos());
    }
}
