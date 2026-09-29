package io.devbobcorn.nekoration.fabric.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import io.devbobcorn.nekoration.fabric.xplat.FabricPlatform;
import io.devbobcorn.nekoration.network.PaintingDataBroadcastPayload;
import io.devbobcorn.nekoration.network.PaintingInitPayload;
import io.devbobcorn.nekoration.xplat.PayloadContext;

/**
 * Fabric client payload registration and client-to-server sending.
 */
public final class FabricClientNetwork {
    private FabricClientNetwork() {
    }

    public static void register() {
        FabricPlatform.setClientSender(ClientPlayNetworking::send);
        PayloadTypeRegistry.playS2C().register(
                PaintingDataBroadcastPayload.TYPE, PaintingDataBroadcastPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(
                PaintingInitPayload.TYPE, PaintingInitPayload.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(PaintingDataBroadcastPayload.TYPE,
                (payload, context) -> PaintingDataBroadcastPayload.handle(payload, clientContext(context.player())));
        ClientPlayNetworking.registerGlobalReceiver(PaintingInitPayload.TYPE,
                (payload, context) -> PaintingInitPayload.handle(payload, clientContext(context.player())));
    }

    private static PayloadContext clientContext(Player player) {
        return new PayloadContext() {
            @Override
            public Player player() {
                return player;
            }

            @Override
            public void enqueue(Runnable work) {
                Minecraft.getInstance().execute(work);
            }
        };
    }
}
