package com.devbobcorn.nekoration.client.gui.screen;

import java.awt.Color;
import java.util.Objects;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.network.C2SUpdatePaletteData;
import com.devbobcorn.nekoration.network.ModPacketHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;

public class PaletteScreen extends Screen {
    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Nekoration.MODID, "textures/gui/palette.png");

    public final int COLORMAP_LEFT = 9;
    public final int COLORMAP_TOP = 32;
    public final int COLORMAP_WIDTH = 128;
    public final int COLORMAP_HEIGHT = 128;

    public final int HUE_LEFT = 141;
    public final int HUE_TOP = 32;
    public final int HUE_WIDTH = 6;
    public final int HUE_HEIGHT = 128;
    public final int white = (255 << 24) + (255 << 16) + (255 << 8) + 255; // a, r, g, b...
    public final int black = 255 << 24; // a, r, g, b...

    private final int imageWidth = 156;
    private final int imageHeight = 166;

    private int leftPos;
    private int topPos;

    private Color colorMapColor = Color.RED;
    private Color[] colors = new Color[6];
    private byte activeSlot = 0;

    private int huePos = -1;
    private int[] colorPos = { -1, -1 };
    private Hand hand;

    public boolean renderColorText = false;

    public PaletteScreen(Hand hand, byte active, Color[] oldColors) {
        super(ITextComponent.nullToEmpty("PALETTE"));
        this.hand = hand;
        this.activeSlot = active;
        this.colors = oldColors;
    }

    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

	@Override
    @SuppressWarnings({"resource"})
    public void onClose() {
		try {
			// Update Color Data...
            int[] cls = new int[6];
            for (int idx = 0;idx < 6;idx++)
                cls[idx] = colors[idx].getRGB();
            final C2SUpdatePaletteData packet = new C2SUpdatePaletteData(this.hand, activeSlot, cls);
			ModPacketHandler.CHANNEL.sendToServer(packet);
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
            this.renderColorText = !this.renderColorText;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Objects.requireNonNull(Objects.requireNonNull(this.minecraft).player).closeContainer();
            //return true;
        }
        return super.keyPressed(keyCode, scanCode, modifier);
    }

    public void render(MatrixStack stack, int x, int y, float partialTicks) {
        int i = this.leftPos;
        int j = this.topPos;

        super.render(stack, x, y, partialTicks);

        // Step 1: Render the 6 color slots, and the 'selected color' slot in the middle...
        this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
		for (int idx = 0;idx < 6;idx++){
            RenderSystem.color4f(colors[idx].getRed() / 255.0F, colors[idx].getGreen() / 255.0F, colors[idx].getBlue() / 255.0F, 1.0F);
            this.blit(stack, i + 8 + 18 * idx + (idx > 2 ? 34: 0), j + 13, 172, 32, 16, 16); // Tinted Pure White Quad...
            if (idx == activeSlot){
                this.blit(stack, i + 70, j + 13, 172, 32, 16, 16);
            }
        }
        // Step 2: Render the back ground...
        this.renderBg(stack, partialTicks, x, y);
        // Step 3: Render Active Slot Indicator...
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.blit(stack, i + 8 + 18 * activeSlot + (activeSlot > 2 ? 34: 0), j + 13, 172, 16, 16, 16); // Slot Indicator...
        // Step 4: Render the color map...
        RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
        //int col = (255 << 24) + (255 << 16) + (0 << 8) + 0; // [RED] a, r, g, b...
        stack.pushPose();
        stack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
        stack.translate(j + 32, -i - 137, 0);
        //this.fillGradient(stack, i, j, i + 128, j + 128, col, black);
        this.fillGradient(stack, 0, 0, 128, 128, colorMapColor.getRGB(), white);
        stack.popPose();
        this.fillGradient(stack, i + COLORMAP_LEFT, j + COLORMAP_TOP, i + COLORMAP_LEFT + 128, j + COLORMAP_TOP + 128, 0, black);
        // Step 5: Render Hue & Color cursors...
        if (huePos >= 0)
            this.blit(stack, i + 140, huePos + this.topPos - 1, 156, 48, 8, 4); // Hue Cursor...
        if (colorPos[0] >= 0)
            this.blit(stack, this.leftPos + colorPos[0] - 2, this.topPos + colorPos[1] - 2, 172, 48, 4, 4); // Color Cursor...
        // Step 6: Render Debug Color Value...
        stack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
        stack.translate(j, -i - 167, 0);
        //this.fillGradient(stack, i, j, i + 128, j + 128, col, black);
        if (renderColorText)
            this.font.draw(stack, "Color: " + colors[activeSlot].getRGB() + " R:" +  + colors[activeSlot].getRed() + " G:" +  + colors[activeSlot].getGreen() + " B:" +  + colors[activeSlot].getBlue(), 1.0F, 1.0F, colors[activeSlot].getRGB());
        else this.font.draw(stack, "Press 'E' to toggle Color Info.", 1.0F, 1.0F, (150 << 24) + (255 << 16) + (255 << 8) + 255);
    }

	protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE); //We've bound this before...
		int edgeSpacingX = (this.width - this.imageWidth) / 2;
		int edgeSpacingY = (this.height - this.imageHeight) / 2;
		this.blit(stack, edgeSpacingX, edgeSpacingY, 0, 0, this.imageWidth, this.imageHeight);
	}

    public boolean mouseClicked(double x, double y, int type){
        if (type == 0 && !updateActiveSlot(x, y)){ // Middle Mouse Only, and first update slots...
            //System.out.println((isOnColorMap(x, y) ? getColor(x, y) : "Not on ColorMap...") + " type: " + type);
            if (isOnColorMap(x, y))
                getColor(x, y);
            if (isOnHuePicker(x, y))
                getHue(x, y);
        }
        return super.mouseClicked(x, y, type);
    }

    private boolean isOnColorMap(double x, double y){
        double dx = x - this.leftPos - COLORMAP_LEFT;
        double dy = y - this.topPos - COLORMAP_TOP;
        return dx >= 0.0D && dy >= 0.0D && dx <= COLORMAP_WIDTH && dy <= COLORMAP_HEIGHT;
    }
    
    private boolean updateActiveSlot(double x, double y){
        for (int idx = 0;idx < 6;idx++){
            int l = this.leftPos + 8 + 18 * idx + (idx > 2 ? 34: 0);
            int r = l + 16;
            int t = this.topPos  + 13;
            int b = t + 16;
            if (x >= l && x <= r && y >= t && y <= b && this.activeSlot != idx){
                this.activeSlot = (byte)idx;
                // And also update that hue picker & color map...
                Color nw = colors[idx];
                float[] fl = Color.RGBtoHSB(nw.getRed(), nw.getGreen(), nw.getBlue(), null); // Hue, Saturation, Value(or to say Brightness)...
                this.huePos = HUE_TOP + (int)((1.0F - fl[0]) * HUE_HEIGHT);
                this.colorMapColor = Color.getHSBColor(fl[0], 1.0F, 1.0F);
                return true;
            }
        }
        return false;
    }

    private void getColor(double x, double y){
        double xi = (x - this.leftPos - COLORMAP_LEFT) / COLORMAP_WIDTH;
        double yi = (y - this.topPos - COLORMAP_TOP) / COLORMAP_WIDTH;
        if (xi >= 0.0D && xi <= 1.0D && yi >= 0.0D && yi <= 1.0D){
            //Color.getHSBColor(0.0F,xi, yi);
            Color c1 = NekoColors.getRGBColorBetween(xi, Color.WHITE, colorMapColor);
            if (activeSlot >= 0 && activeSlot < colors.length) {
                colors[activeSlot] = NekoColors.getRGBColorBetween(yi, c1, Color.BLACK);
                colorPos[0] = (int)x - leftPos;
                colorPos[1] = (int)y - topPos;
            }
        }
    }

    private boolean isOnHuePicker(double x, double y){
        double dx = x - this.leftPos - HUE_LEFT;
        double dy = y - this.topPos - HUE_TOP;
        return dx >= 0.0D && dy >= 0.0D && dx <= HUE_WIDTH && dy <= HUE_HEIGHT;
    }

    private void getHue(double x, double y){
        double yi = 1.0D - (y - this.topPos - COLORMAP_TOP) / COLORMAP_WIDTH;
        this.colorMapColor = Color.getHSBColor((float)yi, 1.0F, 1.0F);
        this.colorPos[0] = -1;
        this.colorPos[1] = -1;
        this.huePos = (int)y - this.topPos;
    }

    @Override
    public boolean isPauseScreen() {
        return false; // returns ture by default... interesting...
    }
}