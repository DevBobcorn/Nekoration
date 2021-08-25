package com.devbobcorn.nekoration.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.CullStateShard;
import net.minecraft.client.renderer.RenderStateShard.DepthTestStateShard;
import net.minecraft.client.renderer.RenderStateShard.LightmapStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import net.minecraft.client.renderer.RenderStateShard.WriteMaskStateShard;
import net.minecraft.resources.ResourceLocation;


public class RenderTypeHelper {
    //public static final TransparencyStateShard ALPHA = new RenderState.AlphaState(1F / 255F);
    public static final CullStateShard CULL_DISABLED = new RenderStateShard.CullStateShard(/*enable*/false);
    public static final LightmapStateShard ENABLE_LIGHTMAP = new RenderStateShard.LightmapStateShard(/*enable*/true);
    public static final DepthTestStateShard DEPTH_ALWAYS = new RenderStateShard.DepthTestStateShard("always", GL11.GL_ALWAYS);
    public static final WriteMaskStateShard COLOR_WRITE = new RenderStateShard.WriteMaskStateShard(/*color*/true, /*depth*/false);
    public static final TransparencyStateShard TRANSLUCENT = new RenderStateShard.TransparencyStateShard("translucent", RenderTypeHelper::enableTransparency, RenderTypeHelper::disableTransparency);
    public static final TransparencyStateShard NO_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("translucent", RenderTypeHelper::enableTransparency, RenderTypeHelper::disableTransparency);
    public static final RenderStateShard.OverlayStateShard OVERLAY = new RenderStateShard.OverlayStateShard(true);

    public static final RenderStateShard.EmptyTextureStateShard NO_TEXTURE = new RenderStateShard.EmptyTextureStateShard(() -> { }, () -> { });

    public static final RenderStateShard.ShaderStateShard IMAGE_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntitySolidShader);
    public static final RenderStateShard.ShaderStateShard PIXEL_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeLeashShader);

    public static final RenderStateShard.CullStateShard NO_CULL = new RenderStateShard.CullStateShard(false);

    private static RenderType PAINTING_PIXELS = RenderType.create("painting_pixels", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(PIXEL_SHADER).setTextureState(NO_TEXTURE).setCullState(NO_CULL).setLightmapState(ENABLE_LIGHTMAP).createCompositeState(false));

    public static RenderType paintingPixels(){
        return PAINTING_PIXELS;
    }

    public static RenderType paintingTexture(ResourceLocation location){
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(IMAGE_SHADER).setTextureState(new RenderStateShard.TextureStateShard(location, /*blur*/false, /*mipmap*/true)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(ENABLE_LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return RenderType.create("painting", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, /*buffer size*/256, /*no delegate*/false, /*need sorting data*/true, rendertype$compositestate);
    }

    private static void enableTransparency() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private static void disableTransparency() {
        RenderSystem.disableBlend();
    }
}
