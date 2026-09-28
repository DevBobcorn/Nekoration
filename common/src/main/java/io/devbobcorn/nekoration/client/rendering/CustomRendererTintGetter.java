package io.devbobcorn.nekoration.client.rendering;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

/**
 * Wraps a level and overrides every tint lookup with a fixed RGB color, so a
 * displayed block state can be tinted without touching its block color
 * providers (ported from the v1 CustomRendererTintGetter). GUI previews can
 * additionally force full brightness and resolve biome fallbacks at a fixed
 * position.
 */
public class CustomRendererTintGetter implements BlockAndTintGetter {
    private final Level level;
    private int color = -1;
    private boolean fullBright;
    @Nullable
    private BlockPos tintPos;

    public CustomRendererTintGetter(Level level) {
        this.level = level;
    }

    public void setCustomTint(int rgb) {
        this.color = rgb & 0xFFFFFF;
    }

    public void setFullBright(boolean fullBright) {
        this.fullBright = fullBright;
    }

    public void setTintPos(@Nullable BlockPos tintPos) {
        this.tintPos = tintPos;
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return level.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return level.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return level.getFluidState(pos);
    }

    @Override
    public int getHeight() {
        return level.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return level.getMinBuildHeight();
    }

    @Override
    public float getShade(Direction direction, boolean shade) {
        return level.getShade(direction, shade);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return level.getLightEngine();
    }

    @Override
    public int getBrightness(LightLayer layer, BlockPos pos) {
        return fullBright ? 15 : level.getBrightness(layer, pos);
    }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver resolver) {
        if (color >= 0) {
            return color;
        }
        return level.getBlockTint(tintPos != null ? tintPos : pos, resolver);
    }
}
