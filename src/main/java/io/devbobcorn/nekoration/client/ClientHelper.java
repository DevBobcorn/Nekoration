package io.devbobcorn.nekoration.client;

import io.devbobcorn.nekoration.client.gui.screen.PaletteScreen;
import io.devbobcorn.nekoration.client.gui.screen.PaintingScreen;
import io.devbobcorn.nekoration.client.gui.screen.PaintingSizeScreen;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

/**
 * Client-only screen launchers. Only call these from client-side code paths.
 */
public class ClientHelper {
    public static void showPaletteScreen(InteractionHand hand, byte active, Color[] colors) {
        Minecraft.getInstance().setScreen(new PaletteScreen(hand, active, colors));
    }

    public static void showPaintingSizeScreen(InteractionHand hand, int count) {
        Minecraft.getInstance().setScreen(new PaintingSizeScreen(hand, count));
    }

    public static void showPaintingScreen(int painting) {
        Minecraft.getInstance().setScreen(new PaintingScreen(painting));
    }

    public static void showPaintingScreen(int painting, byte active, Color[] colors) {
        Minecraft.getInstance().setScreen(new PaintingScreen(painting, active, colors));
    }
}
