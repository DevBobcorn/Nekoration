package io.devbobcorn.nekoration.client.rendering.entities;

import java.io.Closeable;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.devbobcorn.nekoration.NekoColors;
import io.devbobcorn.nekoration.client.rendering.PaintingRenderTypes;
import io.devbobcorn.nekoration.entities.PaintingData;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;

public abstract class AbstractPaintingRenderer implements Closeable {
    public abstract void render(PoseStack stack, PoseStack.Pose pose, MultiBufferSource buffers, PaintingData data, short blocHor, short blocVer, float left, float bottom, int light);
    public abstract void renderFull(PoseStack stack, PoseStack.Pose pose, MultiBufferSource buffers, PaintingData data, float left, float bottom, int light);

    @Override
    public void close() {
    }

    public static class PixelsPaintingRenderer extends AbstractPaintingRenderer {
        @Override
        public void render(PoseStack stack, PoseStack.Pose pose, MultiBufferSource buffers, PaintingData data, short blocHor, short blocVer, float left, float bottom, int light) {
            // Use Pixel-by-Pixel Rendering...
            // We don't need textures when rendering the artwork, so we use another RenderType(PAINTING_PIXELS),
            // Which uses vertices which don't have uv data but need rgb color values...
            // Get the VertexBuffer for Image Rendering...
            VertexConsumer vb = buffers.getBuffer(PaintingRenderTypes.paintingPixels());
            // Compensate for the obvious lighting difference caused by different shaders...
            light = Math.max(0, light - 0x300000);
            int[] color;
            for (short posi = 0; posi < 16; posi++)
                for (short posj = 0; posj < 16; posj++) {
                    color = NekoColors.getRGBArray(data.getCompositeAt(data.getWidth() - 1 - (blocHor * 16 + posi), data.getHeight() - 1 - (blocVer * 16 + posj)));
                    vertexPixel(pose, vb, left + posi + 1.0F, bottom + posj, -0.5F, 0, 0, -1, light, color[0], color[1], color[2]);    // >V
                    vertexPixel(pose, vb, left + posi, bottom + posj, -0.5F, 0, 0, -1, light, color[0], color[1], color[2]);           // <V
                    vertexPixel(pose, vb, left + posi, bottom + posj + 1.0F, -0.5F, 0, 0, -1, light, color[0], color[1], color[2]);    // <A
                    vertexPixel(pose, vb, left + posi + 1.0F, bottom + posj + 1.0F, -0.5F, 0, 0, -1, light, color[0], color[1], color[2]); // >A
                }
        }

        @Override
        public void renderFull(PoseStack stack, PoseStack.Pose pose, MultiBufferSource buffers, PaintingData data, float left, float bottom, int light) {
            // Use Pixel-by-Pixel Rendering...
            // We don't need textures when rendering the artwork, so we use another RenderType(PAINTING_PIXELS),
            // Which uses vertices which don't have uv data but need rgb color values...
            // Get the VertexBuffer for Image Rendering...
            VertexConsumer vb = buffers.getBuffer(PaintingRenderTypes.paintingPixels());
            // Compensate for the obvious lighting difference caused by different shaders...
            light = Math.max(0, light - 0x300000);
            int[] color;
            for (short posi = 0; posi < data.getWidth(); posi++)
                for (short posj = 0; posj < data.getHeight(); posj++) {
                    color = NekoColors.getRGBArray(data.getCompositeAt(data.getWidth() - 1 - posi, data.getHeight() - 1 - posj));
                    vertexPixel(pose, vb, left + posi + 1.0F, bottom + posj, -0.5F, 0, 0, -1, light, color[0], color[1], color[2]);    // >V
                    vertexPixel(pose, vb, left + posi, bottom + posj, -0.5F, 0, 0, -1, light, color[0], color[1], color[2]);           // <V
                    vertexPixel(pose, vb, left + posi, bottom + posj + 1.0F, -0.5F, 0, 0, -1, light, color[0], color[1], color[2]);    // <A
                    vertexPixel(pose, vb, left + posi + 1.0F, bottom + posj + 1.0F, -0.5F, 0, 0, -1, light, color[0], color[1], color[2]); // >A
                }
        }
    }

    public static class ImagePaintingRenderer extends AbstractPaintingRenderer {
        private final DynamicTexture texture;
        protected final RenderType renderType;

        public ImagePaintingRenderer() {
            texture = null;
            renderType = null;
        }

        public ImagePaintingRenderer(NativeImage nativeImage, TextureManager manager) {
            this.texture = new DynamicTexture(nativeImage);
            this.renderType = PaintingRenderTypes.paintingTexture(manager.register("painting", this.texture));
        }

        @Override
        public void render(PoseStack stack, PoseStack.Pose pose, MultiBufferSource buffers, PaintingData data, short blocHor, short blocVer, float left, float bottom, int light) {
            // a painting from its texture image...
            VertexConsumer vb = buffers.getBuffer(renderType);
            short blocHorCount = (short) (data.getWidth() / 16);
            short blocVerCount = (short) (data.getHeight() / 16);
            double d0 = 1.0D / blocHorCount;
            double d1 = 1.0D / blocVerCount;
            float right = left + 16.0F;
            float top = bottom + 16.0F;

            float paintU0 = (float) (d0 * (blocHorCount - blocHor));
            float paintU1 = (float) (d0 * (blocHorCount - (blocHor + 1)));
            float paintV0 = (float) (d1 * (blocVerCount - blocVer));
            float paintV1 = (float) (d1 * (blocVerCount - (blocVer + 1)));
            // Neg[Z] // F[ront]
            vertexImage(pose, vb, right, bottom, paintU1, paintV0, -0.5F, 0, 0, -1, light);
            vertexImage(pose, vb, left, bottom, paintU0, paintV0, -0.5F, 0, 0, -1, light);
            vertexImage(pose, vb, left, top, paintU0, paintV1, -0.5F, 0, 0, -1, light);
            vertexImage(pose, vb, right, top, paintU1, paintV1, -0.5F, 0, 0, -1, light);
        }

        @Override
        public void renderFull(PoseStack stack, PoseStack.Pose pose, MultiBufferSource buffers, PaintingData data, float left, float bottom, int light) {
            // a painting from its texture image...
            VertexConsumer vb = buffers.getBuffer(renderType);
            float right = left + data.getWidth();
            float top = bottom + data.getHeight();

            // Neg[Z] // F[ront]
            vertexImage(pose, vb, right, bottom, 0.0F, 1.0F, -0.5F, 0, 0, -1, light);
            vertexImage(pose, vb, left, bottom, 1.0F, 1.0F, -0.5F, 0, 0, -1, light);
            vertexImage(pose, vb, left, top, 1.0F, 0.0F, -0.5F, 0, 0, -1, light);
            vertexImage(pose, vb, right, top, 0.0F, 0.0F, -0.5F, 0, 0, -1, light);
        }

        @Override
        public void close() {
            if (texture != null)
                texture.close();
        }
    }

    private static void vertexPixel(PoseStack.Pose pose, VertexConsumer vertexBuilder, float x, float y, float z, int nx, int ny, int nz, int light, int r, int g, int b) {
        vertexBuilder.addVertex(pose, x, y, z).setColor(r, g, b, 255).setLight(light).setNormal(pose, (float) nx, (float) ny, (float) nz);
    }

    private static void vertexImage(PoseStack.Pose pose, VertexConsumer vertexBuilder, float x, float y, float u, float v, float z, int nx, int ny, int nz, int light) {
        vertexBuilder.addVertex(pose, x, y, z).setColor(255, 255, 255, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, (float) nx, (float) ny, (float) nz);
    }
}
