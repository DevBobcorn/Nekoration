package com.devbobcorn.nekoration.items;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.NekoColors.EnumNekoColor;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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

	public static float getColorPropertyOverride(ItemStack itemStack, @Nullable World world,
		@Nullable LivingEntity livingEntity) {
		EnumNekoColor color = DyeableBlockItem.getColor(itemStack);
		return color.getPropertyOverrideValue();
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		String colorText = (new TranslationTextComponent("color.nekoration." + getColor(stack).getSerializedName())).getString();

		return new TranslationTextComponent(this.getDescriptionId(stack), colorText);
	}
}
