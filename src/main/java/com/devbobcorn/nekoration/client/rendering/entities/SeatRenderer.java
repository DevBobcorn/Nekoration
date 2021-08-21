package com.devbobcorn.nekoration.client.rendering.entities;

import com.devbobcorn.nekoration.entities.SeatEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SeatRenderer extends EntityRenderer<SeatEntity> {
	public SeatRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	public void render(SeatEntity entity, float rotation, float partialTicks, PoseStack stack, MultiBufferSource buffers, int packedLight) {}

    @Override
    public ResourceLocation getTextureLocation(SeatEntity entity) {
        return null;
    }
}