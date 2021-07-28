package com.devbobcorn.nekoration.common.event;

import com.devbobcorn.nekoration.debug.RegisterDebugCommandEvent;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonForgeEventSubscriber {
	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(new Shakaraka());
		MinecraftForge.EVENT_BUS.register(RegisterDebugCommandEvent.class);
	}
}
