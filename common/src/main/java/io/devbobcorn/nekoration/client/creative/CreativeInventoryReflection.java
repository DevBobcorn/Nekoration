package io.devbobcorn.nekoration.client.creative;

import java.lang.reflect.Field;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;

import org.jetbrains.annotations.Nullable;

/**
 * Reads {@link CreativeModeInventoryScreen}'s private static selected tab, and
 * the container screen's gui origin (left/top), which are protected in vanilla
 * and have no public accessor. Works on every loader (mojmap names at runtime).
 */
public final class CreativeInventoryReflection {
    @Nullable
    private static final Field SELECTED_TAB_FIELD;
    @Nullable
    private static final Field LEFT_POS_FIELD;
    @Nullable
    private static final Field TOP_POS_FIELD;

    static {
        Field selectedTab = null;
        Field leftPos = null;
        Field topPos = null;
        try {
            selectedTab = CreativeModeInventoryScreen.class.getDeclaredField("selectedTab");
            selectedTab.setAccessible(true);
        } catch (ReflectiveOperationException ignored) {
        }
        try {
            leftPos = AbstractContainerScreen.class.getDeclaredField("leftPos");
            leftPos.setAccessible(true);
            topPos = AbstractContainerScreen.class.getDeclaredField("topPos");
            topPos.setAccessible(true);
        } catch (ReflectiveOperationException ignored) {
        }
        SELECTED_TAB_FIELD = selectedTab;
        LEFT_POS_FIELD = leftPos;
        TOP_POS_FIELD = topPos;
    }

    private CreativeInventoryReflection() {
    }

    @Nullable
    public static CreativeModeTab getSelectedTab() {
        if (SELECTED_TAB_FIELD == null) {
            return null;
        }
        try {
            return (CreativeModeTab) SELECTED_TAB_FIELD.get(null);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    public static int getGuiLeft(AbstractContainerScreen<?> screen) {
        if (LEFT_POS_FIELD == null) {
            return 0;
        }
        try {
            return LEFT_POS_FIELD.getInt(screen);
        } catch (ReflectiveOperationException e) {
            return 0;
        }
    }

    public static int getGuiTop(AbstractContainerScreen<?> screen) {
        if (TOP_POS_FIELD == null) {
            return 0;
        }
        try {
            return TOP_POS_FIELD.getInt(screen);
        } catch (ReflectiveOperationException e) {
            return 0;
        }
    }
}
