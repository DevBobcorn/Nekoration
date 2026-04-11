package io.devbobcorn.nekoration.items;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.component.CustomData;

public class DyeableBlockItem extends BlockItem {
    public static final String COLOR = "color";

    public DyeableBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        String colorKey = "color.nekoration." + (hasColor(stack) ? getColor(stack).getSerializedName() : "unknown");
        return Component.translatable(getDescriptionId(stack), Component.translatable(colorKey));
    }

    public static boolean hasColor(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.contains(COLOR);
    }

    public static EnumNekoColor getColor(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return EnumNekoColor.fromNbt(tag, COLOR);
    }

    public static void setColor(ItemStack stack, EnumNekoColor color) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> color.putIntoNbt(tag, COLOR));
    }

    /** Creative tabs: one stack per item with plaster color {@link EnumNekoColor#WHITE}. */
    public static ItemStack createCreativeTabStack(ItemLike item) {
        ItemStack stack = new ItemStack(item);
        setColor(stack, EnumNekoColor.WHITE);
        return stack;
    }
}
