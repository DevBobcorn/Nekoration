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
import io.devbobcorn.nekoration.xplat.NekoRegistrar;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.items.NekoBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;



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

    public static final List<RegistrySupplier<DyeableBlockItem>> HALF_TIMBER_BLOCK_ITEMS = new ArrayList<>();
    public static final List<RegistrySupplier<BlockItem>> WINDOW_BLOCK_ITEMS = new ArrayList<>();
    public static final List<RegistrySupplier<? extends BlockItem>> FURNITURE_BLOCK_ITEMS = new ArrayList<>();
    public static final List<RegistrySupplier<? extends BlockItem>> CONTAINER_BLOCK_ITEMS = new ArrayList<>();
    public static final List<RegistrySupplier<DyeableBlockItem>> EASEL_MENU_BLOCK_ITEMS = new ArrayList<>();
    /** All {@link CabinetBlock} instances that use {@link CabinetBlockEntity}. */
    public static final List<RegistrySupplier<Block>> CABINET_BLOCKS_FOR_ENTITY = new ArrayList<>();
    /** Cupboards and wall shelves using {@link io.devbobcorn.nekoration.blocks.entities.ItemDisplayBlockEntity}. */
    public static final List<RegistrySupplier<Block>> ITEM_DISPLAY_BLOCKS_FOR_ENTITY = new ArrayList<>();
    /** Easel menu blocks using {@link io.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity}. */
    public static final List<RegistrySupplier<Block>> EASEL_MENU_BLOCKS_FOR_ENTITY = new ArrayList<>();
    public static final Map<NekoWood, List<RegistrySupplier<DyeableBlockItem>>> DYED_BLOCK_ITEMS_BY_WOOD = new EnumMap<>(NekoWood.class);
    public static final Map<NekoWood, List<RegistrySupplier<BlockItem>>> WINDOW_BLOCK_ITEMS_BY_WOOD = new EnumMap<>(NekoWood.class);
    public static final Map<NekoWood, List<RegistrySupplier<? extends BlockItem>>> FURNITURE_BLOCK_ITEMS_BY_WOOD = new EnumMap<>(NekoWood.class);
    public static final Map<NekoWood, List<RegistrySupplier<? extends BlockItem>>> CONTAINER_BLOCK_ITEMS_BY_WOOD = new EnumMap<>(NekoWood.class);
    public static final Map<NekoWood, List<RegistrySupplier<DyeableBlockItem>>> EASEL_MENU_BLOCK_ITEMS_BY_WOOD = new EnumMap<>(NekoWood.class);
    /** All registered wooden blocks per wood (mineable tags, flammability). */
    private static final Map<NekoWood, List<RegistrySupplier<Block>>> WOODEN_BLOCKS_BY_WOOD = new EnumMap<>(NekoWood.class);

    private static final String TAB_ICON_ITEM_ID = "oak_half_timber_p1";
    private static RegistrySupplier<DyeableBlockItem> tabIconItem;
    private static final String ORNAMENTS_TAB_ICON_ITEM_ID = "spruce_easel_menu";
    private static RegistrySupplier<DyeableBlockItem> ornamentsTabIconItem;
    private static final String FURNITURE_CATEGORY_ICON_ITEM_ID = "spruce_table";
    private static RegistrySupplier<BlockItem> furnitureCategoryIconItem;
    private static final String CONTAINER_CATEGORY_ICON_ITEM_ID = "acacia_drawer_chest";
    private static RegistrySupplier<BlockItem> containerCategoryIconItem;

    private WoodenBlockRegistration() {
    }

    public static void register(NekoRegistrar registrar) {
        for (NekoWood wood : NekoWood.values()) {
            String woodId = wood.id();
            List<RegistrySupplier<DyeableBlockItem>> dyedByWood = DYED_BLOCK_ITEMS_BY_WOOD.computeIfAbsent(wood, ignored -> new ArrayList<>());
            List<RegistrySupplier<BlockItem>> windowsByWood = WINDOW_BLOCK_ITEMS_BY_WOOD.computeIfAbsent(wood,
                    ignored -> new ArrayList<>());
            List<RegistrySupplier<? extends BlockItem>> furnitureByWood = FURNITURE_BLOCK_ITEMS_BY_WOOD.computeIfAbsent(wood,
                    ignored -> new ArrayList<>());
            List<RegistrySupplier<? extends BlockItem>> containersByWood = CONTAINER_BLOCK_ITEMS_BY_WOOD.computeIfAbsent(wood,
                    ignored -> new ArrayList<>());
            List<RegistrySupplier<DyeableBlockItem>> easelMenusByWood = EASEL_MENU_BLOCK_ITEMS_BY_WOOD.computeIfAbsent(wood,
                    ignored -> new ArrayList<>());
            List<RegistrySupplier<Block>> woodenBlocks = WOODEN_BLOCKS_BY_WOOD.computeIfAbsent(wood,
                    ignored -> new ArrayList<>());

            for (int p = 0; p <= 9; p++) {
                int patternIndex = p;
                String id = woodId + "_half_timber_p" + p;
                RegistrySupplier<Block> block = registrar.block(id, () -> createHalfTimberBlock(wood, patternIndex));
                woodenBlocks.add(block);
                RegistrySupplier<DyeableBlockItem> blockItem = registerDyeableBlockItem(registrar, id, block);
                HALF_TIMBER_BLOCK_ITEMS.add(blockItem);
                dyedByWood.add(blockItem);
                if (TAB_ICON_ITEM_ID.equals(id)) {
                    tabIconItem = blockItem;
                }
            }

            for (WindowVariant variant : WindowVariant.values()) {
                String id = woodId + "_window_" + variant.id();
                RegistrySupplier<Block> block = registrar.block(id,
                        () -> new WindowBlock(wood.plankProperties().noOcclusion()));
                woodenBlocks.add(block);
                RegistrySupplier<BlockItem> registered = registerBlockItem(registrar, id, block);
                WINDOW_BLOCK_ITEMS.add(registered);
                windowsByWood.add(registered);

                String paneId = woodId + "_window_pane_" + variant.id();
                RegistrySupplier<Block> pane = registrar.block(paneId,
                        () -> new WindowPaneBlock(wood.plankProperties().noOcclusion()));
                woodenBlocks.add(pane);
                RegistrySupplier<BlockItem> paneItem = registerBlockItem(registrar, paneId, pane);
                WINDOW_BLOCK_ITEMS.add(paneItem);
                windowsByWood.add(paneItem);
            }

            String tableId = woodId + "_table";
            RegistrySupplier<Block> table = registrar.block(tableId,
                    () -> new TableBlock(wood.plankProperties().noOcclusion()));
            woodenBlocks.add(table);
            RegistrySupplier<BlockItem> tableItem = registerBlockItem(registrar, tableId, table);
            FURNITURE_BLOCK_ITEMS.add(tableItem);
            furnitureByWood.add(tableItem);
            if (FURNITURE_CATEGORY_ICON_ITEM_ID.equals(tableId)) {
                furnitureCategoryIconItem = tableItem;
            }

            String roundTableId = woodId + "_round_table";
            RegistrySupplier<Block> roundTable = registrar.block(roundTableId,
                    () -> new RoundTableBlock(wood.plankProperties().noOcclusion()));
            woodenBlocks.add(roundTable);
            RegistrySupplier<BlockItem> roundTableItem = registerBlockItem(registrar, roundTableId, roundTable);
            FURNITURE_BLOCK_ITEMS.add(roundTableItem);
            furnitureByWood.add(roundTableItem);

            String glassTableId = woodId + "_glass_table";
            RegistrySupplier<Block> glassTable = registrar.block(glassTableId,
                    () -> new TableBlock(wood.plankProperties().noOcclusion()));
            woodenBlocks.add(glassTable);
            RegistrySupplier<BlockItem> glassTableItem = registerBlockItem(registrar, glassTableId, glassTable);
            FURNITURE_BLOCK_ITEMS.add(glassTableItem);
            furnitureByWood.add(glassTableItem);

            String roundGlassTableId = woodId + "_round_glass_table";
            RegistrySupplier<Block> roundGlassTable = registrar.block(roundGlassTableId,
                    () -> new RoundTableBlock(wood.plankProperties().noOcclusion()));
            woodenBlocks.add(roundGlassTable);
            RegistrySupplier<BlockItem> roundGlassTableItem = registerBlockItem(registrar, roundGlassTableId, roundGlassTable);
            FURNITURE_BLOCK_ITEMS.add(roundGlassTableItem);
            furnitureByWood.add(roundGlassTableItem);

            String chairId = woodId + "_chair";
            RegistrySupplier<Block> chair = registrar.block(chairId,
                    () -> new ChairBlock(wood.plankProperties().noOcclusion()));
            woodenBlocks.add(chair);
            RegistrySupplier<BlockItem> chairItem = registerBlockItem(registrar, chairId, chair);
            FURNITURE_BLOCK_ITEMS.add(chairItem);
            furnitureByWood.add(chairItem);

            String armchairId = woodId + "_armchair";
            RegistrySupplier<Block> armchair = registrar.block(armchairId,
                    () -> new ArmchairBlock(wood.plankProperties().noOcclusion()));
            woodenBlocks.add(armchair);
            RegistrySupplier<BlockItem> armchairItem = registerBlockItem(registrar, armchairId, armchair);
            FURNITURE_BLOCK_ITEMS.add(armchairItem);
            furnitureByWood.add(armchairItem);

            String benchId = woodId + "_bench";
            RegistrySupplier<Block> bench = registrar.block(benchId,
                    () -> new BenchBlock(wood.plankProperties().noOcclusion()));
            woodenBlocks.add(bench);
            RegistrySupplier<BlockItem> benchItem = registerBlockItem(registrar, benchId, bench);
            FURNITURE_BLOCK_ITEMS.add(benchItem);
            furnitureByWood.add(benchItem);

            String cupboardId = woodId + "_cupboard";
            RegistrySupplier<Block> cupboard = registrar.block(cupboardId,
                    () -> new CupboardBlock(wood.plankProperties().noOcclusion()));
            woodenBlocks.add(cupboard);
            ITEM_DISPLAY_BLOCKS_FOR_ENTITY.add(cupboard);
            RegistrySupplier<BlockItem> cupboardItem = registerBlockItem(registrar, cupboardId, cupboard);
            CONTAINER_BLOCK_ITEMS.add(cupboardItem);
            containersByWood.add(cupboardItem);

            String wallShelfId = woodId + "_wall_shelf";
            RegistrySupplier<Block> wallShelf = registrar.block(wallShelfId,
                    () -> new WallShelfBlock(wood.plankProperties().noOcclusion()));
            woodenBlocks.add(wallShelf);
            ITEM_DISPLAY_BLOCKS_FOR_ENTITY.add(wallShelf);
            RegistrySupplier<BlockItem> wallShelfItem = registerBlockItem(registrar, wallShelfId, wallShelf);
            CONTAINER_BLOCK_ITEMS.add(wallShelfItem);
            containersByWood.add(wallShelfItem);

            String cabinetId = woodId + "_cabinet";
            RegistrySupplier<Block> cabinet = registrar.block(cabinetId,
                    () -> new CabinetBlock(wood.plankProperties(), true));
            woodenBlocks.add(cabinet);
            CABINET_BLOCKS_FOR_ENTITY.add(cabinet);
            RegistrySupplier<BlockItem> cabinetItem = registerBlockItem(registrar, cabinetId, cabinet);
            CONTAINER_BLOCK_ITEMS.add(cabinetItem);
            containersByWood.add(cabinetItem);

            String drawerId = woodId + "_drawer";
            RegistrySupplier<Block> drawer = registrar.block(drawerId,
                    () -> new CabinetBlock(wood.plankProperties(), false));
            woodenBlocks.add(drawer);
            CABINET_BLOCKS_FOR_ENTITY.add(drawer);
            RegistrySupplier<BlockItem> drawerItem = registerBlockItem(registrar, drawerId, drawer);
            CONTAINER_BLOCK_ITEMS.add(drawerItem);
            containersByWood.add(drawerItem);

            String drawerChestId = woodId + "_drawer_chest";
            RegistrySupplier<Block> drawerChest = registrar.block(drawerChestId,
                    () -> new CabinetBlock(wood.plankProperties(), true));
            woodenBlocks.add(drawerChest);
            CABINET_BLOCKS_FOR_ENTITY.add(drawerChest);
            RegistrySupplier<BlockItem> drawerChestItem = registerBlockItem(registrar, drawerChestId, drawerChest);
            CONTAINER_BLOCK_ITEMS.add(drawerChestItem);
            containersByWood.add(drawerChestItem);
            if (CONTAINER_CATEGORY_ICON_ITEM_ID.equals(drawerChestId)) {
                containerCategoryIconItem = drawerChestItem;
            }

            String easelMenuId = woodId + "_easel_menu";
            RegistrySupplier<Block> easelMenu = registrar.block(easelMenuId,
                    () -> new EaselMenuBlock(wood.plankProperties().noOcclusion()));
            woodenBlocks.add(easelMenu);
            EASEL_MENU_BLOCKS_FOR_ENTITY.add(easelMenu);
            RegistrySupplier<DyeableBlockItem> easelMenuItem = registerDyeableBlockItem(registrar, easelMenuId, easelMenu);
            EASEL_MENU_BLOCK_ITEMS.add(easelMenuItem);
            easelMenusByWood.add(easelMenuItem);
            if (ORNAMENTS_TAB_ICON_ITEM_ID.equals(easelMenuId)) {
                ornamentsTabIconItem = easelMenuItem;
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

    private static RegistrySupplier<DyeableBlockItem> registerDyeableBlockItem(NekoRegistrar registrar, String id,
            RegistrySupplier<Block> block) {
        return registrar.item(id, () -> new DyeableBlockItem(block.get(), new Item.Properties()));
    }

    private static RegistrySupplier<BlockItem> registerBlockItem(NekoRegistrar registrar, String id,
            RegistrySupplier<Block> block) {
        return registrar.item(id, () -> new NekoBlockItem(block.get(), new Item.Properties()));
    }

    public static List<RegistrySupplier<DyeableBlockItem>> halfTimberBlockItemsView() {
        return Collections.unmodifiableList(HALF_TIMBER_BLOCK_ITEMS);
    }

    public static List<RegistrySupplier<BlockItem>> windowBlockItemsView() {
        return Collections.unmodifiableList(WINDOW_BLOCK_ITEMS);
    }

    public static List<RegistrySupplier<? extends BlockItem>> furnitureBlockItemsView() {
        return Collections.unmodifiableList(FURNITURE_BLOCK_ITEMS);
    }

    public static Block[] cabinetBlocksForEntity() {
        return CABINET_BLOCKS_FOR_ENTITY.stream().map(RegistrySupplier::get).toArray(Block[]::new);
    }

    public static Block[] itemDisplayBlocksForEntity() {
        return ITEM_DISPLAY_BLOCKS_FOR_ENTITY.stream().map(RegistrySupplier::get).toArray(Block[]::new);
    }

    public static Block[] easelMenuBlocksForEntity() {
        return EASEL_MENU_BLOCKS_FOR_ENTITY.stream().map(RegistrySupplier::get).toArray(Block[]::new);
    }

    /** All registered wooden blocks for every wood (mineable tags). */
    public static List<RegistrySupplier<Block>> woodenBlocksView() {
        return WOODEN_BLOCKS_BY_WOOD.values().stream().flatMap(List::stream).toList();
    }

    /** Blocks registered per wood, for platform-side flammability registration (vanilla planks odds: 5/20). */
    public static Map<NekoWood, List<RegistrySupplier<Block>>> blocksByWoodView() {
        return Collections.unmodifiableMap(WOODEN_BLOCKS_BY_WOOD);
    }

    public static List<RegistrySupplier<DyeableBlockItem>> dyedItemsForWood(NekoWood wood) {
        return Collections.unmodifiableList(DYED_BLOCK_ITEMS_BY_WOOD.getOrDefault(wood, List.of()));
    }

    public static List<RegistrySupplier<BlockItem>> windowItemsForWood(NekoWood wood) {
        return Collections.unmodifiableList(WINDOW_BLOCK_ITEMS_BY_WOOD.getOrDefault(wood, List.of()));
    }

    public static List<RegistrySupplier<? extends BlockItem>> furnitureItemsForWood(NekoWood wood) {
        return Collections.unmodifiableList(FURNITURE_BLOCK_ITEMS_BY_WOOD.getOrDefault(wood, List.of()));
    }

    public static List<RegistrySupplier<? extends BlockItem>> containerItemsForWood(NekoWood wood) {
        return Collections.unmodifiableList(CONTAINER_BLOCK_ITEMS_BY_WOOD.getOrDefault(wood, List.of()));
    }

    public static List<RegistrySupplier<DyeableBlockItem>> easelMenuItemsForWood(NekoWood wood) {
        return Collections.unmodifiableList(EASEL_MENU_BLOCK_ITEMS_BY_WOOD.getOrDefault(wood, List.of()));
    }

    public static List<RegistrySupplier<DyeableBlockItem>> easelMenuBlockItemsView() {
        return Collections.unmodifiableList(EASEL_MENU_BLOCK_ITEMS);
    }

    public static List<RegistrySupplier<? extends BlockItem>> containerBlockItemsView() {
        return Collections.unmodifiableList(CONTAINER_BLOCK_ITEMS);
    }

    /** Creative tab icon ({@value #TAB_ICON_ITEM_ID}). */
    public static RegistrySupplier<DyeableBlockItem> iconItem() {
        return tabIconItem;
    }

    /** Ornaments tab icon ({@value #ORNAMENTS_TAB_ICON_ITEM_ID}). */
    public static RegistrySupplier<DyeableBlockItem> ornamentsTabIconItem() {
        return ornamentsTabIconItem;
    }

    /** Icon for the Furniture category of the Ornaments tab ({@value #FURNITURE_CATEGORY_ICON_ITEM_ID}). */
    public static RegistrySupplier<BlockItem> furnitureCategoryIconItem() {
        return furnitureCategoryIconItem;
    }

    /** Icon for the Container category of the Ornaments tab ({@value #CONTAINER_CATEGORY_ICON_ITEM_ID}). */
    public static RegistrySupplier<BlockItem> containerCategoryIconItem() {
        return containerCategoryIconItem;
    }

    /** Add furniture stacks for all woods (Furniture category of the Ornaments tab, Wooden Blocks tab). */
    public static void addFurnitureCategoryStacks(Consumer<ItemStack> out) {
        for (var holder : furnitureBlockItemsView()) {
            addPlainOrDyedStacks(holder.get(), out);
        }
    }

    /** Add container and easel menu stacks for all woods, grouped by wood (Container category of the Ornaments tab, Wooden Blocks tab). */
    public static void addContainerCategoryStacks(Consumer<ItemStack> out) {
        for (NekoWood wood : NekoWood.values()) {
            addContainerStacksForWood(wood, out);
        }
    }

    /** Add furniture stacks for one wood (Wooden Blocks tab filter). */
    public static void addFurnitureStacksForWood(NekoWood wood, Consumer<ItemStack> out) {
        for (var holder : furnitureItemsForWood(wood)) {
            addPlainOrDyedStacks(holder.get(), out);
        }
    }

    /** Add container and easel menu stacks for one wood, easel menus right after the other containers (Wooden Blocks tab filter). */
    public static void addContainerStacksForWood(NekoWood wood, Consumer<ItemStack> out) {
        for (var holder : containerItemsForWood(wood)) {
            addPlainOrDyedStacks(holder.get(), out);
        }
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
