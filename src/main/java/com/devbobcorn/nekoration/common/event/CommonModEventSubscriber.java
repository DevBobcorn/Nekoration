package com.devbobcorn.nekoration.common.event;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.blocks.PotBlock;
import com.devbobcorn.nekoration.blocks.ChairBlock;
import com.devbobcorn.nekoration.blocks.TableBlock;
import com.devbobcorn.nekoration.blocks.CandleHolderBlock;
import com.devbobcorn.nekoration.blocks.WindowBlock;
import com.devbobcorn.nekoration.common.VanillaCompat;
import com.devbobcorn.nekoration.blocks.DyeableBlock;
import com.devbobcorn.nekoration.blocks.DyeableDoorBlock;
import com.devbobcorn.nekoration.blocks.EaselMenuBlock;
import com.devbobcorn.nekoration.blocks.BiDyeableBlock;
import com.devbobcorn.nekoration.blocks.DyeableHorizontalBlock;
import com.devbobcorn.nekoration.blocks.BiDyeableVerticalConnectBlock;
import com.devbobcorn.nekoration.blocks.DyeableHorizontalConnectBlock;
import com.devbobcorn.nekoration.items.DyeableBlockItem;
import com.devbobcorn.nekoration.items.BiDyeableBlockItem;
import com.devbobcorn.nekoration.items.DyeableWoodenBlockItem;
import com.devbobcorn.nekoration.items.ModItemTabs;
import com.devbobcorn.nekoration.particles.FlameParticleType;
import com.devbobcorn.nekoration.particles.ModParticles;

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
				Item.Properties properties;
				BlockItem blockItem;
				if (block instanceof TableBlock || block instanceof ChairBlock){
					// Classes: HalfTimberBlock / HalfTimberPillarBlock
					properties = new Item.Properties().tab(ModItemTabs.FURNITURE_GROUP);
					if (block == ModBlocks.PUMPKIN_TABLE.get() || block == ModBlocks.PUMPKIN_CHAIR.get()){
						blockItem = new BlockItem(block, properties);
					} else {
						blockItem = new DyeableBlockItem(block, properties);
					}
				} else if (block instanceof BiDyeableBlock || block instanceof BiDyeableVerticalConnectBlock){
					// Classes: HalfTimberBlock / HalfTimberPillarBlock
					properties = new Item.Properties().tab(ModItemTabs.WOODEN_GROUP);
					blockItem = new BiDyeableBlockItem(block, properties);
				} else if (block instanceof WindowBlock || block instanceof EaselMenuBlock){
					// Classes: DyeableBlock / CandleHolderBlock / PotBlock / DyeableHorizontalBlock, Default: White
					properties = new Item.Properties().tab(block instanceof WindowBlock ? ModItemTabs.WINDOW_GROUP : ModItemTabs.DECOR_GROUP);
					blockItem = new DyeableWoodenBlockItem(block, properties);
				} else if (block instanceof DyeableBlock || block instanceof DyeableHorizontalConnectBlock){
					// Classes: DyeableBlock / CandleHolderBlock / PotBlock / DyeableHorizontalBlock, Default: White
					if (block instanceof PotBlock || block instanceof CandleHolderBlock)
						properties = new Item.Properties().tab(ModItemTabs.DECOR_GROUP);
					else if (block instanceof DyeableHorizontalBlock) {
						if (block instanceof DyeableHorizontalConnectBlock) // Window Frame...
							properties = new Item.Properties().tab(ModItemTabs.WINDOW_GROUP);
						else properties = new Item.Properties().tab(ModItemTabs.DECOR_GROUP); // Awning...
					} else properties = new Item.Properties().tab(ModItemTabs.STONE_GROUP);
					blockItem = new DyeableBlockItem(block, properties);
				} else {
					if (block instanceof DyeableDoorBlock)
						properties = new Item.Properties().tab(ModItemTabs.DOOR_GROUP);
					else properties = new Item.Properties().tab(ModItemTabs.DECOR_GROUP);
					blockItem = new BlockItem(block, properties);
				}
				//Set its Registry Name...
				blockItem.setRegistryName(block.getRegistryName());
				//And register it...
				registry.register(blockItem);
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