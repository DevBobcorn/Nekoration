package io.devbobcorn.nekoration.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NekorationNetwork {
    private NekorationNetwork() {
    }

    public static void register(final RegisterPayloadHandlersEvent event) {
        final var registrar = event.registrar("1");
        registrar.playToServer(EaselMenuUpdatePayload.TYPE, EaselMenuUpdatePayload.STREAM_CODEC, EaselMenuUpdatePayload::handle);
    }
}
