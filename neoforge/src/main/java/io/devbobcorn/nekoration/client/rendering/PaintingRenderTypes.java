package io.devbobcorn.nekoration.client.rendering;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;

public class PaintingRenderTypes {
    private static final RenderType PAINTING_PIXELS = RenderType.create("painting_pixels",
            DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_LEASH_SHADER)
                    .setTextureState(RenderStateShard.NO_TEXTURE)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .createCompositeState(false));

    public static RenderType paintingPixels() {
        return PAINTING_PIXELS;
    }

    public static RenderType paintingTexture(ResourceLocation location) {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_SOLID_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(location, /* blur */false, /* mipmap */true))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false);
        return RenderType.create("painting", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, /* buffer size */256,
                /* no delegate */false, /* need sorting data */true, compositeState);
    }
}
