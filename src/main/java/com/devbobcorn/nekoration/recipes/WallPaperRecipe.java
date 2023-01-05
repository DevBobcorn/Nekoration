package com.devbobcorn.nekoration.recipes;

import com.devbobcorn.nekoration.entities.WallPaperEntity;
import com.devbobcorn.nekoration.items.ModItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class WallPaperRecipe extends CustomRecipe {
    public WallPaperRecipe(ResourceLocation id, CraftingBookCategory cat) {
        super(id, cat);
    }

    public boolean matches(CraftingContainer inv, Level world) {
        ItemStack resultStack = ItemStack.EMPTY;
        ItemStack bannerStack = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack curStack = inv.getItem(i);
            if (!curStack.isEmpty()) {
                if (curStack.getItem() instanceof BannerItem) {
                    if (!bannerStack.isEmpty()) {
                        return false;
                    }
                    bannerStack = curStack;
                } else {
                    if (curStack.getItem() != ModItems.WALLPAPER.get()) {
                        return false;
                    }
                    if (!resultStack.isEmpty()) {
                        return false;
                    }
                    CompoundTag tag;
                    if ((tag = curStack.getTagElement("BlockEntityTag")) != null) {
                        if (WallPaperEntity.getBaseColor(tag) != DyeColor.WHITE || WallPaperEntity.getPatterns(tag) != null)
                            return false;
                    }
                    resultStack = curStack;
                }
            }
        }
        return !resultStack.isEmpty() && !bannerStack.isEmpty();
    }

    public ItemStack assemble(CraftingContainer inv) {
        ItemStack bannerStack = ItemStack.EMPTY;
        ItemStack resultStack = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack curStack = inv.getItem(i);
            if (!curStack.isEmpty()) {
                if (curStack.getItem() instanceof BannerItem) {
                    bannerStack = curStack;
                } else if (curStack.getItem() == ModItems.WALLPAPER.get()) {
                    resultStack = curStack.copy();
                    resultStack.setCount(1);
                }
            }
        }
        if (resultStack.isEmpty()) {
            return resultStack;
        } else {
            CompoundTag bannerTag = bannerStack.getTagElement("BlockEntityTag");
            CompoundTag wallpaperTag = bannerTag == null ? new CompoundTag() : bannerTag.copy();
            wallpaperTag.putInt("Base", ((BannerItem) bannerStack.getItem()).getColor().getId());
            resultStack.addTagElement("BlockEntityTag", wallpaperTag);
            return resultStack;
        }
    }

    public boolean canCraftInDimensions(int w, int h) {
        return w * h >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.NEKO_WALLPAPER.get();
    }
}