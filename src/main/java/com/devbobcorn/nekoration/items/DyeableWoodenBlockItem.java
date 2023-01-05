package com.devbobcorn.nekoration.items;

import com.devbobcorn.nekoration.NekoColors.EnumWoodenColor;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;

public class DyeableWoodenBlockItem extends BlockItem {
    public static final String COLOR = "color";

    public DyeableWoodenBlockItem(Block block, Properties settings) {
        super(block, settings);
    }

    /* @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> subItems) {
        if (this.allowedIn(tab)) {
            for (EnumWoodenColor color : EnumWoodenColor.values()) {
                ItemStack subItemStack = new ItemStack(this, 1);
                setColor(subItemStack, color);
                subItems.add(subItemStack);
            }
        }
    } */
    
    public static boolean hasColor(ItemStack stack) {
        return stack.getOrCreateTag().contains(COLOR);
    }

    public static EnumWoodenColor getColor(ItemStack stack) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        return EnumWoodenColor.fromNBT(compoundNBT, COLOR);
    }

    public static void setColor(ItemStack stack, EnumWoodenColor color) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        color.putIntoNBT(compoundNBT, COLOR);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            return Component.translatable(this.getDescriptionId(stack), (Component.translatable("color.wooden." + (hasColor(stack) ? getColor(stack).getSerializedName() : "unknown"))).getString());
        else return CaseTweak.getTweaked(Component.translatable(this.getDescriptionId(stack), (Component.translatable("color.wooden." + (hasColor(stack) ? getColor(stack).getSerializedName() : "unknown"))).getString()));
    }
}
