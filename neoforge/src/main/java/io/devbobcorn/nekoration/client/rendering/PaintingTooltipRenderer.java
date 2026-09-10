package io.devbobcorn.nekoration.client.rendering;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;

import io.devbobcorn.nekoration.NekoColors;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.entities.PaintingData;
import io.devbobcorn.nekoration.items.PaintingTooltipComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;

public class PaintingTooltipRenderer implements ClientTooltipComponent {
    private static final Logger LOGGER = LogUtils.getLogger();

    /** The preview is upscaled / downscaled to fit within this range, in GUI pixels... */
    private static final int MIN_PREVIEW_SIZE = 32;
    private static final int MAX_PREVIEW_SIZE = 96;

    // Preview textures, keyed by the content hash of the painting's pixels...
    private static final Cache<Integer, PreviewTexture> PREVIEWS = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .removalListener(PaintingTooltipRenderer::releasePreview)
            .build();

    // Uniquifies the registered locations, so releasing one preview texture never touches another...
    private static final AtomicInteger PREVIEW_COUNTER = new AtomicInteger();

    private final PaintingTooltipComponent component;

    public PaintingTooltipRenderer(PaintingTooltipComponent component) {
        this.component = component;
    }

    @Override
    public int getHeight() {
        return Math.round(this.component.height() * 16.0F * previewScale());
    }

    @Override
    public int getWidth(Font font) {
        return Math.round(this.component.width() * 16.0F * previewScale());
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        PreviewTexture preview = getPreview(this.component);
        if (preview == null)
            return;
        int texW = this.component.width() * 16;
        int texH = this.component.height() * 16;
        float scale = previewScale();
        graphics.blit(preview.location(), x, y, Math.round(texW * scale), Math.round(texH * scale), 0.0F, 0.0F, texW, texH, texW, texH);
    }

    private float previewScale() {
        float maxSide = Math.max(this.component.width(), this.component.height()) * 16.0F;
        if (maxSide < MIN_PREVIEW_SIZE)
            return MIN_PREVIEW_SIZE / maxSide;
        if (maxSide > MAX_PREVIEW_SIZE)
            return MAX_PREVIEW_SIZE / maxSide;
        return 1.0F;
    }

    private static PreviewTexture getPreview(PaintingTooltipComponent component) {
        int hash = Arrays.hashCode(component.pixels());
        PreviewTexture preview = PREVIEWS.getIfPresent(hash);
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        if (preview != null
                && manager.getTexture(preview.location(), MissingTextureAtlasSprite.getTexture()) == preview.texture())
            return preview;
        if (preview != null)
            PREVIEWS.invalidate(hash); // Releases it (no-op if it was already unregistered)...
        // Rebuild the preview if the cached one is no longer alive...
        preview = PreviewTexture.create(component, hash);
        if (preview != null)
            PREVIEWS.put(hash, preview);
        return preview;
    }

    private static void releasePreview(RemovalNotification<Integer, PreviewTexture> notification) {
        PreviewTexture preview = notification.getValue();
        if (preview != null)
            preview.release();
    }

    // A rendered preview of a painting's content, uploaded as a DynamicTexture...
    private record PreviewTexture(ResourceLocation location, DynamicTexture texture) {
        private static PreviewTexture create(PaintingTooltipComponent component, int hash) {
            int texW = component.width() * 16;
            int texH = component.height() * 16;
            try {
                // Recreate the canvas & composite layers, the same way as in-world paintings...
                PaintingData data = new PaintingData((short) texW, (short) texH, component.pixels().clone(), true, component.dataId());
                NativeImage image = new NativeImage(texW, texH, false);
                for (int i = 0; i < texW; i++)
                    for (int j = 0; j < texH; j++)
                        image.setPixelRGBA(i, j, toAbgr(data.getCompositeAt(i, j)));
                // The location must be unique per texture instance; releasing it must never
                // touch a texture registered by another preview instance...
                ResourceLocation location = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID,
                        "paintings/preview/" + hash + "_" + PREVIEW_COUNTER.incrementAndGet());
                DynamicTexture texture = new DynamicTexture(image);
                Minecraft.getInstance().getTextureManager().register(location, texture);
                return new PreviewTexture(location, texture);
            } catch (Exception e) {
                LOGGER.error("Failed to create painting preview texture", e);
                return null;
            }
        }

        private void release() {
            Minecraft.getInstance().getTextureManager().release(this.location);
        }

        // NativeImage takes colors in ABGR order...
        private static int toAbgr(int rgb) {
            return 0xFF000000 | (NekoColors.getBlue(rgb) << 16) | (NekoColors.getGreen(rgb) << 8) | NekoColors.getRed(rgb);
        }
    }
}
