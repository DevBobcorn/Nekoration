package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.DyeableVerticalConnectBlock;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers dyeable stone columns.
 */
public final class StoneColumnsRegistration {
    public static final List<DeferredItem<DyeableBlockItem>> STONE_BLOCK_ITEMS = new ArrayList<>();

    private StoneColumnsRegistration() {
    }

    private static BlockBehaviour.Properties stoneProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.STONE);
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        registerDyeableStone(blocks, items, "stone_layered");
        registerDyeableStone(blocks, items, "stone_base_bottom");
        registerDyeableStone(blocks, items, "stone_frame_bottom");
        registerDyeableStone(blocks, items, "stone_pillar_bottom");

        registerStonePillar(blocks, items, "stone_base");
        registerStonePillar(blocks, items, "stone_frame");
        registerStonePillar(blocks, items, "stone_pillar");
        registerStonePillar(blocks, items, "stone_doric");
        registerStonePillar(blocks, items, "stone_ionic");
        registerStonePillar(blocks, items, "stone_corinthian");
    }

    private static void registerDyeableStone(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id) {
        DeferredBlock<Block> block = blocks.register(id, () -> new DyeableBlock(stoneProperties()));
        STONE_BLOCK_ITEMS.add(registerDyeableBlockItem(items, id, block));
    }

    private static void registerStonePillar(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new DyeableVerticalConnectBlock(stoneProperties(), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false));
        STONE_BLOCK_ITEMS.add(registerDyeableBlockItem(items, id, block));
    }

    private static DeferredItem<DyeableBlockItem> registerDyeableBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new DyeableBlockItem(block.get(), props), new Item.Properties());
    }

    public static List<DeferredItem<DyeableBlockItem>> blockItemsView() {
        return Collections.unmodifiableList(STONE_BLOCK_ITEMS);
    }

    /** Creative tab icon. */
    public static DeferredItem<DyeableBlockItem> iconItem() {
        return STONE_BLOCK_ITEMS.get(2);
    }
}
