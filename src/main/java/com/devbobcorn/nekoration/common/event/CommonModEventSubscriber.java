package com.devbobcorn.nekoration.common.event;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.StoneBlock;
import com.devbobcorn.nekoration.blocks.StonePillarBlock;
import com.devbobcorn.nekoration.blocks.HalfTimberBlock;
import com.devbobcorn.nekoration.blocks.HalfTimberPillarBlock;
import com.devbobcorn.nekoration.blocks.CandleHolderBlock;
import com.devbobcorn.nekoration.blocks.ChairBlock;
import com.devbobcorn.nekoration.blocks.DyeableBlock;
import com.devbobcorn.nekoration.blocks.DyeableDoorBlock;
import com.devbobcorn.nekoration.blocks.DyeableHorizontalBlock;
import com.devbobcorn.nekoration.blocks.DyeableHorizontalConnectBlock;
import com.devbobcorn.nekoration.blocks.EaselMenuBlock;
import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.blocks.TableBlock;
import com.devbobcorn.nekoration.blocks.WindowBlock;
import com.devbobcorn.nekoration.common.VanillaCompat;
import com.devbobcorn.nekoration.items.HalfTimberBlockItem;
import com.devbobcorn.nekoration.items.DyeableBlockItem;
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
					blockItem = new BlockItem(block, properties);
				} else if (block instanceof HalfTimberBlock || block instanceof HalfTimberPillarBlock){
					// Classes: HalfTimberBlock / HalfTimberPillarBlock
					properties = new Item.Properties().tab(ModItemTabs.WOODEN_GROUP);
					blockItem = new HalfTimberBlockItem(block, properties);
				} else if (block instanceof WindowBlock || block instanceof EaselMenuBlock){
					properties = new Item.Properties().tab(block instanceof WindowBlock ? ModItemTabs.WINDOW_N_DOOR_GROUP : ModItemTabs.DECOR_GROUP);
					blockItem = new DyeableWoodenBlockItem(block, properties);
				} else if (block instanceof StoneBlock || block instanceof StonePillarBlock){
					properties = new Item.Properties().tab(ModItemTabs.STONE_GROUP);
					blockItem = new DyeableBlockItem(block, properties);
				} else out:if (block instanceof DyeableBlock || block instanceof DyeableHorizontalConnectBlock){
					if (block instanceof CandleHolderBlock){
						properties = new Item.Properties().tab(ModItemTabs.DECOR_GROUP);
						blockItem = new DyeableBlockItem(block, properties, false);
						break out;
					} else if (block instanceof DyeableHorizontalBlock) {
						if (block instanceof DyeableHorizontalConnectBlock) // Window Frame...
							properties = new Item.Properties().tab(ModItemTabs.WINDOW_N_DOOR_GROUP);
						else properties = new Item.Properties().tab(ModItemTabs.DECOR_GROUP); // Awning...
					} else properties = new Item.Properties().tab(ModItemTabs.DECOR_GROUP);
					blockItem = new DyeableBlockItem(block, properties);
				} else {
					if (block instanceof DyeableDoorBlock)
						properties = new Item.Properties().tab(ModItemTabs.WINDOW_N_DOOR_GROUP);
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
	public static void onRegisterIParticleTypes(RegistryEvent.Register<ParticleType<?>> event) {
		ModParticles.FLAME = new FlameParticleType();
		ModParticles.FLAME.setRegistryName(Nekoration.MODID, "flame");
		event.getRegistry().register(ModParticles.FLAME);
	}

	@SubscribeEvent
	public static void onFMLCommonSetupEvent(final FMLCommonSetupEvent event) {
		VanillaCompat.Initialize();
	}
}