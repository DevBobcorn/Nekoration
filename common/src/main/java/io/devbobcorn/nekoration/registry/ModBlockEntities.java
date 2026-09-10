package io.devbobcorn.nekoration.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import io.devbobcorn.nekoration.xplat.NekoRegistrar;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;
import io.devbobcorn.nekoration.blocks.entities.CabinetBlockEntity;
import io.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;
import io.devbobcorn.nekoration.blocks.entities.ItemDisplayBlockEntity;

public final class ModBlockEntities {
    public static RegistrySupplier<BlockEntityType<CabinetBlockEntity>> CABINET;
    public static RegistrySupplier<BlockEntityType<ItemDisplayBlockEntity>> ITEM_DISPLAY;
    public static RegistrySupplier<BlockEntityType<EaselMenuBlockEntity>> EASEL_MENU;

    private ModBlockEntities() {
    }

    public static void register(NekoRegistrar registrar) {
        CABINET = registrar.register(Registries.BLOCK_ENTITY_TYPE, "cabinet", () -> BlockEntityType.Builder
                .of((pos, state) -> new CabinetBlockEntity(pos, state), WoodenBlockRegistration.cabinetBlocksForEntity())
                .build(null));
        ITEM_DISPLAY = registrar.register(Registries.BLOCK_ENTITY_TYPE, "item_display", () -> BlockEntityType.Builder
                .of((pos, state) -> new ItemDisplayBlockEntity(pos, state), WoodenBlockRegistration.itemDisplayBlocksForEntity())
                .build(null));
        EASEL_MENU = registrar.register(Registries.BLOCK_ENTITY_TYPE, "easel_menu", () -> BlockEntityType.Builder
                .of((pos, state) -> new EaselMenuBlockEntity(pos, state), WoodenBlockRegistration.easelMenuBlocksForEntity())
                .build(null));
    }
}
