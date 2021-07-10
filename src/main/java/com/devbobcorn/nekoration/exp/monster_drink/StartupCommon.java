package com.devbobcorn.nekoration.exp.monster_drink;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * User: The Grey Ghost Date: 24/12/2014 See MinecraftByExample class for more
 * information
 */
public class StartupCommon {
	public static MonsterDrinkItem monsterDrink; // this holds the unique instance of the item

	@SubscribeEvent
	public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
		monsterDrink = new MonsterDrinkItem();
		monsterDrink.setRegistryName("exp", "monster_drink");
		itemRegisterEvent.getRegistry().register(monsterDrink);
	}
}
