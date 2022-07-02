package com.devbobcorn.nekoration.client.rendering.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

public class CustomRendererTintGetter implements BlockAndTintGetter {
    private Level world;
    private int color = 0;

    public CustomRendererTintGetter(Level world)
    {
        this.world = world;
    }

    public void SetCustomTint(int rgb)
    {
        color = rgb;
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return world.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return world.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return world.getFluidState(pos);
    }

    @Override
    public int getHeight() {
        return world.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return world.getMinBuildHeight();
    }

    @Override
    public float getShade(Direction dir, boolean wtf) {
        return world.getShade(dir, wtf);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return world.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver iDonCare) {
        return color;
    }
    
}
