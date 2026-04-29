package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import io.devbobcorn.nekoration.blocks.NekoStone;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers concrete blocks.
 */
public final class StoneBlockRegistration {
    public static final List<DeferredItem<Item>> STONE_BLOCK_ITEMS = new ArrayList<>();
    public static final Map<NekoStone, List<Supplier<? extends Item>>> STONE_BLOCK_ITEMS_BY_STONE = new EnumMap<>(NekoStone.class);

    private StoneBlockRegistration() {
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        for (NekoStone stone : NekoStone.values()) {
            String stoneId = stone.id();
            List<Supplier<? extends Item>> blockItemsByStone = STONE_BLOCK_ITEMS_BY_STONE.computeIfAbsent(stone,
                ignored -> new ArrayList<>());
            
            String smoothId = "smooth_" + stoneId;

            // Optional smooth stone variants
            if (stone.needsSmoothVariant()) {
                DeferredBlock<Block> block = blocks.register(smoothId, () -> new Block(stone.stoneProperties()));
                DeferredItem<Item> blockItem = registerBlockItem(items, smoothId, block);
                STONE_BLOCK_ITEMS.add(blockItem);
                blockItemsByStone.add(blockItem);
            } else {
                blockItemsByStone.add(() -> stone.vanillaSmoothStoneBlock().asItem());
            }

        }
    }

    private static DeferredItem<Item> registerBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new BlockItem(block.get(), props), new Item.Properties());
    }

    public static List<DeferredItem<Item>> blockItemsView() {
        return Collections.unmodifiableList(STONE_BLOCK_ITEMS);
    }

    /** Creative tab icon. */
    public static DeferredItem<Item> iconItem() {
        return STONE_BLOCK_ITEMS.get(0);
    }
}
