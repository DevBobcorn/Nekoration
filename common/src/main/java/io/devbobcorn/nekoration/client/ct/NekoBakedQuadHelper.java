package io.devbobcorn.nekoration.client.ct;

import java.util.Arrays;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.block.model.BakedQuad;

public final class NekoBakedQuadHelper {
    public static final VertexFormat FORMAT = DefaultVertexFormat.BLOCK;
    public static final int VERTEX_STRIDE = FORMAT.getVertexSize() / 4;

    private static final int U_OFFSET = 4;
    private static final int V_OFFSET = 5;

    private NekoBakedQuadHelper() {
    }

    public static BakedQuad clone(BakedQuad quad) {
        return new BakedQuad(Arrays.copyOf(quad.getVertices(), quad.getVertices().length),
                quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade());
    }

    public static float getU(int[] vertexData, int vertex) {
        return Float.intBitsToFloat(vertexData[vertex * VERTEX_STRIDE + U_OFFSET]);
    }

    public static float getV(int[] vertexData, int vertex) {
        return Float.intBitsToFloat(vertexData[vertex * VERTEX_STRIDE + V_OFFSET]);
    }

    public static void setU(int[] vertexData, int vertex, float u) {
        vertexData[vertex * VERTEX_STRIDE + U_OFFSET] = Float.floatToRawIntBits(u);
    }

    public static void setV(int[] vertexData, int vertex, float v) {
        vertexData[vertex * VERTEX_STRIDE + V_OFFSET] = Float.floatToRawIntBits(v);
    }
}
