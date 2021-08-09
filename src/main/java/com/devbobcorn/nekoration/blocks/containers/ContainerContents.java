package com.devbobcorn.nekoration.blocks.containers;

import java.util.function.Predicate;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;

public class ContainerContents implements IInventory {
	BlockEntity tien;

	public static ContainerContents createForTileEntity(int size, Predicate<PlayerEntity> canPlayerAccessInventoryLambda,
			Notify markDirtyNotificationLambda, TileEntity te) {
		return new ContainerContents(size, canPlayerAccessInventoryLambda, markDirtyNotificationLambda, te);
	}

	public static ContainerContents createForClientSideContainer(int size) {
		return new ContainerContents(size);
	}

	// ----Methods used to load / save the contents to NBT
	public CompoundNBT serializeNBT() {
		return contents.serializeNBT();
	}

	public void deserializeNBT(CompoundNBT nbt) {
		contents.deserializeNBT(nbt);
	}

	// ------------- linking methods -------------
	// The following group of methods are used to establish a link between the
	// parent TileEntity and the chest contents,
	// so that the container can communicate with the parent TileEntity without
	// having to talk to it directly.
	// This is important because the link to the TileEntity only exists on the
	// server side. On the client side, the
	// container gets a dummy link instead- there is no link to the client
	// TileEntity. Linking to the client TileEntity
	// is prohibited because of synchronisation clashes, i.e. vanilla would attempt
	// to synchronise the TileEntity in two
	// different ways at the same time: via the tileEntity server->client packets
	// and via the container directly poking
	// around in the inventory contents.
	// I've used lambdas to make the decoupling more explicit. You could instead
	// * provide an Optional TileEntity to the ContainerContents constructor (and ignore
	// the markDirty() etc calls), or
	// * implement IInventory directly in your TileEntity, and construct your
	// client-side container using an Inventory
	// instead of passing it a TileEntity. (This is how vanilla does it)
	//

	/**
	 * sets the function that the container should call in order to decide if the
	 * given player can access the container's contents not. The lambda function is
	 * only used on the server side
	 */
	public void setCanPlayerAccessInventoryLambda(Predicate<PlayerEntity> canPlayerAccessInventoryLambda) {
		this.canPlayerAccessInventoryLambda = canPlayerAccessInventoryLambda;
	}

	// the function that the container should call in order to tell the parent
	// TileEntity that the
	// contents of its inventory have been changed.
	// default is "do nothing"
	public void setMarkDirtyNotificationLambda(Notify markDirtyNotificationLambda) {
		this.markDirtyNotificationLambda = markDirtyNotificationLambda;
	}

	// the function that the container should call in order to tell the parent
	// TileEntity that the
	// container has been opened by a player (eg so that the chest can animate its
	// lid being opened)
	// default is "do nothing"
	public void setOpenInventoryNotificationLambda(Notify openInventoryNotificationLambda) {
		this.openInventoryNotificationLambda = openInventoryNotificationLambda;
	}

	// the function that the container should call in order to tell the parent
	// TileEntity that the
	// container has been closed by a player
	// default is "do nothing"
	public void setCloseInventoryNotificationLambda(Notify closeInventoryNotificationLambda) {
		this.closeInventoryNotificationLambda = closeInventoryNotificationLambda;
	}

	// ---------- These methods are used by the container to ask whether certain
	// actions are permitted
	// If you need special behaviour (eg a chest can only be used by a particular
	// player) then either modify this method
	// or ask the parent TileEntity.

	@Override
	public boolean stillValid(Player player) {
		return canPlayerAccessInventoryLambda.test(player); // on the client, this does nothing. on the server, ask our
															// parent TileEntity.
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return contents.isItemValid(index, stack);
	}

	// ----- Methods used to inform the parent tile entity that something has
	// happened to the contents
	// you can make direct calls to the parent if you like, I've used lambdas
	// because I think it shows the separation
	// of responsibilities more clearly.

	@FunctionalInterface
	public interface Notify { // Some folks use Runnable, but I prefer not to use it for non-thread-related tasks
		void invoke();
	}

	@Override
	public void setChanged() {
		markDirtyNotificationLambda.invoke();
	}

	@Override
	public void startOpen(Player player) {
		openInventoryNotificationLambda.invoke();
	}

	@Override
	public void stopOpen(Player player) {
		closeInventoryNotificationLambda.invoke();
	}

	// ---------These following methods are called by Vanilla container methods to
	// manipulate the inventory contents ---

	@Override
	public int getContainerSize() {
		return contents.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < contents.getSlots(); ++i) {
			if (!contents.getStackInSlot(i).isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public ItemStack getItem(int index) {
		return contents.getStackInSlot(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return contents.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		int maxPossibleItemStackSize = contents.getSlotLimit(index);
		return contents.extractItem(index, maxPossibleItemStackSize, false);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		contents.setStackInSlot(index, stack);
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < contents.getSlots(); ++i) {
			contents.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	// ---------

	private ContainerContents(int size) {
		this.contents = new ItemStackHandler(size);
	}

	private ContainerContents(int size, Predicate<PlayerEntity> canPlayerAccessInventoryLambda,
			Notify markDirtyNotificationLambda, TileEntity te) {
		this.contents = new ItemStackHandler(size);
		this.canPlayerAccessInventoryLambda = canPlayerAccessInventoryLambda;
		this.markDirtyNotificationLambda = markDirtyNotificationLambda;
		this.tien = te;
	}

	// the function that the container should call in order to decide if the
	// given player can access the container's Inventory or not. Only valid server
	// side
	// default is "true".
	private Predicate<PlayerEntity> canPlayerAccessInventoryLambda = x -> true;

	// the function that the container should call in order to tell the parent
	// TileEntity that the
	// contents of its inventory have been changed.
	// default is "do nothing"
	private Notify markDirtyNotificationLambda = () -> {
	};

	// the function that the container should call in order to tell the parent
	// TileEntity that the
	// container has been opened by a player (eg so that the chest can animate its
	// lid being opened)
	// default is "do nothing"
	private Notify openInventoryNotificationLambda = () -> {
	};

	// the function that the container should call in order to tell the parent
	// TileEntity that the
	// container has been closed by a player
	// default is "do nothing"
	private Notify closeInventoryNotificationLambda = () -> {
		System.out.println("Contents Closed!");
		/*
		if (tien.getLevel().isClientSide)
			System.out.println("This is Client Side.");
		else System.out.println("This is Server Side.");
		*/
	};

	private final ItemStackHandler contents;
}
