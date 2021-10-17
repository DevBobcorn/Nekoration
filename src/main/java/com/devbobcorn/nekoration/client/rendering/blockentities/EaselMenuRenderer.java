package com.devbobcorn.nekoration.client.rendering.blockentities;

import com.devbobcorn.nekoration.blocks.EaselMenuBlock;
import com.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.DyeColor;

public class EaselMenuRenderer implements BlockEntityRenderer<EaselMenuBlockEntity> {
	Font font;

	public EaselMenuRenderer(BlockEntityRendererProvider.Context ctx) {
		font = ctx.getFont();
	}

	@Override
	public void render(EaselMenuBlockEntity tileEntity, float partialTicks, PoseStack stack, MultiBufferSource buffers, int combinedLight, int combinedOverlay) {
		for (int rot = 0;rot < 2;rot++){
			stack.pushPose();
			// Items on Front Side...
			stack.translate(0.5D, 0.5D, 0.5D);
			stack.mulPose(Vector3f.YP.rotationDegrees(3 - tileEntity.getBlockState().getValue(EaselMenuBlock.FACING).get2DDataValue() * 90.0F + rot * 180.0F));
			
			float sc = 0.5F;
			stack.scale(sc, sc, sc);
	
			stack.mulPose(Vector3f.XP.rotationDegrees(-22.5F));
	
			// 0 1  // 4 5
			// 2 3  // 6 7
			stack.translate(-0.3D, 0.0D, 0.4D);
			Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[0 + rot * 4], ItemTransforms.TransformType.GROUND,
				combinedLight, OverlayTexture.NO_OVERLAY, stack, buffers, 1); // TODO The number
	
			stack.translate(0.6D, 0.0D, 0.0D);
			Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[1 + rot * 4], ItemTransforms.TransformType.GROUND,
				combinedLight, OverlayTexture.NO_OVERLAY, stack, buffers, 1);
	
			stack.translate(0.0D, -0.6D, 0.0D);
			Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[3 + rot * 4], ItemTransforms.TransformType.GROUND,
				combinedLight, OverlayTexture.NO_OVERLAY, stack, buffers, 1);
	
			stack.translate(-0.6D, 0.0D, 0.0D);
			Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[2 + rot * 4], ItemTransforms.TransformType.GROUND,
				combinedLight, OverlayTexture.NO_OVERLAY, stack, buffers, 1);
	
			stack.popPose();
			// Texts on Front Side
			stack.pushPose();
	
			stack.translate(0.5D, 0.5D, 0.5D);
			stack.mulPose(Vector3f.YP.rotationDegrees(3 - tileEntity.getBlockState().getValue(EaselMenuBlock.FACING).get2DDataValue() * 90.0F + rot * 180.0F));
			stack.translate(-0.3D, 0.4D, 0.08D);
			stack.mulPose(Vector3f.XP.rotationDegrees(-22.5F));
	
			sc = 0.015F;
			stack.scale(sc, -sc, sc);

			DyeColor[] colors = tileEntity.getColors();
			
			if (tileEntity.getGlowing()) {
				for (int i = 0;i < 4;i++) {
					font.draw(stack, tileEntity.getMessage(i + rot * 4), 1.0F, 1.0F, colors[i + rot * 4].getTextColor());
					stack.translate(0.0F, 12.0F, 0.0F);
				}
			} else {
				for (int i = 0;i < 4;i++) {
					//Params:                                             left  top   color
					font.drawInBatch(tileEntity.getMessage(i + rot * 4), 1.0F, 1.0F, colors[i + rot * 4].getTextColor(), false, stack.last().pose(), buffers, false, 0, combinedLight);
					stack.translate(0.0F, 12.0F, 0.0F);
				}
			}

			stack.popPose();
		}
	}

	@Override
	public boolean shouldRenderOffScreen(EaselMenuBlockEntity BlockEntity) {
		return false;
	}
}
