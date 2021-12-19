package com.devbobcorn.nekoration.client.event;

import java.io.File;
import java.io.IOException;

import com.mojang.blaze3d.platform.NativeImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PhotoEvents {
    private static final Logger LOGGER = LogManager.getLogger("Photo");

    private static Minecraft mc;
    public static boolean shouldTakePhoto = false;
    private static long lastShot = 0L;

	@SubscribeEvent
	public static void onRenderWorldFinish(RenderLevelLastEvent event) {
        if (!shouldTakePhoto) return;
        shouldTakePhoto = false;
        if (System.nanoTime() - lastShot < 500000000) return; // Less than 0.5 second...
        lastShot = System.nanoTime();
        if (mc == null) mc = Minecraft.getInstance();
        NativeImage nativeimage = Screenshot.takeScreenshot(mc.getMainRenderTarget());
        Util.ioPool().execute(() -> {
            int i = nativeimage.getWidth();
            int j = nativeimage.getHeight();
            int k = 0;
            int l = 0;
            if (i > j) {
                k = (i - j) / 2;
                i = j;
            } else {
                l = (j - i) / 2;
                j = i;
            }

            try (NativeImage photo = new NativeImage(96, 96, false)) {
                nativeimage.resizeSubRectTo(k, l, i, j, photo);
                File target = new File(mc.gameDirectory, "nekopaint/photo/");
                if (!target.exists())
                    target.mkdir();
                photo.writeToFile(new File(target, photo.hashCode() + ".png"));
                LOGGER.info("Photo taken successfully.");
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            } finally {
                nativeimage.close();
            }
        });
    }
}
