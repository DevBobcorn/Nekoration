package io.devbobcorn.nekoration.network;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import io.devbobcorn.nekoration.xplat.PayloadContext;

/**
 * NeoForge payload channel registration. The payload records and their
 * handling logic live in common; only this wiring is loader-specific.
 */
public final class NekorationNetwork {
    private NekorationNetwork() {
    }

    public static void register(final RegisterPayloadHandlersEvent event) {
        final var registrar = event.registrar("1");
        registrar.playToServer(EaselMenuUpdatePayload.TYPE, EaselMenuUpdatePayload.STREAM_CODEC,
                (payload, ctx) -> payload.handle(payload, adapt(ctx)));
        // Painting...
        registrar.playToServer(PaintingDataUpdatePayload.TYPE, PaintingDataUpdatePayload.STREAM_CODEC,
                (payload, ctx) -> payload.handle(payload, adapt(ctx)));
        registrar.playToServer(PaintingSizeUpdatePayload.TYPE, PaintingSizeUpdatePayload.STREAM_CODEC,
                (payload, ctx) -> payload.handle(payload, adapt(ctx)));
        registrar.playToServer(PaletteUpdatePayload.TYPE, PaletteUpdatePayload.STREAM_CODEC,
                (payload, ctx) -> payload.handle(payload, adapt(ctx)));
        registrar.playToClient(PaintingDataBroadcastPayload.TYPE, PaintingDataBroadcastPayload.STREAM_CODEC,
                (payload, ctx) -> payload.handle(payload, adapt(ctx)));
    }

    private static PayloadContext adapt(IPayloadContext context) {
        return new PayloadContext() {
            @Override
            public Player player() {
                return context.player();
            }

            @Override
            public void enqueue(Runnable work) {
                context.enqueueWork(work);
            }
        };
    }
}
