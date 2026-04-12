package io.devbobcorn.nekoration;

import java.util.Comparator;

import io.devbobcorn.nekoration.blocks.HalfTimberWood;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

/**
 * Sort order for half-timber creative stacks: wood type, then plaster color (yellow → black), then block variant (item id).
 */
public final class HalfTimberCreativeTabOrdering {
    private HalfTimberCreativeTabOrdering() {
    }

    public static Comparator<ItemStack> stackComparator() {
        return Comparator
                .comparingInt(HalfTimberCreativeTabOrdering::woodOrdinal)
                .thenComparingInt(s -> DyeableBlockItem.getColor(s).ordinal())
                .thenComparingInt(s -> BuiltInRegistries.ITEM.getId(s.getItem()));
    }

    private static int woodOrdinal(ItemStack stack) {
        var key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (key == null) {
            return Integer.MAX_VALUE;
        }
        HalfTimberWood w = HalfTimberItemPaths.parseWood(key.getPath());
        return w != null ? w.ordinal() : Integer.MAX_VALUE;
    }
}
