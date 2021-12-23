package com.devbobcorn.nekoration.blocks.entities;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.blocks.WallShelfBlock;
import com.devbobcorn.nekoration.blocks.CabinetBlock;
import com.devbobcorn.nekoration.blocks.CupboardBlock;
import com.devbobcorn.nekoration.blocks.EaselMenuBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityType {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Nekoration.MODID);

    public static final RegistryObject<BlockEntityType<EaselMenuBlockEntity>> EASEL_MENU_TYPE = TILE_ENTITY_TYPES.register("easel_menu", () -> BlockEntityType.Builder.of((pos, state) -> new EaselMenuBlockEntity(((EaselMenuBlock) state.getBlock()).white, pos, state), ModBlocks.EASEL_MENU.get(), ModBlocks.EASEL_MENU_WHITE.get()).build(null));
    public static final RegistryObject<BlockEntityType<PhonographBlockEntity>> PHONOGRAPH_TYPE = TILE_ENTITY_TYPES.register("phonograph", () -> BlockEntityType.Builder.of(PhonographBlockEntity::new, ModBlocks.PHONOGRAPH.get()).build(null));
    public static final RegistryObject<BlockEntityType<CustomBlockEntity>> CUSTOM_TYPE = TILE_ENTITY_TYPES.register("custom", () -> BlockEntityType.Builder.of(CustomBlockEntity::new, ModBlocks.CUSTOM.get()).build(null));
    public static final RegistryObject<BlockEntityType<PrismapTableBlockEntity>> PRISMAP_TABLE_TYPE = TILE_ENTITY_TYPES.register("prismap_table", () -> BlockEntityType.Builder.of(PrismapTableBlockEntity::new, ModBlocks.PRISMAP_TABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CabinetBlockEntity>> CABINET_TYPE = TILE_ENTITY_TYPES.register("cabinet", () -> BlockEntityType.Builder.of((pos, state) -> new CabinetBlockEntity(pos, state, ((CabinetBlock) state.getBlock()).large), ModBlocks.DRAWER.get(), ModBlocks.CABINET.get(), ModBlocks.DRAWER_CHEST.get()).build(null));
    public static final RegistryObject<BlockEntityType<ItemDisplayBlockEntity>> ITEM_DISPLAY_TYPE = TILE_ENTITY_TYPES.register("item_display", () -> BlockEntityType.Builder.of((pos, state) -> {
        Block bloc = state.getBlock();
        if (bloc instanceof WallShelfBlock)
            return new ItemDisplayBlockEntity(pos, state, true, false);
        else if (bloc instanceof CupboardBlock)
            return new ItemDisplayBlockEntity(pos, state, false, ((CupboardBlock) bloc).playSound);
        return new ItemDisplayBlockEntity(pos, state, false, false);
    }, ModBlocks.CUPBOARD.get(), ModBlocks.SHELF.get(), ModBlocks.WALL_SHELF.get()).build(null));
}
