package io.devbobcorn.nekoration.registry;

import io.devbobcorn.nekoration.blocks.WindowPlantBlock;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers ornaments.
 */
public final class OrnamentRegistration {
    private static DeferredBlock<Block> WINDOW_PLANT_BLOCK;
    public static DeferredItem<DyeableBlockItem> WINDOW_PLANT_BLOCK_ITEM;

    private OrnamentRegistration() {
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        registerWindowPlant(blocks, items, "window_plant");
    }

    private static void registerWindowPlant(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new WindowPlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).noOcclusion()));
        DeferredItem<DyeableBlockItem> item = registerDyeableBlockItem(items, id, block);
        WINDOW_PLANT_BLOCK = block;
        WINDOW_PLANT_BLOCK_ITEM = item;
    }

    private static DeferredItem<DyeableBlockItem> registerDyeableBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new DyeableBlockItem(block.get(), props), new net.minecraft.world.item.Item.Properties());
    }

    public static DeferredBlock<Block> windowPlantBlock() {
        return WINDOW_PLANT_BLOCK;
    }

    public static DeferredItem<DyeableBlockItem> windowPlantBlockItem() {
        return WINDOW_PLANT_BLOCK_ITEM;
    }
}
