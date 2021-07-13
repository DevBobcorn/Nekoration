package com.devbobcorn.nekoration.client.rendering;

import com.devbobcorn.nekoration.blocks.EaselMenuBlock;
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
import net.minecraft.util.math.vector.Vector3f;

public class EaselMenuRenderer extends TileEntityRenderer<EaselMenuBlockEntity> {
	public EaselMenuRenderer(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
		super(tileEntityRendererDispatcher);
	}

	@Override
	public void render(EaselMenuBlockEntity tileEntity, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffers,
			int combinedLight, int combinedOverlay) {
		for (int rot = 0;rot < 2;rot++){
			stack.pushPose();
			// Items on Front Side...
			stack.translate(0.5D, 0.5D, 0.5D);
			stack.mulPose(Vector3f.YP.rotationDegrees(3 - tileEntity.getBlockState().getValue(EaselMenuBlock.FACING).get2DDataValue() * 90.0F + rot * 180.0F));
			
			float sc = 0.5F;
			stack.scale(sc, sc, sc);
	
			stack.mulPose(Vector3f.XP.rotationDegrees(-22.5F));
			int lightAbove = WorldRenderer.getLightColor(tileEntity.getLevel(), tileEntity.getBlockPos().above());
	
			// 0 1  // 4 5
			// 2 3  // 6 7
			stack.translate(-0.3D, 0.0D, 0.4D);
			try {
				Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[0 + rot * 4], ItemCameraTransforms.TransformType.GROUND,
						lightAbove, OverlayTexture.NO_OVERLAY, stack, buffers);
		
				stack.translate(0.6D, 0.0D, 0.0D);
				Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[1 + rot * 4], ItemCameraTransforms.TransformType.GROUND,
						lightAbove, OverlayTexture.NO_OVERLAY, stack, buffers);
		
				stack.translate(0.0D, -0.6D, 0.0D);
				Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[3 + rot * 4], ItemCameraTransforms.TransformType.GROUND,
						lightAbove, OverlayTexture.NO_OVERLAY, stack, buffers);
		
				stack.translate(-0.6D, 0.0D, 0.0D);
				Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[2 + rot * 4], ItemCameraTransforms.TransformType.GROUND,
						lightAbove, OverlayTexture.NO_OVERLAY, stack, buffers);
			} catch (Exception e){
	
			}
	
			stack.popPose();
			// Texts on Front Side
			stack.pushPose();
	
			stack.translate(0.5D, 0.5D, 0.5D);
			stack.mulPose(Vector3f.YP.rotationDegrees(3 - tileEntity.getBlockState().getValue(EaselMenuBlock.FACING).get2DDataValue() * 90.0F + rot * 180.0F));
			stack.translate(-0.3D, 0.4D, 0.08D);
			FontRenderer fontrenderer = this.renderer.getFont();
			stack.mulPose(Vector3f.XP.rotationDegrees(-22.5F));
	
			sc = 0.015F;
			stack.scale(sc, -sc, sc);
			
			for (int i = 0;i < 4;i++) {
				fontrenderer.draw(stack, tileEntity.getMessage(i + rot * 4), 1.0F, 1.0F, 16777215);
				stack.translate(0.0F, 12.0F, 0.0F);
			}

			stack.popPose();
		}
	}

	@Override
	public boolean shouldRenderOffScreen(EaselMenuBlockEntity tileEntity) {
		return false;
	}
}
