package io.devbobcorn.nekoration.client.rendering;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;

/** Multiplies the finished block-model vertex color by a Custom Block entry's tint. */
public final class ColorTintVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final int red;
    private final int green;
    private final int blue;
    private final boolean allFaces;
    private boolean tintThisQuad;

    public ColorTintVertexConsumer(VertexConsumer delegate, int color, boolean allFaces) {
        this.delegate = delegate;
        this.red = color >> 16 & 0xFF;
        this.green = color >> 8 & 0xFF;
        this.blue = color & 0xFF;
        this.allFaces = allFaces;
    }

    @Override
    public void putBulkData(PoseStack.Pose pose, BakedQuad quad, float[] brightness, float red, float green,
            float blue, float alpha, int[] lightmap, int packedOverlay, boolean readAlpha) {
        tintThisQuad = allFaces || quad.isTinted();
        VertexConsumer.super.putBulkData(pose, quad, brightness, red, green, blue, alpha, lightmap, packedOverlay,
                readAlpha);
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        delegate.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        delegate.setColor(tintThisQuad ? red * this.red / 255 : red,
                tintThisQuad ? green * this.green / 255 : green,
                tintThisQuad ? blue * this.blue / 255 : blue, alpha);
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        delegate.setUv(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        delegate.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        delegate.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        delegate.setNormal(x, y, z);
        return this;
    }
}
