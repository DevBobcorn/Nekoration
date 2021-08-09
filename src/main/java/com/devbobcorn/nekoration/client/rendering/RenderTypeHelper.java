package com.devbobcorn.nekoration.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import org.lwjgl.opengl.GL11;

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

    private static RenderType PAINTING_PIXELS =
        RenderType.create("painting_pixels",
            DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, Mode.QUADS,
            /*buffer size*/256, /*no delegate*/false, /*need sorting data*/true,
            RenderType.CompositeState.builder().setLightmapState(ENABLE_LIGHTMAP).setTransparencyState(TRANSLUCENT).createCompositeState(/*outline*/false));

    public static RenderType paintingPixels(){
        return PAINTING_PIXELS;
    }

    public static RenderType paintingTexture(ResourceLocation location){
        return RenderType.create("painting_texture",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS,
            /*buffer size*/256, /*no delegate*/false, /*need sorting data*/true,
            RenderType.CompositeState.builder().setLightmapState(ENABLE_LIGHTMAP).setTransparencyState(TRANSLUCENT)
                .setTextureState(new RenderStateShard.TextureStateShard(location, /*blur*/false, /*mipmap*/true)).createCompositeState(/*outline*/false));
    }

    private static void enableTransparency() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private static void disableTransparency() {
        RenderSystem.disableBlend();
    }
}
