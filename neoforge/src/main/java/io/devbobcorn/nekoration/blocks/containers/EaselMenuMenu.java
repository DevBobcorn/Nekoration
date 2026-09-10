package io.devbobcorn.nekoration.blocks.containers;

import io.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;
import io.devbobcorn.nekoration.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Server/client menu for {@link EaselMenuBlockEntity}.
 */
public class EaselMenuMenu extends AbstractContainerMenu {
    public static final int SLOT_COUNT = EaselMenuBlockEntity.NUMBER_OF_SLOTS;

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_ROW_COUNT * PLAYER_INVENTORY_COLUMN_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;

    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int EASEL_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private final EaselMenuBlockEntity easel;

    public EaselMenuMenu(int containerId, Inventory playerInventory, EaselMenuBlockEntity easel) {
        super(ModMenuTypes.EASEL_MENU.get(), containerId);
        this.easel = easel;

        final int slotXSpacing = 18;
        final int slotYSpacing = 18;

        final int hotbarX = 8;
        final int hotbarY = 198;
        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
            addSlot(new Slot(playerInventory, x, hotbarX + slotXSpacing * x, hotbarY));
        }

        final int playerInventoryX = 8;
        final int playerInventoryY = 140;
        for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
                int slotIndex = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
                addSlot(new Slot(playerInventory, slotIndex, playerInventoryX + x * slotXSpacing,
                        playerInventoryY + y * slotYSpacing));
            }
        }

        final int easelInventoryX = 8;
        final int easelInventoryY = 18;
        for (int i = 0; i < SLOT_COUNT; i++) {
            int xOffset = i > 3 ? i + 1 : i;
            addSlot(new Slot(easel, i, easelInventoryX + slotXSpacing * xOffset, easelInventoryY));
        }
    }

    public EaselMenuMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, readBlockEntity(playerInventory, buffer));
    }

    public EaselMenuBlockEntity getEasel() {
        return easel;
    }

    @Override
    public boolean stillValid(Player player) {
        return easel.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int sourceSlotIndex) {
        Slot sourceSlot = slots.get(sourceSlotIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, EASEL_FIRST_SLOT_INDEX, EASEL_FIRST_SLOT_INDEX + SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (sourceSlotIndex >= EASEL_FIRST_SLOT_INDEX && sourceSlotIndex < EASEL_FIRST_SLOT_INDEX + SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    private static EaselMenuBlockEntity readBlockEntity(Inventory inventory, FriendlyByteBuf buffer) {
        if (buffer == null) {
            throw new IllegalStateException("Missing menu open data for EaselMenuMenu");
        }
        BlockPos pos = buffer.readBlockPos();
        Level level = inventory.player.level();
        if (level.getBlockEntity(pos) instanceof EaselMenuBlockEntity easel) {
            return easel;
        }
        throw new IllegalStateException("Expected EaselMenuBlockEntity at " + pos);
    }
}
