package io.devbobcorn.nekoration.client;

import io.devbobcorn.nekoration.client.gui.screen.PaletteScreen;
import io.devbobcorn.nekoration.client.gui.screen.PaintingScreen;
import io.devbobcorn.nekoration.client.gui.screen.PaintingSizeScreen;
import io.devbobcorn.nekoration.entities.PaintingEntity;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.network.PaintingDataBroadcastPayload;
import io.devbobcorn.nekoration.network.PaintingInitPayload;

import java.awt.Color;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

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

    /** Shows a clickable chat message (client side only). */
    public static void displayClientMessage(Component message) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.displayClientMessage(message, false);
        }
    }

    /** Applies a {@link PaintingInitPayload} (client side only, see the payload's handler). */
    public static void handlePaintingInit(PaintingInitPayload payload) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(payload.paintingId());
        if (entity instanceof PaintingEntity painting) {
            painting.initializeFromPayload(payload);
        }
    }

    /** Applies a {@link PaintingDataBroadcastPayload} (client side only, see the payload's handler). */
    public static void handlePaintingBroadcast(PaintingDataBroadcastPayload payload) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(payload.paintingId());
        if (entity instanceof PaintingEntity painting && painting.data != null) {
            if (painting.data.getPaintingHash() != payload.compositeHash()) { // Not the client who edited the painting...
                // Cached image already obsoleted, clear...
                painting.data.clearCache(painting.data.getPaintingHash());
                // Update pixels, meanwhile updating the hash value...
                painting.data.setAreaPixels(payload.partX(), payload.partY(), payload.partW(), payload.partH(), payload.pixels());
                boolean synced = painting.data.getPaintingHash() == payload.compositeHash();
                Nekoration.LOGGER.info(String.format("Painting %s Synced: %s", payload.compositeHash(), synced));
                if (synced && io.devbobcorn.nekoration.xplat.NekoPlatform.config().useImageRendering()) // The whole picture synced, then re-cache the updated painting...
                    painting.data.cache();
            }
        }
    }
}
