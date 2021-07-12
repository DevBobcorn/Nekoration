package com.devbobcorn.nekoration.client.rendering;

import com.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;

public class EaselMenuRenderer extends TileEntityRenderer<EaselMenuBlockEntity> {
	private static ItemStack itemstack = new ItemStack(Items.COOKIE, 1);

	public EaselMenuRenderer(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
		super(tileEntityRendererDispatcher);
	}

	@Override
	public void render(EaselMenuBlockEntity tileEntity, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffers,
			int combinedLight, int combinedOverlay) {
		
		/*
		stack.pushPose();
		double offset = Math.sin((tileEntity.getLevel().dayTime() + partialTicks) / 8.0) / 4.0;
		stack.translate(0.5, 1.25 + offset, 0.5);
		stack.mulPose(Vector3f.YP.rotationDegrees((tileEntity.getLevel().dayTime() + partialTicks) * 4F));

		int lightAbove = WorldRenderer.getLightColor(tileEntity.getLevel(), tileEntity.getBlockPos().above());
		Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemCameraTransforms.TransformType.GROUND,
				lightAbove, OverlayTexture.NO_OVERLAY, stack, buffers);

		stack.popPose();
		*/

		stack.pushPose();

		stack.translate(0.5D, 0.9D, 0.56D);
		stack.mulPose(Vector3f.XP.rotationDegrees(-22.5F));
		stack.translate(0.0D, -0.5D, 0.0D);

		float sc = 0.5F;
		stack.scale(sc, sc, sc);

		int lightAbove = WorldRenderer.getLightColor(tileEntity.getLevel(), tileEntity.getBlockPos().above());
		Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemCameraTransforms.TransformType.GROUND,
				lightAbove, OverlayTexture.NO_OVERLAY, stack, buffers);

		stack.popPose();

		// QuadRenderer.renderCubeUsingQuads(tileEntity, partialTicks, stack, buffers, combinedLight, combinedOverlay);

		stack.pushPose();

		stack.translate(0.17D, 0.9D, 0.55D);
  
		FontRenderer fontrenderer = this.renderer.getFont();
		stack.mulPose(Vector3f.XP.rotationDegrees(-22.5F));

		sc = 0.020F;
		stack.scale(sc, -sc, sc);

		ITextComponent itextcomponent;
		try {
			itextcomponent = ITextComponent.Serializer.fromJsonLenient("{\"text\":\"Cookies\", \"color\":\"white\", \"font\":\"segoesc\"}");
		} catch (Exception exception) {
			itextcomponent = ITextComponent.Serializer.fromJsonLenient("{\"text\":\"ERROR\", \"color\":\"red\"}");
		}

		//fontrenderer.draw(stack, "TEST", 1.0F, 1.0F, 65535);
		fontrenderer.draw(stack, itextcomponent, 1.0F, 1.0F, 0);

		sc = 2.0F;
		stack.translate(-8.0D, -22.0, 0.0D);
		stack.scale(sc, sc, sc);
		fontrenderer.draw(stack, "\u00a74N\u00a7cE\u00a76K\u00a7eO\u00a7aR\u00a7bA\u00a73T\u00a79I\u00a71O\u00a75N", 1.0F, 1.0f, 65280);

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
	public boolean shouldRenderOffScreen(EaselMenuBlockEntity tileEntity) {
		return false;
	}
}
