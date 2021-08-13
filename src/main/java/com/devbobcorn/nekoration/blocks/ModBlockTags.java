package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.Nekoration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.level.block.Block;;

public class ModBlockTags {
    public static final Named<Block> WINDOWS = BlockTags.createOptional(new ResourceLocation(Nekoration.MODID, "windows"));
}
