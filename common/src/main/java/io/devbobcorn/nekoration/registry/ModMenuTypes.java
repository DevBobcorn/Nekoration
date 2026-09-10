package io.devbobcorn.nekoration.registry;

import net.minecraft.world.inventory.MenuType;
import io.devbobcorn.nekoration.blocks.containers.EaselMenuMenu;
import io.devbobcorn.nekoration.xplat.NekoRegistrar;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;

public final class ModMenuTypes {
    public static RegistrySupplier<MenuType<EaselMenuMenu>> EASEL_MENU;

    private ModMenuTypes() {
    }

    public static void register(NekoRegistrar registrar) {
        // The extended menu type carries the easel block position as opening data
        // (encoded by each platform's extended-menu mechanism).
        EASEL_MENU = registrar.menuType("easel_menu", EaselMenuMenu::fromPosition);
    }
}
