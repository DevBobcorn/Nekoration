package com.devbobcorn.nekoration.blocks.entities;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PhonographBlockEntity extends BlockEntity implements ITickableTileEntity {
    public PhonographBlockEntity() {
        super(ModTileEntityType.PHONOGRAPH_TYPE.get());
    }

    @Override
    public void tick() {
        // TODO

    }

    public void load(BlockState state, CompoundNBT tag){
        super.load(state, tag);
        // Update Record Info...
        SoundEvents.MUSIC_DISC_BLOCKS.getLocation();
    }

    public CompoundNBT save(CompoundNBT tag){
        return super.save(tag);
    }

    @Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.worldPosition, 1202, this.getUpdateTag());
	}

	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}
}
