package io.devbobcorn.nekoration.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CtTestRegistration {
    private static DeferredBlock<Block> CT_TEST_BLOCK;
    private static DeferredItem<BlockItem> CT_TEST_BLOCK_ITEM;

    private CtTestRegistration() {
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        CT_TEST_BLOCK = blocks.register("ct_test_block",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
        CT_TEST_BLOCK_ITEM = items.registerItem("ct_test_block",
                props -> new BlockItem(CT_TEST_BLOCK.get(), props), new Item.Properties());
    }

    public static DeferredItem<BlockItem> ctTestBlockItem() {
        return CT_TEST_BLOCK_ITEM;
    }
}
