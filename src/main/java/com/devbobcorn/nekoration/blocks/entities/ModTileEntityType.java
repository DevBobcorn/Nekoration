package com.devbobcorn.nekoration.blocks.entities;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.ModBlocks;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityType {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Nekoration.MODID);

    public static final RegistryObject<TileEntityType<EaselMenuBlockEntity>> EASEL_MENU_TYPE = TILE_ENTITY_TYPES.register("easel_menu", () -> TileEntityType.Builder.of(EaselMenuBlockEntity::new, ModBlocks.EASEL_MENU.get(), ModBlocks.EASEL_MENU_WHITE.get()).build(null));
    public static final RegistryObject<TileEntityType<PhonographBlockEntity>> PHONOGRAPH_TYPE = TILE_ENTITY_TYPES.register("phonograph", () -> TileEntityType.Builder.of(PhonographBlockEntity::new, ModBlocks.PHONOGRAPH.get()).build(null));
    public static final RegistryObject<TileEntityType<CustomBlockEntity>> CUSTOM_TYPE = TILE_ENTITY_TYPES.register("custom", () -> TileEntityType.Builder.of(CustomBlockEntity::new, ModBlocks.CUSTOM.get()).build(null));
}
