package com.devbobcorn.nekoration.client.rendering.blockentities;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.entities.PhonographBlockEntity;
import com.devbobcorn.nekoration.client.rendering.RenderTypeHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class PhonographRenderer implements BlockEntityRenderer<PhonographBlockEntity> {
	public static final ResourceLocation CAT_SKY = new ResourceLocation(Nekoration.MODID, "textures/misc/cat_sky.png");
	public static final ResourceLocation CAT_PORTAL = new ResourceLocation(Nekoration.MODID, "textures/misc/cat_portal.png");

	public PhonographRenderer(BlockEntityRendererProvider.Context ctx) {
	}

	public void render(PhonographBlockEntity tileEntity, float partialTicks, PoseStack stack, MultiBufferSource buffers, int combinedLight, int combinedOverlay) {
		Matrix4f matrix4f = stack.last().pose();
		this.renderCube(tileEntity, 1.0F, matrix4f, buffers.getBuffer(this.renderType()));
	}

	private void renderCube(PhonographBlockEntity tileEntity, float hght, Matrix4f pose, VertexConsumer vertexBuilder) {
		float gap = 0.125F;
		this.renderFace(tileEntity, pose, vertexBuilder, 0.0F + gap, 1.0F - gap, 0.0F + gap, 1.0F - gap, 1.0F - gap, 1.0F - gap, 1.0F - gap, 1.0F - gap, Direction.SOUTH);
		this.renderFace(tileEntity, pose, vertexBuilder, 0.0F + gap, 1.0F - gap, 1.0F - gap, 0.0F + gap, 0.0F + gap, 0.0F + gap, 0.0F + gap, 0.0F + gap, Direction.NORTH);
		this.renderFace(tileEntity, pose, vertexBuilder, 1.0F - gap, 1.0F - gap, 1.0F - gap, 0.0F + gap, 0.0F + gap, 1.0F - gap, 1.0F - gap, 0.0F + gap, Direction.EAST);
		this.renderFace(tileEntity, pose, vertexBuilder, 0.0F + gap, 0.0F + gap, 0.0F + gap, 1.0F - gap, 0.0F + gap, 1.0F - gap, 1.0F - gap, 0.0F + gap, Direction.WEST);
		this.renderFace(tileEntity, pose, vertexBuilder, 0.0F + gap, 1.0F - gap, 0.0F + gap, 0.0F + gap, 0.0F + gap, 0.0F + gap, 1.0F - gap, 1.0F - gap, Direction.DOWN);
		this.renderFace(tileEntity, pose, vertexBuilder, 0.0F + gap, 1.0F - gap, hght - gap, hght - gap, 1.0F - gap, 1.0F - gap, 0.0F + gap, 0.0F + gap, Direction.UP);
	}

	private void renderFace(PhonographBlockEntity tileEntity, Matrix4f pose, VertexConsumer vertexBuilder, float x1, float x2, float y1, float y2, float z1, float z2, float z3, float z4, Direction dir) {
		if (tileEntity.shouldRenderFace(dir)) {
			vertexBuilder.vertex(pose, x1, y1, z1).endVertex();
			vertexBuilder.vertex(pose, x2, y1, z2).endVertex();
			vertexBuilder.vertex(pose, x2, y2, z3).endVertex();
			vertexBuilder.vertex(pose, x1, y2, z4).endVertex();
		}
	}

	protected RenderType renderType() {
		return RenderTypeHelper.catPortal();
	}
}
