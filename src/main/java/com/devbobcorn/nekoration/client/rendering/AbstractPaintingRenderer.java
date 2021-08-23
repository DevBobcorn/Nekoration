package com.devbobcorn.nekoration.client.rendering;

import java.io.Closeable;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.entities.PaintingData;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;

public abstract class AbstractPaintingRenderer implements Closeable {
    public abstract void render(PoseStack stack, Matrix4f pose, Matrix3f normal, MultiBufferSource buffers, PaintingData data, short blocHor, short blocVer, float left, float bottom, int light);

    @Override
    public void close() {}

    public static class PixelsPaintingRenderer extends AbstractPaintingRenderer {
        public void render(PoseStack stack, Matrix4f pose, Matrix3f normal, MultiBufferSource buffers, PaintingData data, short blocHor, short blocVer, float left, float bottom, int light){
            // Use Pixel-by-Pixel Rendering...
            // We don't need textures when rendering the artwork, so we use another RenderType(PAINTING),
			// Which uses vertices which don't have uv data but need rgb color values...
			// Get the VertexBuffer for Image Rendering...
            VertexConsumer vb = buffers.getBuffer(RenderTypeHelper.paintingPixels());
            int[] color;
            for (short posi = 0;posi < 16;posi++)
                for (short posj = 0;posj < 16;posj++) {
                    color = NekoColors.getRGBArray(data.getCompositeAt(data.getWidth() - 1 - (blocHor * 16 + posi), data.getHeight() - 1 - (blocVer * 16 + posj)));
                    vertexPixel(pose, normal, vb, left + posi + 1.0F, bottom + posj,        -0.5F, 0, 0, -1, light, color[0], color[1], color[2]);    // >V
                    vertexPixel(pose, normal, vb, left + posi, bottom + posj,               -0.5F, 0, 0, -1, light, color[0], color[1], color[2]);    // <V
                    vertexPixel(pose, normal, vb, left + posi, bottom + posj + 1.0F,        -0.5F, 0, 0, -1, light, color[0], color[1], color[2]);    // <A
                    vertexPixel(pose, normal, vb, left + posi + 1.0F, bottom + posj + 1.0F, -0.5F, 0, 0, -1, light, color[0], color[1], color[2]);    // >A
                }
        }
    }

    public static class ImagePaintingRenderer extends AbstractPaintingRenderer {
        private final DynamicTexture texture;
        protected final RenderType renderType;

        public ImagePaintingRenderer(){
            texture = null;
            renderType = null;
        }

        public ImagePaintingRenderer(NativeImage nativeImage, TextureManager manager) {
            this.texture = new DynamicTexture(nativeImage);
            this.renderType = RenderTypeHelper.paintingTexture(manager.register("painting", this.texture));
        }

        public void render(PoseStack stack, Matrix4f pose, Matrix3f normal, MultiBufferSource buffers, PaintingData data, short blocHor, short blocVer, float left, float bottom, int light){
            // a painting from its texture image...
            VertexConsumer vb = buffers.getBuffer(renderType);
            
            short blocHorCount = (short)(data.getWidth() / 16);
            short blocVerCount = (short)(data.getHeight() / 16);
            double d0 = 1.0D / blocHorCount;
            double d1 = 1.0D / blocVerCount;
            float right = left   + 16.0F;
            float top   = bottom + 16.0F;
            
            float paintU0 = (float)(d0 * (blocHorCount - blocHor));
            float paintU1 = (float)(d0 * (blocHorCount - (blocHor + 1)));
            float paintV0 = (float)(d1 * (blocVerCount - blocVer));
            float paintV1 = (float)(d1 * (blocVerCount - (blocVer + 1)));
            // Neg[Z] // F[ront]
            vertexImage(pose, normal, vb, right, bottom, paintU1, paintV0, -0.5F, 0, 0, -1, light);
            vertexImage(pose, normal, vb, left,  bottom, paintU0, paintV0, -0.5F, 0, 0, -1, light);
            vertexImage(pose, normal, vb, left,  top,    paintU0, paintV1, -0.5F, 0, 0, -1, light);
            vertexImage(pose, normal, vb, right, top,    paintU1, paintV1, -0.5F, 0, 0, -1, light);
        }

        @Override
        public void close() {
            texture.close();
        }
    }

    private static void vertexPixel(Matrix4f pose, Matrix3f normal, VertexConsumer vertexBuilder, float x, float y, float z, int nx, int ny, int nz, int light, int r, int g, int b) {
		vertexBuilder.vertex(pose, x, y, z).color(r, g, b, 255).uv2(light).normal(normal, (float)nx, (float)ny, (float)nz).endVertex();
	}

    private static void vertexImage(Matrix4f pose, Matrix3f normal, VertexConsumer vertexBuilder, float x, float y, float u, float v, float z, int nx, int ny, int nz, int light) {
		vertexBuilder.vertex(pose, x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, (float)nx, (float)ny, (float)nz).endVertex();
	}
}
