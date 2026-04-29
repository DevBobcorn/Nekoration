package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.devbobcorn.nekoration.blocks.DyeableBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers concrete blocks.
 */
public final class StoneBlockRegistration {
    public static final List<DeferredItem<Item>> STONE_BLOCK_ITEMS = new ArrayList<>();

    private StoneBlockRegistration() {
    }

    private static BlockBehaviour.Properties stoneProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.STONE);
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        
    }

    private static void registerBlock(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id) {
        DeferredBlock<Block> block = blocks.register(id, () -> new DyeableBlock(stoneProperties()));
        STONE_BLOCK_ITEMS.add(registerBlockItem(items, id, block));
    }

    private static DeferredItem<Item> registerBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new BlockItem(block.get(), props), new Item.Properties());
    }

    public static List<DeferredItem<Item>> blockItemsView() {
        return Collections.unmodifiableList(STONE_BLOCK_ITEMS);
    }

    /** Creative tab icon. */
    public static DeferredItem<Item> iconItem() {
        return STONE_BLOCK_ITEMS.get(0);
    }
}
