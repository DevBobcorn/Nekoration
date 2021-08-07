package com.devbobcorn.nekoration.client.rendering;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.client.rendering.AbstractPaintingRenderer.*;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.systems.RenderSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;

public class PaintingRendererManager {
    private static final String SAVED_PATH = "nekocache/";

    private static final Logger LOGGER = LogManager.getLogger(Nekoration.MODID + " Painting Manager");

    private static PixelsPaintingRenderer PIXELS_RENDERER = new PixelsPaintingRenderer();

    public static PixelsPaintingRenderer PixelsRenderer(){
        return PIXELS_RENDERER;
    }

    public static LoadingCache<Integer, AtomicReference<AbstractPaintingRenderer>> PAINTING_RENDERERS = CacheBuilder.newBuilder()
        .<Integer, AtomicReference<AbstractPaintingRenderer>>removalListener(old -> old.getValue().get().close())
        .expireAfterWrite(600, TimeUnit.SECONDS).build(new CacheLoader<Integer, AtomicReference<AbstractPaintingRenderer>>(){
            @Override
            public AtomicReference<AbstractPaintingRenderer> load(Integer key) throws Exception {
                try {
                    AtomicReference<AbstractPaintingRenderer> ref = new AtomicReference<AbstractPaintingRenderer>(PIXELS_RENDERER);
                    byte[] bytes = LocalImageLoader.read(SAVED_PATH + Integer.toString(key) + ".png");
                    NativeImage image = NativeImage.read(new ByteArrayInputStream(bytes));

                    RenderSystem.recordRenderCall(() -> {
                        TextureManager manager = Minecraft.getInstance().getTextureManager();
                        ref.compareAndSet(PIXELS_RENDERER, new ImagePaintingRenderer(image, manager));
                        // PAINTING_RENDERERS.refresh(key);
                    });
                    LOGGER.info("Image #" + key + " Loaded.");

                    return ref;
                } catch (Exception e) {
                    e.printStackTrace();
                    return new AtomicReference<AbstractPaintingRenderer>(PIXELS_RENDERER);
                }
            }
        });

    @Nullable
    public static AbstractPaintingRenderer get(Integer hash) {
        try{
            AtomicReference<AbstractPaintingRenderer> rd = PAINTING_RENDERERS.getIfPresent(hash);
            return (rd == null) ? PAINTING_RENDERERS.get(hash).get() : rd.get();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
