package com.devbobcorn.nekoration.common;

import java.util.Map;

import com.devbobcorn.nekoration.Nekoration;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class VanillaCompat {
	public static final Map<Block, Block> BLOCK_STRIPPING_MAP_PLUS = Maps.newHashMap();
	
	public static final Map<Item, Integer> COLOR_ITEMS = Maps.newHashMap();
	
	public static final Map<Item, Integer> RAW_COLOR_ITEMS = Maps.newHashMap();

	public static final Map<Item, Integer> FLAME_ITEMS = Maps.newHashMap();
	
    public static void Initialize()
    {
    	registerColorItem(Items.BLACK_DYE, 0);
    	registerColorItem(Items.BLUE_DYE, 1);
    	registerColorItem(Items.BROWN_DYE, 2);
    	registerColorItem(Items.CYAN_DYE, 3);
    	registerColorItem(Items.GRAY_DYE, 4);
    	registerColorItem(Items.GREEN_DYE, 5);
    	registerColorItem(Items.LIGHT_BLUE_DYE, 6);
    	registerColorItem(Items.LIGHT_GRAY_DYE, 7);
    	registerColorItem(Items.LIME_DYE, 8);
    	registerColorItem(Items.MAGENTA_DYE, 9);
    	registerColorItem(Items.ORANGE_DYE, 10);
    	registerColorItem(Items.PINK_DYE, 11);
    	registerColorItem(Items.PURPLE_DYE, 12);
    	registerColorItem(Items.RED_DYE, 13);
    	registerColorItem(Items.WHITE_DYE, 14);
    	registerColorItem(Items.YELLOW_DYE, 15);
    	
    	registerRawColorItem(Items.INK_SAC, 0);
    	registerRawColorItem(Items.WITHER_ROSE, 0);
    	registerRawColorItem(Items.LAPIS_LAZULI, 1);
    	registerRawColorItem(Items.COCOA_BEANS, 2);
		registerRawColorItem(Items.CORNFLOWER, 3);
    	registerRawColorItem(Items.STONE, 4);
    	registerRawColorItem(Items.COBBLESTONE, 4);
    	registerRawColorItem(Items.CACTUS, 5);
    	registerRawColorItem(Items.BLUE_ORCHID, 6);
    	registerRawColorItem(Items.AZURE_BLUET, 7);
    	registerRawColorItem(Items.OXEYE_DAISY, 7);
    	registerRawColorItem(Items.WHITE_TULIP, 7);
    	registerRawColorItem(Items.SEA_PICKLE, 8);
    	registerRawColorItem(Items.LILAC, 9);
    	registerRawColorItem(Items.ORANGE_TULIP, 10);
    	registerRawColorItem(Items.PEONY, 11);
    	registerRawColorItem(Items.PINK_TULIP, 11);
		registerRawColorItem(Items.ALLIUM, 12);
    	registerRawColorItem(Items.POPPY, 13);
    	registerRawColorItem(Items.ROSE_BUSH, 13);
    	registerRawColorItem(Items.RED_TULIP, 13);
    	registerRawColorItem(Items.BEETROOT, 13);
    	registerRawColorItem(Items.POPPY, 13);
    	registerRawColorItem(Items.BONE_MEAL, 14);
    	registerRawColorItem(Items.LILY_OF_THE_VALLEY, 14);
    	registerRawColorItem(Items.DANDELION, 15);
    	registerRawColorItem(Items.SUNFLOWER, 15);

		registerFlameItem(Items.TORCH, 1);
		registerFlameItem(Items.FLINT_AND_STEEL, 1);
		registerFlameItem(Items.LANTERN, 1);
		registerFlameItem(Items.CAMPFIRE, 1);
		registerFlameItem(Items.SOUL_TORCH, 2);
		registerFlameItem(Items.SOUL_LANTERN, 2);
		registerFlameItem(Items.SOUL_CAMPFIRE, 2);
		registerFlameItem(Items.NETHER_STAR, 3);
		registerFlameItem(Items.BEACON, 3);
		registerFlameItem(Items.END_CRYSTAL, 3);
        
        Nekoration.LOGGER.debug("Vanilla Compat Initialized!");
    }
	
    public static void registerColorItem(Item item, Integer num) {
    	COLOR_ITEMS.put(item, num);
    }
    
    public static void registerRawColorItem(Item item, Integer num) {
    	RAW_COLOR_ITEMS.put(item, num);
    }

	public static void registerFlameItem(Item item, Integer num) {
    	FLAME_ITEMS.put(item, num);
    }
}