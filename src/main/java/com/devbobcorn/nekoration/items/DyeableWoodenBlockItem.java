package com.devbobcorn.nekoration.items;

import com.devbobcorn.nekoration.NekoColors.EnumWoodenColor;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DyeableWoodenBlockItem extends BlockItem {
    public static final String COLOR = "color";

    public DyeableWoodenBlockItem(Block block, Properties settings) {
        super(block, settings);
    }

	@Override
	public void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> subItems) {
		if (this.allowdedIn(tab)) {
			for (EnumWoodenColor color : EnumWoodenColor.values()) {
				ItemStack subItemStack = new ItemStack(this, 1);
				setColor(subItemStack, color);
				subItems.add(subItemStack);
			}
		}
	}

    public static EnumWoodenColor getColor(ItemStack stack) {
		CompoundNBT compoundNBT = stack.getOrCreateTag();
		return EnumWoodenColor.fromNBT(compoundNBT, COLOR);
	}

    public static void setColor(ItemStack stack, EnumWoodenColor color) {
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        color.putIntoNBT(compoundNBT, COLOR);
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		String colorText = (new TranslationTextComponent("color.wooden." + getColor(stack).getSerializedName())).getString();

		return new TranslationTextComponent(this.getDescriptionId(stack), colorText);
	}
}
