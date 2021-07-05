package com.devbobcorn.nekoration.client.rendering;

import com.devbobcorn.nekoration.blocks.entities.PhonographBlockEnity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;

public class PhonographRenderer extends TileEntityRenderer<PhonographBlockEnity> {
	public PhonographRenderer(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
		super(tileEntityRendererDispatcher);
	}

	@Override
	public void render(PhonographBlockEnity tileEntity, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffers,
			int combinedLight, int combinedOverlay) {

            stack.pushPose();

            stack.translate(0.17D, 0.9D, 0.55D);
        
            FontRenderer fontrenderer = this.renderer.getFont();
      
            float sc = 0.2F;
            stack.scale(sc, -sc, sc);

			stack.translate(-80.0D, -16.0D, 0.0D);
            fontrenderer.draw(stack, tileEntity.getMessage(0), 1.0F, 1.0F, 0);
    
			/*
            sc = 0.4F;
            stack.translate(-8.0D, -12.0D, 0.0D);
            stack.scale(sc, sc, sc);
            fontrenderer.draw(stack, "\u00a74N\u00a7cE\u00a76K\u00a7eO\u00a7aR\u00a7bA\u00a73T\u00a79I\u00a71O\u00a75N", 1.0F, 1.0f, 65280);
			*/
            stack.popPose();
	}

	@Override
	public boolean shouldRenderOffScreen(PhonographBlockEnity tileEntity) {
		return true;
	}
}
