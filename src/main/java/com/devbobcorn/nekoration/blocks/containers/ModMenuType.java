package com.devbobcorn.nekoration.blocks.containers;

import com.devbobcorn.nekoration.Nekoration;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModMenuType {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Nekoration.MODID);

    public static final RegistryObject<MenuType<EaselMenuMenu>> EASEL_MENU_TYPE = MENU_TYPES.register("easel_menu", () -> IForgeContainerType.create(EaselMenuMenu::new));
}
