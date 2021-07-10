package com.devbobcorn.nekoration.items;

import com.devbobcorn.nekoration.NekoColors.EnumNekoColor;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class DyeableBlockItem extends BlockItem {
    public static final String NBT_TAG_NAME_COLOR = "color";

    public DyeableBlockItem(Block block, Properties settings) {
        super(block, settings);
    }

	@Override
	public void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> subItems) {
		if (this.allowdedIn(tab)) {
			for (EnumNekoColor color : EnumNekoColor.values()) {
				ItemStack subItemStack = new ItemStack(this, 1);
				setColor(subItemStack, color);
				subItems.add(subItemStack);
			}
		}
	}

    public static EnumNekoColor getColor(ItemStack stack) {
		CompoundNBT compoundNBT = stack.getOrCreateTag();
		return EnumNekoColor.fromNBT(compoundNBT, NBT_TAG_NAME_COLOR);
	}

    public static void setColor(ItemStack stack, EnumNekoColor color) {
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        color.putIntoNBT(compoundNBT, NBT_TAG_NAME_COLOR);
	}
}
