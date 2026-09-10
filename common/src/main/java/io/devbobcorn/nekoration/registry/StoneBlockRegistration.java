package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import io.devbobcorn.nekoration.blocks.HorizontalConnectedBlock;
import io.devbobcorn.nekoration.blocks.NekoStone;
import io.devbobcorn.nekoration.blocks.VerticalConnectedBlock;
import io.devbobcorn.nekoration.blocks.stone.ColumnBlock;
import io.devbobcorn.nekoration.blocks.stone.DirectionalColumnBlock;
import io.devbobcorn.nekoration.blocks.stone.FrameSideBlock;
import io.devbobcorn.nekoration.blocks.stone.PedestalBlock;
import io.devbobcorn.nekoration.blocks.stone.PotBlock;
import io.devbobcorn.nekoration.blocks.stone.SelfDroppingBlock;
import io.devbobcorn.nekoration.blocks.stone.SelfDroppingSlabBlock;
import io.devbobcorn.nekoration.blocks.stone.SelfDroppingStairBlock;
import io.devbobcorn.nekoration.blocks.stone.SelfDroppingWallBlock;
import io.devbobcorn.nekoration.items.NekoBlockItem;
import io.devbobcorn.nekoration.xplat.NekoRegistrar;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;



/**
 * Registers concrete blocks.
 */
public final class StoneBlockRegistration {
    public static final List<RegistrySupplier<Item>> STONE_BLOCK_ITEMS = new ArrayList<>();
    public static final Map<NekoStone, List<Supplier<? extends Item>>> STONE_BLOCK_ITEMS_BY_STONE = new EnumMap<>(NekoStone.class);
    private static final List<RegistrySupplier<Item>> POT_BLOCK_ITEMS = new ArrayList<>();
    /** All mod-registered stone blocks (mineable tags). */
    private static final List<RegistrySupplier<Block>> STONE_BLOCKS = new ArrayList<>();
    /** All mod-registered stone walls (walls tag). */
    private static final List<RegistrySupplier<Block>> STONE_WALL_BLOCKS = new ArrayList<>();
    
    private static final String TAB_ICON_ITEM_ID = "granite_tiles";
    private static RegistrySupplier<Item> tabIconItem;

    private StoneBlockRegistration() {
    }

    public static void register(NekoRegistrar registrar) {
        for (NekoStone stone : NekoStone.values()) {
            String stoneId = stone.id();
            List<Supplier<? extends Item>> blockItemsByStone = STONE_BLOCK_ITEMS_BY_STONE.computeIfAbsent(stone,
                ignored -> new ArrayList<>());
            registerWallBlock(registrar, stoneId + "_wall", stone.vanillaWallBlock(), blockItemsByStone, stone);
            if (stone.needsPolishedVariant()) {
                registerStoneBlockSet(registrar, "polished_" + stoneId, blockItemsByStone, stone);
            } else {
                for (Block polishedBlock : stone.vanillaPolishedStoneBlockSet()) {
                    blockItemsByStone.add(() -> polishedBlock.asItem());
                }
            }
            registerWallBlock(registrar, "polished_" + stoneId + "_wall", stone.vanillaPolishedWallBlock(), blockItemsByStone, stone);
            if (stone.needsBricksVariant()) {
                registerStoneBlockSet(registrar, stoneId + "_bricks", blockItemsByStone, stone);
            } else {
                for (Block bricksBlock : stone.vanillaBricksStoneBlockSet()) {
                    blockItemsByStone.add(() -> bricksBlock.asItem());
                }
            }
            registerWallBlock(registrar, stoneId + "_brick_wall", stone.vanillaBrickWallBlock(), blockItemsByStone, stone);
            registerStoneBlockSet(registrar, stoneId + "_tiles", blockItemsByStone, stone);
            registerWallBlock(registrar, stoneId + "_tile_wall", stone.vanillaTileWallBlock(), blockItemsByStone, stone);
            if (stone.needsSmoothVariant()) {
                registerStoneBlockSet(registrar, "smooth_" + stoneId, blockItemsByStone, stone);
            } else {
                for (Block smoothBlock : stone.vanillaSmoothStoneBlockSet()) {
                    blockItemsByStone.add(() -> smoothBlock.asItem());
                }
                if (stone == NekoStone.STONE) {
                    registerStairBlock(registrar, "smooth_stone_stairs", Blocks.SMOOTH_STONE, blockItemsByStone, stone);
                    blockItemsByStone.add(() -> Blocks.SMOOTH_STONE_SLAB.asItem());
                }
            }
            registerStoneBlockSet(registrar, "polished_smooth_" + stoneId, blockItemsByStone, stone);
            if (stone.needsChiseledVariant()) {
                registerBlock(registrar, "chiseled_" + stoneId, blockItemsByStone, stone);
            } else {
                blockItemsByStone.add(() -> stone.vanillaChiseledStoneBlock().asItem());
            }
            if (stone.needsChiseledBricksVariant()) {
                registerBlock(registrar, "chiseled_" + stoneId + "_bricks", blockItemsByStone, stone);
            } else {
                blockItemsByStone.add(() -> stone.vanillaChiseledBricksStoneBlock().asItem());
            }
            registerVerticalConnectedBlock(registrar, "chiseled_smooth_" + stoneId,
                    VerticalConnectedBlock.ConnectionType.PILLAR, blockItemsByStone, stone);
            registerHorizontalConnectedBlock(registrar, "horizontal_chiseled_smooth_" + stoneId,
                    HorizontalConnectedBlock.ConnectionType.BEAM, 16, 16, 0, blockItemsByStone, stone);
            registerPedestalBlock(registrar, stoneId + "_pedestal", blockItemsByStone, stone);
            registerColumnBlock(registrar, stoneId + "_column_doric", false, 3, blockItemsByStone, stone);
            registerColumnBlock(registrar, stoneId + "_column_ionic", true, 7, blockItemsByStone, stone);
            registerColumnBlock(registrar, stoneId + "_column_corinthian", false, 7, blockItemsByStone, stone);
            registerPotBlock(registrar, stoneId + "_pot", 6, blockItemsByStone, stone);
            registerPotBlock(registrar, stoneId + "_planter", 8, blockItemsByStone, stone);
            registerHorizontalConnectedBlock(registrar, stoneId + "_frame_head",
                    HorizontalConnectedBlock.ConnectionType.BEAM, 2, 3, 0, blockItemsByStone, stone);
            registerHorizontalConnectedBlock(registrar, stoneId + "_frame_peak",
                    HorizontalConnectedBlock.ConnectionType.TRIPLE, 5, 12, 0, blockItemsByStone, stone);
            registerHorizontalConnectedBlock(registrar, stoneId + "_frame_sill",
                    HorizontalConnectedBlock.ConnectionType.BEAM, 4, 4, 12, blockItemsByStone, stone);
            registerFrameSideBlock(registrar, stoneId + "_frame_side", blockItemsByStone, stone);
        }
    }

    private static void registerStoneBlockSet(NekoRegistrar registrar, String id,
            List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        RegistrySupplier<Block> fullBlock = registerBlock(registrar, id, blockItemsByStone, stone);
        String variantId = NekoStone.singularizedSetId(id);
        registerStairBlock(registrar, variantId + "_stairs", fullBlock, blockItemsByStone, stone);
        registerSlabBlock(registrar, variantId + "_slab", blockItemsByStone, stone);
    }

    /** Registers a wall block, or uses the vanilla wall when one is provided. */
    private static void registerWallBlock(NekoRegistrar registrar, String id,
            @Nullable Block vanillaWall, List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        if (vanillaWall != null) {
            blockItemsByStone.add(() -> vanillaWall.asItem());
            return;
        }
        RegistrySupplier<Block> block = registrar.block(id, () -> new SelfDroppingWallBlock(stone.wallStoneProperties()));
        trackStoneBlock(block);
        STONE_WALL_BLOCKS.add(block);
        RegistrySupplier<Item> blockItem = registerBlockItem(registrar, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
    }

    private static RegistrySupplier<Block> registerBlock(NekoRegistrar registrar, String id,
            List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        RegistrySupplier<Block> block = registrar.block(id, () -> new SelfDroppingBlock(stone.stoneProperties()));
        trackStoneBlock(block);
        RegistrySupplier<Item> blockItem = registerBlockItem(registrar, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
        if (TAB_ICON_ITEM_ID.equals(id)) {
            tabIconItem = blockItem;
        }
        return block;
    }

    private static RegistrySupplier<Block> registerPotBlock(NekoRegistrar registrar, String id,
        int radius, List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        RegistrySupplier<Block> block = registrar.block(id, () -> new PotBlock(stone.stoneProperties(), radius));
        trackStoneBlock(block);
        RegistrySupplier<Item> blockItem = registerBlockItem(registrar, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        POT_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
        return block;
    }

    private static RegistrySupplier<Block> registerVerticalConnectedBlock(NekoRegistrar registrar, String id,
            VerticalConnectedBlock.ConnectionType connectionType, List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        RegistrySupplier<Block> block = registrar.block(id,
                () -> new VerticalConnectedBlock(stone.stoneProperties(), connectionType, false));
        trackStoneBlock(block);
        RegistrySupplier<Item> blockItem = registerBlockItem(registrar, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
        return block;
    }

    private static RegistrySupplier<Block> registerHorizontalConnectedBlock(NekoRegistrar registrar, String id,
            HorizontalConnectedBlock.ConnectionType connectionType, int thickness, int height, int bottom, List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        RegistrySupplier<Block> block = registrar.block(id,
                () -> new HorizontalConnectedBlock(stone.stoneProperties(), connectionType, false, thickness, height, bottom));
        trackStoneBlock(block);
        RegistrySupplier<Item> blockItem = registerBlockItem(registrar, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
        return block;
    }

    private static RegistrySupplier<Block> registerFrameSideBlock(NekoRegistrar registrar, String id,
            List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        RegistrySupplier<Block> block = registrar.block(id,
                () -> new FrameSideBlock(stone.stoneProperties()));
        trackStoneBlock(block);
        RegistrySupplier<Item> blockItem = registerBlockItem(registrar, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
        return block;
    }

    private static RegistrySupplier<Block> registerPedestalBlock(NekoRegistrar registrar, String id,
            List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        RegistrySupplier<Block> block = registrar.block(id,
                () -> new PedestalBlock(stone.stoneProperties()));
        trackStoneBlock(block);
        RegistrySupplier<Item> blockItem = registerBlockItem(registrar, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
        return block;
    }

    private static RegistrySupplier<Block> registerColumnBlock(NekoRegistrar registrar, String id,
            boolean hasHorizontalAxis, int topPartHeight, List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        RegistrySupplier<Block> block = registrar.block(id,
                () -> hasHorizontalAxis ? new DirectionalColumnBlock(stone.stoneProperties(), topPartHeight) : new ColumnBlock(stone.stoneProperties(), topPartHeight));
        trackStoneBlock(block);
        RegistrySupplier<Item> blockItem = registerBlockItem(registrar, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
        return block;
    }

    private static void registerStairBlock(NekoRegistrar registrar, String id,
            RegistrySupplier<Block> sourceBlock, List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        RegistrySupplier<Block> block = registrar.block(id,
                () -> new SelfDroppingStairBlock(sourceBlock.get().defaultBlockState(), stone.stoneProperties()));
        trackStoneBlock(block);
        RegistrySupplier<Item> blockItem = registerBlockItem(registrar, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
    }

    private static void registerStairBlock(NekoRegistrar registrar, String id,
            Block sourceBlock, List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        RegistrySupplier<Block> block = registrar.block(id,
                () -> new SelfDroppingStairBlock(sourceBlock.defaultBlockState(), stone.stoneProperties()));
        trackStoneBlock(block);
        RegistrySupplier<Item> blockItem = registerBlockItem(registrar, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
    }

    private static void registerSlabBlock(NekoRegistrar registrar, String id,
            List<Supplier<? extends Item>> blockItemsByStone, NekoStone stone) {
        RegistrySupplier<Block> block = registrar.block(id, () -> new SelfDroppingSlabBlock(stone.stoneProperties()));
        trackStoneBlock(block);
        RegistrySupplier<Item> blockItem = registerBlockItem(registrar, id, block);
        STONE_BLOCK_ITEMS.add(blockItem);
        blockItemsByStone.add(blockItem);
    }

    private static void trackStoneBlock(RegistrySupplier<Block> block) {
        STONE_BLOCKS.add(block);
    }

    private static RegistrySupplier<Item> registerBlockItem(NekoRegistrar registrar, String id,
            RegistrySupplier<Block> block) {
        return registrar.item(id, () -> new NekoBlockItem(block.get(), new Item.Properties()));
    }

    public static List<RegistrySupplier<Item>> blockItemsView() {
        return Collections.unmodifiableList(STONE_BLOCK_ITEMS);
    }

    public static List<RegistrySupplier<Block>> stoneBlocksView() {
        return Collections.unmodifiableList(STONE_BLOCKS);
    }

    /** All mod-registered stone walls (vanilla-provided walls excluded). */
    public static List<RegistrySupplier<Block>> stoneWallBlocksView() {
        return Collections.unmodifiableList(STONE_WALL_BLOCKS);
    }

    /** Items for the creative stone tab when filtering by {@link io.devbobcorn.nekoration.blocks.NekoStone}. */
    public static List<Supplier<? extends Item>> itemSuppliersForStone(NekoStone stone) {
        return Collections.unmodifiableList(STONE_BLOCK_ITEMS_BY_STONE.getOrDefault(stone, List.of()));
    }

    /** Add pot and planter stacks for every stone (Pots and Planters category of the Ornaments tab). */
    public static void addPotsAndPlantersStacks(Consumer<ItemStack> out) {
        for (var holder : POT_BLOCK_ITEMS) {
            out.accept(new ItemStack(holder.get()));
        }
    }

    /** Creative tab icon ({@value #TAB_ICON_ITEM_ID}). */
    public static RegistrySupplier<Item> iconItem() {
        return tabIconItem;
    }
}
