package com.devbobcorn.nekoration.client.rendering.blockentities;

import com.devbobcorn.nekoration.blocks.EaselMenuBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.DyeColor;

public class EaselMenuRenderer extends BlockEntityRenderer<EaselMenuBlockEntity> {
	public EaselMenuRenderer(BlockEntityRenderDispatcher BlockEntityRendererDispatcher) {
		super(BlockEntityRendererDispatcher);
	}

	@Override
	public void render(EaselMenuBlockEntity BlockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffers, int combinedLight, int combinedOverlay) {
		for (int rot = 0;rot < 2;rot++){
			stack.pushPose();
			// Items on Front Side...
			stack.translate(0.5D, 0.5D, 0.5D);
			stack.mulPose(Vector3f.YP.rotationDegrees(3 - BlockEntity.getBlockState().getValue(EaselMenuBlock.FACING).get2DDataValue() * 90.0F + rot * 180.0F));
			
			float sc = 0.5F;
			stack.scale(sc, sc, sc);
	
			stack.mulPose(Vector3f.XP.rotationDegrees(-22.5F));
			int lightAbove = WorldRenderer.getLightColor(BlockEntity.getLevel(), BlockEntity.getBlockPos().above());
	
			// 0 1  // 4 5
			// 2 3  // 6 7
			stack.translate(-0.3D, 0.0D, 0.4D);
			Minecraft.getInstance().getItemRenderer().renderStatic(BlockEntity.renderItems[0 + rot * 4], ItemTransforms.TransformType.GROUND,
					lightAbove, OverlayTexture.NO_OVERLAY, stack, buffers);
	
			stack.translate(0.6D, 0.0D, 0.0D);
			Minecraft.getInstance().getItemRenderer().renderStatic(BlockEntity.renderItems[1 + rot * 4], ItemTransforms.TransformType.GROUND,
					lightAbove, OverlayTexture.NO_OVERLAY, stack, buffers);
	
			stack.translate(0.0D, -0.6D, 0.0D);
			Minecraft.getInstance().getItemRenderer().renderStatic(BlockEntity.renderItems[3 + rot * 4], ItemTransforms.TransformType.GROUND,
					lightAbove, OverlayTexture.NO_OVERLAY, stack, buffers);
	
			stack.translate(-0.6D, 0.0D, 0.0D);
			Minecraft.getInstance().getItemRenderer().renderStatic(BlockEntity.renderItems[2 + rot * 4], ItemTransforms.TransformType.GROUND,
					lightAbove, OverlayTexture.NO_OVERLAY, stack, buffers);
	
			stack.popPose();
			// Texts on Front Side
			stack.pushPose();
	
			stack.translate(0.5D, 0.5D, 0.5D);
			stack.mulPose(Vector3f.YP.rotationDegrees(3 - BlockEntity.getBlockState().getValue(EaselMenuBlock.FACING).get2DDataValue() * 90.0F + rot * 180.0F));
			stack.translate(-0.3D, 0.4D, 0.08D);
			FontRenderer fontrenderer = this.renderer.getFont();
			stack.mulPose(Vector3f.XP.rotationDegrees(-22.5F));
	
			sc = 0.015F;
			stack.scale(sc, -sc, sc);

			DyeColor[] colors = BlockEntity.getColor();
			
			if (BlockEntity.getGlowing()) {
				for (int i = 0;i < 4;i++) {
					fontrenderer.draw(stack, BlockEntity.getMessage(i + rot * 4), 1.0F, 1.0F, colors[i + rot * 4].getColorValue());
					stack.translate(0.0F, 12.0F, 0.0F);
				}
			} else {
				for (int i = 0;i < 4;i++) {
					//Params:                                                    left  top   color
					fontrenderer.drawInBatch(BlockEntity.getMessage(i + rot * 4), 1.0F, 1.0F, colors[i + rot * 4].getColorValue(), false, stack.last().pose(), buffers, false, 0, combinedLight);
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
