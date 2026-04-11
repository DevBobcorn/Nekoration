package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.DyeableVerticalConnectBlock;
import io.devbobcorn.nekoration.blocks.HalfTimberWood;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers {@code half_timber_<wood>_p0..p9} and {@code half_timber_<wood>_pillar_p0..p2} for each {@link HalfTimberWood}.
 */
public final class HalfTimberRegistration {
    public static final List<DeferredItem<DyeableBlockItem>> BLOCK_ITEMS = new ArrayList<>();

    private HalfTimberRegistration() {
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        for (HalfTimberWood wood : HalfTimberWood.values()) {
            String w = wood.id();

            for (int p = 0; p <= 9; p++) {
                String id = "half_timber_" + w + "_p" + p;
                DeferredBlock<Block> block = blocks.register(id, () -> new DyeableBlock(wood.plankProperties()));
                BLOCK_ITEMS.add(registerDyeableBlockItem(items, id, block));
            }

            registerPillar(blocks, items, wood, w, 0, DyeableVerticalConnectBlock.ConnectionType.PILLAR);
            registerPillar(blocks, items, wood, w, 1, DyeableVerticalConnectBlock.ConnectionType.TRIPLE);
            registerPillar(blocks, items, wood, w, 2, DyeableVerticalConnectBlock.ConnectionType.TRIPLE);
        }
    }

    private static void registerPillar(DeferredRegister.Blocks blocks, DeferredRegister.Items items, HalfTimberWood wood,
            String woodId, int index, DyeableVerticalConnectBlock.ConnectionType type) {
        String id = "half_timber_" + woodId + "_pillar_p" + index;
        DeferredBlock<Block> block = blocks.register(id, () -> new DyeableVerticalConnectBlock(wood.plankProperties(), type, false));
        BLOCK_ITEMS.add(registerDyeableBlockItem(items, id, block));
    }

    private static DeferredItem<DyeableBlockItem> registerDyeableBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new DyeableBlockItem(block.get(), props), new Item.Properties());
    }

    public static List<DeferredItem<DyeableBlockItem>> blockItemsView() {
        return Collections.unmodifiableList(BLOCK_ITEMS);
    }

    /** {@code half_timber_oak_p1} — default creative tab icon. */
    public static DeferredItem<DyeableBlockItem> oakHalfTimberP1Item() {
        return BLOCK_ITEMS.get(1);
    }
}
