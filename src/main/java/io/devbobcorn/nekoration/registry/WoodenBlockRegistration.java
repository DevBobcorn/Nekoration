package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.DyeableVerticalConnectedBlock;
import io.devbobcorn.nekoration.blocks.NekoWood;
import io.devbobcorn.nekoration.blocks.VerticalConnectedBlock;
import io.devbobcorn.nekoration.blocks.WindowBlock;
import io.devbobcorn.nekoration.blocks.WindowPaneBlock;
import io.devbobcorn.nekoration.blocks.entities.CabinetBlockEntity;
import io.devbobcorn.nekoration.blocks.containers.CabinetBlock;
import io.devbobcorn.nekoration.blocks.containers.CupboardBlock;
import io.devbobcorn.nekoration.blocks.containers.EaselMenuBlock;
import io.devbobcorn.nekoration.blocks.containers.WallShelfBlock;
import io.devbobcorn.nekoration.blocks.furniture.ChairBlock;
import io.devbobcorn.nekoration.blocks.furniture.ArmchairBlock;
import io.devbobcorn.nekoration.blocks.furniture.BenchBlock;
import io.devbobcorn.nekoration.blocks.furniture.RoundTableBlock;
import io.devbobcorn.nekoration.blocks.furniture.TableBlock;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.items.NekoBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
    public static final List<DeferredItem<? extends BlockItem>> CONTAINER_BLOCK_ITEMS = new ArrayList<>();
    public static final List<DeferredItem<DyeableBlockItem>> EASEL_MENU_BLOCK_ITEMS = new ArrayList<>();
    /** All {@link CabinetBlock} instances that use {@link CabinetBlockEntity}. */
    public static final List<DeferredBlock<Block>> CABINET_BLOCKS_FOR_ENTITY = new ArrayList<>();
    /** Cupboards and wall shelves using {@link io.devbobcorn.nekoration.blocks.entities.ItemDisplayBlockEntity}. */
    public static final List<DeferredBlock<Block>> ITEM_DISPLAY_BLOCKS_FOR_ENTITY = new ArrayList<>();
    /** Easel menu blocks using {@link io.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity}. */
    public static final List<DeferredBlock<Block>> EASEL_MENU_BLOCKS_FOR_ENTITY = new ArrayList<>();
    public static final Map<NekoWood, List<DeferredItem<DyeableBlockItem>>> DYED_BLOCK_ITEMS_BY_WOOD = new EnumMap<>(NekoWood.class);
    public static final Map<NekoWood, List<DeferredItem<BlockItem>>> WINDOW_BLOCK_ITEMS_BY_WOOD = new EnumMap<>(NekoWood.class);
    public static final Map<NekoWood, List<DeferredItem<? extends BlockItem>>> FURNITURE_BLOCK_ITEMS_BY_WOOD = new EnumMap<>(NekoWood.class);
    public static final Map<NekoWood, List<DeferredItem<? extends BlockItem>>> CONTAINER_BLOCK_ITEMS_BY_WOOD = new EnumMap<>(NekoWood.class);
    public static final Map<NekoWood, List<DeferredItem<DyeableBlockItem>>> EASEL_MENU_BLOCK_ITEMS_BY_WOOD = new EnumMap<>(NekoWood.class);

    private static final String TAB_ICON_ITEM_ID = "oak_half_timber_p1";
    private static DeferredItem<DyeableBlockItem> tabIconItem;
    private static final String ORNAMENTS_TAB_ICON_ITEM_ID = "spruce_easel_menu";
    private static DeferredItem<DyeableBlockItem> ornamentsTabIconItem;
    private static final String EASEL_MENU_CATEGORY_ICON_ITEM_ID = "oak_easel_menu";
    private static DeferredItem<DyeableBlockItem> easelMenuCategoryIconItem;
    private static final String FURNITURE_CATEGORY_ICON_ITEM_ID = "spruce_table";
    private static DeferredItem<BlockItem> furnitureCategoryIconItem;
    private static final String CONTAINER_CATEGORY_ICON_ITEM_ID = "acacia_drawer_chest";
    private static DeferredItem<BlockItem> containerCategoryIconItem;

    private WoodenBlockRegistration() {
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        for (NekoWood wood : NekoWood.values()) {
            String woodId = wood.id();
            List<DeferredItem<DyeableBlockItem>> dyedByWood = DYED_BLOCK_ITEMS_BY_WOOD.computeIfAbsent(wood, ignored -> new ArrayList<>());
            List<DeferredItem<BlockItem>> windowsByWood = WINDOW_BLOCK_ITEMS_BY_WOOD.computeIfAbsent(wood,
                    ignored -> new ArrayList<>());
            List<DeferredItem<? extends BlockItem>> furnitureByWood = FURNITURE_BLOCK_ITEMS_BY_WOOD.computeIfAbsent(wood,
                    ignored -> new ArrayList<>());
            List<DeferredItem<? extends BlockItem>> containersByWood = CONTAINER_BLOCK_ITEMS_BY_WOOD.computeIfAbsent(wood,
                    ignored -> new ArrayList<>());
            List<DeferredItem<DyeableBlockItem>> easelMenusByWood = EASEL_MENU_BLOCK_ITEMS_BY_WOOD.computeIfAbsent(wood,
                    ignored -> new ArrayList<>());

            for (int p = 0; p <= 9; p++) {
                int patternIndex = p;
                String id = woodId + "_half_timber_p" + p;
                DeferredBlock<Block> block = blocks.register(id, () -> createHalfTimberBlock(wood, patternIndex));
                DeferredItem<DyeableBlockItem> blockItem = registerDyeableBlockItem(items, id, block);
                HALF_TIMBER_BLOCK_ITEMS.add(blockItem);
                dyedByWood.add(blockItem);
                if (TAB_ICON_ITEM_ID.equals(id)) {
                    tabIconItem = blockItem;
                }
            }

            for (WindowVariant variant : WindowVariant.values()) {
                String id = woodId + "_window_" + variant.id();
                DeferredBlock<Block> block = blocks.register(id,
                        () -> new WindowBlock(wood.plankProperties().noOcclusion()));
                DeferredItem<BlockItem> registered = registerBlockItem(items, id, block);
                WINDOW_BLOCK_ITEMS.add(registered);
                windowsByWood.add(registered);

                String paneId = woodId + "_window_pane_" + variant.id();
                DeferredBlock<Block> pane = blocks.register(paneId,
                        () -> new WindowPaneBlock(wood.plankProperties().noOcclusion()));
                DeferredItem<BlockItem> paneItem = registerBlockItem(items, paneId, pane);
                WINDOW_BLOCK_ITEMS.add(paneItem);
                windowsByWood.add(paneItem);
            }

            String tableId = woodId + "_table";
            DeferredBlock<Block> table = blocks.register(tableId,
                    () -> new TableBlock(wood.plankProperties().noOcclusion()));
            DeferredItem<BlockItem> tableItem = registerBlockItem(items, tableId, table);
            FURNITURE_BLOCK_ITEMS.add(tableItem);
            furnitureByWood.add(tableItem);
            if (FURNITURE_CATEGORY_ICON_ITEM_ID.equals(tableId)) {
                furnitureCategoryIconItem = tableItem;
            }

            String roundTableId = woodId + "_round_table";
            DeferredBlock<Block> roundTable = blocks.register(roundTableId,
                    () -> new RoundTableBlock(wood.plankProperties().noOcclusion()));
            DeferredItem<BlockItem> roundTableItem = registerBlockItem(items, roundTableId, roundTable);
            FURNITURE_BLOCK_ITEMS.add(roundTableItem);
            furnitureByWood.add(roundTableItem);

            String glassTableId = woodId + "_glass_table";
            DeferredBlock<Block> glassTable = blocks.register(glassTableId,
                    () -> new TableBlock(wood.plankProperties().noOcclusion()));
            DeferredItem<BlockItem> glassTableItem = registerBlockItem(items, glassTableId, glassTable);
            FURNITURE_BLOCK_ITEMS.add(glassTableItem);
            furnitureByWood.add(glassTableItem);

            String roundGlassTableId = woodId + "_round_glass_table";
            DeferredBlock<Block> roundGlassTable = blocks.register(roundGlassTableId,
                    () -> new RoundTableBlock(wood.plankProperties().noOcclusion()));
            DeferredItem<BlockItem> roundGlassTableItem = registerBlockItem(items, roundGlassTableId, roundGlassTable);
            FURNITURE_BLOCK_ITEMS.add(roundGlassTableItem);
            furnitureByWood.add(roundGlassTableItem);

            String chairId = woodId + "_chair";
            DeferredBlock<Block> chair = blocks.register(chairId,
                    () -> new ChairBlock(wood.plankProperties().noOcclusion()));
            DeferredItem<BlockItem> chairItem = registerBlockItem(items, chairId, chair);
            FURNITURE_BLOCK_ITEMS.add(chairItem);
            furnitureByWood.add(chairItem);

            String armchairId = woodId + "_armchair";
            DeferredBlock<Block> armchair = blocks.register(armchairId,
                    () -> new ArmchairBlock(wood.plankProperties().noOcclusion()));
            DeferredItem<BlockItem> armchairItem = registerBlockItem(items, armchairId, armchair);
            FURNITURE_BLOCK_ITEMS.add(armchairItem);
            furnitureByWood.add(armchairItem);

            String benchId = woodId + "_bench";
            DeferredBlock<Block> bench = blocks.register(benchId,
                    () -> new BenchBlock(wood.plankProperties().noOcclusion()));
            DeferredItem<BlockItem> benchItem = registerBlockItem(items, benchId, bench);
            FURNITURE_BLOCK_ITEMS.add(benchItem);
            furnitureByWood.add(benchItem);

            String cupboardId = woodId + "_cupboard";
            DeferredBlock<Block> cupboard = blocks.register(cupboardId,
                    () -> new CupboardBlock(wood.plankProperties().noOcclusion()));
            ITEM_DISPLAY_BLOCKS_FOR_ENTITY.add(cupboard);
            DeferredItem<BlockItem> cupboardItem = registerBlockItem(items, cupboardId, cupboard);
            CONTAINER_BLOCK_ITEMS.add(cupboardItem);
            containersByWood.add(cupboardItem);

            String wallShelfId = woodId + "_wall_shelf";
            DeferredBlock<Block> wallShelf = blocks.register(wallShelfId,
                    () -> new WallShelfBlock(wood.plankProperties().noOcclusion()));
            ITEM_DISPLAY_BLOCKS_FOR_ENTITY.add(wallShelf);
            DeferredItem<BlockItem> wallShelfItem = registerBlockItem(items, wallShelfId, wallShelf);
            CONTAINER_BLOCK_ITEMS.add(wallShelfItem);
            containersByWood.add(wallShelfItem);

            String cabinetId = woodId + "_cabinet";
            DeferredBlock<Block> cabinet = blocks.register(cabinetId,
                    () -> new CabinetBlock(wood.plankProperties(), true));
            CABINET_BLOCKS_FOR_ENTITY.add(cabinet);
            DeferredItem<BlockItem> cabinetItem = registerBlockItem(items, cabinetId, cabinet);
            CONTAINER_BLOCK_ITEMS.add(cabinetItem);
            containersByWood.add(cabinetItem);

            String drawerId = woodId + "_drawer";
            DeferredBlock<Block> drawer = blocks.register(drawerId,
                    () -> new CabinetBlock(wood.plankProperties(), false));
            CABINET_BLOCKS_FOR_ENTITY.add(drawer);
            DeferredItem<BlockItem> drawerItem = registerBlockItem(items, drawerId, drawer);
            CONTAINER_BLOCK_ITEMS.add(drawerItem);
            containersByWood.add(drawerItem);

            String drawerChestId = woodId + "_drawer_chest";
            DeferredBlock<Block> drawerChest = blocks.register(drawerChestId,
                    () -> new CabinetBlock(wood.plankProperties(), true));
            CABINET_BLOCKS_FOR_ENTITY.add(drawerChest);
            DeferredItem<BlockItem> drawerChestItem = registerBlockItem(items, drawerChestId, drawerChest);
            CONTAINER_BLOCK_ITEMS.add(drawerChestItem);
            containersByWood.add(drawerChestItem);
            if (CONTAINER_CATEGORY_ICON_ITEM_ID.equals(drawerChestId)) {
                containerCategoryIconItem = drawerChestItem;
            }

            String easelMenuId = woodId + "_easel_menu";
            DeferredBlock<Block> easelMenu = blocks.register(easelMenuId,
                    () -> new EaselMenuBlock(wood.plankProperties().noOcclusion()));
            EASEL_MENU_BLOCKS_FOR_ENTITY.add(easelMenu);
            DeferredItem<DyeableBlockItem> easelMenuItem = registerDyeableBlockItem(items, easelMenuId, easelMenu);
            EASEL_MENU_BLOCK_ITEMS.add(easelMenuItem);
            easelMenusByWood.add(easelMenuItem);
            if (ORNAMENTS_TAB_ICON_ITEM_ID.equals(easelMenuId)) {
                ornamentsTabIconItem = easelMenuItem;
            }
            if (EASEL_MENU_CATEGORY_ICON_ITEM_ID.equals(easelMenuId)) {
                easelMenuCategoryIconItem = easelMenuItem;
            }
        }

    }

    private static Block createHalfTimberBlock(NekoWood wood, int patternIndex) {
        if (patternIndex == 0) {
            return new DyeableVerticalConnectedBlock(wood.plankProperties(), VerticalConnectedBlock.ConnectionType.PILLAR, false);
        }
        if (patternIndex <= 2) {
            return new DyeableVerticalConnectedBlock(wood.plankProperties(), VerticalConnectedBlock.ConnectionType.TRIPLE, false);
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

    public static List<DeferredItem<BlockItem>> windowItemsForWood(NekoWood wood) {
        return Collections.unmodifiableList(WINDOW_BLOCK_ITEMS_BY_WOOD.getOrDefault(wood, List.of()));
    }

    public static List<DeferredItem<? extends BlockItem>> furnitureItemsForWood(NekoWood wood) {
        return Collections.unmodifiableList(FURNITURE_BLOCK_ITEMS_BY_WOOD.getOrDefault(wood, List.of()));
    }

    public static List<DeferredItem<? extends BlockItem>> containerItemsForWood(NekoWood wood) {
        return Collections.unmodifiableList(CONTAINER_BLOCK_ITEMS_BY_WOOD.getOrDefault(wood, List.of()));
    }

    public static List<DeferredItem<DyeableBlockItem>> easelMenuItemsForWood(NekoWood wood) {
        return Collections.unmodifiableList(EASEL_MENU_BLOCK_ITEMS_BY_WOOD.getOrDefault(wood, List.of()));
    }

    public static List<DeferredItem<DyeableBlockItem>> easelMenuBlockItemsView() {
        return Collections.unmodifiableList(EASEL_MENU_BLOCK_ITEMS);
    }

    public static List<DeferredItem<? extends BlockItem>> containerBlockItemsView() {
        return Collections.unmodifiableList(CONTAINER_BLOCK_ITEMS);
    }

    /** Creative tab icon ({@value #TAB_ICON_ITEM_ID}). */
    public static DeferredItem<DyeableBlockItem> iconItem() {
        return tabIconItem;
    }

    /** Ornaments tab icon ({@value #ORNAMENTS_TAB_ICON_ITEM_ID}). */
    public static DeferredItem<DyeableBlockItem> ornamentsTabIconItem() {
        return ornamentsTabIconItem;
    }

    /** Icon for the Easel Menu category of the Ornaments tab ({@value #EASEL_MENU_CATEGORY_ICON_ITEM_ID}). */
    public static DeferredItem<DyeableBlockItem> easelMenuCategoryIconItem() {
        return easelMenuCategoryIconItem;
    }

    /** Add easel menu stacks for all woods (Easel Menu category of the Ornaments tab, Wooden Blocks tab). */
    public static void addEaselMenuCategoryStacks(Consumer<ItemStack> out) {
        for (var holder : easelMenuBlockItemsView()) {
            addPlainOrDyedStacks(holder.get(), out);
        }
    }

    /** Icon for the Furniture category of the Ornaments tab ({@value #FURNITURE_CATEGORY_ICON_ITEM_ID}). */
    public static DeferredItem<BlockItem> furnitureCategoryIconItem() {
        return furnitureCategoryIconItem;
    }

    /** Icon for the Container category of the Ornaments tab ({@value #CONTAINER_CATEGORY_ICON_ITEM_ID}). */
    public static DeferredItem<BlockItem> containerCategoryIconItem() {
        return containerCategoryIconItem;
    }

    /** Add furniture stacks for all woods (Furniture category of the Ornaments tab, Wooden Blocks tab). */
    public static void addFurnitureCategoryStacks(Consumer<ItemStack> out) {
        for (var holder : furnitureBlockItemsView()) {
            addPlainOrDyedStacks(holder.get(), out);
        }
    }

    /** Add container stacks for all woods (Container category of the Ornaments tab, Wooden Blocks tab). */
    public static void addContainerCategoryStacks(Consumer<ItemStack> out) {
        for (var holder : containerBlockItemsView()) {
            addPlainOrDyedStacks(holder.get(), out);
        }
    }

    /** Add furniture stacks for one wood (Wooden Blocks tab filter). */
    public static void addFurnitureStacksForWood(NekoWood wood, Consumer<ItemStack> out) {
        for (var holder : furnitureItemsForWood(wood)) {
            addPlainOrDyedStacks(holder.get(), out);
        }
    }

    /** Add container stacks for one wood (Wooden Blocks tab filter). */
    public static void addContainerStacksForWood(NekoWood wood, Consumer<ItemStack> out) {
        for (var holder : containerItemsForWood(wood)) {
            addPlainOrDyedStacks(holder.get(), out);
        }
    }

    /** Add easel menu stacks for one wood (Wooden Blocks tab filter). */
    public static void addEaselMenuStacksForWood(NekoWood wood, Consumer<ItemStack> out) {
        for (var holder : easelMenuItemsForWood(wood)) {
            addPlainOrDyedStacks(holder.get(), out);
        }
    }

    /** Dyed items get white and black variants, plain items a single stack. */
    private static void addPlainOrDyedStacks(Item item, Consumer<ItemStack> out) {
        if (item instanceof DyeableBlockItem) {
            out.accept(DyeableBlockItem.createCreativeTabStack(item, EnumNekoColor.WHITE));
            out.accept(DyeableBlockItem.createCreativeTabStack(item, EnumNekoColor.BLACK));
        } else {
            out.accept(new ItemStack(item));
        }
    }
}
