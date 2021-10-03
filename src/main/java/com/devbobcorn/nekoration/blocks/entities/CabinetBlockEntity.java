package com.devbobcorn.nekoration.blocks.entities;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.CabinetBlock;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class CabinetBlockEntity extends RandomizableContainerBlockEntity {
	public final boolean large;
    private ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(Level world, BlockPos pos, BlockState state) {
            CabinetBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
            CabinetBlockEntity.this.updateBlockState(state, true);
        }

        protected void onClose(Level world, BlockPos pos, BlockState state) {
            CabinetBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
            CabinetBlockEntity.this.updateBlockState(state, false);
        }

        protected void openerCountChanged(Level world, BlockPos pos, BlockState state, int a, int b) {
        }

        protected boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof ChestMenu) {
                Container container = ((ChestMenu) player.containerMenu).getContainer();
                return container == CabinetBlockEntity.this;
            } else {
                return false;
            }
        }
    };
	private NonNullList<ItemStack> items;

	public CabinetBlockEntity(BlockPos pos, BlockState state) {
		this(pos, state, true);
	}

	public CabinetBlockEntity(BlockPos pos, BlockState state, boolean l) {
		super(ModBlockEntityType.CABINET_TYPE.get(), pos, state);
		this.large = l;
		this.items = NonNullList.withSize((l ? 6 : 3) * 9, ItemStack.EMPTY);
	}

	public CompoundTag save(CompoundTag tag) {
		super.save(tag);
		if (!this.trySaveLootTable(tag)) {
			ContainerHelper.saveAllItems(tag, this.items);
		}
		return tag;
	}

	public void load(CompoundTag tag) {
		super.load(tag);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(tag)) {
			ContainerHelper.loadAllItems(tag, this.items);
		}
	}

	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return new ClientboundBlockEntityDataPacket(this.worldPosition, 2025, this.getUpdateTag());
	}

	public CompoundTag getUpdateTag() {
		return this.save(new CompoundTag());
	}

	public boolean onlyOpCanSetNbt() {
		return true;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory) {
		if (large)
			return ChestMenu.sixRows(windowID, playerInventory, this);
		else
			return ChestMenu.threeRows(windowID, playerInventory, this);
	}

	@Override
	public int getContainerSize() {
		return (large ? 6 : 3) * 9;
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
		return new TranslatableComponent("block." + Nekoration.MODID + ".cabinet");
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
		this.level.setBlock(this.getBlockPos(), state.setValue(CabinetBlock.OPEN, Boolean.valueOf(open)), 3);
	}

	private void playSound(BlockState state, SoundEvent sound) {
		Vec3i vector3i = state.getValue(CabinetBlock.FACING).getNormal();
		double d0 = (double) this.worldPosition.getX() + 0.5D + (double) vector3i.getX() / 2.0D;
		double d1 = (double) this.worldPosition.getY() + 0.5D + (double) vector3i.getY() / 2.0D;
		double d2 = (double) this.worldPosition.getZ() + 0.5D + (double) vector3i.getZ() / 2.0D;
		this.level.playSound((Player) null, d0, d1, d2, sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
	}
}