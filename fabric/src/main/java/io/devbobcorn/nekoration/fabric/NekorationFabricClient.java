package io.devbobcorn.nekoration.fabric;

import net.fabricmc.api.ClientModInitializer;
import io.devbobcorn.nekoration.fabric.client.FabricClientSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NekorationFabricClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("nekoration");

    @Override
    public void onInitializeClient() {
        FabricClientSetup.initialize();
    }
}
