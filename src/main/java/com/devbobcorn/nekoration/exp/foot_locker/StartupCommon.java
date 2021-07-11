package com.devbobcorn.nekoration.exp.foot_locker;

import com.devbobcorn.nekoration.exp.ExpCommon;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * User: brandon3055 Date: 06/01/2015
 *
 * The Startup classes for this example are called during startup in the
 * following order * Register<Block> * Register<Item> *
 * Register<TileEntityType<?>> * Register<ContainerType<?>> See
 * MinecraftByExample class for more information
 */
public class StartupCommon {
	public static Block blockInventoryBasic; // this holds the unique instance of your block
	public static BlockItem itemBlockInventoryBasic; // and the corresponding item form that block

	public static TileEntityType<TileEntityInventoryBasic> tileEntityTypeMBE30; // Holds the type of our tile entity;
																				// needed for the TileEntityData
																				// constructor
	public static ContainerType<ContainerBasic> containerTypeContainerBasic;

	@SubscribeEvent
	public static void onBlocksRegistration(final RegistryEvent.Register<Block> blockRegisterEvent) {
		blockInventoryBasic = new BlockInventoryBasic().setRegistryName(ExpCommon.ExpNameSpace, "foot_locker");
		blockRegisterEvent.getRegistry().register(blockInventoryBasic);
	}

	@SubscribeEvent
	public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
		// We need to create a BlockItem so the player can carry this block in their
		// hand and it can appear in the inventory
		final int MAXIMUM_STACK_SIZE = 1; // player can only hold 1 of this block in their hand at once

		Item.Properties itemSimpleProperties = new Item.Properties().stacksTo(MAXIMUM_STACK_SIZE)
				.tab(ItemGroup.TAB_DECORATIONS); // which inventory tab?
		itemBlockInventoryBasic = new BlockItem(blockInventoryBasic, itemSimpleProperties);
		itemBlockInventoryBasic.setRegistryName(blockInventoryBasic.getRegistryName());
		itemRegisterEvent.getRegistry().register(itemBlockInventoryBasic);
	}

	@SubscribeEvent
	public static void onTileEntityTypeRegistration(final RegistryEvent.Register<TileEntityType<?>> event) {
		tileEntityTypeMBE30 = TileEntityType.Builder.of(TileEntityInventoryBasic::new, blockInventoryBasic).build(null);
		// you probably don't need a datafixer --> null should be fine
		tileEntityTypeMBE30.setRegistryName(ExpCommon.ExpNameSpace, "foot_locker");
		event.getRegistry().register(tileEntityTypeMBE30);
	}

	@SubscribeEvent
	public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
		containerTypeContainerBasic = IForgeContainerType.create(ContainerBasic::createContainerClientSide);
		containerTypeContainerBasic.setRegistryName(ExpCommon.ExpNameSpace, "foot_locker");
		event.getRegistry().register(containerTypeContainerBasic);
	}
}
