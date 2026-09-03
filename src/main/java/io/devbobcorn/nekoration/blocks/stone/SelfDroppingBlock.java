package io.devbobcorn.nekoration.blocks.stone;

import java.util.Collections;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

/**
 * Full cube block that drops itself without needing a loot table.
 */
public class SelfDroppingBlock extends Block {

    public SelfDroppingBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(asItem()));
    }
}
