package com.devbobcorn.nekoration.client.rendering.blockentities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.devbobcorn.nekoration.blocks.entities.PrismapTableBlockEntity;
import com.devbobcorn.nekoration.client.rendering.ChunkModel;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;

public class PrismapTableRenderer implements BlockEntityRenderer<PrismapTableBlockEntity> {
    private static Minecraft mc = null;
    public static Method MRenderLevel = null;
    public static Class<?> CLocalRenderInfoContainer = null;
    public static Field FChunk = null;
    public static Field FRenderChunkLayer = null;
    private static boolean error = false;

    public PrismapTableRenderer(BlockEntityRendererProvider.Context ctx) { }

    @SuppressWarnings({ "deprecation", "null" })
    public void render(PrismapTableBlockEntity tileEntity, float partialTicks, PoseStack stack, MultiBufferSource buffers, int combinedLight, int combinedOverlay) {
        if (mc == null)
            mc = Minecraft.getInstance();

        if (!error)
            try {
                final ChunkModel miniModel = tileEntity.chunkModel;

                if (miniModel == null)
                    return;
        
                if (!miniModel.isCompiled() && !miniModel.isCompiling())
                    miniModel.compile(mc, tileEntity.getBlockPos());
                
                // Render the world...
                BlockPos pos = tileEntity.getBlockPos();
                double x = pos.getX();
                double y = pos.getY();
                double z = pos.getZ();

                stack.pushPose();

                //stack.translate(0.0, 0.75, 0.0);
                //stack.scale(0.0625F, 0.0625F, 0.0625F);
                stack.translate(0.5, 0.75, 0.5);
                stack.scale(0.0125F, 0.0125F, 0.0125F);

                double chunkSize = 16.0;
                int r = tileEntity.viewAreaRadius;
                int l = tileEntity.getLevel().getSectionsCount();

                for (int cy = 0;cy < l;cy++) {
                //for (int cy = -r;cy <= r;cy++) {
                    for (int cx = -r;cx <= r;cx++)
                        for (int cz = -r;cz <= r;cz++) {
                            RenderChunk chunk = miniModel.getRenderChunk(cx + r, cy, cz + r);

                            Matrix4f projection = RenderSystem.getProjectionMatrix();

                            // Horizontal translation...
                            stack.translate(chunkSize * cx, 0D, chunkSize * cz);

                            renderModelChunkLayer(RenderType.solid(), stack, chunk, x, y, z, projection);
                            mc.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).setBlurMipmap(false, mc.options.mipmapLevels().get() > 0); // FORGE: fix flickering leaves when mods mess up the blurMipmap settings
                            renderModelChunkLayer(RenderType.cutoutMipped(), stack, chunk, x, y, z, projection);
                            mc.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).restoreLastBlurMipmap();
                            renderModelChunkLayer(RenderType.cutout(), stack, chunk, x, y, z, projection);

                            // Reset horizontal translation...
                            stack.translate(-chunkSize * cx, 0D, -chunkSize * cz);
                        }
                    
                    stack.translate(0D, chunkSize, 0D);
                }

                stack.popPose();

                //MRenderWorld.invoke(mc.levelRenderer, RenderType.solid(), stack, x, y, z); Or call vanilla WorldRenderer's method to do the rendering (Blend options not customizable)
            } catch (Exception e) {
                error = true;
                e.printStackTrace();
            }
            
    }

    @SuppressWarnings("null")
    private void renderModelChunkLayer(RenderType type, PoseStack stack, RenderChunk chunkRender, double x, double y, double z, Matrix4f projection) {
        // Note that in 1.16, the projection matrix didn't need to be passed in separately
        type.setupRenderState();

        //VertexFormat vertexformat = type.format();
        ShaderInstance shaderinstance = RenderSystem.getShader();
        BufferUploader.reset();

        for (int k = 0; k < 12; ++k) {
            int i = RenderSystem.getShaderTexture(k);
            shaderinstance.setSampler("Sampler" + k, i);
        }

        if (shaderinstance.MODEL_VIEW_MATRIX != null) {
            shaderinstance.MODEL_VIEW_MATRIX.set(stack.last().pose());
        }

        if (shaderinstance.PROJECTION_MATRIX != null) {
            shaderinstance.PROJECTION_MATRIX.set(projection);
        }

        if (shaderinstance.COLOR_MODULATOR != null) {
            shaderinstance.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
        }

        if (shaderinstance.TEXTURE_MATRIX != null) {
            shaderinstance.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
        }

        if (shaderinstance.GAME_TIME != null) {
            shaderinstance.GAME_TIME.set(RenderSystem.getShaderGameTime());
        }

        RenderSystem.setupShaderLights(shaderinstance);
        shaderinstance.apply();

        if (!chunkRender.getCompiledChunk().isEmpty(type)) {
            VertexBuffer vertexbuffer = chunkRender.getBuffer(type);
            // Translucency
            GlStateManager._blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            GlStateManager._enableBlend();
            //vertexbuffer.drawChunkLayer();
            vertexbuffer.bind();
            vertexbuffer.draw();
        }

        shaderinstance.clear();

        VertexBuffer.unbind();
        //VertexBuffer.unbindVertexArray();
        type.clearRenderState();
    }

}
