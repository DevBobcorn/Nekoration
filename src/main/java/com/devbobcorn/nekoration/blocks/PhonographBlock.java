package com.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.blocks.entities.PhonographBlockEntity;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

public class PhonographBlock extends Block implements EntityBlock {
    public PhonographBlock(Properties settings) {
        super(settings);
    }

    // Called when the block is placed or loaded client side to get the tile entity
    // for the block
    // Should return a new instance of the tile entity for the block
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PhonographBlockEntity(pos, state);
    }

    // Called just after the player places a block.
    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);

        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof PhonographBlockEntity) { // prevent a crash if not the right type, or is null
            return;
        }
        LogUtils.getLogger().error("Tile Entity NOT Found!");
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return Collections.singletonList(new ItemStack(this.asItem()));
    }
}
