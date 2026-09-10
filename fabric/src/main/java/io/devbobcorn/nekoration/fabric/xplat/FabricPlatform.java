package io.devbobcorn.nekoration.fabric.xplat;

import java.nio.file.Path;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import io.devbobcorn.nekoration.blocks.containers.EaselMenuMenu;
import io.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;
import io.devbobcorn.nekoration.fabric.network.FabricNetwork;
import java.util.function.Consumer;
import io.devbobcorn.nekoration.xplat.NekoConfigData;
import io.devbobcorn.nekoration.xplat.NekoPlatformImpl;

public final class FabricPlatform implements NekoPlatformImpl {
    public static final FabricPlatform INSTANCE = new FabricPlatform();

    private static volatile net.minecraft.server.MinecraftServer currentServer;

    /** Set by the client setup; the server environment never sends clientbound... C2S packets without a client. */
    private static volatile Consumer<CustomPacketPayload> clientSender;

    private FabricPlatform() {
    }

    /** Tracks the running server for server-wide broadcasts. */
    public static void onServerStarted(net.minecraft.server.MinecraftServer server) {
        currentServer = server;
    }

    public static net.minecraft.server.MinecraftServer currentServer() {
        net.minecraft.server.MinecraftServer server = currentServer;
        if (server == null) {
            throw new IllegalStateException("No server is running");
        }
        return server;
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public Path gameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public NekoConfigData config() {
        return FabricConfig.get();
    }

    /** Called by the client setup to wire client-to-server sending. */
    public static void setClientSender(Consumer<CustomPacketPayload> sender) {
        clientSender = sender;
    }

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        Consumer<CustomPacketPayload> sender = clientSender;
        if (sender != null) {
            sender.accept(payload);
        }
    }

    @Override
    public void sendToClient(ServerPlayer player, CustomPacketPayload payload) {
        FabricNetwork.sendToClient(player, payload);
    }

    @Override
    public void sendToAllPlayers(CustomPacketPayload payload) {
        FabricNetwork.sendToAllPlayers(payload);
    }

    @Override
    public void openEaselMenu(ServerPlayer player, EaselMenuBlockEntity easel) {
        ExtendedScreenHandlerFactory<net.minecraft.core.BlockPos> provider = new ExtendedScreenHandlerFactory<>() {
            @Override
            public net.minecraft.core.BlockPos getScreenOpeningData(ServerPlayer openingPlayer) {
                return easel.getBlockPos();
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory inventory,
                    net.minecraft.world.entity.player.Player openingPlayer) {
                return easel.createMenu(containerId, inventory, openingPlayer);
            }

            @Override
            public net.minecraft.network.chat.Component getDisplayName() {
                return easel.getDisplayName();
            }
        };
        player.openMenu(provider);
    }
}
