package com.devbobcorn.nekoration.common;

import java.lang.reflect.Method;
import java.util.Map;

import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.google.common.collect.Maps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class VanillaCompat {
	private static final Logger LOGGER = LogManager.getLogger("Vanilla Compat");

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

		registerCandleFlameItem(Items.TORCH, 1);
		registerCandleFlameItem(Items.FLINT_AND_STEEL, 1);
		registerCandleFlameItem(Items.LANTERN, 1);
		registerCandleFlameItem(Items.CAMPFIRE, 1);
		registerCandleFlameItem(Items.SOUL_TORCH, 2);
		registerCandleFlameItem(Items.SOUL_LANTERN, 2);
		registerCandleFlameItem(Items.SOUL_CAMPFIRE, 2);
		registerCandleFlameItem(Items.NETHER_STAR, 3);
		registerCandleFlameItem(Items.BEACON, 3);
		registerCandleFlameItem(Items.END_CRYSTAL, 3);

		registerFlammablity();
        
        LOGGER.debug("Vanilla Compat Initialized!");
    }
	
    public static void registerColorItem(Item item, Integer num) {
    	COLOR_ITEMS.put(item, num);
    }
    
    public static void registerRawColorItem(Item item, Integer num) {
    	RAW_COLOR_ITEMS.put(item, num);
    }

	public static void registerCandleFlameItem(Item item, Integer num) {
    	FLAME_ITEMS.put(item, num);
    }

	public static void registerFlammablity(){
		try{
			Method setFlammable = ObfuscationReflectionHelper.findMethod(FireBlock.class, "m_53444_", Block.class, int.class, int.class);
			// Half-Timber Blocks...
			final FireBlock fire = (FireBlock)Blocks.FIRE;
			setFlammable.invoke(fire, ModBlocks.HALF_TIMBER_P0.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.HALF_TIMBER_P1.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.HALF_TIMBER_P2.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.HALF_TIMBER_P3.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.HALF_TIMBER_P4.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.HALF_TIMBER_P5.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.HALF_TIMBER_P6.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.HALF_TIMBER_P7.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.HALF_TIMBER_P8.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.HALF_TIMBER_P9.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.HALF_TIMBER_PILLAR_P0.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.HALF_TIMBER_PILLAR_P1.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.HALF_TIMBER_PILLAR_P2.get(), 5, 20);
			// Furniture...
			setFlammable.invoke(fire, ModBlocks.PUMPKIN_CHAIR.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.PUMPKIN_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.OAK_CHAIR.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.OAK_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.OAK_ROUND_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.JUNGLE_CHAIR.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.JUNGLE_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.JUNGLE_ROUND_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.ACACIA_CHAIR.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.ACACIA_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.ACACIA_ROUND_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.BIRCH_CHAIR.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.BIRCH_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.BIRCH_ROUND_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.DARK_OAK_CHAIR.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.DARK_OAK_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.DARK_OAK_ROUND_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.SPRUCE_CHAIR.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.SPRUCE_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.SPRUCE_ROUND_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.GLASS_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.GLASS_ROUND_TABLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.ARM_CHAIR.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.DRAWER.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.CABINET.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.DRAWER_CHEST.get(), 5, 20);
			// (Crimson / Warped not flammable)
			// Windows...
			setFlammable.invoke(fire, ModBlocks.WINDOW_SIMPLE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.WINDOW_ARCH.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.WINDOW_CROSS.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.WINDOW_LANCET.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.WINDOW_SHADE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.WINDOW_SILL.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.WINDOW_TOP.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.WINDOW_PLANT.get(), 5, 20);
			// Awning...
			setFlammable.invoke(fire, ModBlocks.AWNING_PURE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.AWNING_PURE_SHORT.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.AWNING_STRIPE.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.AWNING_STRIPE_SHORT.get(), 5, 20);
			// Easel Menu...
			setFlammable.invoke(fire, ModBlocks.EASEL_MENU.get(), 5, 20);
			setFlammable.invoke(fire, ModBlocks.EASEL_MENU_WHITE.get(), 5, 20);
			LOGGER.info("Flammability Registered!");
		} catch (Exception e){
			LOGGER.info("Failed to Register Flammability");
			e.printStackTrace();
		}
	}
}