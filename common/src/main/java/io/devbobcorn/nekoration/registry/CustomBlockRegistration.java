package io.devbobcorn.nekoration.registry;

import io.devbobcorn.nekoration.blocks.CustomBlock;
import io.devbobcorn.nekoration.xplat.NekoRegistrar;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Registers the Custom Block and its item.
 */
public final class CustomBlockRegistration {
    public static RegistrySupplier<Block> CUSTOM_BLOCK;
    public static RegistrySupplier<BlockItem> CUSTOM_BLOCK_ITEM;

    private CustomBlockRegistration() {
    }

    public static void register(NekoRegistrar registrar) {
        CUSTOM_BLOCK = registrar.block("custom_block", () -> new CustomBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                        .strength(1.5F, 6.0F)
                        .lightLevel(state -> state.getValue(CustomBlock.LIGHT))
                        .noOcclusion()));
        CUSTOM_BLOCK_ITEM = registrar.item("custom_block",
                () -> new BlockItem(CUSTOM_BLOCK.get(), new Item.Properties()));
    }
}
