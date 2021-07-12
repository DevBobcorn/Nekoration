package com.devbobcorn.nekoration.blocks.containers;

import com.devbobcorn.nekoration.Nekoration;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.extensions.IForgeContainerType;

public class ModContainerType {
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Nekoration.MODID);

    public static final RegistryObject<ContainerType<EaselMenuContainer>> EASEL_MENU_TYPE = CONTAINER_TYPES.register("easel_menu", () -> IForgeContainerType.create(EaselMenuContainer::createContainerClientSide));
}
