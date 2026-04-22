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
import io.devbobcorn.nekoration.blocks.containers.WallShelfBlock;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
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
    public static final List<DeferredItem<BlockItem>> CONTAINER_BLOCK_ITEMS = new ArrayList<>();
    /** All {@link CabinetBlock} instances that use {@link CabinetBlockEntity}. */
    public static final List<DeferredBlock<Block>> CABINET_BLOCKS_FOR_ENTITY = new ArrayList<>();
    public static final Map<NekoWood, List<DeferredItem<DyeableBlockItem>>> DYED_BLOCK_ITEMS_BY_WOOD = new EnumMap<>(NekoWood.class);
    public static final Map<NekoWood, List<DeferredItem<BlockItem>>> PLAIN_BLOCK_ITEMS_BY_WOOD = new EnumMap<>(NekoWood.class);

    private WoodenBlockRegistration() {
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        for (NekoWood wood : NekoWood.values()) {
            String w = wood.id();
            List<DeferredItem<DyeableBlockItem>> dyedByWood = DYED_BLOCK_ITEMS_BY_WOOD.computeIfAbsent(wood, ignored -> new ArrayList<>());
            List<DeferredItem<BlockItem>> plainByWood = PLAIN_BLOCK_ITEMS_BY_WOOD.computeIfAbsent(wood, ignored -> new ArrayList<>());

            for (int p = 0; p <= 9; p++) {
                String id = w + "_half_timber_p" + p;
                DeferredBlock<Block> block = blocks.register(id, () -> new DyeableBlock(wood.plankProperties()));
                DeferredItem<DyeableBlockItem> registered = registerDyeableBlockItem(items, id, block);
                HALF_TIMBER_BLOCK_ITEMS.add(registered);
                dyedByWood.add(registered);
            }

            registerPillar(blocks, items, wood, w, 0, VerticalConnectBlock.ConnectionType.PILLAR, dyedByWood);
            registerPillar(blocks, items, wood, w, 1, VerticalConnectBlock.ConnectionType.TRIPLE, dyedByWood);
            registerPillar(blocks, items, wood, w, 2, VerticalConnectBlock.ConnectionType.TRIPLE, dyedByWood);

            for (WindowVariant variant : WindowVariant.values()) {
                String id = w + "_window_" + variant.id();
                DeferredBlock<Block> block = blocks.register(id,
                        () -> new WindowBlock(wood.plankProperties().noOcclusion(),
                                VerticalConnectBlock.ConnectionType.PILLAR, false));
                DeferredItem<BlockItem> registered = registerBlockItem(items, id, block);
                WINDOW_BLOCK_ITEMS.add(registered);
                plainByWood.add(registered);
            }

            String cupboardId = w + "_cupboard";
            DeferredBlock<Block> cupboard = blocks.register(cupboardId,
                    () -> new CupboardBlock(wood.plankProperties().noOcclusion()));
            DeferredItem<BlockItem> cupboardItem = registerBlockItem(items, cupboardId, cupboard);
            CONTAINER_BLOCK_ITEMS.add(cupboardItem);
            plainByWood.add(cupboardItem);

            String wallShelfId = w + "_wall_shelf";
            DeferredBlock<Block> wallShelf = blocks.register(wallShelfId,
                    () -> new WallShelfBlock(wood.plankProperties().noOcclusion()));
            DeferredItem<BlockItem> wallShelfItem = registerBlockItem(items, wallShelfId, wallShelf);
            CONTAINER_BLOCK_ITEMS.add(wallShelfItem);
            plainByWood.add(wallShelfItem);

            String cabinetId = w + "_cabinet";
            DeferredBlock<Block> cabinet = blocks.register(cabinetId,
                    () -> new CabinetBlock(wood.plankProperties(), true));
            CABINET_BLOCKS_FOR_ENTITY.add(cabinet);
            DeferredItem<BlockItem> cabinetItem = registerBlockItem(items, cabinetId, cabinet);
            CONTAINER_BLOCK_ITEMS.add(cabinetItem);
            plainByWood.add(cabinetItem);

            String drawerId = w + "_drawer";
            DeferredBlock<Block> drawer = blocks.register(drawerId,
                    () -> new CabinetBlock(wood.plankProperties(), false));
            CABINET_BLOCKS_FOR_ENTITY.add(drawer);
            DeferredItem<BlockItem> drawerItem = registerBlockItem(items, drawerId, drawer);
            CONTAINER_BLOCK_ITEMS.add(drawerItem);
            plainByWood.add(drawerItem);

            String drawerChestId = w + "_drawer_chest";
            DeferredBlock<Block> drawerChest = blocks.register(drawerChestId,
                    () -> new CabinetBlock(wood.plankProperties(), true));
            CABINET_BLOCKS_FOR_ENTITY.add(drawerChest);
            DeferredItem<BlockItem> drawerChestItem = registerBlockItem(items, drawerChestId, drawerChest);
            CONTAINER_BLOCK_ITEMS.add(drawerChestItem);
            plainByWood.add(drawerChestItem);
        }

    }

    private static void registerPillar(DeferredRegister.Blocks blocks, DeferredRegister.Items items, NekoWood wood,
            String woodId, int index, VerticalConnectBlock.ConnectionType type, List<DeferredItem<DyeableBlockItem>> dyedByWood) {
        String id = woodId + "_half_timber_pillar_p" + index;
        DeferredBlock<Block> block = blocks.register(id, () -> new DyeableVerticalConnectBlock(wood.plankProperties(), type, false));
        DeferredItem<DyeableBlockItem> registered = registerDyeableBlockItem(items, id, block);
        HALF_TIMBER_BLOCK_ITEMS.add(registered);
        dyedByWood.add(registered);
    }

    private static DeferredItem<DyeableBlockItem> registerDyeableBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new DyeableBlockItem(block.get(), props), new Item.Properties());
    }

    private static DeferredItem<BlockItem> registerBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new BlockItem(block.get(), props), new Item.Properties());
    }

    public static List<DeferredItem<DyeableBlockItem>> halfTimberBlockItemsView() {
        return Collections.unmodifiableList(HALF_TIMBER_BLOCK_ITEMS);
    }

    public static List<DeferredItem<BlockItem>> windowBlockItemsView() {
        return Collections.unmodifiableList(WINDOW_BLOCK_ITEMS);
    }

    public static List<DeferredItem<BlockItem>> furnitureBlockItemsView() {
        return Collections.unmodifiableList(CONTAINER_BLOCK_ITEMS);
    }

    public static Block[] cabinetBlocksForEntity() {
        return CABINET_BLOCKS_FOR_ENTITY.stream().map(DeferredBlock::get).toArray(Block[]::new);
    }

    public static List<DeferredItem<DyeableBlockItem>> dyedItemsForWood(NekoWood wood) {
        return Collections.unmodifiableList(DYED_BLOCK_ITEMS_BY_WOOD.getOrDefault(wood, List.of()));
    }

    public static List<DeferredItem<BlockItem>> plainItemsForWood(NekoWood wood) {
        return Collections.unmodifiableList(PLAIN_BLOCK_ITEMS_BY_WOOD.getOrDefault(wood, List.of()));
    }

    /** Creative tab icon. */
    public static DeferredItem<DyeableBlockItem> iconItem() {
        return HALF_TIMBER_BLOCK_ITEMS.get(1);
    }
}
