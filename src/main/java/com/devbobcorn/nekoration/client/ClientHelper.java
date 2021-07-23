package com.devbobcorn.nekoration.client;

import com.devbobcorn.nekoration.client.gui.screen.PaletteScreen;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Hand;

public class ClientHelper {
    public static void showGuiDraw(Hand hand, byte active, Color[] colors) {
        Minecraft.getInstance().setScreen(new PaletteScreen(hand, active, colors));
    }
}
