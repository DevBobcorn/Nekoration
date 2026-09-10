package io.devbobcorn.nekoration.client.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Public mirror of vanilla's package-private
 * {@code MenuScreens.ScreenConstructor}, so common code can describe screen
 * factories without reaching into loader internals.
 */
@FunctionalInterface
public interface MenuScreenConstructor<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> {
    U create(M menu, net.minecraft.world.entity.player.Inventory inventory, net.minecraft.network.chat.Component title);
}
