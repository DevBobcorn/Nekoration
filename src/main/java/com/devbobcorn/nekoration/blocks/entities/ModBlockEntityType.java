package com.devbobcorn.nekoration.blocks.entities;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.ModBlocks;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlockEntityType {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Nekoration.MODID);

    public static final RegistryObject<BlockEntityType<EaselMenuBlockEntity>> EASEL_MENU_TYPE = TILE_ENTITY_TYPES.register("easel_menu", () -> BlockEntityType.Builder.of(EaselMenuBlockEntity::new, ModBlocks.EASEL_MENU.get(), ModBlocks.EASEL_MENU_WHITE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CustomBlockEntity>> CUSTOM_TYPE = TILE_ENTITY_TYPES.register("custom", () -> BlockEntityType.Builder.of(CustomBlockEntity::new, ModBlocks.CUSTOM.get()).build(null));
}
