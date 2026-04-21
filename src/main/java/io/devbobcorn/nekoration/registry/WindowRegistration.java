package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.devbobcorn.nekoration.blocks.NekoWood;
import io.devbobcorn.nekoration.blocks.VerticalConnectBlock;
import io.devbobcorn.nekoration.blocks.WindowBlock;
import io.devbobcorn.nekoration.blocks.WindowPlantBlock;
import io.devbobcorn.nekoration.blocks.frames.DyeableWindowFrameBlock;
import io.devbobcorn.nekoration.blocks.frames.DyeableWindowSillBlock;
import io.devbobcorn.nekoration.blocks.frames.DyeableWindowTopBlock;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers {@code <wood>_window_<variant>} for each {@link NekoWood} and {@link WindowVariant}.
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
    public static final List<DeferredItem<DyeableBlockItem>> WINDOW_FRAME_BLOCK_ITEMS = new ArrayList<>();
    private static DeferredBlock<Block> WINDOW_PLANT_BLOCK;
    public static DeferredItem<DyeableBlockItem> WINDOW_PLANT_BLOCK_ITEM;

    private WindowRegistration() {
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        for (NekoWood wood : NekoWood.values()) {
            String w = wood.id();
            for (WindowVariant variant : WindowVariant.values()) {
                String id = w + "_window_" + variant.id();
                DeferredBlock<Block> block = blocks.register(id,
                        () -> new WindowBlock(wood.plankProperties().noOcclusion(),
                                VerticalConnectBlock.ConnectionType.PILLAR, false));
                WINDOW_BLOCK_ITEMS.add(registerBlockItem(items, id, block));
            }
        }

        registerWindowFrame(blocks, items, "window_frame");
        registerWindowSill(blocks, items, "window_sill");
        registerWindowTop(blocks, items, "window_top");
        registerWindowPlant(blocks, items, "window_plant");
    }

    private static BlockBehaviour.Properties stoneWindowFrameProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion();
    }

    private static void registerWindowFrame(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new DyeableWindowFrameBlock(stoneWindowFrameProperties()));
        WINDOW_FRAME_BLOCK_ITEMS.add(registerDyeableBlockItem(items, id, block));
    }

    private static void registerWindowSill(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new DyeableWindowSillBlock(stoneWindowFrameProperties()));
        WINDOW_FRAME_BLOCK_ITEMS.add(registerDyeableBlockItem(items, id, block));
    }

    private static void registerWindowTop(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new DyeableWindowTopBlock(stoneWindowFrameProperties()));
        WINDOW_FRAME_BLOCK_ITEMS.add(registerDyeableBlockItem(items, id, block));
    }

    private static void registerWindowPlant(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new WindowPlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).noOcclusion()));
        DeferredItem<DyeableBlockItem> item = registerDyeableBlockItem(items, id, block);
        WINDOW_PLANT_BLOCK = block;
        WINDOW_PLANT_BLOCK_ITEM = item;
    }

    private static DeferredItem<BlockItem> registerBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new BlockItem(block.get(), props), new Item.Properties());
    }

    private static DeferredItem<DyeableBlockItem> registerDyeableBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new DyeableBlockItem(block.get(), props), new Item.Properties());
    }

    public static List<DeferredItem<BlockItem>> windowBlockItemsView() {
        return Collections.unmodifiableList(WINDOW_BLOCK_ITEMS);
    }

    public static List<DeferredItem<DyeableBlockItem>> windowFrameBlockItemsView() {
        return Collections.unmodifiableList(WINDOW_FRAME_BLOCK_ITEMS);
    }

    public static DeferredBlock<Block> windowPlantBlock() {
        return WINDOW_PLANT_BLOCK;
    }

    public static DeferredItem<DyeableBlockItem> windowPlantBlockItem() {
        return WINDOW_PLANT_BLOCK_ITEM;
    }

    /** Creative tab icon. */
    public static DeferredItem<BlockItem> iconItem() {
        return WINDOW_BLOCK_ITEMS.get(4);
    }
}
