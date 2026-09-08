package io.devbobcorn.nekoration.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NekorationNetwork {
    private NekorationNetwork() {
    }

    public static void register(final RegisterPayloadHandlersEvent event) {
        final var registrar = event.registrar("1");
        registrar.playToServer(EaselMenuUpdatePayload.TYPE, EaselMenuUpdatePayload.STREAM_CODEC, EaselMenuUpdatePayload::handle);
        // Painting...
        registrar.playToServer(PaintingDataUpdatePayload.TYPE, PaintingDataUpdatePayload.STREAM_CODEC, PaintingDataUpdatePayload::handle);
        registrar.playToServer(PaintingSizeUpdatePayload.TYPE, PaintingSizeUpdatePayload.STREAM_CODEC, PaintingSizeUpdatePayload::handle);
        registrar.playToServer(PaletteUpdatePayload.TYPE, PaletteUpdatePayload.STREAM_CODEC, PaletteUpdatePayload::handle);
        registrar.playToClient(PaintingDataBroadcastPayload.TYPE, PaintingDataBroadcastPayload.STREAM_CODEC, PaintingDataBroadcastPayload::handle);
    }
}
