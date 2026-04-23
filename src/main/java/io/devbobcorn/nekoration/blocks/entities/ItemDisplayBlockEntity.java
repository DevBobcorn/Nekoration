package io.devbobcorn.nekoration.blocks.entities;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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

import io.devbobcorn.nekoration.blocks.containers.ItemDisplayBlock;
import io.devbobcorn.nekoration.blocks.containers.WallShelfBlock;
import io.devbobcorn.nekoration.registry.ModBlockEntities;

/**
 * Inventory and open/close behaviour for {@link ItemDisplayBlock} containers (cupboards, wall shelves).
 *
 * <p>
 * Mirrors legacy {@code ItemDisplayBlockEntity}: 27 slots, barrel-style open state, and up to four
 * stacks cached for in-world item rendering. Display stacks sync to clients whenever they change,
 * including while the container GUI is open, so the BER stays in step with the menu.
 * </p>
 */
public class ItemDisplayBlockEntity extends RandomizableContainerBlockEntity {

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            if (!state.getValue(ItemDisplayBlock.OPEN)) {
                if (ItemDisplayBlockEntity.this.playSound) {
                    ItemDisplayBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
                }
                level.setBlock(pos, state.setValue(ItemDisplayBlock.OPEN, true), 3);
            }
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            if (state.getValue(ItemDisplayBlock.OPEN)) {
                if (ItemDisplayBlockEntity.this.playSound) {
                    ItemDisplayBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
                }
                BlockState closed = state.setValue(ItemDisplayBlock.OPEN, false);
                level.setBlock(pos, closed, 3);
                ItemDisplayBlockEntity.this.rebuildRenderStacksFromContents();
                ItemDisplayBlockEntity.this.syncDisplayToClients(level, pos, closed);
            }
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof ChestMenu chestMenu) {
                return chestMenu.getContainer() == ItemDisplayBlockEntity.this;
            }
            return false;
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int oldCount) {
        }
    };

    private NonNullList<ItemStack> items;
    /** First four non-empty stacks from the container, for client BER when closed (legacy {@code renderItems}). */
    private final NonNullList<ItemStack> renderStacks = NonNullList.withSize(4, ItemStack.EMPTY);

    public final boolean wallShelf;
    public final boolean playSound;

    /** Suppresses display sync while NBT is loading (avoids spamming updates during chunk load). */
    private boolean loading;

    public ItemDisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ITEM_DISPLAY.get(), pos, state);
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        this.wallShelf = state.getBlock() instanceof WallShelfBlock;
        this.playSound = !this.wallShelf;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ItemDisplayBlockEntity blockEntity) {
        if (!level.isClientSide()) {
            blockEntity.openersCounter.recheckOpeners(level, pos, state);
        }
    }

    public NonNullList<ItemStack> getRenderStacks() {
        return renderStacks;
    }

    private void rebuildRenderStacksFromContents() {
        int idx = 0;
        for (int i = 0; i < items.size() && idx < 4; i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                renderStacks.set(idx, stack.copy());
                idx++;
            }
        }
        for (int j = idx; j < 4; j++) {
            renderStacks.set(j, ItemStack.EMPTY);
        }
    }

    private static boolean sameDisplayStacks(NonNullList<ItemStack> a, NonNullList<ItemStack> b) {
        for (int i = 0; i < 4; i++) {
            if (!ItemStack.isSameItemSameComponents(a.get(i), b.get(i))) {
                return false;
            }
        }
        return true;
    }

    private void syncDisplayToClients(Level level, BlockPos pos, BlockState state) {
        setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
    }

    /**
     * Pushes display data to clients when the first four visible stacks change (server only).
     * Runs while the block is {@linkplain ItemDisplayBlock#OPEN} so world items track the open GUI.
     */
    private void maybeSyncDisplayAfterInventoryChange() {
        if (loading) {
            return;
        }
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }
        NonNullList<ItemStack> before = NonNullList.withSize(4, ItemStack.EMPTY);
        for (int i = 0; i < 4; i++) {
            before.set(i, renderStacks.get(i).copy());
        }
        rebuildRenderStacksFromContents();
        if (!sameDisplayStacks(before, renderStacks)) {
            syncDisplayToClients(level, getBlockPos(), getBlockState());
        }
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        maybeSyncDisplayAfterInventoryChange();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = super.removeItem(slot, amount);
        maybeSyncDisplayAfterInventoryChange();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = super.removeItemNoUpdate(slot);
        maybeSyncDisplayAfterInventoryChange();
        return result;
    }

    @Override
    public void clearContent() {
        super.clearContent();
        maybeSyncDisplayAfterInventoryChange();
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> newItems) {
        items = newItems;
        maybeSyncDisplayAfterInventoryChange();
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
        loading = true;
        try {
            super.loadAdditional(tag, registries);
            this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
            if (!tryLoadLootTable(tag)) {
                ContainerHelper.loadAllItems(tag, items, registries);
            }
            rebuildRenderStacksFromContents();
        } finally {
            loading = false;
        }
    }

    /**
     * Full serialized state for {@link ClientboundBlockEntityDataPacket} / chunk sync.
     *
     * <p>
     * NeoForge's default {@code handleUpdateTag} applies this tag with {@code loadWithComponents}; a
     * partial tag (e.g. display-only) does not update the client container and breaks live BER sync until
     * the chunk is reloaded.
     * </p>
     */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        rebuildRenderStacksFromContents();
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
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
        var dir = state.getValue(ItemDisplayBlock.FACING);
        double x = getBlockPos().getX() + 0.5D + (double) dir.getStepX() / 2.0D;
        double y = getBlockPos().getY() + 0.5D + (double) dir.getStepY() / 2.0D;
        double z = getBlockPos().getZ() + 0.5D + (double) dir.getStepZ() / 2.0D;
        level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
