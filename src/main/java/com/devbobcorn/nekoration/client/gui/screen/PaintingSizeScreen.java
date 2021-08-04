package com.devbobcorn.nekoration.client.gui.screen;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.network.C2SUpdatePaintingSize;
import com.devbobcorn.nekoration.network.ModPacketHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PaintingSizeScreen extends Screen {
	public static final ResourceLocation PAINTING_SIZE_BACKGROUND = new ResourceLocation(Nekoration.MODID, "textures/gui/painting_size.png");
	public static final int BACKGROUND_WIDTH = 124;
	public static final int BACKGROUND_HEIGHT = 146;
	public static final int GRID_LEFT = 8;
	public static final int GRID_TOP = 30;
	public static final int SLOT_LEN = 18;

	private int leftPos;
	private int topPos;

	private Hand hand;

	private short pickedWidth; // 1-6
	private short pickedHeight; // 1-6

	public PaintingSizeScreen(Hand hand) {
		super(ITextComponent.nullToEmpty("PAINTING_SIZE"));
		this.hand = hand;
	}

	protected void init() {
		super.init();
		pickedWidth = 3;
		pickedHeight = 3;
		leftPos = (this.width - BACKGROUND_WIDTH) / 2;
		topPos = (this.height - BACKGROUND_HEIGHT) / 2;
	}

	public void render(MatrixStack stack, int x, int y, float partialTicks) {
		stack.pushPose();
		RenderSystem.enableBlend();
		this.minecraft.getTextureManager().bind(PAINTING_SIZE_BACKGROUND);
		int i = leftPos, j = topPos;
		blit(stack, i, j, 0, 0, 124, 146);
		stack.popPose();
		super.render(stack, x, y, partialTicks);
		// Draw slots...
		for (int ix = 0; ix < 6; ix++)
			for (int iy = 0; iy < 6; iy++)
				blit(stack, i + 8 + ix * SLOT_LEN, j + 30 + iy * SLOT_LEN, (ix < pickedWidth && iy < pickedHeight) ? 18 : 0, 160, 18, 18);
		// Draw text...
		drawCenteredString(stack, this.font, "Size:  " + pickedWidth + "x" + pickedHeight, this.width / 2, this.height / 2 - 66, -1);
	}

	@Override
	public void mouseMoved(double x, double y){
		double gridx = x - leftPos - GRID_LEFT;
		double gridy = y - topPos - GRID_TOP;
		if (gridx < 0.0 || gridy < 0.0 || gridx > SLOT_LEN * 6.0 || gridy > SLOT_LEN * 6.0)
			return;
		pickedWidth = (short)Math.min(Math.max((int)Math.ceil(gridx / SLOT_LEN), 1), 6);
		pickedHeight = (short)Math.min(Math.max((int)Math.ceil(gridy / SLOT_LEN), 1), 6);
	}

	@Override
	public boolean mouseClicked(double x, double y, int type){
		double gridx = x - leftPos - GRID_LEFT;
		double gridy = y - topPos - GRID_TOP;
		if (gridx < 0.0 || gridy < 0.0 || gridx > SLOT_LEN * 6.0 || gridy > SLOT_LEN * 6.0)
			return false;
		// Get it & Close it...
		pickedWidth = (short)Math.min(Math.max((int)Math.ceil(gridx / SLOT_LEN), 1), 6);
		pickedHeight = (short)Math.min(Math.max((int)Math.ceil(gridy / SLOT_LEN), 1), 6);
		final C2SUpdatePaintingSize packet = new C2SUpdatePaintingSize(this.hand, pickedWidth, pickedHeight);
		ModPacketHandler.CHANNEL.sendToServer(packet);
		this.minecraft.setScreen((Screen)null);
		return true;
	}

	public boolean keyPressed(int i1, int i2, int i3) {
		return super.keyPressed(i1, i2, i3);
	}

	public boolean isPauseScreen() {
		return false;
	}
}