package com.devbobcorn.nekoration.exp.tile_entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.ChunkRender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * A wrapper around ChunkRender.
 * Stores the a render of the chunk (16x16x16) surrounding a TileEntity
 *
 * @author Cadiboo
 */
public class ModelMap {
  	// We only create one of these per cache, we reset it each time we rebuild
  	public final RegionRenderCacheBuilder regionRenderCacheBuilder;
  	private final ChunkRender chunkRender;
  	//public ChunkRenderDispatcher.ChunkRender.RebuildTask generator;
	private boolean isCompiled = false;

  	private ModelMap(final ChunkRender chunkRender, final RegionRenderCacheBuilder regionRenderCacheBuilder) {
  		this.chunkRender = chunkRender;
  		this.regionRenderCacheBuilder = regionRenderCacheBuilder;
  	}

/*
	public static ModelMap forTileEntity(final TileEntity tileEntity) {
  		final ChunkRender chunkRender = new ChunkRender(tileEntity.getLevel(), Minecraft.getInstance().levelRenderer);
  		final BlockPos pos = tileEntity.getBlockPos();
//
//		// We want to render everything in a 16x16x16 radius, with the centre being the TileEntity
  		chunkRender.setOrigin(pos.getX() - 8, pos.getY() - 8, pos.getZ() - 8);
//
  		return new ModelMap(chunkRender, new RegionRenderCacheBuilder());
		return null;
	}

	//
	// (re)build the render
	//
	public void compile() {
		final ChunkRender chunkRender = this.chunkRender;
		final RegionRenderCacheBuilder buffers = this.regionRenderCacheBuilder;

		final ChunkRenderTask generator = chunkRender.makeCompileTaskChunk();
		this.generator = generator;

		// Setup generator
		generator.setStatus(ChunkRenderTask.Status.COMPILING);
		generator.setRegionRenderCacheBuilder(buffers);

		final Vec3d vec3d = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();

		// Rebuild the ChunkRender.
		// This resets all the buffers it uses and renders every block in the chunk to the buffers
		chunkRender.rebuildChunk((float) vec3d.x, (float) vec3d.y, (float) vec3d.z, generator);

		// ChunkRender#rebuildChunk increments this, we don't want it incremented so we decrement it.
		--ChunkRender.renderChunksUpdated;

		// Set the translation of each buffer back to 0
		final int length = BLOCK_RENDER_LAYERS.length;
		for (int ordinal = 0; ordinal < length; ++ordinal) {
			buffers.getBuilder(ordinal).setTranslation(0, 0, 0);
		}
		this.isBuilt = true;
	}

	public boolean isCompiled() {
		return isCompiled;
	}
*/
}
