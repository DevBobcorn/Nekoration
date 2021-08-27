package com.devbobcorn.nekoration.client.gui.screen;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.network.C2SUpdatePaintingSize;
import com.devbobcorn.nekoration.network.ModPacketHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PaintingSizeScreen extends Screen {
	public static final ResourceLocation PAINTING_SIZE = new ResourceLocation(Nekoration.MODID, "textures/gui/painting_size.png");
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

	private short pickedWidth; // 1-6
	private short pickedHeight; // 1-6

	private TranslatableComponent tipMessage;

	public PaintingSizeScreen(InteractionHand hand) {
		super(Component.nullToEmpty("PAINTING_SIZE"));
		this.hand = hand;
		tipMessage = new TranslatableComponent("gui.nekoration.message.press_key_change_grid", "'E'");
	}

	protected void init() {
		super.init();
		pickedWidth = (short)(slotNum / 2);
		pickedHeight = (short)(slotNum / 2);
		leftPos = (this.width - BACKGROUND_WIDTH) / 2;
		topPos = (this.height - BACKGROUND_HEIGHT) / 2;
	}

	public void render(PoseStack stack, int x, int y, float partialTicks) {
		stack.pushPose();
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, PAINTING_SIZE);
		int i = leftPos, j = topPos;
		blit(stack, i, j, 0, 0, 124, 146);
		stack.popPose();
		super.render(stack, x, y, partialTicks);
		// Draw slots...
		for (int ix = 0; ix < slotNum; ix++)
			for (int iy = 0; iy < slotNum; iy++)
				blit(stack, i + 8 + ix * slotLen, j + 30 + iy * slotLen, (ix < pickedWidth && iy < pickedHeight) ? 18 : 0, useLargeSize ? 178 : 160, 18, 18);
		// Draw text...
		drawCenteredString(stack, this.font, new TranslatableComponent("gui.nekoration.message.size", pickedWidth, pickedHeight), this.width / 2, this.height / 2 - 66, -1);
		// Render Tip Text...
        stack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
        stack.translate(j, -i - 136, 0);
        this.font.draw(stack, tipMessage, 1.0F, 1.0F, (150 << 24) + (255 << 16) + (255 << 8) + 255);
	}

	@Override
	public void mouseMoved(double x, double y){
		double gridx = x - leftPos - GRID_LEFT;
		double gridy = y - topPos - GRID_TOP;
		if (gridx < 0.0 || gridy < 0.0 || gridx > 108.0 || gridy > 108.0)
			return;
		pickedWidth = (short)Math.min(Math.max((int)Math.ceil(gridx / slotLen), 1), slotNum);
		pickedHeight = (short)Math.min(Math.max((int)Math.ceil(gridy / slotLen), 1), slotNum);
	}

	@Override
	public boolean mouseClicked(double x, double y, int type){
		double gridx = x - leftPos - GRID_LEFT;
		double gridy = y - topPos - GRID_TOP;
		if (gridx < 0.0 || gridy < 0.0 || gridx > 108.0 || gridy > 108.0)
			return false;
		// Get it & Close it...
		pickedWidth = (short)Math.min(Math.max((int)Math.ceil(gridx / slotLen), 1), slotNum);
		pickedHeight = (short)Math.min(Math.max((int)Math.ceil(gridy / slotLen), 1), slotNum);
		final C2SUpdatePaintingSize packet = new C2SUpdatePaintingSize(this.hand, pickedWidth, pickedHeight);
		ModPacketHandler.CHANNEL.sendToServer(packet);
		this.minecraft.setScreen((Screen)null);
		return true;
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifier) {
		if (keyCode == GLFW.GLFW_KEY_E) {
			slotLen = (useLargeSize = !useLargeSize) ? 6 : 18;
			slotNum = (useLargeSize) ? 18 : 6;
            return true;
        }
		return super.keyPressed(keyCode, scanCode, modifier);
	}

	public boolean isPauseScreen() {
		return false;
	}
}