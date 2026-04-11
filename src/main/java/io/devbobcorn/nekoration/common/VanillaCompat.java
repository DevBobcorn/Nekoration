package io.devbobcorn.nekoration.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * Vanilla item mappings used by gameplay (dye-on-block, half-timber fill, etc.).
 */
public final class VanillaCompat {
    /** Dye items → 0–15 (classic half-timber frame tint when used on {@link io.devbobcorn.nekoration.blocks.DyeableBlock}). */
    public static final Map<Item, Integer> COLOR_ITEMS = createColorItemsMap();
    /** Plants and misc. → 0–15 (half-timber plaster / fill, matching 1.16.5 {@code RAW_COLOR_ITEMS}). */
    public static final Map<Item, Integer> RAW_COLOR_ITEMS = createRawColorItemsMap();

    private VanillaCompat() {
    }

    private static Map<Item, Integer> createColorItemsMap() {
        Map<Item, Integer> map = new HashMap<>();
        map.put(Items.BLACK_DYE, 0);
        map.put(Items.BLUE_DYE, 1);
        map.put(Items.BROWN_DYE, 2);
        map.put(Items.CYAN_DYE, 3);
        map.put(Items.GRAY_DYE, 4);
        map.put(Items.GREEN_DYE, 5);
        map.put(Items.LIGHT_BLUE_DYE, 6);
        map.put(Items.LIGHT_GRAY_DYE, 7);
        map.put(Items.LIME_DYE, 8);
        map.put(Items.MAGENTA_DYE, 9);
        map.put(Items.ORANGE_DYE, 10);
        map.put(Items.PINK_DYE, 11);
        map.put(Items.PURPLE_DYE, 12);
        map.put(Items.RED_DYE, 13);
        map.put(Items.WHITE_DYE, 14);
        map.put(Items.YELLOW_DYE, 15);
        return Collections.unmodifiableMap(map);
    }

    private static Map<Item, Integer> createRawColorItemsMap() {
        Map<Item, Integer> map = new HashMap<>();
        map.put(Items.INK_SAC, 0);
        map.put(Items.WITHER_ROSE, 0);
        map.put(Items.LAPIS_LAZULI, 1);
        map.put(Items.COCOA_BEANS, 2);
        map.put(Items.CORNFLOWER, 3);
        map.put(Items.STONE, 4);
        map.put(Items.COBBLESTONE, 4);
        map.put(Items.CACTUS, 5);
        map.put(Items.BLUE_ORCHID, 6);
        map.put(Items.AZURE_BLUET, 7);
        map.put(Items.OXEYE_DAISY, 7);
        map.put(Items.WHITE_TULIP, 7);
        map.put(Items.SEA_PICKLE, 8);
        map.put(Items.LILAC, 9);
        map.put(Items.ORANGE_TULIP, 10);
        map.put(Items.PEONY, 11);
        map.put(Items.PINK_TULIP, 11);
        map.put(Items.ALLIUM, 12);
        map.put(Items.POPPY, 13);
        map.put(Items.ROSE_BUSH, 13);
        map.put(Items.RED_TULIP, 13);
        map.put(Items.BEETROOT, 13);
        map.put(Items.BONE_MEAL, 14);
        map.put(Items.LILY_OF_THE_VALLEY, 14);
        map.put(Items.DANDELION, 15);
        map.put(Items.SUNFLOWER, 15);
        return Collections.unmodifiableMap(map);
    }
}
