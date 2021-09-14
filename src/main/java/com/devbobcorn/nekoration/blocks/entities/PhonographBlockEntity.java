package com.devbobcorn.nekoration.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PhonographBlockEntity extends BlockEntity {
    public PhonographBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.PHONOGRAPH_TYPE.get(), pos, state);
    }

    public CompoundTag save(CompoundTag tag){
        return super.save(tag);
    }

	public CompoundTag getUpdateTag() {
		return this.save(new CompoundTag());
	}

    public boolean shouldRenderFace(Direction dir) {
        return Block.shouldRenderFace(this.getBlockState(), this.level, this.getBlockPos(), dir, this.getBlockPos().relative(dir));
    }
}
