package com.devbobcorn.nekoration.client.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.*;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.OverlayTexture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import com.devbobcorn.nekoration.blockentities.EaselMenuBlockEnity;

public class EaselMenuRenderer extends TileEntityRenderer<EaselMenuBlockEnity> {
	private static ItemStack itemstack = new ItemStack(Items.COOKIE, 1);

	public EaselMenuRenderer(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
		super(tileEntityRendererDispatcher);
	}

	@Override
	public void render(EaselMenuBlockEnity tileEntity, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffers,
			int combinedLight, int combinedOverlay) {
		stack.pushPose();
		double offset = Math.sin((tileEntity.getLevel().dayTime() + partialTicks) / 8.0) / 4.0;
		stack.translate(0.5, 1.25 + offset, 0.5);
		stack.mulPose(Vector3f.YP.rotationDegrees((tileEntity.getLevel().dayTime() + partialTicks) * 4F));

		int lightAbove = WorldRenderer.getLightColor(tileEntity.getLevel(), tileEntity.getBlockPos().above());

		Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemCameraTransforms.TransformType.GROUND,
				lightAbove, OverlayTexture.NO_OVERLAY, stack, buffers);

		stack.popPose();

		// QuadRenderer.renderCubeUsingQuads(tileEntity, partialTicks, stack, buffers, combinedLight, combinedOverlay);

		stack.pushPose();

		stack.translate(0.0D, 0.9D, 1.0D);
		//stack.translate(0.0D, -0.3125D, -0.4375D);
  
		FontRenderer fontrenderer = this.renderer.getFont();
		stack.mulPose(Vector3f.XP.rotationDegrees(-22.5F));
		//stack.translate(0.0D, (double)0.33333334F, (double)0.046666667F);

		float sc = 0.1F; //0.010416667F
		stack.scale(sc, -sc, sc);

		fontrenderer.draw(stack, "TESUTOOOOOOOOOO", 1.0F, 1.0f, 65535);

		/*
		int i = tileEntity.getColor().getTextColor();

		int j = (int)((double)NativeImage.getR(i) * 0.4D);
		int k = (int)((double)NativeImage.getG(i) * 0.4D);
		int l = (int)((double)NativeImage.getB(i) * 0.4D);
		int i1 = NativeImage.combine(0, l, k, j);
  
		for(int k1 = 0; k1 < 4; ++k1) {
		   IReorderingProcessor ireorderingprocessor = tileEntity.getRenderMessage(k1, (p_243502_1_) -> {
			  List<IReorderingProcessor> list = fontrenderer.split(p_243502_1_, 90);
			  return list.isEmpty() ? IReorderingProcessor.EMPTY : list.get(0);
		   });
		   if (ireorderingprocessor != null) {
			  float f3 = (float)(-fontrenderer.width(ireorderingprocessor) / 2);
			  fontrenderer.drawInBatch(ireorderingprocessor, f3, (float)(k1 * 10 - 20), i1, false, stack.last().pose(), buffers, false, 0, combinedLight);
		   }
		}
		*/
  
		stack.popPose();
	}

	@Override
	public boolean shouldRenderOffScreen(EaselMenuBlockEnity tileEntityMBE21) {
		return false;
	}

	private static final Logger LOGGER = LogManager.getLogger();
}
