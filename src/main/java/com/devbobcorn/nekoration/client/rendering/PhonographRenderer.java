package com.devbobcorn.nekoration.client.rendering;

import com.devbobcorn.nekoration.blocks.entities.PhonographBlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class PhonographRenderer extends TileEntityRenderer<PhonographBlockEntity> {
	public PhonographRenderer(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
		super(tileEntityRendererDispatcher);
	}

	@Override
	public void render(PhonographBlockEntity tileEntity, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffers,
			int combinedLight, int combinedOverlay) {
        stack.pushPose();
        stack.scale(0.1F, 0.1F, 0.1F);
        Minecraft.getInstance().font.draw(stack, "p_238421_2_", 1.0F, 1.0F, 1);
        stack.popPose();
	}

	@Override
	public boolean shouldRenderOffScreen(PhonographBlockEntity tileEntity) {
		return false;
	}
}
