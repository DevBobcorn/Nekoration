package io.devbobcorn.nekoration.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.network.PaintingSizeUpdatePayload;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.PacketDistributor;

public class PaintingSizeScreen extends Screen {
    public static final ResourceLocation PAINTING_SIZE = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "textures/gui/painting_size.png");
    public static final int BACKGROUND_WIDTH = 124;
    public static final int BACKGROUND_HEIGHT = 146;
    public static final int GRID_LEFT = 8;
    public static final int GRID_TOP = 30;
    public static boolean useLargeSize = false;
    public static int slotLen = 18;
    public static int slotNum = 6;

    private int leftPos;
    private int topPos;

    private InteractionHand hand;
    private int stackCount;

    private short pickedWidth; // 1-6
    private short pickedHeight; // 1-6

    private Component tipMessage;
    private Component[] warningMessages = new Component[2];

    public PaintingSizeScreen(InteractionHand hand, int count) {
        super(Component.literal("PAINTING_SIZE"));
        this.hand = hand;
        this.stackCount = count;
        tipMessage = Component.translatable("gui.nekoration.message.press_key_change_grid", "'E'");
        warningMessages[0] = Component.translatable("gui.nekoration.message.painting_size_warning");
        warningMessages[1] = Component.translatable("gui.nekoration.message.painting_size_warning_help");
    }

    protected void init() {
        super.init();
        pickedWidth = (short) (slotNum / 2);
        pickedHeight = (short) (slotNum / 2);
        leftPos = (this.width - BACKGROUND_WIDTH) / 2;
        topPos = (this.height - BACKGROUND_HEIGHT) / 2;
    }

    private int warningOpacity = 0;

    @Override
    public void render(GuiGraphics graphics, int x, int y, float partialTicks) {
        RenderSystem.enableBlend();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = leftPos, j = topPos;
        graphics.blit(PAINTING_SIZE, i, j, 0, 0, 124, 146);
        // Draw slots...
        for (int ix = 0; ix < slotNum; ix++)
            for (int iy = 0; iy < slotNum; iy++)
                graphics.blit(PAINTING_SIZE, i + 8 + ix * slotLen, j + 30 + iy * slotLen, (ix < pickedWidth && iy < pickedHeight) ? 18 : 0, useLargeSize ? 178 : 160, 18, 18);
        // Draw text...
        graphics.drawCenteredString(this.font, Component.translatable("gui.nekoration.message.size", pickedWidth, pickedHeight), this.width / 2, this.height / 2 - 66, -1);
        // Render Warning if too Large...
        if ((pickedHeight > 6 || pickedWidth > 6)) {
            if (warningOpacity < 210)
                warningOpacity += 30;
        } else if (warningOpacity > 0)
            warningOpacity -= 30;
        if (warningOpacity > 0) {
            graphics.drawCenteredString(this.font, warningMessages[0], this.width / 2, this.height - 44, (warningOpacity << 24) + (255 << 16) + (70 << 8) + 70);
            graphics.drawCenteredString(this.font, warningMessages[1], this.width / 2, this.height - 34, (warningOpacity << 24) + (220 << 16) + (30 << 8) + 250);
        }
        // Render Tip Text...
        graphics.pose().pushPose();
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(90.0F));
        graphics.pose().translate(j, -i - 136, 0);
        graphics.drawString(this.font, tipMessage, 1, 1, (150 << 24) + (255 << 16) + (255 << 8) + 255);
        graphics.pose().popPose();
    }

    @Override
    public void mouseMoved(double x, double y) {
        double gridx = x - leftPos - GRID_LEFT;
        double gridy = y - topPos - GRID_TOP;
        if (gridx < 0.0 || gridy < 0.0 || gridx > 108.0 || gridy > 108.0)
            return;
        pickedWidth = (short) Math.min(Math.max((int) Math.ceil(gridx / slotLen), 1), slotNum);
        pickedHeight = (short) Math.min(Math.max((int) Math.ceil(gridy / slotLen), 1), slotNum);
    }

    @Override
    public boolean mouseClicked(double x, double y, int type) {
        double gridx = x - leftPos - GRID_LEFT;
        double gridy = y - topPos - GRID_TOP;
        if (gridx < 0.0 || gridy < 0.0 || gridx > 108.0 || gridy > 108.0)
            return false;
        // Get it & Close it...
        pickedWidth = (short) Math.min(Math.max((int) Math.ceil(gridx / slotLen), 1), slotNum);
        pickedHeight = (short) Math.min(Math.max((int) Math.ceil(gridy / slotLen), 1), slotNum);
        final var packet = new PaintingSizeUpdatePayload(this.hand, pickedWidth, pickedHeight, stackCount);
        PacketDistributor.sendToServer(packet);
        this.minecraft.setScreen((Screen) null);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        if (keyCode == GLFW.GLFW_KEY_E) {
            slotLen = (useLargeSize = !useLargeSize) ? 6 : 18;
            slotNum = (useLargeSize) ? 18 : 6;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifier);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
