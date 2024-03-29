package com.devbobcorn.nekoration.recipes;

import com.devbobcorn.nekoration.Nekoration;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Nekoration.MODID);

    public static final RegistryObject<RecipeSerializer<NekoShapedRecipe>> NEKO_SHAPED = RECIPE_SERIALIZERS.register("neko_crafting_shaped", () -> new NekoShapedRecipe.Serializer());
    public static final RegistryObject<RecipeSerializer<NekoColorInheritRecipe>> NEKO_COLOR_INHERIT = RECIPE_SERIALIZERS.register("neko_color_inherit", () -> new NekoColorInheritRecipe.Serializer());

    public static final RegistryObject<RecipeSerializer<NekoStonecuttingRecipe>> NEKO_STONY = RECIPE_SERIALIZERS.register("neko_stonecutting", () -> new NekoStonecuttingRecipe.Serializer());

    public static final RegistryObject<RecipeSerializer<WallPaperRecipe>> NEKO_WALLPAPER = RECIPE_SERIALIZERS.register("neko_crafting_wallpaper", () -> new SimpleCraftingRecipeSerializer<>(WallPaperRecipe::new));
}
