package com.devbobcorn.nekoration.items;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.NekoColors.EnumNekoColor;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;


public class DyeableBlockItem extends BlockItem {
    public static final String COLOR = "color";
    private final boolean showAllVariants;

    public DyeableBlockItem(Block block, Properties settings) {
        super(block, settings);
        showAllVariants = true;
    }

    public DyeableBlockItem(Block block, Properties settings, boolean allVariants) {
        super(block, settings);
        showAllVariants = allVariants;
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> subItems) {
        if (this.allowedIn(tab)) {
            if (showAllVariants)
                for (EnumNekoColor color : EnumNekoColor.values()) {
                    ItemStack subItemStack = new ItemStack(this, 1);
                    setColor(subItemStack, color);
                    subItems.add(subItemStack);
                }
            else {
                ItemStack subItemStack = new ItemStack(this, 1);
                setColor(subItemStack, EnumNekoColor.WHITE);
                subItems.add(subItemStack);
            }
        }
    }

    public static boolean hasColor(ItemStack stack) {
        return stack.getOrCreateTag().contains(COLOR);
    }

    public static EnumNekoColor getColor(ItemStack stack) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        return EnumNekoColor.fromNBT(compoundNBT, COLOR);
    }

    public static void setColor(ItemStack stack, EnumNekoColor color) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        color.putIntoNBT(compoundNBT, COLOR);
    }

    public static float getColorPropertyOverride(ItemStack itemStack, @Nullable Level world, @Nullable LivingEntity livingEntity, int what) {
        EnumNekoColor color = DyeableBlockItem.getColor(itemStack);
        return color.getPropertyOverrideValue();
    }

    @Override
    public Component getName(ItemStack stack) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            return Component.translatable(this.getDescriptionId(stack), (Component.translatable("color.nekoration." + (hasColor(stack) ? getColor(stack).getSerializedName() : "unknown"))).getString());
        else return CaseTweak.getTweaked(Component.translatable(this.getDescriptionId(stack), (Component.translatable("color.nekoration." + (hasColor(stack) ? getColor(stack).getSerializedName() : "unknown"))).getString()));
    }
}
