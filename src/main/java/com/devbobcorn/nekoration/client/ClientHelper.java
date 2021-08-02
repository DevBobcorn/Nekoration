package com.devbobcorn.nekoration.client;

import com.devbobcorn.nekoration.client.gui.screen.PaintingScreen;
import com.devbobcorn.nekoration.client.gui.screen.PaletteScreen;
import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Hand;

public class ClientHelper {
    public static void showPaletteScreen(Hand hand, byte active, Color[] colors) {
        Minecraft.getInstance().setScreen(new PaletteScreen(hand, active, colors));
    }

    public static void showPaintingScreen(int painting) {
        Minecraft.getInstance().setScreen(new PaintingScreen(painting));
    }

    public static void showPaintingScreen(int painting, byte active, Color[] colors) {
        Minecraft.getInstance().setScreen(new PaintingScreen(painting, active, colors));
    }
}
