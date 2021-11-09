package com.devbobcorn.nekoration.client.rendering.blockentities;

import com.devbobcorn.nekoration.blocks.ItemDisplayBlock;
import com.devbobcorn.nekoration.blocks.entities.ItemDisplayBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class CupboardRenderer implements BlockEntityRenderer<ItemDisplayBlockEntity> {
	Font font;

	public CupboardRenderer(BlockEntityRendererProvider.Context ctx) {
		font = ctx.getFont();
	}

	@Override
	public void render(ItemDisplayBlockEntity tileEntity, float partialTicks, PoseStack stack, MultiBufferSource buffers, int combinedLight, int combinedOverlay) {
        stack.pushPose();
        if (tileEntity.shelf)
            renderShelfItems(tileEntity, stack, buffers, combinedLight);
        else renderCabinetItems(tileEntity, stack, buffers, combinedLight);
        stack.popPose();
	}

    private void renderShelfItems(ItemDisplayBlockEntity tileEntity, PoseStack stack, MultiBufferSource buffers, int combinedLight){
        // Items...
        stack.translate(0.5D, 0.5D, 0.5D);
        stack.mulPose(Vector3f.YP.rotationDegrees(-tileEntity.getBlockState().getValue(ItemDisplayBlock.FACING).get2DDataValue() * 90.0F));
        float sc = 0.5F;
        stack.scale(sc, sc, sc);
        stack.mulPose(Vector3f.XP.rotationDegrees(-10));

        int rand = (int)tileEntity.getBlockPos().asLong();

        // 0 1 2 3
        stack.translate(-1.35D, 0.2D, -0.5D);
        for (int i = 0;i < 4;i++){
            stack.translate(0.55D, 0.0D, -0.0D);
            Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[i], ItemTransforms.TransformType.GROUND,
                combinedLight, OverlayTexture.NO_OVERLAY, stack, buffers, rand + i + 1);
        }
    }

    private void renderCabinetItems(ItemDisplayBlockEntity tileEntity, PoseStack stack, MultiBufferSource buffers, int combinedLight){
        // Items...
        stack.translate(0.5D, 0.5D, 0.5D);
        stack.mulPose(Vector3f.YP.rotationDegrees(-tileEntity.getBlockState().getValue(ItemDisplayBlock.FACING).get2DDataValue() * 90.0F));
        float sc = 0.5F;
        stack.scale(sc, sc, sc);
        stack.mulPose(Vector3f.XP.rotationDegrees(-10));

        int rand = (int)tileEntity.getBlockPos().asLong();

        // 0 1
        // 2 3
        stack.translate(-0.4D, 0.2D, -0.5D);
        Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[0], ItemTransforms.TransformType.GROUND,
            combinedLight, OverlayTexture.NO_OVERLAY, stack, buffers, rand + 1);

        stack.translate(0.8D, 0.0D, 0.0D);
        Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[1], ItemTransforms.TransformType.GROUND,
            combinedLight, OverlayTexture.NO_OVERLAY, stack, buffers, rand + 2);

        stack.translate(0.0D, -0.7D, -0.2D);
        Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[3], ItemTransforms.TransformType.GROUND,
            combinedLight, OverlayTexture.NO_OVERLAY, stack, buffers, rand + 3);

        stack.translate(-0.8D, 0.0D, 0.0D);
        Minecraft.getInstance().getItemRenderer().renderStatic(tileEntity.renderItems[2], ItemTransforms.TransformType.GROUND,
            combinedLight, OverlayTexture.NO_OVERLAY, stack, buffers, rand + 4);
    }

	@Override
	public boolean shouldRenderOffScreen(ItemDisplayBlockEntity BlockEntity) {
		return false;
	}
}
