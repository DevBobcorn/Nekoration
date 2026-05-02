package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.DyeableVerticalConnectBlock;
import io.devbobcorn.nekoration.blocks.NekoWood;
import io.devbobcorn.nekoration.blocks.VerticalConnectBlock;
import io.devbobcorn.nekoration.blocks.WindowBlock;
import io.devbobcorn.nekoration.blocks.entities.CabinetBlockEntity;
import io.devbobcorn.nekoration.blocks.containers.CabinetBlock;
import io.devbobcorn.nekoration.blocks.containers.CupboardBlock;
import io.devbobcorn.nekoration.blocks.containers.EaselMenuBlock;
import io.devbobcorn.nekoration.blocks.containers.WallShelfBlock;
import io.devbobcorn.nekoration.blocks.furniture.ChairBlock;
import io.devbobcorn.nekoration.blocks.furniture.TableBlock;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.items.NekoBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers wooden blocks for each {@link NekoWood}.
 */
public final class WoodenBlockRegistration {
    public enum WindowVariant {
        SIMPLE("simple"),
        ARCH("arch"),
        CROSS("cross"),
        SHADE("shade"),
        LANCET("lancet");

        private final String id;

        WindowVariant(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }
    }

    public enum ContainerVariant {
        CABINET("cabinet"),
        CUPBOARD("cupboard"),
        DRAWER("drawer"),
        DRAWER_CHEST("drawer_chest"),
        EASEL_MENU("easel_menu"),
        WALL_SHELF("wall_shelf");

        private final String id;

        ContainerVariant(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }
    }

    public static final List<DeferredItem<DyeableBlockItem>> HALF_TIMBER_BLOCK_ITEMS = new ArrayList<>();
    public static final List<DeferredItem<BlockItem>> WINDOW_BLOCK_ITEMS = new ArrayList<>();
    public static final List<DeferredItem<? extends BlockItem>> FURNITURE_BLOCK_ITEMS = new ArrayList<>();
    public static final List<DeferredItem<DyeableBlockItem>> EASEL_MENU_BLOCK_ITEMS = new ArrayList<>();
    /** All {@link CabinetBlock} instances that use {@link CabinetBlockEntity}. */
    public static final List<DeferredBlock<Block>> CABINET_BLOCKS_FOR_ENTITY = new ArrayList<>();
    /** Cupboards and wall shelves using {@link io.devbobcorn.nekoration.blocks.entities.ItemDisplayBlockEntity}. */
    public static final List<DeferredBlock<Block>> ITEM_DISPLAY_BLOCKS_FOR_ENTITY = new ArrayList<>();
    /** Easel menu blocks using {@link io.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity}. */
    public static final List<DeferredBlock<Block>> EASEL_MENU_BLOCKS_FOR_ENTITY = new ArrayList<>();
    public static final Map<NekoWood, List<DeferredItem<DyeableBlockItem>>> DYED_BLOCK_ITEMS_BY_WOOD = new EnumMap<>(NekoWood.class);
    public static final Map<NekoWood, List<DeferredItem<? extends BlockItem>>> PLAIN_BLOCK_ITEMS_BY_WOOD = new EnumMap<>(NekoWood.class);

    private WoodenBlockRegistration() {
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        for (NekoWood wood : NekoWood.values()) {
            String woodId = wood.id();
            List<DeferredItem<DyeableBlockItem>> dyedByWood = DYED_BLOCK_ITEMS_BY_WOOD.computeIfAbsent(wood, ignored -> new ArrayList<>());
            List<DeferredItem<? extends BlockItem>> plainByWood = PLAIN_BLOCK_ITEMS_BY_WOOD.computeIfAbsent(wood,
                    ignored -> new ArrayList<>());

            for (int p = 0; p <= 9; p++) {
                int patternIndex = p;
                String id = woodId + "_half_timber_p" + p;
                DeferredBlock<Block> block = blocks.register(id, () -> createHalfTimberBlock(wood, patternIndex));
                DeferredItem<DyeableBlockItem> blockItem = registerDyeableBlockItem(items, id, block);
                HALF_TIMBER_BLOCK_ITEMS.add(blockItem);
                dyedByWood.add(blockItem);
            }

            for (WindowVariant variant : WindowVariant.values()) {
                String id = woodId + "_window_" + variant.id();
                DeferredBlock<Block> block = blocks.register(id,
                        () -> new WindowBlock(wood.plankProperties().noOcclusion()));
                DeferredItem<BlockItem> registered = registerBlockItem(items, id, block);
                WINDOW_BLOCK_ITEMS.add(registered);
                plainByWood.add(registered);
            }

            String tableId = woodId + "_table";
            DeferredBlock<Block> table = blocks.register(tableId,
                    () -> new TableBlock(wood.plankProperties().noOcclusion()));
            DeferredItem<BlockItem> tableItem = registerBlockItem(items, tableId, table);
            FURNITURE_BLOCK_ITEMS.add(tableItem);
            plainByWood.add(tableItem);

            String chairId = woodId + "_chair";
            DeferredBlock<Block> chair = blocks.register(chairId,
                    () -> new ChairBlock(wood.plankProperties().noOcclusion()));
            DeferredItem<BlockItem> chairItem = registerBlockItem(items, chairId, chair);
            FURNITURE_BLOCK_ITEMS.add(chairItem);
            plainByWood.add(chairItem);

            String cupboardId = woodId + "_cupboard";
            DeferredBlock<Block> cupboard = blocks.register(cupboardId,
                    () -> new CupboardBlock(wood.plankProperties().noOcclusion()));
            ITEM_DISPLAY_BLOCKS_FOR_ENTITY.add(cupboard);
            DeferredItem<BlockItem> cupboardItem = registerBlockItem(items, cupboardId, cupboard);
            FURNITURE_BLOCK_ITEMS.add(cupboardItem);
            plainByWood.add(cupboardItem);

            String wallShelfId = woodId + "_wall_shelf";
            DeferredBlock<Block> wallShelf = blocks.register(wallShelfId,
                    () -> new WallShelfBlock(wood.plankProperties().noOcclusion()));
            ITEM_DISPLAY_BLOCKS_FOR_ENTITY.add(wallShelf);
            DeferredItem<BlockItem> wallShelfItem = registerBlockItem(items, wallShelfId, wallShelf);
            FURNITURE_BLOCK_ITEMS.add(wallShelfItem);
            plainByWood.add(wallShelfItem);

            String cabinetId = woodId + "_cabinet";
            DeferredBlock<Block> cabinet = blocks.register(cabinetId,
                    () -> new CabinetBlock(wood.plankProperties(), true));
            CABINET_BLOCKS_FOR_ENTITY.add(cabinet);
            DeferredItem<BlockItem> cabinetItem = registerBlockItem(items, cabinetId, cabinet);
            FURNITURE_BLOCK_ITEMS.add(cabinetItem);
            plainByWood.add(cabinetItem);

            String drawerId = woodId + "_drawer";
            DeferredBlock<Block> drawer = blocks.register(drawerId,
                    () -> new CabinetBlock(wood.plankProperties(), false));
            CABINET_BLOCKS_FOR_ENTITY.add(drawer);
            DeferredItem<BlockItem> drawerItem = registerBlockItem(items, drawerId, drawer);
            FURNITURE_BLOCK_ITEMS.add(drawerItem);
            plainByWood.add(drawerItem);

            String drawerChestId = woodId + "_drawer_chest";
            DeferredBlock<Block> drawerChest = blocks.register(drawerChestId,
                    () -> new CabinetBlock(wood.plankProperties(), true));
            CABINET_BLOCKS_FOR_ENTITY.add(drawerChest);
            DeferredItem<BlockItem> drawerChestItem = registerBlockItem(items, drawerChestId, drawerChest);
            FURNITURE_BLOCK_ITEMS.add(drawerChestItem);
            plainByWood.add(drawerChestItem);

            String easelMenuId = woodId + "_easel_menu";
            DeferredBlock<Block> easelMenu = blocks.register(easelMenuId,
                    () -> new EaselMenuBlock(wood.plankProperties().noOcclusion()));
            EASEL_MENU_BLOCKS_FOR_ENTITY.add(easelMenu);
            DeferredItem<DyeableBlockItem> easelMenuItem = registerDyeableBlockItem(items, easelMenuId, easelMenu);
            EASEL_MENU_BLOCK_ITEMS.add(easelMenuItem);
            FURNITURE_BLOCK_ITEMS.add(easelMenuItem);
            plainByWood.add(easelMenuItem);
        }

    }

    private static Block createHalfTimberBlock(NekoWood wood, int patternIndex) {
        if (patternIndex == 0) {
            return new DyeableVerticalConnectBlock(wood.plankProperties(), VerticalConnectBlock.ConnectionType.PILLAR, false);
        }
        if (patternIndex <= 2) {
            return new DyeableVerticalConnectBlock(wood.plankProperties(), VerticalConnectBlock.ConnectionType.TRIPLE, false);
        }
        return new DyeableBlock(wood.plankProperties());
    }

    private static DeferredItem<DyeableBlockItem> registerDyeableBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new DyeableBlockItem(block.get(), props), new Item.Properties());
    }

    private static DeferredItem<BlockItem> registerBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new NekoBlockItem(block.get(), props), new Item.Properties());
    }

    public static List<DeferredItem<DyeableBlockItem>> halfTimberBlockItemsView() {
        return Collections.unmodifiableList(HALF_TIMBER_BLOCK_ITEMS);
    }

    public static List<DeferredItem<BlockItem>> windowBlockItemsView() {
        return Collections.unmodifiableList(WINDOW_BLOCK_ITEMS);
    }

    public static List<DeferredItem<? extends BlockItem>> furnitureBlockItemsView() {
        return Collections.unmodifiableList(FURNITURE_BLOCK_ITEMS);
    }

    public static Block[] cabinetBlocksForEntity() {
        return CABINET_BLOCKS_FOR_ENTITY.stream().map(DeferredBlock::get).toArray(Block[]::new);
    }

    public static Block[] itemDisplayBlocksForEntity() {
        return ITEM_DISPLAY_BLOCKS_FOR_ENTITY.stream().map(DeferredBlock::get).toArray(Block[]::new);
    }

    public static Block[] easelMenuBlocksForEntity() {
        return EASEL_MENU_BLOCKS_FOR_ENTITY.stream().map(DeferredBlock::get).toArray(Block[]::new);
    }

    public static List<DeferredItem<DyeableBlockItem>> dyedItemsForWood(NekoWood wood) {
        return Collections.unmodifiableList(DYED_BLOCK_ITEMS_BY_WOOD.getOrDefault(wood, List.of()));
    }

    public static List<DeferredItem<? extends BlockItem>> plainItemsForWood(NekoWood wood) {
        return Collections.unmodifiableList(PLAIN_BLOCK_ITEMS_BY_WOOD.getOrDefault(wood, List.of()));
    }

    public static List<DeferredItem<DyeableBlockItem>> easelMenuBlockItemsView() {
        return Collections.unmodifiableList(EASEL_MENU_BLOCK_ITEMS);
    }

    /** Creative tab icon. */
    public static DeferredItem<DyeableBlockItem> iconItem() {
        return HALF_TIMBER_BLOCK_ITEMS.get(1);
    }
}
