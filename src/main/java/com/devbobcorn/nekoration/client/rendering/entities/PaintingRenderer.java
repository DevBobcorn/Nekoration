package com.devbobcorn.nekoration.client.rendering.entities;

import com.devbobcorn.nekoration.client.rendering.AbstractPaintingRenderer;
import com.devbobcorn.nekoration.client.rendering.PaintingRendererManager;
import com.devbobcorn.nekoration.entities.PaintingEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.PaintingSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class PaintingRenderer extends EntityRenderer<PaintingEntity> {
	public PaintingRenderer(EntityRendererManager manager) {
		super(manager);
	}

	public void render(PaintingEntity entity, float rotation, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffers, int packedLight) {
		stack.pushPose();
		stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - rotation));

		stack.scale(0.0625F, 0.0625F, 0.0625F); 
		PaintingSpriteUploader paintingspriteuploader = Minecraft.getInstance().getPaintingTextures();
		//renderPainting(stack, ivertexbuilder, entity, paintingtype.getWidth(), paintingtype.getHeight(), paintingspriteuploader.get(paintingtype), paintingspriteuploader.getBackSprite());
		renderPainting(stack, buffers, entity, entity.getWidth(), entity.getHeight(), paintingspriteuploader.getBackSprite());
		stack.popPose();
		super.render(entity, rotation, partialTicks, stack, buffers, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(PaintingEntity entity) {
		return Minecraft.getInstance().getPaintingTextures().getBackSprite().atlas().location();
	}

	@SuppressWarnings("resource")
	private void renderPainting(MatrixStack stack, IRenderTypeBuffer buffers, PaintingEntity entity, int width, int height, TextureAtlasSprite woodTex) {
		MatrixStack.Entry matrixstack$entry = stack.last();
		Matrix4f pose = matrixstack$entry.pose();
		Matrix3f normal = matrixstack$entry.normal();
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
				IVertexBuilder vb1 = buffers.getBuffer(RenderType.entitySolid(this.getTextureLocation(entity)));
				// Draw a 16 * 16 sized painting section at a time...
				// This allows us to render different sections with more specific lighting values...
				float right  = LEFT + (float) ((blocHor + 1) * 16);
				float left   = LEFT + (float) (blocHor * 16);
				float top    = TOP + (float) ((blocVer + 1) * 16);
				float bottom = TOP + (float) (blocVer * 16);
				// Get the accurate Block Position, thus get a better lighting value
				int blocx = MathHelper.floor(entity.getX());
				int blocy = MathHelper.floor(entity.getY() + (double) ((top + bottom) / 2.0F / 16.0F));
				int blocz = MathHelper.floor(entity.getZ());
				Direction direction = entity.getDirection();
				if (direction == Direction.NORTH)
					blocx = MathHelper.floor(entity.getX() + (double) ((right + left) / 2.0F / 16.0F));
				if (direction == Direction.WEST)
					blocz = MathHelper.floor(entity.getZ() - (double) ((right + left) / 2.0F / 16.0F));
				if (direction == Direction.SOUTH)
					blocx = MathHelper.floor(entity.getX() - (double) ((right + left) / 2.0F / 16.0F));
				if (direction == Direction.EAST)
					blocz = MathHelper.floor(entity.getZ() + (double) ((right + left) / 2.0F / 16.0F));
				int light = WorldRenderer.getLightColor(entity.level, new BlockPos(blocx, blocy, blocz));
				// Pos[Z] // B[ack]
				vertexFrame(pose, normal, vb1, right, top,    woodU0, woodV0,  0.5F, 0, 0,  1, light); 
				vertexFrame(pose, normal, vb1, left,  top,    woodU1, woodV0,  0.5F, 0, 0,  1, light);
				vertexFrame(pose, normal, vb1, left,  bottom, woodU1, woodV1,  0.5F, 0, 0,  1, light);
				vertexFrame(pose, normal, vb1, right, bottom, woodU0, woodV1,  0.5F, 0, 0,  1, light);
				// Pos[Y] // U[p]
				vertexFrame(pose, normal, vb1, right, top,    woodU0, woodV0, -0.5F, 0,  1, 0, light);
				vertexFrame(pose, normal, vb1, left,  top,    woodU1, woodV0, -0.5F, 0,  1, 0, light);
				vertexFrame(pose, normal, vb1, left,  top,    woodU1, woodV_,  0.5F, 0,  1, 0, light);
				vertexFrame(pose, normal, vb1, right, top,    woodU0, woodV_,  0.5F, 0,  1, 0, light);
				// Neg[Y] // D[own]
				vertexFrame(pose, normal, vb1, right, bottom, woodU0, woodV0,  0.5F, 0, -1, 0, light);
				vertexFrame(pose, normal, vb1, left,  bottom, woodU1, woodV0,  0.5F, 0, -1, 0, light);
				vertexFrame(pose, normal, vb1, left,  bottom, woodU1, woodV_, -0.5F, 0, -1, 0, light);
				vertexFrame(pose, normal, vb1, right, bottom, woodU0, woodV_, -0.5F, 0, -1, 0, light);
				// Neg[X] // L[eft]
				vertexFrame(pose, normal, vb1, right, top,    woodU_, woodV0,  0.5F, -1, 0, 0, light);
				vertexFrame(pose, normal, vb1, right, bottom, woodU_, woodV1,  0.5F, -1, 0, 0, light);
				vertexFrame(pose, normal, vb1, right, bottom, woodU0, woodV1, -0.5F, -1, 0, 0, light);
				vertexFrame(pose, normal, vb1, right, top,    woodU0, woodV0, -0.5F, -1, 0, 0, light);
				// Pos[X] // R[ight]
				vertexFrame(pose, normal, vb1, left,  top,    woodU_, woodV0, -0.5F,  1, 0, 0, light);
				vertexFrame(pose, normal, vb1, left,  bottom, woodU_, woodV1, -0.5F,  1, 0, 0, light);
				vertexFrame(pose, normal, vb1, left,  bottom, woodU0, woodV1,  0.5F,  1, 0, 0, light);
				vertexFrame(pose, normal, vb1, left,  top,    woodU0, woodV0,  0.5F,  1, 0, 0, light);
				// Then render the artwork
				AbstractPaintingRenderer rd = null;
				if (entity.data.imageReady) {
					rd = PaintingRendererManager.get(entity.data.getPaintingHash());
					if (rd == null){
						// Reset and the Pixel Renderer...
						System.err.println("Image Renderer Not Ready!");
						entity.data.imageReady = false;
						rd = PaintingRendererManager.PixelsRenderer();
					}
				} else rd = PaintingRendererManager.PixelsRenderer();
				rd.render(stack, pose, normal, buffers, entity.data, blocHor, blocVer, left, bottom, light);
				// Draw Debug Text...
				stack.pushPose();
				stack.translate(-LEFT - 1.0D, TOP + 3.0D, -0.6D);
				stack.scale(-0.2F, -0.2F, 0.2F);
				//Minecraft.getInstance().font.draw(stack, "Ceci n'est pas une painting!", 1.0F, 1.0F, 0xFFFFFF);
				Minecraft.getInstance().font.draw(stack, "P:" + String.valueOf(entity.data.getPaintingHash()) + (entity.data.imageReady ? "T" : "F"), 1.0F, 1.0F, 0xFFFFFF);
				stack.popPose();
			}
		}

	}

	private static void vertexFrame(Matrix4f pose, Matrix3f normal, IVertexBuilder vertexBuilder, float x, float y, float u, float v, float z, int nx, int ny, int nz, int light) {
		vertexBuilder.vertex(pose, x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, (float)nx, (float)ny, (float)nz).endVertex();
	}

}
