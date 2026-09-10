package io.devbobcorn.nekoration.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.devbobcorn.nekoration.blocks.states.CandleFlameType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * Vanilla item mappings used by gameplay (dye-on-block, half-timber fill, etc.).
 */
public final class VanillaCompat {
    /** Dye items to color mapping. */
    public static final Map<Item, Integer> COLOR_ITEMS = createColorItemsMap();
    /** Item to candle flame type mapping (candle holders). */
    public static final Map<Item, CandleFlameType> FLAME_ITEMS = createFlameItemsMap();

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

    private static Map<Item, CandleFlameType> createFlameItemsMap() {
        Map<Item, CandleFlameType> map = new HashMap<>();
        map.put(Items.TORCH, CandleFlameType.FLAME);
        map.put(Items.FLINT_AND_STEEL, CandleFlameType.FLAME);
        map.put(Items.LANTERN, CandleFlameType.FLAME);
        map.put(Items.CAMPFIRE, CandleFlameType.FLAME);
        map.put(Items.SOUL_TORCH, CandleFlameType.SOUL_FLAME);
        map.put(Items.SOUL_LANTERN, CandleFlameType.SOUL_FLAME);
        map.put(Items.SOUL_CAMPFIRE, CandleFlameType.SOUL_FLAME);
        map.put(Items.NETHER_STAR, CandleFlameType.FIREWORK);
        map.put(Items.BEACON, CandleFlameType.FIREWORK);
        map.put(Items.END_CRYSTAL, CandleFlameType.FIREWORK);
        return Collections.unmodifiableMap(map);
    }
}
