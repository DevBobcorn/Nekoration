package com.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import com.devbobcorn.nekoration.blocks.entities.PrismapTableBlockEntity;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;

public class PrismapTableBlock extends Block implements EntityBlock {
    public PrismapTableBlock(Properties settings) {
        super(settings);
    }

    // Called when the block is placed or loaded client side to get the tile entity
    // for the block
    // Should return a new instance of the tile entity for the block
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PrismapTableBlockEntity(pos, state);
    }

    // Called just after the player places a block.
    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);

        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof PrismapTableBlockEntity) { // prevent a crash if not the right type, or is null
            return;
        }
        LogUtils.getLogger().error("Tile Entity NOT Found!");
    }

    // Press 'F5' to refresh... ^._.^==~
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide){
            BlockEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof PrismapTableBlockEntity) { // prevent a crash if not the right type, or is null
                //PrismapTableBlockEntity te = (PrismapTableBlockEntity) tileentity;
                //te.createIfNull();
                //te.refresh();
                LogUtils.getLogger().info("Refreshing...");
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return Collections.singletonList(new ItemStack(this.asItem()));
    }
}
