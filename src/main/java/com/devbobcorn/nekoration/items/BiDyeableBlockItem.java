package com.devbobcorn.nekoration.items;

import java.util.ArrayList;

import com.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import com.devbobcorn.nekoration.NekoColors.EnumWoodenColor;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class BiDyeableBlockItem extends BlockItem {
    public static final String NBT_TAG_NAME_COLOR_0 = "color_0"; // Color of outer layer
    public static final String NBT_TAG_NAME_COLOR_1 = "color_1"; // Color of inner layer

    public BiDyeableBlockItem(Block block, Properties settings) {
        super(block, settings);
    }

	@Override
	public void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> subItems) {
        // Only add those with
		if (this.allowdedIn(tab)) {
			for (EnumNekoColor color : EnumNekoColor.values()) {
				ItemStack subItemStack = new ItemStack(this, 1);
				setColor0(subItemStack, EnumWoodenColor.BROWN);
                setColor1(subItemStack, color);
				subItems.add(subItemStack);
			}
		}
	}

	public void fillItemCategoryWithWoodType(ItemGroup tab, EnumWoodenColor type, NonNullList<ItemStack> subItems) {
		if (this.allowdedIn(tab)) {
			for (EnumNekoColor color : EnumNekoColor.values()){
				ItemStack subItemStack = new ItemStack(this, 1);
				setColor0(subItemStack, type);
				setColor1(subItemStack, color);
				subItems.add(subItemStack);
			}
		}
	}

    public static EnumWoodenColor getColor0(ItemStack stack) {
		CompoundNBT compoundNBT = stack.getOrCreateTag();
		return EnumWoodenColor.fromNBT(compoundNBT, NBT_TAG_NAME_COLOR_0);
	}

    public static void setColor0(ItemStack stack, EnumWoodenColor color) {
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        color.putIntoNBT(compoundNBT, NBT_TAG_NAME_COLOR_0);
	}

    public static EnumNekoColor getColor1(ItemStack stack) {
		CompoundNBT compoundNBT = stack.getOrCreateTag();
		return EnumNekoColor.fromNBT(compoundNBT, NBT_TAG_NAME_COLOR_1);
	}

    public static void setColor1(ItemStack stack, EnumNekoColor color) {
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        color.putIntoNBT(compoundNBT, NBT_TAG_NAME_COLOR_1);
	}
}