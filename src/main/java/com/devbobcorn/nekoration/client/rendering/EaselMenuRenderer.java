package com.devbobcorn.nekoration.client.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.client.renderer.texture.NativeImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import com.devbobcorn.nekoration.blockentities.EaselMenuBlockEnity;
import com.devbobcorn.nekoration.exp.tile_entity.RenderLines;

public class EaselMenuRenderer extends TileEntityRenderer<EaselMenuBlockEnity> {

	public EaselMenuRenderer(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
		super(tileEntityRendererDispatcher);
	}

	@Override
	public void render(EaselMenuBlockEnity tileEntity, float partialTicks, MatrixStack stack,
			IRenderTypeBuffer buffers, int combinedLight, int combinedOverlay) {
		stack.pushPose();
		stack.translate(0.5D, 1.5D, 0.5D);
		stack.translate(0.0D, -0.3125D, -0.4375D);

		FontRenderer fontrenderer = this.renderer.getFont();
		stack.translate(0.0D, (double) 0.33333334F, (double) 0.046666667F);

		stack.scale(0.010416667F, -0.010416667F, 0.010416667F);
		int i = tileEntity.getColor().getTextColor();
		int j = (int) ((double) NativeImage.getR(i) * 0.4D);
		int k = (int) ((double) NativeImage.getG(i) * 0.4D);
		int l = (int) ((double) NativeImage.getB(i) * 0.4D);
		int i1 = NativeImage.combine(0, l, k, j);

		for (int k1 = 0; k1 < 4; ++k1) {
			IReorderingProcessor ireorderingprocessor = tileEntity.getRenderMessage(k1, (texts) -> {
				List<IReorderingProcessor> list = fontrenderer.split(texts, 90);
				return list.isEmpty() ? IReorderingProcessor.EMPTY : list.get(0);
			});
			if (ireorderingprocessor != null) {
				float f3 = (float) (-fontrenderer.width(ireorderingprocessor) / 2);
				fontrenderer.drawInBatch(ireorderingprocessor, f3, (float) (k1 * 10 - 20), i1, false,
						stack.last().pose(), buffers, false, 0, combinedLight);
			}
		}
		stack.popPose();
		
		QuadRenderer.renderCubeUsingQuads(tileEntity, partialTicks, stack, buffers, combinedLight, combinedOverlay);
	}

	@Override
	public boolean shouldRenderOffScreen(EaselMenuBlockEnity tileEntityMBE21) {
		return false;
	}

	private static final Logger LOGGER = LogManager.getLogger();
}
