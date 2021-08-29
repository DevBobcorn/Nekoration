package com.devbobcorn.nekoration.client.rendering.entities;

import com.devbobcorn.nekoration.client.rendering.PaintingRendererManager;
import com.devbobcorn.nekoration.entities.PaintingEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PaintingRenderer extends EntityRenderer<PaintingEntity> {
	Font font;
	public PaintingRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
		font = ctx.getFont();
	}

	public void render(PaintingEntity entity, float rotation, float partialTicks, PoseStack stack, MultiBufferSource buffers, int packedLight) {
		stack.pushPose();
		stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - rotation));

		stack.scale(0.0625F, 0.0625F, 0.0625F);
		PaintingTextureManager paintingspriteuploader = Minecraft.getInstance().getPaintingTextures();
		//renderPainting(stack, vb, entity, paintingtype.getWidth(), paintingtype.getHeight(), paintingspriteuploader.get(paintingtype), paintingspriteuploader.getBackSprite());
		renderPainting(stack, buffers, entity, entity.getWidth(), entity.getHeight(), paintingspriteuploader.getBackSprite());
		stack.popPose();
		super.render(entity, rotation, partialTicks, stack, buffers, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(PaintingEntity entity) {
		return Minecraft.getInstance().getPaintingTextures().getBackSprite().atlas().location();
	}

	private void renderPainting(PoseStack stack, MultiBufferSource buffers, PaintingEntity entity, int width, int height, TextureAtlasSprite woodTex) {
		PoseStack.Pose PoseStack$entry = stack.last();
		Matrix4f pose = PoseStack$entry.pose();
		Matrix3f normal = PoseStack$entry.normal();
		float LEFT = (float) (-width) / 2.0F;
		float TOP = (float) (-height) / 2.0F;

		float woodU0 = woodTex.getU0();
		float woodU1 = woodTex.getU1();
		float woodV0 = woodTex.getV0();
		float woodV1 = woodTex.getV1();
		float woodV_ = woodTex.getV(1.0D);
		float woodU_ = woodTex.getU(1.0D);

		short blocHorCount = (short)(width / 16);
		short blocVerCount = (short)(height / 16);

		for (short blocHor = 0; blocHor < blocHorCount; ++blocHor) { // BlockCount Horizontally...
			for (short blocVer = 0; blocVer < blocVerCount; ++blocVer) { // BlockCount Vertically...
				// Get the VertexBuffer for Frame Rendering...
				// Draw a 16 * 16 sized painting section at a time...
				// This allows us to render different sections with more specific lighting values...
				float right  = LEFT + (float) ((blocHor + 1) * 16);
				float left   = LEFT + (float) (blocHor * 16);
				float top    = TOP + (float) ((blocVer + 1) * 16);
				float bottom = TOP + (float) (blocVer * 16);
				// Get the accurate Block Position, thus get a better lighting value
				int blocx = Mth.floor(entity.getX());
				int blocy = Mth.floor(entity.getY() + (double) ((top + bottom) / 2.0F / 16.0F));
				int blocz = Mth.floor(entity.getZ());
				Direction direction = entity.getDirection();
				if (direction == Direction.NORTH)
					blocx = Mth.floor(entity.getX() + (double) ((right + left) / 2.0F / 16.0F));
				if (direction == Direction.WEST)
					blocz = Mth.floor(entity.getZ() - (double) ((right + left) / 2.0F / 16.0F));
				if (direction == Direction.SOUTH)
					blocx = Mth.floor(entity.getX() - (double) ((right + left) / 2.0F / 16.0F));
				if (direction == Direction.EAST)
					blocz = Mth.floor(entity.getZ() + (double) ((right + left) / 2.0F / 16.0F));
				int light = LevelRenderer.getLightColor(entity.level, new BlockPos(blocx, blocy, blocz));
				VertexConsumer vb = buffers.getBuffer(RenderType.entitySolid(this.getTextureLocation(entity)));
				// Pos[Z] // B[ack]
				vertexFrame(pose, normal, vb, right, top,    woodU0, woodV0,  0.5F, 0, 0,  1, light);
				vertexFrame(pose, normal, vb, left,  top,    woodU1, woodV0,  0.5F, 0, 0,  1, light);
				vertexFrame(pose, normal, vb, left,  bottom, woodU1, woodV1,  0.5F, 0, 0,  1, light);
				vertexFrame(pose, normal, vb, right, bottom, woodU0, woodV1,  0.5F, 0, 0,  1, light);
				// Pos[Y] // U[p]
				vertexFrame(pose, normal, vb, right, top,    woodU0, woodV0, -0.5F, 0,  1, 0, light);
				vertexFrame(pose, normal, vb, left,  top,    woodU1, woodV0, -0.5F, 0,  1, 0, light);
				vertexFrame(pose, normal, vb, left,  top,    woodU1, woodV_,  0.5F, 0,  1, 0, light);
				vertexFrame(pose, normal, vb, right, top,    woodU0, woodV_,  0.5F, 0,  1, 0, light);
				// Neg[Y] // D[own]
				vertexFrame(pose, normal, vb, right, bottom, woodU0, woodV0,  0.5F, 0, -1, 0, light);
				vertexFrame(pose, normal, vb, left,  bottom, woodU1, woodV0,  0.5F, 0, -1, 0, light);
				vertexFrame(pose, normal, vb, left,  bottom, woodU1, woodV_, -0.5F, 0, -1, 0, light);
				vertexFrame(pose, normal, vb, right, bottom, woodU0, woodV_, -0.5F, 0, -1, 0, light);
				// Neg[X] // L[eft]
				vertexFrame(pose, normal, vb, right, top,    woodU_, woodV0,  0.5F, -1, 0, 0, light);
				vertexFrame(pose, normal, vb, right, bottom, woodU_, woodV1,  0.5F, -1, 0, 0, light);
				vertexFrame(pose, normal, vb, right, bottom, woodU0, woodV1, -0.5F, -1, 0, 0, light);
				vertexFrame(pose, normal, vb, right, top,    woodU0, woodV0, -0.5F, -1, 0, 0, light);
				// Pos[X] // R[ight]
				vertexFrame(pose, normal, vb, left,  top,    woodU_, woodV0, -0.5F,  1, 0, 0, light);
				vertexFrame(pose, normal, vb, left,  bottom, woodU_, woodV1, -0.5F,  1, 0, 0, light);
				vertexFrame(pose, normal, vb, left,  bottom, woodU0, woodV1,  0.5F,  1, 0, 0, light);
				vertexFrame(pose, normal, vb, left,  top,    woodU0, woodV0,  0.5F,  1, 0, 0, light);
				// Then render the artwork
				AbstractPaintingRenderer rd = null;
				if (entity.data.imageReady) {
					rd = PaintingRendererManager.get(entity.data.getPaintingHash());
					if (rd == null){
						// Reset the Pixel Renderer...
						System.err.println("Image Renderer Not Ready!");
						entity.data.imageReady = false;
						rd = PaintingRendererManager.PixelsRenderer();
					}
				} else rd = PaintingRendererManager.PixelsRenderer();
				rd.render(stack, pose, normal, buffers, entity.data, blocHor, blocVer, left, bottom, light);
			}
		}
		// Draw Debug Text...
		stack.pushPose();
		stack.translate(-LEFT - 1.0D, TOP + 3.0D, -0.6D);
		stack.scale(-0.2F, -0.2F, 0.2F);
		//font.draw(stack, "Ceci n'est pas une painting!", 1.0F, 1.0F, 0xFFFFFF);
		font.draw(stack, (entity.data.imageReady) ? "Rendered with Image" : "Rendered Pixel-by-Pixel", 1.0F, 1.0F, 0xFFFFFF);
		stack.translate(0.0D, -10.0D, 0.0D);
		int light = LevelRenderer.getLightColor(entity.level, entity.getPos());
		font.draw(stack, "#" + String.valueOf(entity.data.getPaintingHash()) + String.format(" L: %x", light), 1.0F, 1.0F, 0xFFFFFF);
		stack.translate(0.0D, 30.0D, 0.0D);
		font.draw(stack, String.valueOf(entity.data.getUUID()), 1.0F, 1.0F, 0xFFFFFF);
		stack.translate(0.0D, 10.0D, 0.0D);
		font.draw(stack, String.valueOf(entity.getUUID()), 1.0F, 1.0F, 0xFFFFFF);
		stack.popPose();
	}

	private static void vertexFrame(Matrix4f pose, Matrix3f normal, VertexConsumer vertexBuilder, float x, float y, float u, float v, float z, int nx, int ny, int nz, int light) {
		vertexBuilder.vertex(pose, x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, (float)nx, (float)ny, (float)nz).endVertex();
	}

}
