package io.devbobcorn.nekoration.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.world.level.block.Block;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.fabric.xplat.FabricPlatform;
import io.devbobcorn.nekoration.fabric.xplat.FabricRegistrar;
import io.devbobcorn.nekoration.fabric.network.FabricNetwork;
import io.devbobcorn.nekoration.registry.OrnamentRegistration;
import io.devbobcorn.nekoration.registry.WoodenBlockRegistration;
import io.devbobcorn.nekoration.xplat.NekoPlatform;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;

/**
 * Fabric entrypoint. Sets up the platform facade, then delegates to the
 * loader-agnostic bootstrap in common.
 */
public class NekorationFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // Platform facade must be initialized before any common code runs.
        FabricRegistrar registrar = new FabricRegistrar();
        NekoPlatform.init(FabricPlatform.INSTANCE);

        // Register every registry object through the loader-agnostic facade.
        Nekoration.init(registrar);

        FabricNetwork.registerCommon();

        // Flammability via Fabric's registry (vanilla planks odds: 5/20, wool/leaves: 30/60).
        FlammableBlockRegistry flammable = FlammableBlockRegistry.getDefaultInstance();
        for (var entry : WoodenBlockRegistration.blocksByWoodView().entrySet()) {
            if (!entry.getKey().isFlammable()) {
                continue;
            }
            for (RegistrySupplier<Block> holder : entry.getValue()) {
                flammable.add(holder.get(), 5, 20);
            }
        }
        for (RegistrySupplier<Block> holder : OrnamentRegistration.awningBlocksView()) {
            flammable.add(holder.get(), 30, 60);
        }
        flammable.add(OrnamentRegistration.windowPlantBlock().get(), 30, 60);

        ServerLifecycleEvents.SERVER_STARTED.register(FabricPlatform::onServerStarted);
    }
}
