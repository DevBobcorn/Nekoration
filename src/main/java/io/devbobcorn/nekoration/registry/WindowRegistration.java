package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.devbobcorn.nekoration.blocks.HalfTimberWood;
import io.devbobcorn.nekoration.blocks.VerticalConnectBlock;
import io.devbobcorn.nekoration.blocks.WindowBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers {@code <wood>_window_<variant>} for each {@link HalfTimberWood} and {@link WindowVariant}.
 */
public final class WindowRegistration {

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

    public static final List<DeferredItem<BlockItem>> WINDOW_BLOCK_ITEMS = new ArrayList<>();

    private WindowRegistration() {
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        for (HalfTimberWood wood : HalfTimberWood.values()) {
            String w = wood.id();
            for (WindowVariant variant : WindowVariant.values()) {
                String id = w + "_window_" + variant.id();
                DeferredBlock<Block> block = blocks.register(id,
                        () -> new WindowBlock(wood.plankProperties().noOcclusion(),
                                VerticalConnectBlock.ConnectionType.TRIPLE, false));
                WINDOW_BLOCK_ITEMS.add(registerBlockItem(items, id, block));
            }
        }
    }

    private static DeferredItem<BlockItem> registerBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new BlockItem(block.get(), props), new Item.Properties());
    }

    public static List<DeferredItem<BlockItem>> blockItemsView() {
        return Collections.unmodifiableList(WINDOW_BLOCK_ITEMS);
    }

    /** Creative tab icon. */
    public static DeferredItem<BlockItem> iconItem() {
        return WINDOW_BLOCK_ITEMS.get(4);
    }
}
