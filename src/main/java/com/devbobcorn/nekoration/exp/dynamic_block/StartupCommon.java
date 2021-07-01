package com.devbobcorn.nekoration.exp.dynamic_block;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * User: The Grey Ghost
 * Date: 24/12/2014
 *
 * The methods for this example are called during startup
 *  See MinecraftByExample class for more information
 */
public class StartupCommon
{
  public static BlockCamouflage blockCamouflage;  // this holds the unique instance of your block
  public static BlockItem itemBlockCamouflage;  // this holds the unique instance of the ItemBlock corresponding to your block

  @SubscribeEvent
  public static void onBlocksRegistration(final RegistryEvent.Register<Block> blockRegisterEvent) {
    blockCamouflage = (BlockCamouflage)(new BlockCamouflage().setRegistryName("exp", "block_camouflage"));
    blockRegisterEvent.getRegistry().register(blockCamouflage);
  }

  @SubscribeEvent
  public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
    // We need to create a BlockItem so the player can carry this block in their hand and it can appear in the inventory
    final int MAXIMUM_STACK_SIZE = 64;  // player can hold 64 of this block in their hand at once

    Item.Properties itemProperties = new Item.Properties()
            .stacksTo(MAXIMUM_STACK_SIZE)
            .tab(ItemGroup.TAB_BUILDING_BLOCKS);  // which inventory tab?
    itemBlockCamouflage = new BlockItem(blockCamouflage, itemProperties);
    itemBlockCamouflage.setRegistryName(blockCamouflage.getRegistryName());
    itemRegisterEvent.getRegistry().register(itemBlockCamouflage);
  }
}
