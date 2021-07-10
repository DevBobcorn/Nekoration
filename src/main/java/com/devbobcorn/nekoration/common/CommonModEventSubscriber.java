package com.devbobcorn.nekoration.common;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.blocks.DyeableBlock;
import com.devbobcorn.nekoration.blocks.HalfTimberBlock;
import com.devbobcorn.nekoration.blocks.HalfTimberPillarBlock;
import com.devbobcorn.nekoration.blocks.DyeableHorizontalConnectBlock;
import com.devbobcorn.nekoration.items.DyeableBlockItem;
import com.devbobcorn.nekoration.items.BiDyeableBlockItem;
import com.devbobcorn.nekoration.particles.FlameParticleType;
import com.devbobcorn.nekoration.particles.ModParticles;
import com.devbobcorn.nekoration.tabs.ModItemTabs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = Nekoration.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class CommonModEventSubscriber {
	private static final Logger LOGGER = LogManager.getLogger(Nekoration.MODID + " Mod Event Subscriber");

	/**
	 * This method will be called by Forge when it is time for the mod to register
	 * its Items. This method will always be called after the Block registry method.
	 */
	@SubscribeEvent
	public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		// Automatically register BlockItems for all our Blocks
		ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)
				// You can do extra filtering here if you don't want some blocks to have an
				// BlockItem automatically registered for them
				// .filter(block -> needsItemBlock(block))
				// Register the BlockItem for the block
				.forEach(block -> {
					if (block instanceof HalfTimberBlock || block instanceof HalfTimberPillarBlock){
						// Classes: HalfTimberBlock / HalfTimberPillarBlock
						Item.Properties properties = new Item.Properties().tab(ModItemTabs.NEKORATION_GROUP);
						// Create the new BlockItem with the block and it's properties
						final BiDyeableBlockItem blockItem = new BiDyeableBlockItem(block, properties);
						// Set the new BlockItem's registry name to the block's registry name
						blockItem.setRegistryName(block.getRegistryName());
						// Register the BlockItem
						registry.register(blockItem);
					} else if (block instanceof DyeableBlock || block instanceof DyeableHorizontalConnectBlock){
						// Classes: DyeableBlock / CandleHolderBlock / PotBlock / DyeableHorizontalBlock, Default: White
						Item.Properties properties = new Item.Properties().tab(ModItemTabs.NEKORATION_GROUP);
						// Create the new BlockItem with the block and it's properties
						final DyeableBlockItem blockItem = new DyeableBlockItem(block, properties);
						// Set the new BlockItem's registry name to the block's registry name
						blockItem.setRegistryName(block.getRegistryName());
						// Register the BlockItem
						registry.register(blockItem);
					} else {
						Item.Properties properties = new Item.Properties().tab(ModItemTabs.NEKORATION_GROUP);
						// Create the new BlockItem with the block and it's properties
						final BlockItem blockItem = new BlockItem(block, properties);
						// Set the new BlockItem's registry name to the block's registry name
						blockItem.setRegistryName(block.getRegistryName());
						// Register the BlockItem
						registry.register(blockItem);
					}
				});
		LOGGER.info("BlockItems Registered.");
	}

	@SubscribeEvent
	public static void onIParticleTypeRegistration(RegistryEvent.Register<ParticleType<?>> iParticleTypeRegisterEvent) {
		ModParticles.FLAME = new FlameParticleType();
		ModParticles.FLAME.setRegistryName(Nekoration.MODID, "flame");
		iParticleTypeRegisterEvent.getRegistry().register(ModParticles.FLAME);
	}

	@SubscribeEvent
	public static void onFMLCommonSetupEvent(final FMLCommonSetupEvent event) {
		VanillaCompat.Initialize();
	}
}