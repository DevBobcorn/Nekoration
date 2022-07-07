package com.devbobcorn.nekoration.common.event;

import com.devbobcorn.nekoration.Nekoration;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Nekoration.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class CommonForgeEventSubscriber {
    //private static final Logger LOGGER = LogManager.getLogger(Nekoration.MODID + " Forge Event Subscriber");

    @SubscribeEvent
	public static void onRegisterTrades(VillagerTradesEvent event) {
		/* TODO
        if (event.getType() == VillagerProfession.LIBRARIAN) {
			event.getTrades().get(1).add(new TradeHelper.BrochureForEmeralds(1));

            LOGGER.info("Brochure Trade Registered.");
		} */

	}
}
