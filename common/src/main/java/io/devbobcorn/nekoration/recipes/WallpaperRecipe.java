package io.devbobcorn.nekoration.recipes;

import io.devbobcorn.nekoration.registry.ModItems;
import io.devbobcorn.nekoration.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class WallpaperRecipe extends CustomRecipe {
    public WallpaperRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack wallpaper = ItemStack.EMPTY;
        ItemStack banner = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof BannerItem) {
                if (!banner.isEmpty()) {
                    return false;
                }
                banner = stack;
            } else if (stack.is(ModItems.WALLPAPER.get())) {
                if (!wallpaper.isEmpty() || stack.has(DataComponents.BASE_COLOR)
                        || !stack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY).layers().isEmpty()) {
                    return false;
                }
                wallpaper = stack;
            } else {
                return false;
            }
        }
        return !wallpaper.isEmpty() && !banner.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack wallpaper = ItemStack.EMPTY;
        ItemStack banner = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof BannerItem) {
                banner = stack;
            } else if (stack.is(ModItems.WALLPAPER.get())) {
                wallpaper = stack.copyWithCount(1);
            }
        }
        if (wallpaper.isEmpty() || banner.isEmpty()) {
            return ItemStack.EMPTY;
        }
        wallpaper.set(DataComponents.BASE_COLOR, ((BannerItem) banner.getItem()).getColor());
        wallpaper.set(DataComponents.BANNER_PATTERNS,
                banner.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
        return wallpaper;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.WALLPAPER.get();
    }
}
