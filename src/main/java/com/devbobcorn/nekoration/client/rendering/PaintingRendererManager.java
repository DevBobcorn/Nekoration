package com.devbobcorn.nekoration.client.rendering;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.client.rendering.entities.AbstractPaintingRenderer;
import com.devbobcorn.nekoration.client.rendering.entities.AbstractPaintingRenderer.*;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;

public class PaintingRendererManager {
    private static final String SAVED_PATH = "nekocache/";

    private static final Logger LOGGER = LogManager.getLogger("Painting Manager");

    private static PixelsPaintingRenderer PIXELS_RENDERER = new PixelsPaintingRenderer();

    public static PixelsPaintingRenderer PixelsRenderer(){
        return PIXELS_RENDERER;
    }

    @SuppressWarnings("null")
    public static byte[] read(String name){
        InputStream input = null;
        ByteArrayOutputStream arrStream = new ByteArrayOutputStream();

        try {
            input = new FileInputStream(name);
            byte[] arr = new byte[4 * 128 * 128];
            int len = -1;
            while ((len = input.read(arr)) != -1){
                arrStream.write(arr, 0, len);
            }
            return arrStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static LoadingCache<Integer, AtomicReference<AbstractPaintingRenderer>> PAINTING_RENDERERS = CacheBuilder.newBuilder()
        .<Integer, AtomicReference<AbstractPaintingRenderer>>removalListener(old -> old.getValue().get().close())
        .expireAfterWrite(360000, TimeUnit.SECONDS).build(new CacheLoader<Integer, AtomicReference<AbstractPaintingRenderer>>(){
            @Override
            public AtomicReference<AbstractPaintingRenderer> load(Integer key) throws Exception {
                try {
                    AtomicReference<AbstractPaintingRenderer> ref = new AtomicReference<AbstractPaintingRenderer>(PIXELS_RENDERER);
                    byte[] bytes = read(SAVED_PATH + Integer.toString(key) + ".png");
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
