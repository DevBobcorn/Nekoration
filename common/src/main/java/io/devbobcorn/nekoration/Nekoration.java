package io.devbobcorn.nekoration;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import io.devbobcorn.nekoration.registry.CementBlockRegistration;
import io.devbobcorn.nekoration.registry.ModBlockEntities;
import io.devbobcorn.nekoration.registry.ModCreativeTabs;
import io.devbobcorn.nekoration.registry.ModEntities;
import io.devbobcorn.nekoration.registry.ModItems;
import io.devbobcorn.nekoration.registry.ModMenuTypes;
import io.devbobcorn.nekoration.registry.ModRecipes;
import io.devbobcorn.nekoration.registry.OrnamentRegistration;
import io.devbobcorn.nekoration.registry.StoneBlockRegistration;
import io.devbobcorn.nekoration.registry.WoodenBlockRegistration;
import io.devbobcorn.nekoration.xplat.NekoRegistrar;

/**
 * Common mod bootstrap. Platform entrypoints call {@link #init} with their own
 * {@link NekoRegistrar} implementation before doing anything else.
 */
public final class Nekoration {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "nekoration";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    private Nekoration() {
    }

    /** Registers every registry object. Call once per game instance, first thing. */
    public static void init(NekoRegistrar registrar) {
        ModItems.register(registrar);
        WoodenBlockRegistration.register(registrar);
        StoneBlockRegistration.register(registrar);
        CementBlockRegistration.register(registrar);
        OrnamentRegistration.register(registrar);
        ModBlockEntities.register(registrar);
        ModEntities.register(registrar);
        ModMenuTypes.register(registrar);
        ModRecipes.register(registrar);
        ModCreativeTabs.register(registrar);
    }
}
