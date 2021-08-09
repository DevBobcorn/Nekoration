package com.devbobcorn.nekoration.client;

import com.devbobcorn.nekoration.client.gui.screen.PaintingScreen;
import com.devbobcorn.nekoration.client.gui.screen.PaintingSizeScreen;
import com.devbobcorn.nekoration.client.gui.screen.PaletteScreen;
import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

public class ClientHelper {
    public static void showPaletteScreen(InteractionHand hand, byte active, Color[] colors) {
        Minecraft.getInstance().setScreen(new PaletteScreen(hand, active, colors));
    }

    public static void showPaintingSizeScreen(InteractionHand hand) {
        Minecraft.getInstance().setScreen(new PaintingSizeScreen(hand));
    }

    public static void showPaintingScreen(int painting) {
        Minecraft.getInstance().setScreen(new PaintingScreen(painting));
    }

    public static void showPaintingScreen(int painting, byte active, Color[] colors) {
        Minecraft.getInstance().setScreen(new PaintingScreen(painting, active, colors));
    }
}
