package io.devbobcorn.nekoration.fabric.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import io.devbobcorn.nekoration.network.CustomBlockClearPayload;
import io.devbobcorn.nekoration.network.CustomBlockEditPayload;
import io.devbobcorn.nekoration.network.CustomBlockUpdatePayload;
import io.devbobcorn.nekoration.network.EaselMenuUpdatePayload;
import io.devbobcorn.nekoration.network.PaintingDataUpdatePayload;
import io.devbobcorn.nekoration.network.PaintingSignUpdatePayload;
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
        PayloadTypeRegistry.playC2S().register(PaintingSignUpdatePayload.TYPE, PaintingSignUpdatePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(PaletteUpdatePayload.TYPE, PaletteUpdatePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(CustomBlockUpdatePayload.TYPE, CustomBlockUpdatePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(CustomBlockClearPayload.TYPE, CustomBlockClearPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(CustomBlockEditPayload.TYPE, CustomBlockEditPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(EaselMenuUpdatePayload.TYPE,
                (payload, context) -> EaselMenuUpdatePayload.handle(payload, serverContext(context.player())));
        ServerPlayNetworking.registerGlobalReceiver(PaintingDataUpdatePayload.TYPE,
                (payload, context) -> PaintingDataUpdatePayload.handle(payload, serverContext(context.player())));
        ServerPlayNetworking.registerGlobalReceiver(PaintingSizeUpdatePayload.TYPE,
                (payload, context) -> PaintingSizeUpdatePayload.handle(payload, serverContext(context.player())));
        ServerPlayNetworking.registerGlobalReceiver(PaintingSignUpdatePayload.TYPE,
                (payload, context) -> PaintingSignUpdatePayload.handle(payload, serverContext(context.player())));
        ServerPlayNetworking.registerGlobalReceiver(PaletteUpdatePayload.TYPE,
                (payload, context) -> PaletteUpdatePayload.handle(payload, serverContext(context.player())));
        ServerPlayNetworking.registerGlobalReceiver(CustomBlockUpdatePayload.TYPE,
                (payload, context) -> CustomBlockUpdatePayload.handle(payload, serverContext(context.player())));
        ServerPlayNetworking.registerGlobalReceiver(CustomBlockClearPayload.TYPE,
                (payload, context) -> CustomBlockClearPayload.handle(payload, serverContext(context.player())));
        ServerPlayNetworking.registerGlobalReceiver(CustomBlockEditPayload.TYPE,
                (payload, context) -> CustomBlockEditPayload.handle(payload, serverContext(context.player())));
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
