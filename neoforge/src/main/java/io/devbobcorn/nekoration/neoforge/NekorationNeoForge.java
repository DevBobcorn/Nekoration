package io.devbobcorn.nekoration.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import io.devbobcorn.nekoration.NekoConfig;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.neoforge.xplat.NeoForgeRegistrar;
import io.devbobcorn.nekoration.neoforge.xplat.NeoForgePlatform;
import io.devbobcorn.nekoration.network.NekorationNetwork;
import io.devbobcorn.nekoration.xplat.NekoPlatform;

/**
 * NeoForge entrypoint. Sets up the platform facade, then delegates to the
 * loader-agnostic bootstrap in common.
 */
@Mod(Nekoration.MODID)
public class NekorationNeoForge {
    public NekorationNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        // Platform facade must be initialized before any common code runs.
        NeoForgeRegistrar registrar = new NeoForgeRegistrar();
        NekoPlatform.init(NeoForgePlatform.INSTANCE);

        // Register every registry object through the loader-agnostic facade,
        // then bind the created DeferredRegisters to the mod event bus.
        Nekoration.init(registrar);
        registrar.registerModEventBus(modEventBus);

        // Networking channels.
        modEventBus.addListener(NekorationNetwork::register);
        modEventBus.addListener(this::onCommonSetup);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.SERVER, NekoConfig.SERVER_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, NekoConfig.CLIENT_SPEC);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(NeoForgeFlammability::register);
    }
}
