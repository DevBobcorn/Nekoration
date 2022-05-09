package com.devbobcorn.nekoration.items;

import com.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import com.devbobcorn.nekoration.NekoColors.EnumWoodenColor;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;

public class HalfTimberBlockItem extends BlockItem {
    public static final String COLOR_0 = "color_0"; // Color of outer layer
    public static final String COLOR_1 = "color_1"; // Color of inner layer

    public HalfTimberBlockItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> subItems) {
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

    public void fillItemCategoryWithWoodType(CreativeModeTab tab, EnumWoodenColor type, NonNullList<ItemStack> subItems) {
        if (this.allowdedIn(tab)) {
            for (EnumNekoColor color : EnumNekoColor.values()){
                ItemStack subItemStack = new ItemStack(this, 1);
                setColor0(subItemStack, type);
                setColor1(subItemStack, color);
                subItems.add(subItemStack);
            }
        }
    }

    public static boolean hasColor(ItemStack stack) {
        return stack.getOrCreateTag().contains(COLOR_0) && stack.getOrCreateTag().contains(COLOR_1);
    }

    public static EnumWoodenColor getColor0(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return EnumWoodenColor.fromNBT(tag, COLOR_0);
    }

    public static void setColor0(ItemStack stack, EnumWoodenColor color) {
        CompoundTag tag = stack.getOrCreateTag();
        color.putIntoNBT(tag, COLOR_0);
    }

    public static EnumNekoColor getColor1(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return EnumNekoColor.fromNBT(tag, COLOR_1);
    }

    public static void setColor1(ItemStack stack, EnumNekoColor color) {
        CompoundTag tag = stack.getOrCreateTag();
        color.putIntoNBT(tag, COLOR_1);
    }

    @Override
    public Component getName(ItemStack stack) {
        boolean hasColor = hasColor(stack);
        String color0Text = (new TranslatableComponent("color.wooden." + (hasColor ? getColor0(stack).getSerializedName() : "unknown"))).getString();
        String color1Text = (new TranslatableComponent("color.nekoration." + (hasColor ? getColor1(stack).getSerializedName() : "unknown"))).getString();

        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            return new TranslatableComponent(this.getDescriptionId(stack), color0Text, color1Text);
        else return CaseTweak.getTweaked(new TranslatableComponent(this.getDescriptionId(stack), color0Text, color1Text));
    }
}
