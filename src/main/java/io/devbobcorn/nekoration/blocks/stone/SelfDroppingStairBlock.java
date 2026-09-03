package io.devbobcorn.nekoration.blocks.stone;

import java.util.Collections;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

/**
 * Stairs block that drops itself without needing a loot table.
 */
public class SelfDroppingStairBlock extends StairBlock {

    public SelfDroppingStairBlock(BlockState baseState, Properties settings) {
        super(baseState, settings);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(asItem()));
    }
}
