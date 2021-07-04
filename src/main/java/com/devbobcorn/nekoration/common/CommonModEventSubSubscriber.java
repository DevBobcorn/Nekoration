package com.devbobcorn.nekoration.common;

import com.google.common.base.Preconditions;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blockentities.EaselMenuBlockEnity;
import com.devbobcorn.nekoration.blockentities.ModEntityType;
import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.particles.FlameParticleType;
import com.devbobcorn.nekoration.particles.ModParticles;
import com.devbobcorn.nekoration.tabs.ModItemTabs;

@EventBusSubscriber(modid = Nekoration.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class CommonModEventSubSubscriber {
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
					// Make the properties, and make it so that the item will be on our ItemGroup
					// (CreativeTab)
					Item.Properties properties = new Item.Properties().tab(ModItemTabs.NEKORATION_GROUP);
					// Create the new BlockItem with the block and it's properties
					final BlockItem blockItem = new BlockItem(block, properties);
					// Set the new BlockItem's registry name to the block's registry name
					blockItem.setRegistryName(block.getRegistryName());
					// Register the BlockItem
					registry.register(blockItem);
				});
		LOGGER.debug("Registered BlockItems");
	}

	@SubscribeEvent
	public static void onTileEntityTypeRegistration(final RegistryEvent.Register<TileEntityType<?>> event) {
		ModEntityType.EASEL_MENU_TYPE = TileEntityType.Builder.of(EaselMenuBlockEnity::new, ModBlocks.EASEL_MENU.get())
				.build(null);
		// you probably don't need a datafixer -> null should be fine
		setup(ModEntityType.EASEL_MENU_TYPE, "easel_menu");
	}

	@SubscribeEvent
	public static void onIParticleTypeRegistration(RegistryEvent.Register<ParticleType<?>> iParticleTypeRegisterEvent) {
		ModParticles.FLAME = new FlameParticleType();
		ModParticles.FLAME.setRegistryName(Nekoration.MODID, "flame");
		iParticleTypeRegisterEvent.getRegistry().register(ModParticles.FLAME);
	}

	/**
	 * Performs setup on a registry entry
	 *
	 * @param name
	 *            The path of the entry's name. Used to make a name who's domain is
	 *            our mod's modid
	 */
	@Nonnull
	private static <T extends IForgeRegistryEntry<T>> T setup(@Nonnull final T entry, @Nonnull final String name) {
		Preconditions.checkNotNull(name, "Name to assign to entry cannot be null!");
		return setup(entry, new ResourceLocation(Nekoration.MODID, name));
	}

	/**
	 * Performs setup on a registry entry
	 *
	 * @param registryName
	 *            The full registry name of the entry
	 */
	@Nonnull
	private static <T extends IForgeRegistryEntry<T>> T setup(@Nonnull final T entry,
			@Nonnull final ResourceLocation registryName) {
		Preconditions.checkNotNull(entry, "Entry cannot be null!");
		Preconditions.checkNotNull(registryName, "Registry name to assign to entry cannot be null!");
		entry.setRegistryName(registryName);
		return entry;
	}

	@SubscribeEvent
	public static void onFMLCommonSetupEvent(final FMLCommonSetupEvent event) {
		VanillaCompat.Initialize();
	}
}