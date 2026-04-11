package io.devbobcorn.nekoration.client.creative;

import java.lang.reflect.Field;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;

import org.jetbrains.annotations.Nullable;

/**
 * Reads {@link CreativeModeInventoryScreen}'s private static selected tab (NeoForge / vanilla).
 */
public final class CreativeInventoryReflection {
    @Nullable
    private static final Field SELECTED_TAB_FIELD;

    static {
        Field f = null;
        try {
            f = CreativeModeInventoryScreen.class.getDeclaredField("selectedTab");
            f.setAccessible(true);
        } catch (ReflectiveOperationException ignored) {
        }
        SELECTED_TAB_FIELD = f;
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
}
