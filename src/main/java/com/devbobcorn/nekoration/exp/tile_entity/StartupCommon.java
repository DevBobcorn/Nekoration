package com.devbobcorn.nekoration.exp.tile_entity;

import com.devbobcorn.nekoration.exp.ExpCommon;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * User: The Grey Ghost Date: 24/12/2014
 *
 * The Startup classes for this example are called during startup, in the
 * following order: Register<Block> Register<Item> Register<TileEntityType<?>>
 * See MinecraftByExample class for more information
 */
public class StartupCommon {
	public static BlockMBE21 blockMBE21; // this holds the unique instance of your block
	public static BlockItem itemBlockMBE21; // this holds the unique instance of the itemblock corresponding to the
											// block
	public static TileEntityType<TileEntityMBE21> tileEntityDataTypeMBE21; // Holds the type of our tile entity; needed
																			// for the TileEntityData constructor

	@SubscribeEvent
	public static void onBlocksRegistration(final RegistryEvent.Register<Block> event) {
		blockMBE21 = (BlockMBE21) (new BlockMBE21().setRegistryName(ExpCommon.ExpNameSpace, "hourglass"));
		event.getRegistry().register(blockMBE21);
	}

	@SubscribeEvent
	public static void onItemsRegistration(final RegistryEvent.Register<Item> event) {
		// We need to create a BlockItem so the player can carry this block in their
		// hand and it can appear in the inventory
		final int MAXIMUM_STACK_SIZE = 1; // player can only hold 1 of this block in their hand at once

		Item.Properties itemSimpleProperties = new Item.Properties().stacksTo(MAXIMUM_STACK_SIZE)
				.tab(ItemGroup.TAB_DECORATIONS); // which inventory tab?
		itemBlockMBE21 = new BlockItem(blockMBE21, itemSimpleProperties);
		itemBlockMBE21.setRegistryName(blockMBE21.getRegistryName());
		event.getRegistry().register(itemBlockMBE21);
	}

	@SubscribeEvent
	public static void onTileEntityTypeRegistration(final RegistryEvent.Register<TileEntityType<?>> event) {
		tileEntityDataTypeMBE21 = TileEntityType.Builder.of(TileEntityMBE21::new, blockMBE21).build(null); // you
																											// probably
																											// don't
																											// need a
																											// datafixer
																											// --> null
																											// should be
																											// fine
		tileEntityDataTypeMBE21.setRegistryName(ExpCommon.ExpNameSpace, "hourglass");
		event.getRegistry().register(tileEntityDataTypeMBE21);
	}
}
