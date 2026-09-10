package io.devbobcorn.nekoration.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import io.devbobcorn.nekoration.xplat.NekoRegistrar;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;
import io.devbobcorn.nekoration.recipes.ColorInheritShapedRecipe;
import io.devbobcorn.nekoration.recipes.ColorInheritStonecuttingRecipe;
import io.devbobcorn.nekoration.recipes.WallpaperRecipe;

public final class ModRecipes {
    public static RegistrySupplier<RecipeSerializer<WallpaperRecipe>> WALLPAPER;
    public static RegistrySupplier<RecipeSerializer<ColorInheritShapedRecipe>> NEKO_COLOR_INHERIT;
    public static RegistrySupplier<RecipeSerializer<ColorInheritStonecuttingRecipe>> NEKO_STONECUTTING;

    private ModRecipes() {
    }

    public static void register(NekoRegistrar registrar) {
        WALLPAPER = registrar.register(Registries.RECIPE_SERIALIZER, "neko_crafting_wallpaper",
                () -> new SimpleCraftingRecipeSerializer<>(WallpaperRecipe::new));
        NEKO_COLOR_INHERIT = registrar.register(Registries.RECIPE_SERIALIZER, "neko_color_inherit",
                ColorInheritShapedRecipe.Serializer::new);
        NEKO_STONECUTTING = registrar.register(Registries.RECIPE_SERIALIZER, "neko_stonecutting",
                ColorInheritStonecuttingRecipe.Serializer::new);
    }
}
