package com.devbobcorn.nekoration.client.gui.screen;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.containers.EaselMenuContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

public class EaselMenuScreen extends ContainerScreen<EaselMenuContainer> {

	public EaselMenuScreen(EaselMenuContainer containerBasic, PlayerInventory playerInventory, ITextComponent title) {
		super(containerBasic, playerInventory, title);
		// Set the width and height of the gui. Should match the size of the texture!
		imageWidth = 176;
		imageHeight = 222;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the
	 * items) Taken directly from ContainerScreen
	 */
	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
		final float LABEL_XPOS_1 = 6;
		final float FONT_Y_SPACING = 10;
		final float CHEST_LABEL_YPOS = EaselMenuContainer.TILE_INVENTORY_YPOS - FONT_Y_SPACING;
		this.font.draw(matrixStack, this.title, LABEL_XPOS_1, CHEST_LABEL_YPOS, Color.darkGray.getRGB()); // this.font.drawString;

		final float LABEL_XPOS_2 = 96;
		this.font.draw(matrixStack, this.title, LABEL_XPOS_2, CHEST_LABEL_YPOS, Color.darkGray.getRGB()); // this.font.drawString;

		/*
		final float PLAYER_INV_LABEL_YPOS = EaselMenuContainer.PLAYER_INVENTORY_YPOS - FONT_Y_SPACING;
		this.font.draw(matrixStack, this.inventory.getDisplayName(), /// this.font.drawString
				LABEL_XPOS, PLAYER_INV_LABEL_YPOS, Color.darkGray.getRGB());
		*/
	}

	/**
	 * Draws the background layer of this container (behind the items). Taken
	 * directly from ChestScreen / BeaconScreen
	 * 
	 */
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX,
			int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE); // this.minecraft.getTextureManager()
		// width and height are the size provided to the window when initialised after
		// creation.
		// xSize, ySize are the expected size of the texture-? usually seems to be left
		// as a default.
		// The code below is typical for vanilla containers, so I've just copied that-
		// it appears to centre the texture within
		// the available window
		int edgeSpacingX = (this.width - this.imageWidth) / 2;
		int edgeSpacingY = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, this.imageWidth, this.imageHeight);
	}

	// This is the resource location for the background image for the GUI
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Nekoration.MODID,
			"textures/gui/easel_menu.png");
}
