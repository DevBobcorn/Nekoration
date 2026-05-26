package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import io.devbobcorn.nekoration.blocks.HorizontalConnectedBlock;
import io.devbobcorn.nekoration.blocks.NekoStone;
import io.devbobcorn.nekoration.blocks.VerticalConnectedBlock;
import io.devbobcorn.nekoration.blocks.stone.BaseBlock;
import io.devbobcorn.nekoration.blocks.stone.ColumnBlock;
import io.devbobcorn.nekoration.blocks.stone.DirectionalColumnBlock;
import io.devbobcorn.nekoration.blocks.stone.FrameSideBlock;
import io.devbobcorn.nekoration.blocks.stone.PotBlock;
import io.devbobcorn.nekoration.items.NekoBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
    
    private static final String TAB_ICON_ITEM_ID = "chiseled_smooth_granite";
    private static DeferredItem<Item> tabIconItem;

    private StoneBlockRegistration() {
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        for (NekoStone stone : NekoStone.values()) {
            String stoneId = stone.id();
            List<Supplier<? extends Item>> blockItemsByStone = STONE_BLOCK_ITEMS_BY_STONE.computeIfAbsent(stone,
                ignored -> new ArrayList<>());
            if (stone.needsPolishedVariant()) {
                registerStoneBlockSet(blocks, items, "polished_" + stoneId, blockItemsByStone, stone);
            } else {
                for (Block polishedBlock : stone.vanillaPolishedStoneBlockSet()) {
                    blockItemsByStone.add(() -> polishedBlock.asItem());
                }
            }
            if (stone.needsBricksVariant()) {
                registerStoneBlockSet(blocks, items, stoneId + "_bricks", blockItemsByStone, stone);
            } else {
                for (Block bricksBlock : stone.vanillaBricksStoneBlockSet()) {
                    blockItemsByStone.add(() -> bricksBlock.asItem());
                }
            }
            registerStoneBlockSet(blocks, items, stoneId + "_tiles", blockItemsByStone, stone);
            if (stone.needsSmoothVariant()) {
                registerStoneBlockSet(blocks, items, "smooth_" + stoneId, blockItemsByStone, stone);
            } else {
                for (Block smoothBlock : stone.vanillaSmoothStoneBlockSet()) {
                    blockItemsByStone.add(() -> smoothBlock.asItem());
                }
                if (stone == NekoStone.STONE) {
                    registerStairBlock(blocks, items, "smooth_stone_stairs", Blocks.SMOOTH_STONE, blockItemsByStone, stone);
                    blockItemsByStone.add(() -> Blocks.SMOOTH_STONE_SLAB.asItem());
                }
            }
            registerStoneBlockSet(blocks, items, "polished_smooth_" + stoneId, blockItemsByStone, stone);
            registerVerticalConnectedBlock(blocks, items, "chiseled_smooth_" + stoneId,
                    VerticalConnectedBlock.ConnectionType.PILLAR, blockItemsByStone, stone);
            registerHorizontalConnectedBlock(blocks, items, "horizontal_chiseled_smooth_" + stoneId,
                    HorizontalConnectedBlock.ConnectionType.BEAM, 16, 16, 0, blockItemsByStone, stone);
            registerBaseBlock(blocks, items, stoneId + "_base", blockItemsByStone, stone);
            registerColumnBlock(blocks, items, stoneId + "_column_doric", false, 3, blockItemsByStone, stone);
            registerColumnBlock(blocks, items, stoneId + "_column_ionic", true, 7, blockItemsByStone, stone);
            registerColumnBlock(blocks, items, stoneId + "_column_corinthian", false, 7, blockItemsByStone, stone);
            registerPotBlock(blocks, items, stoneId + "_pot", 7, blockItemsByStone, stone);
            registerPotBlock(blocks, items, stoneId + "_planter", 8, blockItemsByStone, stone);
            registerHorizontalConnectedBlock(blocks, items, stoneId + "_frame_head",
                    HorizontalConnectedBlock.ConnectionType.BEAM, 2, 3, 0, blockItemsByStone, stone);
            registerHorizontalConnectedBlock(blocks, items, stoneId + "_frame_peak",
                    HorizontalConnectedBlock.ConnectionType.TRIPLE, 5, 12, 0, blockItemsByStone, stone);
            registerHorizontalConnectedBlock(blocks, items, stoneId + "_frame_sill",
                    HorizontalConnectedBlock.ConnectionType.BEAM, 4, 4, 12, blockItemsByStone, stone);
            registerFrameSideBlock(blocks, items, stoneId + "_frame_side", blockItemsByStone, stone);
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

    private static DeferredBlock<Block> registerPotBlock(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
        int radius, List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        DeferredBlock<Block> block = blocks.register(id, () -> new PotBlock(stone.stoneProperties(), radius));
        DeferredItem<Item> blockItem = registerBlockItem(items, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
        return block;
    }

    private static DeferredBlock<Block> registerVerticalConnectedBlock(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
            VerticalConnectedBlock.ConnectionType connectionType, List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new VerticalConnectedBlock(stone.stoneProperties(), connectionType, false));
        DeferredItem<Item> blockItem = registerBlockItem(items, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
        if (TAB_ICON_ITEM_ID.equals(id)) {
            tabIconItem = blockItem;
        }
        return block;
    }

    private static DeferredBlock<Block> registerHorizontalConnectedBlock(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
            HorizontalConnectedBlock.ConnectionType connectionType, int thickness, int height, int bottom, List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new HorizontalConnectedBlock(stone.stoneProperties(), connectionType, false, thickness, height, bottom));
        DeferredItem<Item> blockItem = registerBlockItem(items, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
        return block;
    }

    private static DeferredBlock<Block> registerFrameSideBlock(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
            List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new FrameSideBlock(stone.stoneProperties()));
        DeferredItem<Item> blockItem = registerBlockItem(items, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
        return block;
    }

    private static DeferredBlock<Block> registerBaseBlock(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
            List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new BaseBlock(stone.stoneProperties()));
        DeferredItem<Item> blockItem = registerBlockItem(items, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
        return block;
    }

    private static DeferredBlock<Block> registerColumnBlock(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
            boolean hasHorizontalAxis, int topPartHeight, List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> hasHorizontalAxis ? new DirectionalColumnBlock(stone.stoneProperties(), topPartHeight) : new ColumnBlock(stone.stoneProperties(), topPartHeight));
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

    private static void registerStairBlock(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
            Block sourceBlock, List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new StairBlock(sourceBlock.defaultBlockState(), stone.stoneProperties()));
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

    /** Creative tab icon ({@value #TAB_ICON_ITEM_ID}). */
    public static DeferredItem<Item> iconItem() {
        return tabIconItem;
    }
}
