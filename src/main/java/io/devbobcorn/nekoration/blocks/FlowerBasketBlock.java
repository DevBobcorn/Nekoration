package io.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import io.devbobcorn.nekoration.blocks.stone.PotBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

/**
 * Flower basket that can host plants and drops itself when broken.
 */
public class FlowerBasketBlock extends PotBlock {
    public FlowerBasketBlock(Properties properties) {
        super(properties, 6.0D);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(asItem()));
    }
}
