package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.DyeableVerticalConnectBlock;
import io.devbobcorn.nekoration.blocks.VerticalConnectBlock;
import io.devbobcorn.nekoration.blocks.HalfTimberWood;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers {@code <wood>_half_timber_p0..p9} and {@code <wood>_half_timber_pillar_p0..p2} for each {@link HalfTimberWood}.
 */
public final class HalfTimberRegistration {
    public static final List<DeferredItem<DyeableBlockItem>> HALF_TIMBER_BLOCK_ITEMS = new ArrayList<>();

    private HalfTimberRegistration() {
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        for (HalfTimberWood wood : HalfTimberWood.values()) {
            String w = wood.id();

            for (int p = 0; p <= 9; p++) {
                String id = w + "_half_timber_p" + p;
                DeferredBlock<Block> block = blocks.register(id, () -> new DyeableBlock(wood.plankProperties()));
                HALF_TIMBER_BLOCK_ITEMS.add(registerDyeableBlockItem(items, id, block));
            }

            registerPillar(blocks, items, wood, w, 0, VerticalConnectBlock.ConnectionType.PILLAR);
            registerPillar(blocks, items, wood, w, 1, VerticalConnectBlock.ConnectionType.TRIPLE);
            registerPillar(blocks, items, wood, w, 2, VerticalConnectBlock.ConnectionType.TRIPLE);
        }
    }

    private static void registerPillar(DeferredRegister.Blocks blocks, DeferredRegister.Items items, HalfTimberWood wood,
            String woodId, int index, VerticalConnectBlock.ConnectionType type) {
        String id = woodId + "_half_timber_pillar_p" + index;
        DeferredBlock<Block> block = blocks.register(id, () -> new DyeableVerticalConnectBlock(wood.plankProperties(), type, false));
        HALF_TIMBER_BLOCK_ITEMS.add(registerDyeableBlockItem(items, id, block));
    }

    private static DeferredItem<DyeableBlockItem> registerDyeableBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new DyeableBlockItem(block.get(), props), new Item.Properties());
    }

    public static List<DeferredItem<DyeableBlockItem>> blockItemsView() {
        return Collections.unmodifiableList(HALF_TIMBER_BLOCK_ITEMS);
    }

    /** Creative tab icon. */
    public static DeferredItem<DyeableBlockItem> iconItem() {
        return HALF_TIMBER_BLOCK_ITEMS.get(13 * 5 + 1);
    }
}
