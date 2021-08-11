package com.devbobcorn.nekoration.blocks.containers;

import com.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

public class EaselMenuMenu extends AbstractContainerMenu {
	public final EaselMenuBlockEntity easel;

	private static final int HOTBAR_SLOT_COUNT = 9;
	private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
	private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
	private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
	private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;

	private static final int VANILLA_FIRST_SLOT_INDEX = 0;
	private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
	private static final int TE_INVENTORY_SLOT_COUNT = EaselMenuBlockEntity.NUMBER_OF_SLOTS; // must match EaselMenuBlockEntity.NUMBER_OF_SLOTS

	public static final int TILE_INVENTORY_YPOS = 18; // the ContainerScreenBasic needs to know these so it can tell where to draw the Tiles
	public static final int PLAYER_INVENTORY_YPOS = 140;

	// Create Container(Menu) Server Side
	public EaselMenuMenu(int windowId, Inventory playerInventory, EaselMenuBlockEntity blockEntity){
		super(ModMenuType.EASEL_MENU_TYPE.get(), windowId);
		this.easel = blockEntity;

		PlayerInvWrapper playerInventoryForge = new PlayerInvWrapper(playerInventory); // wrap the IInventory in a Forge IItemHandler.
		// Not actually necessary - can use Slot(playerInventory) instead of SlotItemHandler(playerInventoryForge)

		final int SLOT_X_SPACING = 18;
		final int SLOT_Y_SPACING = 18;
		final int HOTBAR_XPOS = 8;
		final int HOTBAR_YPOS = 198;
		// Add the players hotbar to the gui - the [xpos, ypos] location of each item
		for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
			int slotNumber = x;
			addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * x,
					HOTBAR_YPOS));
		}

		final int PLAYER_INVENTORY_XPOS = 8;
		// Add the rest of the player's inventory to the gui
		for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
			for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
				int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
				int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
				int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
				addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, xpos, ypos));
			}
		}

		final int TILE_INVENTORY_XPOS = 8;
		// Add the tile inventory container to the gui
		for (int x = 0; x < TE_INVENTORY_SLOT_COUNT; x++) {
			int slotNumber = x;
			addSlot(new Slot(easel, slotNumber, TILE_INVENTORY_XPOS + SLOT_X_SPACING * (x > 3 ? x + 1 : x), TILE_INVENTORY_YPOS));
            // 0 1 2 3 _ 4 5 6 7, leave an empty space in the middle....
		}
	}

	// Create Container(Menu) Client Side
	public EaselMenuMenu(int windowId, Inventory playerInventory, FriendlyByteBuf buf) {
		this(windowId, playerInventory, (EaselMenuBlockEntity) playerInventory.player.level.getBlockEntity(buf.readBlockPos()));
	}

	@Override
	public boolean stillValid(Player playerEntity) {
		return easel.stillValid(playerEntity);
	}

	@Override
	public ItemStack quickMoveStack(Player playerEntity, int sourceSlotIndex) {
		Slot sourceSlot = slots.get(sourceSlotIndex);
		if (sourceSlot == null || !sourceSlot.hasItem())
			return ItemStack.EMPTY; // EMPTY_ITEM
		ItemStack sourceStack = sourceSlot.getItem();
		ItemStack copyOfSourceStack = sourceStack.copy();

		// Check if the slot clicked is one of the vanilla container slots
		if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX
				&& sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
			// This is a vanilla container slot so merge the stack into the tile inventory
			if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX,
					TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
				return ItemStack.EMPTY; // EMPTY_ITEM
			}
		} else if (sourceSlotIndex >= TE_INVENTORY_FIRST_SLOT_INDEX
				&& sourceSlotIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
			// This is a TE slot so merge the stack into the players inventory
			if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT,
					false)) {
				return ItemStack.EMPTY;
			}
		} else {
			LOGGER.warn("Invalid slotIndex:" + sourceSlotIndex);
			return ItemStack.EMPTY;
		}

		// If stack size == 0 (the entire stack was moved) set slot contents to null
		if (sourceStack.getCount() == 0) {
			sourceSlot.set(ItemStack.EMPTY);
		} else {
			sourceSlot.setChanged(); //?
		}

		sourceSlot.onTake(playerEntity, sourceStack);
		return copyOfSourceStack;
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
	}

	private static final Logger LOGGER = LogManager.getLogger();
}
