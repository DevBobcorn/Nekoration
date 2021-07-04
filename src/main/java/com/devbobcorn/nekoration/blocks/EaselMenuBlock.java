package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.blockentities.EaselMenuBlockEnity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class EaselMenuBlock extends DyeableBlock {

    public EaselMenuBlock(Properties settings) {
        super(settings);
    }
    
    public TileEntity newBlockEntity(IBlockReader world) {
        return new EaselMenuBlockEnity();
     }
}
