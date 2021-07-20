package com.devbobcorn.nekoration.client.rendering;

import com.devbobcorn.nekoration.blocks.CustomBlock;
import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraft.util.math.vector.Vector3f;

public class CustomRenderer extends TileEntityRenderer<CustomBlockEntity> {
	final double frac = 1.0D / 32.0D;
	final double frac2 = 10.0D / 32.0D;

	public CustomRenderer(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
		super(tileEntityRendererDispatcher);
	}

	@Override
	public void render(CustomBlockEntity tileEntity, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffers,
			int combinedLight, int combinedOverlay) {
		// Make sure the blockstate is rendering nothing now...
		if (tileEntity.getBlockState().getValue(CustomBlock.MODEL) != 0)
			return;
		
		// and then we rendering models with our TE renderer...
		if (tileEntity.model <= 0){
			stack.pushPose(); // push the current transformation matrix + normals matrix
			stack.translate(0.5, 0.0, 0.5); // Change its pivot point before rotation...
			stack.mulPose(Vector3f.YP.rotationDegrees(tileEntity.dir * 15F));
			stack.translate(-0.5, 0.0, -0.5); // Then just get it back...
			// To translate 1 here is to translate 2 meters(blocks), so we translate 1/32 for a single-voxel-long offset(1/16 block)...
			stack.translate(tileEntity.offset[0] * frac, tileEntity.offset[1] * frac, tileEntity.offset[2] * frac); // Offset by certain voxels (1 block = 16 * 16 * 16 voxels)

			QuadRenderer.renderCubeUsingQuads(tileEntity, partialTicks, stack, buffers, combinedLight, combinedOverlay);

			stack.popPose(); // restore the original transformation matrix + normals matrix
		} else if (tileEntity.model <= 15) {
			stack.pushPose();

			stack.translate(0.5, 0.0, 0.5);

			// BED stack.translate(0.0F, -0.4375, 0.0F);
			// CARPET stack.translate(0.0F, -0.875, 0.0F);
			stack.mulPose(Vector3f.YP.rotationDegrees(tileEntity.dir * 15F));
			stack.scale(0.1F, 0.1F, 0.1F);
			stack.translate(tileEntity.offset[0] * frac2, tileEntity.offset[1] * frac2, tileEntity.offset[2] * frac2);

			BlockState state = ModBlocks.CUSTOM.get().defaultBlockState().setValue(CustomBlock.MODEL, tileEntity.model);
			BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
			IBakedModel model = dispatcher.getBlockModel(state);

			MatrixStack.Entry currentMatrix = stack.last();

			IVertexBuilder vertexBuffer = buffers.getBuffer(RenderType.solid());
			int[] colors = tileEntity.color;
			dispatcher.getModelRenderer().renderModel(currentMatrix, vertexBuffer, null, model, colors[0] / 255.0F, colors[0] / 255.0F, colors[0] / 255.0F, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

			stack.popPose();
		} else {
			stack.pushPose();
			stack.translate(0.5, 0.0, 0.5);
			stack.mulPose(Vector3f.YP.rotationDegrees(tileEntity.dir * 15F));
			stack.translate(-0.5, 0.0, -0.5); // Then just get it back...
			stack.translate(tileEntity.offset[0] * frac, tileEntity.offset[1] * frac, tileEntity.offset[2] * frac);

			BlockState state = tileEntity.displayBlock;
			BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
			IBakedModel model = dispatcher.getBlockModel(state);

			MatrixStack.Entry currentMatrix = stack.last();

			IVertexBuilder vertexBuffer = buffers.getBuffer(RenderType.solid());
			int[] colors = tileEntity.color;
			dispatcher.getModelRenderer().renderModel(currentMatrix, vertexBuffer, null, model, colors[0] / 255.0F, colors[0] / 255.0F, colors[0] / 255.0F, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

			stack.popPose();
		}
	}

	@Override
	public boolean shouldRenderOffScreen(CustomBlockEntity tileEntity) {
		return true;
	}
}
