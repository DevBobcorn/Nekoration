package com.devbobcorn.nekoration.client.jei;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.recipes.NekoColorInheritRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;

public class NekoColorInheritRecipeExtension implements ICraftingCategoryExtension {
    public static final ResourceLocation NAME = new ResourceLocation(Nekoration.MODID, "neko_color_inherit");
    private final NekoColorInheritRecipe recipe;

    public NekoColorInheritRecipeExtension(NekoColorInheritRecipe recipe){
        this.recipe = recipe;
    }

    @Nullable
    public ResourceLocation getRegistryName() {
        return NAME;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        craftingGridHelper.setInputs(builder, VanillaTypes.ITEM_STACK, recipe.getInputs(), getWidth(), getHeight());
        craftingGridHelper.setOutputs(builder, VanillaTypes.ITEM_STACK, recipe.getOutputs());
    }

    @Override
    public int getWidth() {
        return recipe.getWidth();
    }

    @Override
    public int getHeight() {
        return recipe.getHeight();
    }
}
