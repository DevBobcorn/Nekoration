package com.devbobcorn.nekoration.utils;

import com.devbobcorn.nekoration.blocks.ModBlocks;
import net.minecraft.world.item.ItemStack;

public class ItemIconHelper {
    public static final ItemStack getCustomBlockItem(int customModelData){
        ItemStack item = new ItemStack(ModBlocks.CUSTOM.get());
        item.getOrCreateTag().putInt("CustomModelData", customModelData);
        return item;
    }
}