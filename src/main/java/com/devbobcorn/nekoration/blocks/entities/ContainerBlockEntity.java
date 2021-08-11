package com.devbobcorn.nekoration.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Iterator;
import java.util.stream.IntStream;

public abstract class ContainerBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer{
    private final int[] slots;
    protected NonNullList<ItemStack> items;

    protected ContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.slots = IntStream.range(0, this.getContainerSize()).toArray();
    }

    @Override
    public int[] getSlotsForFace(Direction dir) {
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public boolean isEmpty()
    {
        Iterator<ItemStack> it = this.items.iterator();
        ItemStack stack;
        do
        {
            if(!it.hasNext())
            {
                return true;
            }
            stack = (ItemStack) it.next();
        }
        while(stack.isEmpty());
        return false;
    }

    public boolean isFull()
    {
        for(ItemStack stack : this.items)
        {
            if(stack.isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public CompoundTag save(CompoundTag compound)
    {
        super.save(compound);
        if(!this.trySaveLootTable(compound))
        {
            ContainerHelper.saveAllItems(compound, this.items);
        }
        return compound;
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if(!this.tryLoadLootTable(compound))
        {
            ContainerHelper.loadAllItems(compound, this.items);
        }
    }

    public void onOpen(Level level, BlockPos pos, BlockState state) {}

    public void onClose(Level level, BlockPos pos, BlockState state) {}
}
