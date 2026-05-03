package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import io.devbobcorn.nekoration.blocks.NekoStone;
import io.devbobcorn.nekoration.blocks.VerticalConnectedBlock;
import io.devbobcorn.nekoration.items.NekoBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
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
            if (stone.needsSmoothVariant()) {
                registerStoneBlockSet(blocks, items, "smooth_" + stoneId, blockItemsByStone, stone);
            } else {
                // Add vanilla smooth variant
                blockItemsByStone.add(() -> stone.vanillaSmoothStoneBlock().asItem());
            }
            registerStoneBlockSet(blocks, items, "polished_smooth_" + stoneId, blockItemsByStone, stone);
            registerVerticalConnectedBlock(blocks, items, "chiseled_" + stoneId, blockItemsByStone, stone);
            
        }
    }

    private static void registerStoneBlockSet(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
            List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        DeferredBlock<Block> fullBlock = registerBlock(blocks, items, id, blockItemsByStone, stone);
        registerStairBlock(blocks, items, id + "_stairs", fullBlock, blockItemsByStone, stone);
        registerSlabBlock(blocks, items, id + "_slab", blockItemsByStone, stone);
    }

    private static DeferredBlock<Block> registerBlock(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
            List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        DeferredBlock<Block> block = blocks.register(id, () -> new Block(stone.stoneProperties()));
        DeferredItem<Item> blockItem = registerBlockItem(items, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
        return block;
    }

    private static DeferredBlock<Block> registerVerticalConnectedBlock(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
            List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new VerticalConnectedBlock(stone.stoneProperties(), VerticalConnectedBlock.ConnectionType.PILLAR, false));
        DeferredItem<Item> blockItem = registerBlockItem(items, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
        return block;
    }

    private static void registerStairBlock(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
            DeferredBlock<Block> sourceBlock, List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new StairBlock(sourceBlock.get().defaultBlockState(), stone.stoneProperties()));
        DeferredItem<Item> blockItem = registerBlockItem(items, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
    }

    private static void registerSlabBlock(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
            List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        DeferredBlock<Block> block = blocks.register(id, () -> new SlabBlock(stone.stoneProperties()));
        DeferredItem<Item> blockItem = registerBlockItem(items, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
    }

    private static DeferredItem<Item> registerBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new NekoBlockItem(block.get(), props), new Item.Properties());
    }

    public static List<DeferredItem<Item>> blockItemsView() {
        return Collections.unmodifiableList(STONE_BLOCK_ITEMS);
    }

    /** Items for the creative stone tab when filtering by {@link io.devbobcorn.nekoration.blocks.NekoStone}. */
    public static List<Supplier<? extends Item>> itemSuppliersForStone(NekoStone stone) {
        return Collections.unmodifiableList(STONE_BLOCK_ITEMS_BY_STONE.getOrDefault(stone, List.of()));
    }

    /** Creative tab icon. */
    public static DeferredItem<Item> iconItem() {
        return STONE_BLOCK_ITEMS.get(0);
    }
}
