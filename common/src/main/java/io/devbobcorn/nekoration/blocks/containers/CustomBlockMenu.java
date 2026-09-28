package io.devbobcorn.nekoration.blocks.containers;

import io.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import io.devbobcorn.nekoration.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Empty container menu that backs the Custom Block entry selection screen.
 */
public class CustomBlockMenu extends AbstractContainerMenu {
    private final CustomBlockEntity customBlock;

    public CustomBlockMenu(int containerId, Inventory playerInventory, CustomBlockEntity customBlock) {
        super(ModMenuTypes.CUSTOM_BLOCK.get(), containerId);
        this.customBlock = customBlock;
    }

    /** Menu type factory: resolves the custom block entity from the opening position. */
    public static CustomBlockMenu fromPosition(int containerId, Inventory playerInventory, BlockPos pos) {
        if (pos == null) {
            throw new IllegalStateException("Missing menu open data for CustomBlockMenu");
        }
        Level level = playerInventory.player.level();
        if (level.getBlockEntity(pos) instanceof CustomBlockEntity customBlock) {
            return new CustomBlockMenu(containerId, playerInventory, customBlock);
        }
        throw new IllegalStateException("Expected CustomBlockEntity at " + pos);
    }

    public CustomBlockEntity getCustomBlock() {
        return customBlock;
    }

    @Override
    public boolean stillValid(Player player) {
        return customBlock.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
