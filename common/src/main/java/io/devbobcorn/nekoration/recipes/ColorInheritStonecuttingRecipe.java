package io.devbobcorn.nekoration.recipes;

import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * Stonecutting recipe that transfers the dye color (see {@link DyeableBlockItem})
 * of the input stack to the result, so e.g. cutting red Cement yields a red variant.
 */
public class ColorInheritStonecuttingRecipe extends StonecutterRecipe {
    public ColorInheritStonecuttingRecipe(String group, Ingredient ingredient, ItemStack result) {
        super(group, ingredient, result);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.NEKO_STONECUTTING.get();
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        ItemStack result = this.getResultItem(registries).copy();
        if (result.getItem() instanceof DyeableBlockItem) {
            DyeableBlockItem.setColor(result, DyeableBlockItem.getColor(input.item()));
        }
        return result;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.STONECUTTER);
    }

    public static class Serializer extends SingleItemRecipe.Serializer<ColorInheritStonecuttingRecipe> {
        public Serializer() {
            super(ColorInheritStonecuttingRecipe::new);
        }
    }
}
