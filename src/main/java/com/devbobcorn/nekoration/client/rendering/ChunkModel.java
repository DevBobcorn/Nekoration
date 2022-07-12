package com.devbobcorn.nekoration.client.rendering;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

// See ViewArea.class for more
public class ChunkModel {
    private static final Logger LOGGER = LogManager.getLogger("Chunk Model");

    private Level level;
    private BlockPos pos;

    protected int chunkGridSizeY;
    protected int chunkGridSizeX;
    protected int chunkGridSizeZ;

    public ChunkRenderDispatcher.RenderChunk[] chunks;
    private boolean isCompiled = false, isCompiling = false;

    private ChunkModel(final Level level, BlockPos pos, final ChunkBufferBuilderPack regionRenderCacheBuilder, int radius) {
        this.level = level;
        this.pos = pos;
        setAreaRadius(radius);
    }

    public static ChunkModel forTileEntity(Level level, final BlockEntity tileEntity, int radius) {
        final BlockPos pos = tileEntity.getBlockPos();
        ChunkModel model = new ChunkModel(level, pos, new ChunkBufferBuilderPack(), radius);

        return model;
    }

    // From ViewArea.class, line 54
    private int getChunkIndex(int posx, int posy, int posz) {
        return (posz * this.chunkGridSizeY + posy) * this.chunkGridSizeX + posx;
    }

    public RenderChunk getRenderChunk(int x, int y, int z) {
        return chunks[getChunkIndex(x, y, z)];
    }

    @SuppressWarnings("resource")
    protected void setAreaRadius(int radius) {
        this.chunkGridSizeX = radius * 2 + 1;
        this.chunkGridSizeZ = radius * 2 + 1;
        this.chunkGridSizeY = this.level.getSectionsCount();

        // Refresh chunks...
        ChunkRenderDispatcher dispatcher = Minecraft.getInstance().levelRenderer.getChunkRenderDispatcher();

        int count = this.chunkGridSizeX * this.chunkGridSizeY * this.chunkGridSizeZ;
        this.chunks = new ChunkRenderDispatcher.RenderChunk[count];

        int orgX = (pos.getX() >> 4) << 4;
        int orgY = (pos.getY() >> 4) << 4;
        int orgZ = (pos.getZ() >> 4) << 4;

        for(int x = 0; x < this.chunkGridSizeX; ++x) {
            for(int y = 0; y < this.chunkGridSizeY; ++y) {
                for(int z = 0; z < this.chunkGridSizeZ; ++z) {
                    int index = this.getChunkIndex(x, y, z);
                    this.chunks[index] = dispatcher.new RenderChunk(index, orgX + x * 16, orgY + y * 16, orgZ + z * 16);
                }
            }
        }
    }

    public void compile(Minecraft mc, BlockPos camPos) {
        isCompiling = true;

        RenderRegionCache cache = new RenderRegionCache();

        for (RenderChunk chunk : chunks) {
            // Rebuild chunks...
            ChunkPos chunkpos = new ChunkPos(chunk.getOrigin());

            //if (chunkRender.isDirty() && this.level.getChunk(chunkpos.x, chunkpos.z).isClientLightReady()) {
            if (this.level.getChunk(chunkpos.x, chunkpos.z).isClientLightReady()) {
                mc.levelRenderer.getChunkRenderDispatcher().rebuildChunkSync(chunk, cache);
                //chunkRender.setNotDirty();
                isCompiled = true;
                LOGGER.info("Chunk model compilation at " + chunkpos + " complete.");
            }
        }

        isCompiling = false;
        
    }

    public boolean isCompiled() {
        return isCompiled;
    }

    public boolean isCompiling() {
        return isCompiling;
    }
}