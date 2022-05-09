package com.devbobcorn.nekoration.blocks.entities;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.client.rendering.ChunkModel;

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
    @Nullable // May be accessed before onLoad
    // @OnlyIn(Dist.CLIENT) Makes it so this field will be removed from the class on the PHYSICAL SERVER
    // This is because we only want the ChunkModel on the physical client - its rendering only.
    @OnlyIn(Dist.CLIENT)
    public ChunkModel chunkModel;
    
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
        createIfNull();
    }

    public void createIfNull(){
        if (chunkModel == null)
            chunkModel = ChunkModel.forTileEntity(this);
    }

    public void refresh(){
        if (chunkModel != null && !chunkModel.getError())
            chunkModel.compile();
    }

    public boolean shouldRenderFace(Direction dir) {
        return Block.shouldRenderFace(this.getBlockState(), this.level, this.getBlockPos(), dir, this.getBlockPos().relative(dir));
    }
}