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
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final ResourceLocation UID = new ResourceLocation(Nekoration.MODID, "jei_nekoration_plugin");
    private static final Logger LOGGER = LogManager.getLogger("JEI Plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    private static final IIngredientSubtypeInterpreter<ItemStack> woodenSubtypeInterpreter = (stack, ctx) -> String.valueOf(HalfTimberBlockItem.getColor0(stack));
    private static final IIngredientSubtypeInterpreter<ItemStack> dyeableSubtypeInterpreter = (stack, ctx) -> String.valueOf(DyeableBlockItem.getColor(stack));
    private static final IIngredientSubtypeInterpreter<ItemStack> dyeableWoodenSubtypeInterpreter = (stack, ctx) -> String.valueOf(DyeableWoodenBlockItem.getColor(stack));

    public void registerItemSubtypes(ISubtypeRegistration registration){
        // Wooden
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.HALF_TIMBER_P0.get().asItem(), woodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.HALF_TIMBER_P1.get().asItem(), woodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.HALF_TIMBER_P2.get().asItem(), woodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.HALF_TIMBER_P3.get().asItem(), woodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.HALF_TIMBER_P4.get().asItem(), woodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.HALF_TIMBER_P5.get().asItem(), woodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.HALF_TIMBER_P6.get().asItem(), woodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.HALF_TIMBER_P7.get().asItem(), woodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.HALF_TIMBER_P8.get().asItem(), woodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.HALF_TIMBER_P9.get().asItem(), woodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.HALF_TIMBER_PILLAR_P0.get().asItem(), woodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.HALF_TIMBER_PILLAR_P1.get().asItem(), woodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.HALF_TIMBER_PILLAR_P2.get().asItem(), woodenSubtypeInterpreter);
        // Stone - Ignored
        // Window - Stone Stuff Ignored
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.WINDOW_SIMPLE.get().asItem(), dyeableSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.WINDOW_ARCH.get().asItem(), dyeableSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.WINDOW_CROSS.get().asItem(), dyeableSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.WINDOW_SHADE.get().asItem(), dyeableSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.WINDOW_LANCET.get().asItem(), dyeableSubtypeInterpreter);
        // Decorations
        // - Planter - Ignored
        // - Awning
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.AWNING_PURE.get().asItem(), dyeableSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.AWNING_STRIPE.get().asItem(), dyeableSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.AWNING_PURE_SHORT.get().asItem(), dyeableSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.AWNING_STRIPE_SHORT.get().asItem(), dyeableSubtypeInterpreter);
        // - Furniture
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.GLASS_TABLE.get().asItem(), dyeableWoodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.GLASS_ROUND_TABLE.get().asItem(), dyeableWoodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.ARM_CHAIR.get().asItem(), dyeableWoodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.BENCH.get().asItem(), dyeableWoodenSubtypeInterpreter);
        // - Container
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.EASEL_MENU.get().asItem(), dyeableWoodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.EASEL_MENU_WHITE.get().asItem(), dyeableWoodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.DRAWER.get().asItem(), dyeableWoodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.CABINET.get().asItem(), dyeableWoodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.DRAWER_CHEST.get().asItem(), dyeableWoodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.CUPBOARD.get().asItem(), dyeableWoodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.SHELF.get().asItem(), dyeableWoodenSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.WALL_SHELF.get().asItem(), dyeableWoodenSubtypeInterpreter);
        // - Misc
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.CANDLE_HOLDER_GOLD.get().asItem(), dyeableSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.CANDLE_HOLDER_IRON.get().asItem(), dyeableSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.CANDLE_HOLDER_QUARTZ.get().asItem(), dyeableSubtypeInterpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.WINDOW_PLANT.get().asItem(), dyeableSubtypeInterpreter);
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
