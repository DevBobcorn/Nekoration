package io.devbobcorn.nekoration.registry;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.containers.EaselMenuMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTER =
            DeferredRegister.create(Registries.MENU, Nekoration.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<EaselMenuMenu>> EASEL_MENU =
            REGISTER.register("easel_menu", () -> IMenuTypeExtension.create(EaselMenuMenu::new));

    private ModMenuTypes() {
    }
}
