package io.devbobcorn.nekoration.blocks.entities;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import io.devbobcorn.nekoration.blocks.containers.CabinetBlock;
import io.devbobcorn.nekoration.registry.ModBlockEntities;

/**
 * Inventory and open/close behaviour for {@link CabinetBlock} (cabinets, drawers, and chest of drawers).
 */
public class CabinetBlockEntity extends RandomizableContainerBlockEntity {
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            if (!state.getValue(CabinetBlock.OPEN)) {
                CabinetBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
                level.setBlock(pos, state.setValue(CabinetBlock.OPEN, true), 3);
            }
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            if (state.getValue(CabinetBlock.OPEN)) {
                CabinetBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
                level.setBlock(pos, state.setValue(CabinetBlock.OPEN, false), 3);
            }
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof ChestMenu chestMenu) {
                return chestMenu.getContainer() == CabinetBlockEntity.this;
            }
            return false;
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int oldCount) {
        }
    };

    private NonNullList<ItemStack> items;

    public CabinetBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABINET.get(), pos, state);
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CabinetBlockEntity blockEntity) {
        if (!level.isClientSide()) {
            blockEntity.openersCounter.recheckOpeners(level, pos, state);
        }
    }

    private boolean isLarge() {
        return getBlockState().getBlock() instanceof CabinetBlock cabinet && cabinet.large;
    }

    @Override
    public int getContainerSize() {
        return (isLarge() ? 6 : 3) * 9;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> newItems) {
        items = newItems;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, items, registries);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, items, registries);
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        if (isLarge()) {
            return ChestMenu.sixRows(containerId, playerInventory, this);
        }
        return ChestMenu.threeRows(containerId, playerInventory, this);
    }

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            Level level = getLevel();
            if (level != null) {
                openersCounter.incrementOpeners(player, level, getBlockPos(), getBlockState());
            }
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            Level level = getLevel();
            if (level != null) {
                openersCounter.decrementOpeners(player, level, getBlockPos(), getBlockState());
            }
        }
    }

    private void playSound(BlockState state, SoundEvent sound) {
        Level level = getLevel();
        if (level == null) {
            return;
        }
        Direction dir = state.getValue(CabinetBlock.FACING);
        double x = getBlockPos().getX() + 0.5D + (double) dir.getStepX() / 2.0D;
        double y = getBlockPos().getY() + 0.5D + (double) dir.getStepY() / 2.0D;
        double z = getBlockPos().getZ() + 0.5D + (double) dir.getStepZ() / 2.0D;
        level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
}
