package com.devbobcorn.nekoration.client.rendering.chunks;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.BufferBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.VisibilitySet;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CompiledModelChunk {
    public static final CompiledModelChunk UNCOMPILED = new CompiledModelChunk() {
        public boolean facesCanSeeEachother(Direction dir1, Direction dir2) {
            return false;
        }
    };
    final Set<RenderType> hasBlocks = new ObjectArraySet<>();
    final Set<RenderType> hasLayer = new ObjectArraySet<>();
    boolean isCompletelyEmpty = true;
    final List<BlockEntity> renderableBlockEntities = Lists.newArrayList();
    VisibilitySet visibilitySet = new VisibilitySet();
    @Nullable
    BufferBuilder.SortState transparencyState;

    public boolean hasNoRenderableLayers() {
        return this.isCompletelyEmpty;
    }

    public boolean isEmpty(RenderType type) {
        return !this.hasBlocks.contains(type);
    }

    public List<BlockEntity> getRenderableBlockEntities() {
        return this.renderableBlockEntities;
    }

    public boolean facesCanSeeEachother(Direction dir1, Direction dir2) {
        return this.visibilitySet.visibilityBetween(dir1, dir2);
    }
}