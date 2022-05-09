package com.devbobcorn.nekoration.blocks.entities;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.ItemDisplayBlock;
import com.devbobcorn.nekoration.network.ModPacketHandler;
import com.devbobcorn.nekoration.network.S2CUpdateCupboardData;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;


public class ItemDisplayBlockEntity extends ContainerBlockEntity {
    private final ItemStack airStack = ItemStack.EMPTY;
    public ItemStack[] renderItems = { airStack, airStack, airStack, airStack };

    public final boolean wallShelf;
    public final boolean playSound;

    private ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(Level world, BlockPos pos, BlockState state) {
            if (playSound)
                ItemDisplayBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
            ItemDisplayBlockEntity.this.updateBlockState(state, true);
        }

        protected void onClose(Level world, BlockPos pos, BlockState state) {
            if (playSound)
                ItemDisplayBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
            ItemDisplayBlockEntity.this.updateBlockState(state, false);
            ItemStack[] its = { airStack, airStack, airStack, airStack };
            // Find out the 4 items to display...
            int idx = 0;
            for (ItemStack item : ItemDisplayBlockEntity.this.items){
                if (!item.is(Items.AIR)){
                    its[idx] = item.copy();
                    idx++;
                    if (idx >= 4)
                        break;
                }
            }
            // This called on SERVER SIDE...
            final S2CUpdateCupboardData packet = new S2CUpdateCupboardData(pos, its);
            ModPacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
        }

        protected void openerCountChanged(Level world, BlockPos pos, BlockState state, int a, int b) {
        }

        protected boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof ChestMenu) {
                Container container = ((ChestMenu) player.containerMenu).getContainer();
                return container == ItemDisplayBlockEntity.this;
            } else {
                return false;
            }
        }
    };

    public ItemDisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.ITEM_DISPLAY_TYPE.get(), pos, state);
        this.items = NonNullList.withSize(27, ItemStack.EMPTY);
        this.wallShelf = false;
        this.playSound = true;
    }

    public ItemDisplayBlockEntity(BlockPos pos, BlockState state, boolean s, boolean p) {
        super(ModBlockEntityType.ITEM_DISPLAY_TYPE.get(), pos, state);
        this.items = NonNullList.withSize(27, ItemStack.EMPTY);
        this.wallShelf = s;
        this.playSound = p;
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items);
        }
        // Init render items...
        int idx = 0;
        for (ItemStack item : this.items){
            if (!item.is(Items.AIR)){
                renderItems[idx] = item.copy();
                idx++;
                if (idx >= 4)
                    break;
            }
        }
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory) {
        return ChestMenu.threeRows(windowID, playerInventory, this);
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
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("block." + Nekoration.MODID + ".cupboard");
    }

    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    private void updateBlockState(BlockState state, boolean open) {
        this.level.setBlock(this.getBlockPos(), state.setValue(ItemDisplayBlock.OPEN, Boolean.valueOf(open)), 3);
    }

    private void playSound(BlockState state, SoundEvent sound) {
        Vec3i vector3i = state.getValue(ItemDisplayBlock.FACING).getNormal();
        double d0 = (double) this.worldPosition.getX() + 0.5D + (double) vector3i.getX() / 2.0D;
        double d1 = (double) this.worldPosition.getY() + 0.5D + (double) vector3i.getY() / 2.0D;
        double d2 = (double) this.worldPosition.getZ() + 0.5D + (double) vector3i.getZ() / 2.0D;
        this.level.playSound((Player) null, d0, d1, d2, sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }
}