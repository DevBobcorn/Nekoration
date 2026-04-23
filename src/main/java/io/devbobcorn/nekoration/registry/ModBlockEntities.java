package io.devbobcorn.nekoration.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.entities.CabinetBlockEntity;
import io.devbobcorn.nekoration.blocks.entities.ItemDisplayBlockEntity;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Nekoration.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CabinetBlockEntity>> CABINET =
            REGISTER.register("cabinet", () -> BlockEntityType.Builder
                    .of(CabinetBlockEntity::new, WoodenBlockRegistration.cabinetBlocksForEntity())
                    .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDisplayBlockEntity>> ITEM_DISPLAY =
            REGISTER.register("item_display", () -> BlockEntityType.Builder
                    .of(ItemDisplayBlockEntity::new, WoodenBlockRegistration.itemDisplayBlocksForEntity())
                    .build(null));

    private ModBlockEntities() {
    }
}
