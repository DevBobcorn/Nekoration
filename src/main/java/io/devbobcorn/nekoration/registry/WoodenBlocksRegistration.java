package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.DyeableVerticalConnectBlock;
import io.devbobcorn.nekoration.blocks.NekoWood;
import io.devbobcorn.nekoration.blocks.VerticalConnectBlock;
import io.devbobcorn.nekoration.blocks.WindowBlock;
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
public final class WoodenBlocksRegistration {
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

    public static final List<DeferredItem<DyeableBlockItem>> HALF_TIMBER_BLOCK_ITEMS = new ArrayList<>();
    public static final List<DeferredItem<BlockItem>> WINDOW_BLOCK_ITEMS = new ArrayList<>();

    private WoodenBlocksRegistration() {
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        for (NekoWood wood : NekoWood.values()) {
            String w = wood.id();

            for (int p = 0; p <= 9; p++) {
                String id = w + "_half_timber_p" + p;
                DeferredBlock<Block> block = blocks.register(id, () -> new DyeableBlock(wood.plankProperties()));
                HALF_TIMBER_BLOCK_ITEMS.add(registerDyeableBlockItem(items, id, block));
            }

            registerPillar(blocks, items, wood, w, 0, VerticalConnectBlock.ConnectionType.PILLAR);
            registerPillar(blocks, items, wood, w, 1, VerticalConnectBlock.ConnectionType.TRIPLE);
            registerPillar(blocks, items, wood, w, 2, VerticalConnectBlock.ConnectionType.TRIPLE);

            for (WindowVariant variant : WindowVariant.values()) {
                String id = w + "_window_" + variant.id();
                DeferredBlock<Block> block = blocks.register(id,
                        () -> new WindowBlock(wood.plankProperties().noOcclusion(),
                                VerticalConnectBlock.ConnectionType.PILLAR, false));
                WINDOW_BLOCK_ITEMS.add(registerBlockItem(items, id, block));
            }
        }

    }

    private static void registerPillar(DeferredRegister.Blocks blocks, DeferredRegister.Items items, NekoWood wood,
            String woodId, int index, VerticalConnectBlock.ConnectionType type) {
        String id = woodId + "_half_timber_pillar_p" + index;
        DeferredBlock<Block> block = blocks.register(id, () -> new DyeableVerticalConnectBlock(wood.plankProperties(), type, false));
        HALF_TIMBER_BLOCK_ITEMS.add(registerDyeableBlockItem(items, id, block));
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

    /** Creative tab icon. */
    public static DeferredItem<DyeableBlockItem> iconItem() {
        return HALF_TIMBER_BLOCK_ITEMS.get(1);
    }
}
