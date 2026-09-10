package io.devbobcorn.nekoration.fabric.network;

import java.util.List;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import io.devbobcorn.nekoration.network.EaselMenuUpdatePayload;
import io.devbobcorn.nekoration.network.PaintingDataBroadcastPayload;
import io.devbobcorn.nekoration.network.PaintingDataUpdatePayload;
import io.devbobcorn.nekoration.network.PaintingInitPayload;
import io.devbobcorn.nekoration.network.PaintingSizeUpdatePayload;
import io.devbobcorn.nekoration.network.PaletteUpdatePayload;
import io.devbobcorn.nekoration.xplat.PayloadContext;

/**
 * Fabric payload channel registration. The payload records and their handling
 * logic live in common; only this wiring is loader-specific.
 */
public final class FabricNetwork {
    private FabricNetwork() {
    }

    public static void registerCommon() {
        // Client -> server
        PayloadTypeRegistry.playC2S().register(EaselMenuUpdatePayload.TYPE, EaselMenuUpdatePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(PaintingDataUpdatePayload.TYPE, PaintingDataUpdatePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(PaintingSizeUpdatePayload.TYPE, PaintingSizeUpdatePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(PaletteUpdatePayload.TYPE, PaletteUpdatePayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(EaselMenuUpdatePayload.TYPE,
                (payload, context) -> payload.handle(payload, serverContext(context.player())));
        ServerPlayNetworking.registerGlobalReceiver(PaintingDataUpdatePayload.TYPE,
                (payload, context) -> payload.handle(payload, serverContext(context.player())));
        ServerPlayNetworking.registerGlobalReceiver(PaintingSizeUpdatePayload.TYPE,
                (payload, context) -> payload.handle(payload, serverContext(context.player())));
        ServerPlayNetworking.registerGlobalReceiver(PaletteUpdatePayload.TYPE,
                (payload, context) -> payload.handle(payload, serverContext(context.player())));
    }

    public static void sendToClient(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendToAllPlayers(CustomPacketPayload payload) {
        for (ServerPlayer player : io.devbobcorn.nekoration.fabric.xplat.FabricPlatform.currentServer()
                .getPlayerList().getPlayers()) {
            sendToClient(player, payload);
        }
    }

    private static PayloadContext serverContext(Player player) {
        return new PayloadContext() {
            @Override
            public Player player() {
                return player;
            }

            @Override
            public void enqueue(Runnable work) {
                if (player instanceof ServerPlayer serverPlayer && serverPlayer.getServer() != null) {
                    serverPlayer.getServer().execute(work);
                } else {
                    work.run();
                }
            }
        };
    }

}
