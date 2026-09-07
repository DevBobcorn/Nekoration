package io.devbobcorn.nekoration.registry;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.recipes.WallpaperRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTER =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Nekoration.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WallpaperRecipe>> WALLPAPER =
            REGISTER.register("neko_crafting_wallpaper", () -> new SimpleCraftingRecipeSerializer<>(WallpaperRecipe::new));

    private ModRecipes() {
    }
}
