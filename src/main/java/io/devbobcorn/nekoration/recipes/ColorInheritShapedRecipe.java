package io.devbobcorn.nekoration.recipes;

import com.mojang.serialization.MapCodec;

import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

/**
 * Shaped crafting recipe that transfers the dye color (see {@link DyeableBlockItem})
 * of the first dyeable ingredient to the result, so recolored ingredients keep their
 * color when crafted into other variants.
 * Unlike vanilla shaped recipes, patterns are never mirrored, so mirrored patterns of
 * direction-sensitive blocks (e.g. Half-Timbers) stay distinct.
 */
public class ColorInheritShapedRecipe extends ShapedRecipe {
    public ColorInheritShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern,
            ItemStack result, boolean showNotification) {
        super(group, category, pattern, result, showNotification);
    }

    public ColorInheritShapedRecipe(ShapedRecipe recipe) {
        this(recipe.getGroup(), recipe.category(), recipe.pattern, recipe.getResultItem(null),
                recipe.showNotification());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.NEKO_COLOR_INHERIT.get();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != this.ingredientCount() || input.width() != this.pattern.width()
                || input.height() != this.pattern.height()) {
            return false;
        }
        for (int row = 0; row < this.pattern.height(); row++) {
            for (int column = 0; column < this.pattern.width(); column++) {
                if (!this.pattern.ingredients().get(column + row * this.pattern.width())
                        .test(input.getItem(column, row))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack result = this.getResultItem(registries).copy();
        for (int i = 0; i < input.size(); i++) {
            ItemStack ingredient = input.getItem(i);
            if (ingredient.getItem() instanceof DyeableBlockItem) {
                DyeableBlockItem.setColor(result, DyeableBlockItem.getColor(ingredient));
                break;
            }
        }
        return result;
    }

    private int ingredientCount() {
        return (int) this.pattern.ingredients().stream().filter(ingredient -> !ingredient.isEmpty()).count();
    }

    public static class Serializer implements RecipeSerializer<ColorInheritShapedRecipe> {
        public static final MapCodec<ColorInheritShapedRecipe> CODEC = ShapedRecipe.Serializer.CODEC
                .xmap(ColorInheritShapedRecipe::new, recipe -> recipe);
        public static final StreamCodec<RegistryFriendlyByteBuf, ColorInheritShapedRecipe> STREAM_CODEC = StreamCodec
                .of(
                        (buffer, recipe) -> ShapedRecipe.Serializer.STREAM_CODEC.encode(buffer, recipe),
                        buffer -> new ColorInheritShapedRecipe(ShapedRecipe.Serializer.STREAM_CODEC.decode(buffer)));

        @Override
        public MapCodec<ColorInheritShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ColorInheritShapedRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
