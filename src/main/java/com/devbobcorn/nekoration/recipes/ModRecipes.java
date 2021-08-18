package com.devbobcorn.nekoration.recipes;

import com.devbobcorn.nekoration.Nekoration;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Nekoration.MODID);

    public static final RegistryObject<RecipeSerializer<HalfTimberRecipe>> HALF_TIMBER = RECIPE_SERIALIZERS.register("crafting_half_timber", () -> new HalfTimberRecipe.Serializer());
    public static final RegistryObject<RecipeSerializer<NekoStonecuttingRecipe>> NEKO_STONY = RECIPE_SERIALIZERS.register("neko_stonecutting", () -> new NekoStonecuttingRecipe.Serializer());
}
