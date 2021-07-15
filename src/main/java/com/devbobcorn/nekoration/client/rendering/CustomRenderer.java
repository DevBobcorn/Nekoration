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

public class CustomRenderer extends TileEntityRenderer<CustomBlockEntity> {
	public CustomRenderer(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
		super(tileEntityRendererDispatcher);
	}

	@Override
	public void render(CustomBlockEntity tileEntity, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffers,
			int combinedLight, int combinedOverlay) {
		if (tileEntity.model > 15 || tileEntity.model <= 0){
			//QuadRenderer.renderCubeUsingQuads(tileEntity, partialTicks, stack, buffers, combinedLight, combinedOverlay);
		} else {
			stack.pushPose();

			stack.translate(0.5, 1.0, 0.5);
			//stack.translate(0, 1, 0);
			stack.scale(0.1F, 0.1F, 0.1F);

			BlockState state = ModBlocks.CUSTOM.get().defaultBlockState().setValue(CustomBlock.MODEL, tileEntity.model);
			BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
			IBakedModel model = dispatcher.getBlockModel(state);

			MatrixStack.Entry currentMatrix = stack.last();

			IVertexBuilder vertexBuffer = buffers.getBuffer(RenderType.solid());
			dispatcher.getModelRenderer().renderModel(currentMatrix, vertexBuffer, null, model, 1.0F, 1.0F, 1.0F, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

			stack.popPose();
		}
	}

	@Override
	public boolean shouldRenderOffScreen(CustomBlockEntity tileEntity) {
		return true;
	}
}
