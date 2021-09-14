package com.devbobcorn.nekoration.client.rendering;

import com.devbobcorn.nekoration.client.rendering.chunks.ChunkModelRender;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class ChunkModel {
    public final ChunkModelRender chunkRender;
    private boolean isCompiled = false;
    private boolean error = false;

    private ChunkModel(final ChunkModelRender chunkRender, final ChunkBufferBuilderPack regionRenderCacheBuilder) {
        this.chunkRender = chunkRender;
    }

    @SuppressWarnings("resource")
    public static ChunkModel forTileEntity(final BlockEntity tileEntity) {
        final ChunkModelRender chunkRender = new ChunkModelRender(tileEntity.getLevel(), Minecraft.getInstance().levelRenderer, 0); // TODO
        final BlockPos pos = tileEntity.getBlockPos();

        chunkRender.setOrigin((pos.getX() >> 4) << 4, (pos.getY() >> 4) << 4, (pos.getZ() >> 4) << 4);

        return new ChunkModel(chunkRender, new ChunkBufferBuilderPack());
    }

    @SuppressWarnings("resource")
    public void compile() {
        if (error) return;
        try {
            final ChunkModelRender modelRender = this.chunkRender;
            final Vec3 pos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            modelRender.setCamera(pos);
            modelRender.compileModel();
            System.out.println("Compiled. Raely. I am haz Error: " + (error ? "Yiss" : "Nu"));

            this.isCompiled = true;
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
        }
    }

    public boolean isCompiled() {
        return isCompiled;
    }

    public boolean getError() {
        return error;
    }
}