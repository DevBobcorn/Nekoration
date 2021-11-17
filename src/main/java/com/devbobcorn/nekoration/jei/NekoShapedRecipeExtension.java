package com.devbobcorn.nekoration.jei;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.recipes.NekoShapedRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Size2i;

public class NekoShapedRecipeExtension implements ICraftingCategoryExtension {
    public static final ResourceLocation NAME = new ResourceLocation(Nekoration.MODID, "neko_crafting_shaped");
    private final NekoShapedRecipe recipe;

    public NekoShapedRecipeExtension(NekoShapedRecipe recipe){
        this.recipe = recipe;
    }

	@Nullable
	public ResourceLocation getRegistryName() {
		return NAME;
	}

    @Override
    public void setIngredients(IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, recipe.getInputs());
        ingredients.setOutputLists(VanillaTypes.ITEM, recipe.getOutputs());
    }

    @Nullable
	public Size2i getSize() {
		return new Size2i(recipe.getWidth(), recipe.getHeight());
	}
}
