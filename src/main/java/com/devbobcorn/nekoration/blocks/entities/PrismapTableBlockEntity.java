package com.devbobcorn.nekoration.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PrismapTableBlockEntity extends BlockEntity {
    public PrismapTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.PRISMAP_TABLE_TYPE.get(), pos, state);
    }
    
    // @OnlyIn(Dist.CLIENT) Makes it so this method will be removed from the class on the PHYSICAL SERVER
    // This is because we only want the MiniModel on the physical client - its rendering only.
    @OnlyIn(Dist.CLIENT)
    @Override
    public void load(CompoundTag tag){
        super.load(tag);
        Level world = getLevel();
        if (world == null || !world.isClientSide)
            return; // Return if the world is null or if we are on the logical server
        //createIfNull();
    }

    public boolean shouldRenderFace(Direction dir) {
        return Block.shouldRenderFace(this.getBlockState(), this.level, this.getBlockPos(), dir, this.getBlockPos().relative(dir));
    }
}