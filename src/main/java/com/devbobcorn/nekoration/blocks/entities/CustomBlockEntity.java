package com.devbobcorn.nekoration.blocks.entities;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.common.TagTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CustomBlockEntity extends BlockEntity {
    public Integer model = 0;
	public Byte dir = 0;
	public int[] offset = { 0, 0, 0 };
	public int[] color = { 255, 255, 255 }; // RGB Color...

	public BlockState displayBlock = Blocks.AIR.defaultBlockState();
	public ItemStack containItem = new ItemStack(Blocks.AIR);

    public CustomBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.CUSTOM_TYPE.get(), pos, state);
    }
    
	/*
	@Override
	public CompoundTag save(CompoundTag tag) {
		super.save(tag); // The super call is required to save the tile's location
		tag.putInt("Model", model);
		tag.putByte("Dir", dir);
		tag.putIntArray("Offset", offset);
		tag.putIntArray("Color", color);
		tag.put("Display", NBTUtil.writeBlockState(displayBlock));
		CompoundTag itm = new CompoundTag();
		tag.put("Contain", containItem.save(itm));
		return tag;
	}

	// This is where you load the data that you saved in writeToNBT
	@Override
	public void load(BlockState blockState, CompoundTag tag) {
		super.load(blockState, tag); // The super call is required to load the tile's location

		if (tag.contains("Model", TagTypes.INT_NBT_ID)) {
			model = tag.getInt("Model");
		}
		if (tag.contains("Dir", TagTypes.BYTE_NBT_ID)) {
			dir = tag.getByte("Dir");
		}
		if (tag.contains("Offset", TagTypes.INT_ARRAY_NBT_ID)) {
			offset = tag.getIntArray("Offset");
		}
		if (tag.contains("Color", TagTypes.INT_ARRAY_NBT_ID)) {
			color = tag.getIntArray("Color");
		}
		if (tag.contains("Display", TagTypes.COMPOUND_NBT_ID)) {
			CompoundTag dat = tag.getCompound("Display");
			displayBlock = NBTUtil.readBlockState(dat);
		}
		if (tag.contains("Contain", TagTypes.COMPOUND_NBT_ID)) {
			CompoundTag dat = tag.getCompound("Contain");
			containItem = ItemStack.of(dat);
		}
	}

    @Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundTag nbtTagCompound = new CompoundTag();
		save(nbtTagCompound);
		int tileEntityType = 1024; // arbitrary number for only vanilla TileEntities. You can use it, or not.
		return new SUpdateTileEntityPacket(this.worldPosition, tileEntityType, nbtTagCompound);
	}

	public CompoundTag getUpdateTag() {
		return this.save(new CompoundTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		BlockState blockState = level.getBlockState(worldPosition);
		load(blockState, pkt.getTag()); // read from the nbt in the packet
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		BlockPos pos = getBlockPos();
		InventoryHelper.dropItemStack(getLevel(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), containItem);
	}
	*/
}