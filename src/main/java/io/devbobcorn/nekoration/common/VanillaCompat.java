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
    /** Dye items to color mapping. */
    public static final Map<Item, Integer> COLOR_ITEMS = createColorItemsMap();
    /** Raw material to color mapping. */
    public static final Map<Item, Integer> RAW_COLOR_ITEMS = createRawColorItemsMap();

    private VanillaCompat() {
    }

    private static Map<Item, Integer> createColorItemsMap() {
        Map<Item, Integer> map = new HashMap<>();
        map.put(Items.WHITE_DYE, 0);
        map.put(Items.LIGHT_GRAY_DYE, 1);
        map.put(Items.GRAY_DYE, 2);
        map.put(Items.BLACK_DYE, 3);
        map.put(Items.BROWN_DYE, 4);
        map.put(Items.RED_DYE, 5);
        map.put(Items.ORANGE_DYE, 6);
        map.put(Items.YELLOW_DYE, 7);
        map.put(Items.LIME_DYE, 8);
        map.put(Items.GREEN_DYE, 9);
        map.put(Items.CYAN_DYE, 10);
        map.put(Items.LIGHT_BLUE_DYE, 11);
        map.put(Items.BLUE_DYE, 12);
        map.put(Items.PURPLE_DYE, 13);
        map.put(Items.MAGENTA_DYE, 14);
        map.put(Items.PINK_DYE, 15);
        return Collections.unmodifiableMap(map);
    }

    private static Map<Item, Integer> createRawColorItemsMap() {
        Map<Item, Integer> map = new HashMap<>();
        // White
        map.put(Items.BONE_MEAL, 0);
        map.put(Items.LILY_OF_THE_VALLEY, 0);
        // Light Gray
        map.put(Items.AZURE_BLUET, 1);
        map.put(Items.OXEYE_DAISY, 1);
        map.put(Items.WHITE_TULIP, 1);
        // Gray
        map.put(Items.STONE, 2);
        map.put(Items.COBBLESTONE, 2);
        // Black
        map.put(Items.INK_SAC, 3);
        map.put(Items.WITHER_ROSE, 3);
        // Brown
        map.put(Items.COCOA_BEANS, 4);
        // Red
        map.put(Items.POPPY, 5);
        map.put(Items.ROSE_BUSH, 5);
        map.put(Items.RED_TULIP, 5);
        map.put(Items.BEETROOT, 5);
        // Orange
        map.put(Items.ORANGE_TULIP, 6);
        // Yellow
        map.put(Items.DANDELION, 7);
        map.put(Items.SUNFLOWER, 7);
        // Lime
        map.put(Items.SEA_PICKLE, 8);
        // Green
        map.put(Items.CACTUS, 9);
        // Cyan
        map.put(Items.CORNFLOWER, 10);
        // Light Blue
        map.put(Items.BLUE_ORCHID, 11);
        // Blue
        map.put(Items.LAPIS_LAZULI, 12);
        // Purple
        map.put(Items.ALLIUM, 13);
        // Magenta
        map.put(Items.LILAC, 14);
        // Pink
        map.put(Items.PEONY, 15);
        map.put(Items.PINK_TULIP, 15);
        return Collections.unmodifiableMap(map);
    }
}
