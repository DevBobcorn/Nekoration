package com.devbobcorn.nekoration.blocks.entities;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.utils.TagTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CustomBlockEntity extends BlockEntity {
    public Byte dir = 0;
    public int[] offset = { 0, 0, 0 };
    public Boolean retint = false, showHint = false;
    public int[] color = { 255, 255, 255 }; // RGB Color...

    public static BlockState defaultState = Blocks.AIR.defaultBlockState();
    public BlockState displayState = Blocks.AIR.defaultBlockState();

    public CustomBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.CUSTOM_TYPE.get(), pos, state);
    }
    
    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag); // The super call is required to save the tile's location
        tag.putByte("Dir", dir);
        tag.putIntArray("Offset", offset);
        tag.putByte("StateFlag", GetStateFlag());
        tag.putIntArray("Color", color);
        tag.put("Display", NbtUtils.writeBlockState(displayState));
    }

    // This is where you load the data that you saved in writeToNBT
    @Override
    @SuppressWarnings("null")
    public void load(CompoundTag tag) {
        super.load(tag); // The super call is required to load the tile's location

        if (tag.contains("Dir", TagTypes.BYTE_NBT_ID)) {
            dir = tag.getByte("Dir");
        }
        if (tag.contains("Offset", TagTypes.INT_ARRAY_NBT_ID)) {
            offset = tag.getIntArray("Offset");
        }
        if (tag.contains("StateFlag", TagTypes.BYTE_NBT_ID)) {
            SetStateFlag(tag.getByte("StateFlag"));
        }
        if (tag.contains("Color", TagTypes.INT_ARRAY_NBT_ID)) {
            color = tag.getIntArray("Color");
        }
        if (tag.contains("Display", TagTypes.COMPOUND_NBT_ID)) {
            CompoundTag dat = tag.getCompound("Display");
            displayState = NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK), dat);
        }
    }

    private byte GetStateFlag() {
        int retintFlag = retint ? 1 : 0;
        int showHintFlag = showHint ? 2 : 0;
        return (byte)(retintFlag | showHintFlag);
    }

    public void SetStateFlag(Byte flag) {
        retint = (flag & 1) > 0;
        showHint = (flag & 2) > 0;
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

}