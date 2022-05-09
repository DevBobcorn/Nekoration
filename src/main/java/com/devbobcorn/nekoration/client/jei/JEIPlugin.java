package com.devbobcorn.nekoration.client.jei;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.items.DyeableBlockItem;
import com.devbobcorn.nekoration.items.DyeableWoodenBlockItem;
import com.devbobcorn.nekoration.items.HalfTimberBlockItem;
import com.devbobcorn.nekoration.recipes.NekoColorInheritRecipe;
import com.devbobcorn.nekoration.recipes.NekoShapedRecipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final ResourceLocation UID = new ResourceLocation(Nekoration.MODID, "jei_nekoration_plugin");
    private static final Logger LOGGER = LogManager.getLogger("JEI Plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }
    
    public void registerItemSubtypes(ISubtypeRegistration registration){
        // Wooden
        registration.registerSubtypeInterpreter(ModBlocks.HALF_TIMBER_P0.get().asItem(), (stack, ctx) -> String.valueOf(HalfTimberBlockItem.getColor0(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.HALF_TIMBER_P1.get().asItem(), (stack, ctx) -> String.valueOf(HalfTimberBlockItem.getColor0(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.HALF_TIMBER_P2.get().asItem(), (stack, ctx) -> String.valueOf(HalfTimberBlockItem.getColor0(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.HALF_TIMBER_P3.get().asItem(), (stack, ctx) -> String.valueOf(HalfTimberBlockItem.getColor0(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.HALF_TIMBER_P4.get().asItem(), (stack, ctx) -> String.valueOf(HalfTimberBlockItem.getColor0(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.HALF_TIMBER_P5.get().asItem(), (stack, ctx) -> String.valueOf(HalfTimberBlockItem.getColor0(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.HALF_TIMBER_P6.get().asItem(), (stack, ctx) -> String.valueOf(HalfTimberBlockItem.getColor0(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.HALF_TIMBER_P7.get().asItem(), (stack, ctx) -> String.valueOf(HalfTimberBlockItem.getColor0(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.HALF_TIMBER_P8.get().asItem(), (stack, ctx) -> String.valueOf(HalfTimberBlockItem.getColor0(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.HALF_TIMBER_P9.get().asItem(), (stack, ctx) -> String.valueOf(HalfTimberBlockItem.getColor0(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.HALF_TIMBER_PILLAR_P0.get().asItem(), (stack, ctx) -> String.valueOf(HalfTimberBlockItem.getColor0(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.HALF_TIMBER_PILLAR_P1.get().asItem(), (stack, ctx) -> String.valueOf(HalfTimberBlockItem.getColor0(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.HALF_TIMBER_PILLAR_P2.get().asItem(), (stack, ctx) -> String.valueOf(HalfTimberBlockItem.getColor0(stack)));
        // Stone - Ignored
        // Window - Stone Stuff Ignored
        registration.registerSubtypeInterpreter(ModBlocks.WINDOW_SIMPLE.get().asItem(), (stack, ctx) -> String.valueOf(DyeableBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.WINDOW_ARCH.get().asItem(), (stack, ctx) -> String.valueOf(DyeableBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.WINDOW_CROSS.get().asItem(), (stack, ctx) -> String.valueOf(DyeableBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.WINDOW_SHADE.get().asItem(), (stack, ctx) -> String.valueOf(DyeableBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.WINDOW_LANCET.get().asItem(), (stack, ctx) -> String.valueOf(DyeableBlockItem.getColor(stack)));
        // Decorations
        // - Planter - Ignored
        // - Awning
        registration.registerSubtypeInterpreter(ModBlocks.AWNING_PURE.get().asItem(), (stack, ctx) -> String.valueOf(DyeableBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.AWNING_STRIPE.get().asItem(), (stack, ctx) -> String.valueOf(DyeableBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.AWNING_PURE_SHORT.get().asItem(), (stack, ctx) -> String.valueOf(DyeableBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.AWNING_STRIPE_SHORT.get().asItem(), (stack, ctx) -> String.valueOf(DyeableBlockItem.getColor(stack)));
        // - Furniture
        registration.registerSubtypeInterpreter(ModBlocks.GLASS_TABLE.get().asItem(), (stack, ctx) -> String.valueOf(DyeableWoodenBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.GLASS_ROUND_TABLE.get().asItem(), (stack, ctx) -> String.valueOf(DyeableWoodenBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.ARM_CHAIR.get().asItem(), (stack, ctx) -> String.valueOf(DyeableWoodenBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.BENCH.get().asItem(), (stack, ctx) -> String.valueOf(DyeableWoodenBlockItem.getColor(stack)));
        // - Container
        registration.registerSubtypeInterpreter(ModBlocks.EASEL_MENU.get().asItem(), (stack, ctx) -> String.valueOf(DyeableWoodenBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.EASEL_MENU_WHITE.get().asItem(), (stack, ctx) -> String.valueOf(DyeableWoodenBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.DRAWER.get().asItem(), (stack, ctx) -> String.valueOf(DyeableWoodenBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.CABINET.get().asItem(), (stack, ctx) -> String.valueOf(DyeableWoodenBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.DRAWER_CHEST.get().asItem(), (stack, ctx) -> String.valueOf(DyeableWoodenBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.CUPBOARD.get().asItem(), (stack, ctx) -> String.valueOf(DyeableWoodenBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.SHELF.get().asItem(), (stack, ctx) -> String.valueOf(DyeableWoodenBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.WALL_SHELF.get().asItem(), (stack, ctx) -> String.valueOf(DyeableWoodenBlockItem.getColor(stack)));
        // - Misc
        registration.registerSubtypeInterpreter(ModBlocks.CANDLE_HOLDER_GOLD.get().asItem(), (stack, ctx) -> String.valueOf(DyeableBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.CANDLE_HOLDER_IRON.get().asItem(), (stack, ctx) -> String.valueOf(DyeableBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.CANDLE_HOLDER_QUARTZ.get().asItem(), (stack, ctx) -> String.valueOf(DyeableBlockItem.getColor(stack)));
        registration.registerSubtypeInterpreter(ModBlocks.WINDOW_PLANT.get().asItem(), (stack, ctx) -> String.valueOf(DyeableBlockItem.getColor(stack)));
    }

    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        LOGGER.info("Registering JEI Crafting Extensions");
        registration.getCraftingCategory().addCategoryExtension(NekoShapedRecipe.class, 
            (recipe) -> {
                return new NekoShapedRecipeExtension(recipe);
            });
        registration.getCraftingCategory().addCategoryExtension(NekoColorInheritRecipe.class, 
            (recipe) -> {
                return new NekoColorInheritRecipeExtension(recipe);
            });
    }
}
