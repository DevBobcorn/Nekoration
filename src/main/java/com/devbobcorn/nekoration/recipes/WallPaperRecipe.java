package com.devbobcorn.nekoration.recipes;

import com.devbobcorn.nekoration.entities.WallPaperEntity;
import com.devbobcorn.nekoration.items.ModItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class WallPaperRecipe extends CustomRecipe {
    public WallPaperRecipe(ResourceLocation id) {
        super(id);
    }

    public boolean matches(CraftingContainer inv, Level world) {
        ItemStack itemstack = ItemStack.EMPTY;
        ItemStack itemstack1 = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemstack2 = inv.getItem(i);
            if (!itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof BannerItem) {
                    if (!itemstack1.isEmpty()) {
                        return false;
                    }
                    itemstack1 = itemstack2;
                } else {
                    if (itemstack2.getItem() != ModItems.WALLPAPER.get()) {
                        return false;
                    }
                    if (!itemstack.isEmpty()) {
                        return false;
                    }
                    CompoundTag tag;
                    if ((tag = itemstack2.getTagElement("BlockEntityTag")) != null) {
                        if (WallPaperEntity.getBaseColor(tag) != DyeColor.WHITE || WallPaperEntity.getPatterns(tag) != null)
                            return false;
                    }
                    itemstack = itemstack2;
                }
            }
        }
        return !itemstack.isEmpty() && !itemstack1.isEmpty();
    }

    public ItemStack assemble(CraftingContainer inv) {
        ItemStack itemstack = ItemStack.EMPTY;
        ItemStack itemstack1 = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemstack2 = inv.getItem(i);
            if (!itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof BannerItem) {
                    itemstack = itemstack2;
                } else if (itemstack2.getItem() == ModItems.WALLPAPER.get()) {
                    itemstack1 = itemstack2.copy();
                    itemstack.setCount(1);
                }
            }
        }
        if (itemstack1.isEmpty()) {
            return itemstack1;
        } else {
            CompoundTag compoundnbt = itemstack.getTagElement("BlockEntityTag");
            CompoundTag compoundnbt1 = compoundnbt == null ? new CompoundTag() : compoundnbt.copy();
            compoundnbt1.putInt("Base", ((BannerItem) itemstack.getItem()).getColor().getId());
            itemstack1.addTagElement("BlockEntityTag", compoundnbt1);
            return itemstack1;
        }
    }

    public boolean canCraftInDimensions(int w, int h) {
        return w * h >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.NEKO_WALLPAPER.get();
    }
}