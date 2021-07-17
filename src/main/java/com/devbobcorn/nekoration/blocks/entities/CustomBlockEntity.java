package com.devbobcorn.nekoration.blocks.entities;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

import com.devbobcorn.nekoration.exp.ExpNBTTypes;

public class CustomBlockEntity extends TileEntity {
    public Integer model = 0;
	public Byte dir = 0;
	public int[] offset = { 0, 0, 0 };

    public CustomBlockEntity() {
        super(ModTileEntityType.CUSTOM_TYPE.get());
    }
    
	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag); // The super call is required to save the tile's location
		tag.putInt("Model", model);
		tag.putByte("Dir", dir);
		tag.putIntArray("Offset", offset);
		return tag;
	}

	// This is where you load the data that you saved in writeToNBT
	@Override
	public void load(BlockState blockState, CompoundNBT tag) {
		super.load(blockState, tag); // The super call is required to load the tile's location

		if (tag.contains("Model", ExpNBTTypes.INT_NBT_ID)) {
			model = tag.getInt("Model");
		}
		if (tag.contains("Dir", ExpNBTTypes.BYTE_NBT_ID)) {
			dir = tag.getByte("Dir");
		}
		if (tag.contains("Offset", ExpNBTTypes.INT_ARRAY_NBT_ID)) {
			offset = tag.getIntArray("Offset");
		}
	}

    @Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbtTagCompound = new CompoundNBT();
		save(nbtTagCompound);
		int tileEntityType = 1024; // arbitrary number for only vanilla TileEntities. You can use it, or not.
		return new SUpdateTileEntityPacket(this.worldPosition, tileEntityType, nbtTagCompound);
	}

	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		BlockState blockState = level.getBlockState(worldPosition);
		load(blockState, pkt.getTag()); // read from the nbt in the packet
	}
}
