package io.devbobcorn.nekoration.blocks.stone;

import java.util.Collections;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

/**
 * Wall block that drops itself without needing a loot table.
 */
public class SelfDroppingWallBlock extends WallBlock {

    public SelfDroppingWallBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(asItem()));
    }
}
