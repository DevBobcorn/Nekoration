package com.devbobcorn.nekoration.blocks.entities;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.ModBlocks;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityType {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Nekoration.MODID);

    public static final RegistryObject<TileEntityType<EaselMenuBlockEntity>> EASEL_MENU_TYPE = TILE_ENTITY_TYPES.register("easel_menu", () -> TileEntityType.Builder.of(EaselMenuBlockEntity::new, ModBlocks.EASEL_MENU.get()).build(null));
}
