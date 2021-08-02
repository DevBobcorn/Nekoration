package com.devbobcorn.nekoration.client.rendering;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;


public class RenderTypeHelper {
    public static final RenderState.AlphaState ALPHA = new RenderState.AlphaState(1F / 255F);
    public static final RenderState.CullState CULL_DISABLED = new RenderType.CullState(/*enable*/false);
    public static final RenderState.LightmapState ENABLE_LIGHTMAP = new RenderState.LightmapState(/*enable*/true);
    public static final RenderState.DepthTestState DEPTH_ALWAYS = new RenderType.DepthTestState("always", GL11.GL_ALWAYS);
    public static final RenderState.WriteMaskState COLOR_WRITE = new RenderType.WriteMaskState(/*color*/true, /*depth*/false);
    public static final RenderState.FogState NO_FOG = new RenderType.FogState("no_fog", Runnables.doNothing(), Runnables.doNothing());
    public static final RenderState.TransparencyState TRANSLUCENT = new RenderState.TransparencyState("translucent", RenderTypeHelper::enableTransparency, RenderTypeHelper::disableTransparency);

    public static RenderType PAINTING =
        RenderType.create("painting",
                DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, GL11.GL_QUADS,
                /*buffer size*/256, /*no delegate*/false, /*need sorting data*/true,
                RenderType.State.builder().setAlphaState(ALPHA).setLightmapState(ENABLE_LIGHTMAP).setTransparencyState(TRANSLUCENT).createCompositeState(/*outline*/false));

    private static void enableTransparency() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private static void disableTransparency() {
        RenderSystem.disableBlend();
    }
}
