package com.devbobcorn.nekoration.client.gui.screen;

import java.awt.Color;
import java.util.Objects;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.entities.PaintingEntity;
import com.devbobcorn.nekoration.items.PaletteItem;
import com.devbobcorn.nekoration.network.C2SUpdatePaintingData;
import com.devbobcorn.nekoration.network.ModPacketHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;

public class PaintingScreen extends Screen {
    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Nekoration.MODID, "textures/gui/painting.png");

    public static final int PAINTING_LEFT = 9;
    public static final int PAINTING_TOP = 38;
    public static final int PAINTING_WIDTH = 177;
    public static final int PAINTING_HEIGHT = 128;

    public static final int HUE_LEFT = 189;
    public static final int HUE_TOP = 38;
    public static final int HUE_WIDTH = 6;
    public static final int HUE_HEIGHT = 128;
    public static final int white = (255 << 24) + (255 << 16) + (255 << 8) + 255; // a, r, g, b...
    public static final int black = 255 << 24; // a, r, g, b...

    private final int imageWidth = 204;
    private final int imageHeight = 175;

    private int leftPos;
    private int topPos;
    private int entityId;
    //private Color colorMapColor = Color.RED;
    private Color[] colors = new Color[6];
    private byte activeSlot = 0;
    private int huePos = -1;
    private int[] colorPos = { -1, -1 };
    public boolean renderColorText = false;

    private int[] pixels;
    private short paintingWidth;
    private short paintingHeight;

    private double hor = 0.0D, ver = 0.0D;
    private int pixsize = 8;

    @SuppressWarnings("resource")
    public PaintingScreen(int pt) {
        super(ITextComponent.nullToEmpty("PAINTING"));
        activeSlot = 0;
        colors = PaletteItem.DEFAULT_COLOR_SET;
        entityId = pt;
        try {
            PaintingEntity painting = (PaintingEntity) Minecraft.getInstance().level.getEntity(entityId);
            pixels = painting.data.getPixels();
            paintingWidth  = painting.data.getWidth();
            paintingHeight = painting.data.getHeight();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("resource")
    public PaintingScreen(int pt, byte active, Color[] paletteColors) {
        super(ITextComponent.nullToEmpty("PAINTING"));
        activeSlot = active;
        colors = paletteColors;
        entityId = pt;
        try {
            PaintingEntity painting = (PaintingEntity) Minecraft.getInstance().level.getEntity(entityId);
            pixels = painting.data.getPixels();
            paintingWidth  = painting.data.getWidth();
            paintingHeight = painting.data.getHeight();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

	@Override
    public void onClose() {
		try {
            // Update Color Data...
            final C2SUpdatePaintingData packet = new C2SUpdatePaintingData(entityId, pixels);
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

    private String funtext = "Fun, yeah.";

    @SuppressWarnings("deprecation")
    public void render(MatrixStack stack, int x, int y, float partialTicks) {
        int i = this.leftPos;
        int j = this.topPos;

        super.render(stack, x, y, partialTicks);
        // Step 0: Fill the back ground...
        this.fillGradient(stack, 0, 0, width, height, -1072689136, -804253680);
        // Step 1: Render the 6 color slots, and the 'selected color' slot in the middle...
        this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
		for (int idx = 0;idx < 6;idx++){
            RenderSystem.color4f(colors[idx].getRed() / 255.0F, colors[idx].getGreen() / 255.0F, colors[idx].getBlue() / 255.0F, 1.0F);
            this.blit(stack, i + 34 + 18 * idx, j + 13, 220, 32, 16, 16); // Tinted Pure White Quad...
            if (idx == activeSlot){
                this.blit(stack, i + 8, j + 13, 220, 32, 16, 16);
            }
        }
        // Step 1.5: Render the painting...
        try {
            Color color;
            for (short posi = 0;posi < paintingWidth;posi++){
                if (hor + (posi + 1) * pixsize < 0 || hor + posi * pixsize > PAINTING_WIDTH)
                    continue;
                for (short posj = 0;posj < paintingHeight;posj++) {
                    if (ver + (posj + 1) * pixsize < 0 || ver + posj * pixsize > PAINTING_HEIGHT)
                        continue;
                    color = NekoColors.getRGBColor(pixels[posi + paintingWidth * posj]);
                    RenderSystem.color4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
                    this.blit(stack, i + PAINTING_LEFT + (int)hor + posi * pixsize, j + PAINTING_TOP + (int)ver + posj * pixsize, 220, 32, pixsize, pixsize); // Tinted Pure White Quad...
                }
            }
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        } catch (Exception e){

        }
        // Step 2: Render the back ground...
        this.renderBg(stack, partialTicks, x, y);
        // Step 3: Render Active Slot Indicator...
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.blit(stack, i + 34 + 18 * activeSlot, j + 13, 220, 16, 16, 16); // Slot Indicator...
        // Step 5: Render Hue cursor...
        if (huePos >= 0)
            this.blit(stack, i + HUE_LEFT - 1, huePos + this.topPos - 1, 204, 48, 8, 4); // Hue Cursor...
        // Step 6: Render Debug Color Value...
        stack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
        stack.translate(j + 5, -i - 215, 0);
        //this.fillGradient(stack, i, j, i + 128, j + 128, col, black);
        if (renderColorText)
            this.font.draw(stack, funtext, 1.0F, 1.0F, 0xFFFFFF);
            //this.font.draw(stack, "Color: " + colors[activeSlot].getRGB() + " R:" +  + colors[activeSlot].getRed() + " G:" +  + colors[activeSlot].getGreen() + " B:" +  + colors[activeSlot].getBlue(), 1.0F, 1.0F, colors[activeSlot].getRGB());
        else this.font.draw(stack, "Press 'E' to view operations.", 1.0F, 1.0F, (150 << 24) + (255 << 16) + (255 << 8) + 255);
    }

    @SuppressWarnings("deprecation")
	protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE); //We've bound this before...
		int edgeSpacingX = (this.width - this.imageWidth) / 2;
		int edgeSpacingY = (this.height - this.imageHeight) / 2;
		this.blit(stack, edgeSpacingX, edgeSpacingY, 0, 0, this.imageWidth, this.imageHeight);
	}

    @Override
    public boolean mouseClicked(double x, double y, int type){
        if (type == 0 && !updateActiveSlot(x, y)){ // Left Mouse Only, and first update slots...
            double areax = x - this.leftPos - PAINTING_LEFT;
            double areay = y - this.topPos - PAINTING_TOP;
            double pixX = (areax - hor) / (double)pixsize;
            double pixY = (areay - ver) / (double)pixsize;
            funtext = String.format("Draw: [%.2f, %.2f]", pixX, pixY);
            usePencil(pixX, pixY);
            if (isOnHuePicker(x, y))
                getHue(x, y);
        }
        return super.mouseClicked(x, y, type);
    }

    @Override
    public boolean mouseDragged(double x, double y, int type, double dx, double dy){
        //getColor(x, y);
        if (type == 0 && isOnPainting(x, y)) { // Left Button, draw...
            if (isOnPainting(x, y)) {
                double areax = x - this.leftPos - PAINTING_LEFT;
                double areay = y - this.topPos - PAINTING_TOP;
                double pixX = (areax - hor) / (double)pixsize;
                double pixY = (areay - ver) / (double)pixsize;
                funtext = String.format("Draw: [%.2f, %.2f]", pixX, pixY);
                usePencil(pixX, pixY);
            }
            else if (isOnHuePicker(x, y)) getHue(x, y);
        } else if (type == 2){ // Middle Button, drag...
            hor += dx;
            ver += dy;
            funtext = "Position: " + x + ", " + y + " -> " + dx + ", " + dy;
        }
        return super.mouseDragged(x, y, type, dx, dy);
    }

    private void usePencil(double x, double y){
        int pixX = (int)x, pixY = (int)y;
        if (legal(pixX, pixY))
            pixels[pixY * paintingWidth + pixX] = colors[activeSlot].getRGB();
    }

    private boolean legal(int x, int y){
        return x >= 0 && y >= 0 && x < paintingWidth && y < paintingHeight;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double d2){
        funtext = "Scroll: " + x + ", " + y + ", " + d2;
        double areax = x - this.leftPos - PAINTING_LEFT;
        double areay = y - this.topPos - PAINTING_TOP;

        double oldpixsize = pixsize;
        pixsize += d2;
        pixsize = Math.min(Math.max(1, pixsize), 10);
        double scale = pixsize / oldpixsize;
        double dx = (areax - hor) * scale;
        double dy = (areay - ver) * scale;
        hor = areax - dx;
        ver = areay - dy;
        return super.mouseScrolled(x, y, d2);
    }

    private boolean isOnPainting(double x, double y){
        double dx = x - this.leftPos - PAINTING_LEFT;
        double dy = y - this.topPos - PAINTING_TOP;
        return dx >= 0.0D && dy >= 0.0D && dx <= PAINTING_WIDTH && dy <= PAINTING_HEIGHT;
    }
    
    private boolean updateActiveSlot(double x, double y){
        for (int idx = 0;idx < 6;idx++){
            int l = this.leftPos + 34 + 18 * idx;
            int r = l + 16;
            int t = this.topPos  + 13;
            int b = t + 16;
            if (x >= l && x <= r && y >= t && y <= b && this.activeSlot != idx){
                this.activeSlot = (byte)idx;
                // And also update that hue picker & color map...
                Color nw = colors[idx];
                float[] fl = Color.RGBtoHSB(nw.getRed(), nw.getGreen(), nw.getBlue(), null); // Hue, Saturation, Value(or to say Brightness)...
                this.huePos = HUE_TOP + (int)((1.0F - fl[0]) * HUE_HEIGHT);
                //this.colorMapColor = Color.getHSBColor(fl[0], 1.0F, 1.0F);
                return true;
            }
        }
        return false;
    }

    private boolean isOnHuePicker(double x, double y){
        double dx = x - this.leftPos - HUE_LEFT;
        double dy = y - this.topPos - HUE_TOP;
        return dx >= 0.0D && dy >= 0.0D && dx <= HUE_WIDTH && dy <= HUE_HEIGHT;
    }

    private void getHue(double x, double y){
        //double yi = 1.0D - (y - this.topPos - PAINTING_TOP) / PAINTING_WIDTH;
        //this.colorMapColor = Color.getHSBColor((float)yi, 1.0F, 1.0F);
        this.colorPos[0] = -1;
        this.colorPos[1] = -1;
        this.huePos = (int)y - this.topPos;
    }

    @Override
    public boolean isPauseScreen() {
        return false; // returns ture by default... interesting...
    }
}
