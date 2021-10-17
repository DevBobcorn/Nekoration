package com.devbobcorn.nekoration.client.rendering.blockentities;

import com.devbobcorn.nekoration.blocks.EaselMenuBlock;
import com.devbobcorn.nekoration.blocks.entities.CupboardBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class CupboardRenderer implements BlockEntityRenderer<CupboardBlockEntity> {
	Font font;

	public CupboardRenderer(BlockEntityRendererProvider.Context ctx) {
		font = ctx.getFont();
	}

	@Override
	public void render(CupboardBlockEntity tileEntity, float partialTicks, PoseStack stack, MultiBufferSource buffers, int combinedLight, int combinedOverlay) {
        stack.pushPose();
        // Items...
        stack.translate(0.5D, 0.5D, 0.5D);
        stack.mulPose(Vector3f.YP.rotationDegrees(3 - tileEntity.getBlockState().getValue(EaselMenuBlock.FACING).get2DDataValue() * 90.0F));
        
        float sc = 0.5F;
        stack.scale(sc, sc, sc);

        stack.mulPose(Vector3f.XP.rotationDegrees(-22.5F));

        // 0 1
        // 2 3
        stack.translate(-0.3D, 0.0D, 0.4D);
        Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[0], ItemTransforms.TransformType.GROUND,
            combinedLight, OverlayTexture.NO_OVERLAY, stack, buffers, 1); // TODO The number

        stack.translate(0.6D, 0.0D, 0.0D);
        Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[1], ItemTransforms.TransformType.GROUND,
            combinedLight, OverlayTexture.NO_OVERLAY, stack, buffers, 1);

        stack.translate(0.0D, -0.6D, 0.0D);
        Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[3], ItemTransforms.TransformType.GROUND,
            combinedLight, OverlayTexture.NO_OVERLAY, stack, buffers, 1);

        stack.translate(-0.6D, 0.0D, 0.0D);
        Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[2], ItemTransforms.TransformType.GROUND,
            combinedLight, OverlayTexture.NO_OVERLAY, stack, buffers, 1);

        stack.popPose();
	}

	@Override
	public boolean shouldRenderOffScreen(CupboardBlockEntity BlockEntity) {
		return false;
	}
}
