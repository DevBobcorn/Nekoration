package com.devbobcorn.nekoration.client.gui.screen;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.containers.EaselMenuContainer;
import com.devbobcorn.nekoration.network.C2SUpdateEaselMenuTexts;
import com.devbobcorn.nekoration.network.ModPacketHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.Objects;

public class EaselMenuScreen extends ContainerScreen<EaselMenuContainer> {
	private TextFieldWidget[] textInputs = new TextFieldWidget[8];

	private String[] texts = {"", "", "", "", "", "", "", "" };

	public EaselMenuScreen(EaselMenuContainer containerBasic, PlayerInventory playerInventory, ITextComponent title) {
		super(containerBasic, playerInventory, title);
		// Set the width and height of the gui. Should match the size of the texture!
		imageWidth = 176;
		imageHeight = 222;
	}

	@Override
	@SuppressWarnings({"resource"})
	public void init(){
		super.init();
		Objects.requireNonNull(this.minecraft).keyboardHandler.setSendRepeatsToGui(true);
		for (int i = 0;i < 8;i++){
			this.textInputs[i] = new TextFieldWidget(this.font, this.leftPos + (i < 4 ? 8 : 98), this.topPos + 36 + (i % 4) * 18, 70, 18, new TranslationTextComponent("gui.nekoration.color"));
			this.textInputs[i].setMaxLength(8);
			final int j = i;
			this.textInputs[j].setResponder(input -> {
				this.texts[j] = input;
			});
			this.textInputs[i].setVisible(true);
			this.textInputs[i].setTextColor(65535);
			this.textInputs[i].setBordered(false);
			//this.textInputs[i].setValue("TEXT " + i);
			this.textInputs[i].setValue(this.menu.texts[i].getContents());
			this.children.add(this.textInputs[i]);
		}
		this.setFocused(this.textInputs[0]);
	}

	@Override
	public void tick(){
		super.tick();
		for (int i = 0;i < 8;i++){
			textInputs[i].tick();
		}
	}

	@Override
    public void onClose() {
		try {
			//Send a packet to the Server tu update data...
			DyeColor[] cl = { DyeColor.PURPLE, DyeColor.PINK, DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.LIGHT_BLUE, DyeColor.CYAN, DyeColor.BLUE };
			ITextComponent[] tx = new ITextComponent[8];
			for (int i = 0;i < 8;i++)
				tx[i] = ITextComponent.nullToEmpty(textInputs[i].getValue());

			final C2SUpdateEaselMenuTexts packet = new C2SUpdateEaselMenuTexts(this.menu.pos, tx, cl);
			ModPacketHandler.CHANNEL.sendToServer(packet);
			//System.out.println("Packet Sent");
		} catch (Exception e){
			e.printStackTrace();
		}
        super.onClose();
    }

	@Override
	@SuppressWarnings({"resource"})
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
		if (keyCode == GLFW.GLFW_KEY_E) {
			// I DONT GET IT, WHY THE HELL PRESSING 'E' CAN CLOSE THE SCREEN...
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Objects.requireNonNull(Objects.requireNonNull(this.minecraft).player).closeContainer();
            //return true;
        }
		boolean res = super.keyPressed(keyCode, scanCode, modifier);
		for (int i = 0;i < 8;i++){
			res |= textInputs[i].keyPressed(keyCode, scanCode, modifier);
			res |= textInputs[i].canConsumeInput();
		}
        return res;
    }

	@Override
	public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(stack);
		super.render(stack, mouseX, mouseY, partialTicks);
		for (int i = 0;i < 8;i++){
        	this.textInputs[i].render(stack, mouseX, mouseY, partialTicks);
		}
		this.renderTooltip(stack, mouseX, mouseY);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the
	 * items) Taken directly from ContainerScreen
	 */
	@Override
	protected void renderLabels(MatrixStack stack, int mouseX, int mouseY) {
		final float LABEL_XPOS_1 = 6;
		final float FONT_Y_SPACING = 10;
		final float CHEST_LABEL_YPOS = EaselMenuContainer.TILE_INVENTORY_YPOS - FONT_Y_SPACING;
		this.font.draw(stack, this.title, LABEL_XPOS_1, CHEST_LABEL_YPOS, Color.darkGray.getRGB()); // this.font.drawString;

		final float LABEL_XPOS_2 = 96;
		this.font.draw(stack, this.menu.pos.getX() + " " + this.menu.pos.getY() + " " + this.menu.pos.getZ(), LABEL_XPOS_2, CHEST_LABEL_YPOS, Color.darkGray.getRGB()); // this.font.drawString;

		/*
		final float PLAYER_INV_LABEL_YPOS = EaselMenuContainer.PLAYER_INVENTORY_YPOS - FONT_Y_SPACING;
		this.font.draw(matrixStack, this.inventory.getDisplayName(), /// this.font.drawString
				LABEL_XPOS, PLAYER_INV_LABEL_YPOS, Color.darkGray.getRGB());
		*/
	}

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
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Nekoration.MODID, "textures/gui/easel_menu.png");
}
