package io.devbobcorn.nekoration.blocks.stone;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootParams;

/**
 * Slab block that drops itself without needing a loot table.
 * A double slab drops two items, one per half.
 */
public class SelfDroppingSlabBlock extends SlabBlock {

    public SelfDroppingSlabBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        int count = state.getValue(TYPE) == SlabType.DOUBLE ? 2 : 1;
        List<ItemStack> drops = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            drops.add(new ItemStack(asItem()));
        }
        return drops;
    }
}
