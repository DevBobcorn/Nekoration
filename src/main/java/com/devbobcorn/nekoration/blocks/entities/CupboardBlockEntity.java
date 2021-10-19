package com.devbobcorn.nekoration.blocks.entities;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.CupboardBlock;
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
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.network.PacketDistributor;


public class CupboardBlockEntity extends RandomizableContainerBlockEntity {
	private final ItemStack airStack = ItemStack.EMPTY;
	public ItemStack[] renderItems = { airStack, airStack, airStack, airStack };

    private ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(Level world, BlockPos pos, BlockState state) {
            CupboardBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
            CupboardBlockEntity.this.updateBlockState(state, true);
        }

        protected void onClose(Level world, BlockPos pos, BlockState state) {
            CupboardBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
            CupboardBlockEntity.this.updateBlockState(state, false);
			System.out.println("Cupboard Closes... Client: " + world.isClientSide);
			//ItemStack[] its = { new ItemStack(Items.COOKIE), new ItemStack(Items.APPLE), new ItemStack(Items.CAKE), new ItemStack(Items.HONEY_BOTTLE) };
			ItemStack[] its = { airStack, airStack, airStack, airStack };
			// Find out the 4 items to display...
			int idx = 0;
			for (ItemStack item : CupboardBlockEntity.this.items){
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
                return container == CupboardBlockEntity.this;
            } else {
                return false;
            }
        }
    };
	private NonNullList<ItemStack> items;

	public CupboardBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityType.CUPBOARD_TYPE.get(), pos, state);
		this.items = NonNullList.withSize(27, ItemStack.EMPTY);
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
		return new ClientboundBlockEntityDataPacket(this.worldPosition, 2028, this.getUpdateTag());
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
		this.level.setBlock(this.getBlockPos(), state.setValue(CupboardBlock.OPEN, Boolean.valueOf(open)), 3);
	}

	private void playSound(BlockState state, SoundEvent sound) {
		Vec3i vector3i = state.getValue(CupboardBlock.FACING).getNormal();
		double d0 = (double) this.worldPosition.getX() + 0.5D + (double) vector3i.getX() / 2.0D;
		double d1 = (double) this.worldPosition.getY() + 0.5D + (double) vector3i.getY() / 2.0D;
		double d2 = (double) this.worldPosition.getZ() + 0.5D + (double) vector3i.getZ() / 2.0D;
		this.level.playSound((Player) null, d0, d1, d2, sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
	}
}