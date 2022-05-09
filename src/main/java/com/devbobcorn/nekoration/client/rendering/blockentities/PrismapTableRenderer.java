package com.devbobcorn.nekoration.client.rendering.blockentities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.devbobcorn.nekoration.blocks.entities.PrismapTableBlockEntity;
import com.devbobcorn.nekoration.client.rendering.ChunkModel;
import com.devbobcorn.nekoration.client.rendering.chunks.ChunkModelRender;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class PrismapTableRenderer implements BlockEntityRenderer<PrismapTableBlockEntity> {
    private static Minecraft mc = null;
    public static Method MRenderLevel = null;
    public static Class<?> CLocalRenderInfoContainer = null;
    public static Field FChunk = null;
    public static Field FRenderChunkLayer = null;
    private static boolean error = false;

    public PrismapTableRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @SuppressWarnings("deprecation")
    public void render(PrismapTableBlockEntity tileEntity, float partialTicks, PoseStack stack, MultiBufferSource buffers, int combinedLight, int combinedOverlay) {
        if (mc == null)
            mc = Minecraft.getInstance();

        if (!error)
            if (MRenderLevel == null)
                hackItUp();
            else {
                try {
                    final ChunkModel miniModel = tileEntity.chunkModel;

                    if (miniModel == null)
                        return;
            
                    if (!miniModel.isCompiled() && !miniModel.getError())
                        miniModel.compile();
                    
                    // Render the world...
                    BlockPos pos = tileEntity.getBlockPos();
                    double x = pos.getX();
                    double y = pos.getY();
                    double z = pos.getZ();
                    stack.translate(0.0, 0.75, 0.0);
                    stack.scale(0.0625F, 0.0625F, 0.0625F);
                    Matrix4f projection = RenderSystem.getProjectionMatrix();
                    renderModelChunkLayer(RenderType.solid(), stack, miniModel.chunkRender, x, y, z, projection);
                    mc.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).setBlurMipmap(false, mc.options.mipmapLevels > 0); // FORGE: fix flickering leaves when mods mess up the blurMipmap settings
                    renderModelChunkLayer(RenderType.cutoutMipped(), stack, miniModel.chunkRender, x, y, z, projection);
                    mc.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).restoreLastBlurMipmap();
                    renderModelChunkLayer(RenderType.cutout(), stack, miniModel.chunkRender, x, y, z, projection);

                    //MRenderWorld.invoke(mc.levelRenderer, RenderType.solid(), stack, x, y, z); Or call vanilla WorldRenderer's method to do the rendering (Blend options not customizable)
                } catch (Exception e) {
                    error = true;
                    e.printStackTrace();
                }
            }
    }

    private void renderModelChunkLayer(RenderType type, PoseStack stack, ChunkModelRender chunkRender, double x, double y, double z, Matrix4f projection) {
        // Note that in 1.16, the projection matrix didn't need to be passed in separately
        type.setupRenderState();

        VertexFormat vertexformat = type.format();
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

        if (shaderinstance.FOG_START != null) {
            shaderinstance.FOG_START.set(RenderSystem.getShaderFogStart());
        }

        if (shaderinstance.FOG_END != null) {
            shaderinstance.FOG_END.set(RenderSystem.getShaderFogEnd());
        }

        if (shaderinstance.FOG_COLOR != null) {
            shaderinstance.FOG_COLOR.set(RenderSystem.getShaderFogColor());
        }

        if (shaderinstance.TEXTURE_MATRIX != null) {
            shaderinstance.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
        }

        if (shaderinstance.GAME_TIME != null) {
            shaderinstance.GAME_TIME.set(RenderSystem.getShaderGameTime());
        }

        RenderSystem.setupShaderLights(shaderinstance);
        shaderinstance.apply();
        //Uniform uniform = shaderinstance.CHUNK_OFFSET;
        boolean flag1 = false;

        if (!chunkRender.getCompiledChunk().isEmpty(type)) {
            VertexBuffer vertexbuffer = chunkRender.getBuffer(type);
            // Translucency
            GlStateManager._blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            GlStateManager._enableBlend();
            vertexbuffer.drawChunkLayer();
            flag1 = true;
        }

        shaderinstance.clear();
        if (flag1) {
            vertexformat.clearBufferState();
        }

        VertexBuffer.unbind();
        VertexBuffer.unbindVertexArray();
        type.clearRenderState();
    }

    private void hackItUp(){
        try { // TODO Update when necessary
            MRenderLevel = ObfuscationReflectionHelper.findMethod(LevelRenderer.class, "m_109599_", PoseStack.class, float.class, long.class, Camera.class, GameRenderer.class, LightTexture.class, Matrix4f.class);
            //FRenderChunkLayer = ObfuscationReflectionHelper.findField(LevelRenderer.class, "f_109467_");
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
        }
    }
}
