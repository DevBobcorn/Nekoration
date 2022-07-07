package com.devbobcorn.nekoration.common.event;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.common.VanillaCompat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = Nekoration.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class CommonModEventSubscriber {
    private static final Logger LOGGER = LogManager.getLogger(Nekoration.MODID + " Mod Event Subscriber");

    @SubscribeEvent
    public static void onFMLCommonSetupEvent(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing Vanilla Compat");
        VanillaCompat.Initialize();
    }
}