package com.devbobcorn.nekoration.client.gui.screen;

import java.awt.Color;
import java.util.Objects;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.NekoConfig;
import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.client.gui.widget.IconButton;
import com.devbobcorn.nekoration.entities.PaintingData;
import com.devbobcorn.nekoration.entities.PaintingEntity;
import com.devbobcorn.nekoration.items.PaletteItem;
import com.devbobcorn.nekoration.network.C2SUpdatePaintingData;
import com.devbobcorn.nekoration.network.ModPacketHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class PaintingScreen extends Screen {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Nekoration.MODID, "textures/gui/painting.png");
    public static final ResourceLocation ICONS = new ResourceLocation(Nekoration.MODID, "textures/gui/icons.png");

    public static final int PAINTING_LEFT = 9;
    public static final int PAINTING_TOP = 38;
    public static final int PAINTING_WIDTH = 225;
    public static final int PAINTING_HEIGHT = 145;

    public static final int OPACITY_LEFT = 241;
    public static final int OPACITY_TOP = 38;
    public static final int OPACITY_WIDTH = 6;
    public static final int OPACITY_HEIGHT = 145;

    public static final int TOOLS_LEFT = 148;
    public static final int TOOLS_TOP = 13;
    public static final int TOOLS_NUM = 4;

    public static final int white = (255 << 24) + (255 << 16) + (255 << 8) + 255; // a, r, g, b...
    public static final int black = 255 << 24; // a, r, g, b...

    private final int imageWidth = 256;
    private final int imageHeight = 192;

    private int leftPos;
    private int topPos;
    private int entityId;
    //private Color colorMapColor = Color.RED;
    private Color[] colors = new Color[6];
    private byte activeSlot = 0;
    private int opacity = 255;
    private int opacityPos = -1;
    private byte activeTool = 0; // 0: Pencil, 1: Pen, 2: Eraser, 3: Bucket Fill
    // private int[] pointerPos = { -1, -1 };
    public boolean renderDebugText = false;

    private final PaintingData paintingData;
    private final short paintingWidth;
    private final short paintingHeight;

    private final int oldHash;

    private EditBox nameInput;
    private boolean nameError = false;
    private IconButton[] buttons = new IconButton[3];

    // Used on Client-Side only
    private static double hor = 0.0D, ver = 0.0D;
    private static int pixsize = 8;
    private static int lastEdited = 0;

    private TranslatableComponent tipMessage;
    private static final String[] buttonKeys = { "save_painting", "save_painting_content", "load_image", "clear" };
    private TranslatableComponent[] buttonMessages = new TranslatableComponent[buttonKeys.length];

    public PaintingScreen(int pt) {
        this(pt, (byte)0, PaletteItem.DEFAULT_COLOR_SET);
    }

    @SuppressWarnings("resource")
    public PaintingScreen(int pt, byte active, Color[] paletteColors) {
        super(Component.nullToEmpty("PAINTING"));
        opacityPos = topPos + OPACITY_TOP;
        activeSlot = active;
        colors = paletteColors;
        entityId = pt;
        PaintingEntity painting = (PaintingEntity) Minecraft.getInstance().level.getEntity(entityId);
        paintingData = painting.data;
        paintingWidth  = painting.data.getWidth();
        paintingHeight = painting.data.getHeight();
        paintingData.imageReady = false;
        oldHash = paintingData.getPaintingHash();
        if (oldHash != lastEdited) {
            // Reset editor window position and scale...
            hor = ver = 0.0D;
            pixsize = 8;
        } // Or if we're editing the painting this client last edited, just keep the editor's transforms.
        tipMessage = new TranslatableComponent("gui.nekoration.message.press_key_debug_info", "'F1'");
        for (int idx = 0;idx < buttonKeys.length;idx++)
            buttonMessages[idx] = new TranslatableComponent("gui.nekoration.button." + buttonKeys[idx]);
    }

    private String[] getFileLocation(){
        String file = nameInput.getValue().trim();
        if (file.equals(""))
            return new String[]{ "nekopaint", String.valueOf(paintingData.getPaintingHash()) };
        int slashIdx = Math.max(file.lastIndexOf("/"), file.lastIndexOf("\\"));
        if (slashIdx == -1)
            return new String[]{ "nekopaint", file };
        else return new String[]{ "nekopaint/" + file.substring(0, slashIdx + 1), file.substring(slashIdx + 1) };
    }

    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.nameInput = new EditBox(this.font, leftPos + 36, topPos - 18, 120, 16, new TranslatableComponent("gui.nekoration.color"));
        this.nameInput.setResponder(input -> {
            if (nameError) {
                this.nameInput.setTextColor(0xFFFFFF); // No more Red Error text if text changed.
                nameError = false;
                // Get the Load Button back...
                buttons[2].setIcon(ICONS, 32, 16);
                buttons[2].setMessage(buttonMessages[2]);
            }
        });
        buttons[0] = new IconButton(leftPos + 160, topPos - 20, buttonMessages[0], button -> {
            // Save Image...
            try{
                String[] location = getFileLocation();
                paintingData.save(location[0], location[1], true, true);
            } catch (Exception e){
                e.printStackTrace();
            }
		}, ICONS, 0, 16);
        buttons[1] = new IconButton(leftPos + 180, topPos - 20, buttonMessages[1], button -> {
            // Save Image Content...
            try{
                String[] location = getFileLocation();
                paintingData.save(location[0], location[1], false, true);
            } catch (Exception e){
                e.printStackTrace();
            }
		}, ICONS, 16, 16);
        buttons[2] = new IconButton(leftPos + 200, topPos - 20, buttonMessages[2], button -> {
            if (nameError) { // It's a 'Clear' Button
                nameInput.setValue("");
                nameInput.setTextColor(0xFFFFFF);
                nameError = false;
                // Get the Load Button back...
                buttons[2].setIcon(ICONS, 32, 16);
                buttons[2].setMessage(buttonMessages[2]);
                return;
            }
			// Load Image File...
            if (nameInput.getValue().equals("")) {
                nameInput.setValue("Input the name here...");
                nameError = true;
            } else {
                String[] location = getFileLocation();
                nameError = !paintingData.load(location[0], location[1]);
            }
            if (nameError) {
                // Make the text Red...
                nameInput.setTextColor(0xFF0000);
                // Turn the 3rd button into a clear button...
                buttons[2].setIcon(ICONS, 32, 0);
                buttons[2].setMessage(buttonMessages[3]);
            }
		}, ICONS, 32, 16);
        this.addWidget(nameInput);
        for (int btn = 0;btn < 3;btn++)
            this.addWidget(buttons[btn]);
    }

    @Override
    public void tick() {
        nameInput.tick();
    }

	@Override
    public void onClose() {
		try {
            if (oldHash != paintingData.getPaintingHash()){ // Check if the painting was changed...
                // Clear obsoleted cache of itself (the Server can't help it to)...
                paintingData.clearCache(oldHash);
                byte blocW = (byte)(paintingWidth / 16);
                byte blocH = (byte)(paintingHeight / 16);
                int partCount = 0;
                for (byte blocX = 0;blocX < blocW;blocX += 3)
                    for (byte blocY = 0;blocY < blocH;blocY += 3){
                        // Prepare a Part of the painting...
                        byte ptW = (byte)Math.min(blocW - blocX, 3);
                        byte ptH = (byte)Math.min(blocH - blocY, 3);

                        final int[] partPixels = new int[ptW * 16 * ptH * 16];
                        // Prepare pixel data for this Part...
                        for (int i = 0;i < ptW * 16;i++)
                            for (int j = 0;j < ptH * 16;j++) {
                                int x = blocX * 16 + i;
                                int y = blocY * 16 + j;
                                partPixels[j * ptW * 16 + i] = paintingData.getPixelAt(x, y);
                                // Part Coord.               => Painting Coord.
                            }
                        // Update Painting Data to the Server, and then the Server will notify clients(including this one) to update their cache...
                        //final C2SUpdatePaintingData packet = new C2SUpdatePaintingData(entityId,  paintingData.getPixels(), paintingData.getPaintingHash());
                        System.out.println(String.format("[%s] Sending Painting(%s) Part: %s, %s (%s x %s)", partCount, paintingData.getPaintingHash(), blocX / 3, blocY / 3, ptW, ptH));
                        final C2SUpdatePaintingData packet = new C2SUpdatePaintingData(entityId, (byte)(blocX / 3), (byte)(blocY / 3), ptW, ptH, partPixels, paintingData.getPaintingHash());
                        ModPacketHandler.CHANNEL.sendToServer(packet);
                        partCount++;
                    }
                // Cache on this client...
                if (NekoConfig.CLIENT.useImageRendering.get())
                    paintingData.cache();
            } else {// Not Edited, still ready...
                paintingData.imageReady = true;
            }
		} catch (Exception e){
			e.printStackTrace();
		}
        // Update last edited...
        lastEdited = paintingData.getPaintingHash();
        super.onClose();
    }

	@Override
	@SuppressWarnings({"resource"})
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
		if (keyCode == GLFW.GLFW_KEY_F1) {
			// I DONT GET IT, WHY THE HELL PRESSING 'E' CAN CLOSE THE SCREEN...
            this.renderDebugText = !this.renderDebugText;
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_W){
            // Switch Tool...
            activeTool = (byte)((activeTool + 1) % TOOLS_NUM);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Objects.requireNonNull(Objects.requireNonNull(this.minecraft).player).closeContainer();
        }
        return super.keyPressed(keyCode, scanCode, modifier);
    }

    private String debugText = "A nice line of debug text, isn't it?";

    public void render(PoseStack stack, int x, int y, float partialTicks) {
        int i = this.leftPos;
        int j = this.topPos;

        super.render(stack, x, y, partialTicks);
        // Step 0: Fill the back ground...
        this.fillGradient(stack, 0, 0, width, height, -1072689136, -804253680);
        // Step 1: Render the 6 color slots, and the 'selected color' slot in the middle...
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
		for (int idx = 0;idx < 6;idx++){
            RenderSystem.setShaderColor(colors[idx].getRed() / 255.0F, colors[idx].getGreen() / 255.0F, colors[idx].getBlue() / 255.0F, 1.0F);
            this.blit(stack, i + 34 + 18 * idx, j + 13, 16, 224, 16, 16); // Tinted Pure White Quad...
            if (idx == activeSlot){
                this.blit(stack, i + 8, j + 13, 16, 224, 16, 16);
            }
        }
        // Step 2: Render the painting...
        try {
            Color color;
            for (short posi = 0;posi < paintingWidth;posi++){
                if (hor + (posi + 1) * pixsize < 0 || hor + posi * pixsize > PAINTING_WIDTH)
                    continue;
                for (short posj = 0;posj < paintingHeight;posj++) {
                    if (ver + (posj + 1) * pixsize < 0 || ver + posj * pixsize > PAINTING_HEIGHT)
                        continue;
                    color = NekoColors.getRGBColor(paintingData.getCompositeAt(posi, posj));
                    RenderSystem.setShaderColor(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
                    this.blit(stack, i + PAINTING_LEFT + (int)hor + posi * pixsize, j + PAINTING_TOP + (int)ver + posj * pixsize, 16, 224, pixsize, pixsize); // Tinted Pure White Quad...
                }
            }
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } catch (Exception e){

        }
        // Step 3: Render the back ground...
        this.renderBg(stack, partialTicks, x, y);
        // Step 4: Render Active Slot Indicator...
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.blit(stack, i + 34 + 18 * activeSlot, j + 13, 16, 208, 16, 16); // Slot Indicator...
        // Step 5: Render Opacity Bar...
        this.fillGradient(stack, i + OPACITY_LEFT, j + OPACITY_TOP, i + OPACITY_LEFT + OPACITY_WIDTH, j + OPACITY_TOP + OPACITY_HEIGHT, colors[activeSlot].getRGB(), colors[activeSlot].getRGB() & 0xffffff);
        // Step 6: Render Opacity cursor...
        if (opacityPos >= 0)
        this.blit(stack, i + OPACITY_LEFT - 1, opacityPos + this.topPos - 1, 0, 240, 8, 4); // Opacity Cursor...
        // Step 7: Render Active Tool Icon...
        this.blit(stack, i + TOOLS_LEFT + activeTool * 17, j + TOOLS_TOP, 32 + activeTool * 16, 208, 16, 16);
        // Step 8: Render Import/Export Controls...
        nameInput.render(stack, x, y, partialTicks);
        for (int btn = 0;btn < 3;btn++) {
            buttons[btn].render(stack, x, y, partialTicks);
        }
        for (int tip = 0;tip < 3;tip++) {
            if(buttons[tip].isMouseOver(x, y)) {
                renderTooltip(stack, buttons[tip].getMessage(), x, y);
            }
        }
        // Step 9: Render Debug Text...
        stack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
        stack.translate(j + 5, -i - 265, 0);
        //this.fillGradient(stack, i, j, i + 128, j + 128, col, black);
        if (renderDebugText)
            this.font.draw(stack, debugText, 1.0F, 1.0F, 0xFFFFFF);
        else this.font.draw(stack, tipMessage, 1.0F, 1.0F, (150 << 24) + (255 << 16) + (255 << 8) + 255);
    }

	protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
		this.blit(stack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
	}

    @Override
    public boolean mouseClicked(double x, double y, int type){
        if (type == 0 && !updateActiveSlot(x, y)){ // Left Mouse Only, and first update slots...
            if (isOnOpacityPicker(x, y)){
                getOpacity(x, y);
                debugText = "Opacity: " + opacity;
            } else if (isOnPainting(x, y)){
                useTool(x, y);
            } else {
                for (byte idx = 0;idx < TOOLS_NUM;idx++){
                    // if (x > leftPos + TOOLS_LEFT + idx * 17 && x < leftPos + TOOLS_LEFT + idx * 17 + 16 && y > topPos + TOOLS_TOP && y < topPos + TOOLS_TOP + 16)
                    //     activeTool = idx;
                    if (isOn(x - TOOLS_LEFT - idx * 17, y - TOOLS_TOP, 16, 16))
                        activeTool = idx;
                }
            }
        }
        return super.mouseClicked(x, y, type);
    }

    private boolean isOn(double x, double y, double w, double h){
        double dx = x - this.leftPos;
        double dy = y - this.topPos;
        return dx >= 0.0D && dy >= 0.0D && dx <= w && dy <= h;
    }

    @Override
    public boolean mouseDragged(double x, double y, int type, double dx, double dy){
        //getColor(x, y);
        if (type == 0) { // Left Button, draw...
            if (isOnPainting(x, y)) {
                useTool(x, y);
            }
            else if (isOnOpacityPicker(x, y)) {
                getOpacity(x, y);
                debugText = "Opacity: " + opacity;
            }
        } else if (type == 2){ // Middle Button, drag...
            hor += dx;
            ver += dy;
            debugText = "Position: " + x + ", " + y + " -> " + dx + ", " + dy;
        }
        return super.mouseDragged(x, y, type, dx, dy);
    }

    private void useTool(double x, double y){
        double areax = x - this.leftPos - PAINTING_LEFT;
        double areay = y - this.topPos - PAINTING_TOP;
        double pixX = (areax - hor) / (double)pixsize;
        double pixY = (areay - ver) / (double)pixsize;
        switch (activeTool){
            case 0: // Pencil
                debugText = String.format("Stroke: [%.2f, %.2f]", pixX, pixY);
                usePencil(pixX, pixY);
                break;
            case 1: // Pen
                debugText = String.format("Draw: [%.2f, %.2f]", pixX, pixY);
                usePen(pixX, pixY);
                break;
            case 2: // Eraser
                debugText = String.format("Erase: [%.2f, %.2f]", pixX, pixY);
                useEraser(pixX, pixY);
                break;
            case 3: // Bucket
                debugText = String.format("Fill: [%.2f, %.2f]", pixX, pixY);
                useBucket(pixX, pixY);
                break;
        }
    }

    private void usePencil(double x, double y){
        int pixX = (int)x, pixY = (int)y;
        paintingData.setPixel(pixX, pixY, (opacity << 24) + colors[activeSlot].getRGB());
    }

    private void usePen(double x, double y){
        // TODO: Implement
        int pixX = (int)x, pixY = (int)y;
        paintingData.setPixel(pixX, pixY, (opacity << 24) + colors[activeSlot].getRGB());
    }

    private void useBucket(double x, double y){
        int pixX = (int)x, pixY = (int)y;
        paintingData.fill(pixX, pixY, colors[activeSlot].getRGB(), opacity);
    }

    private static final int radius = 2;

    private void useEraser(double x, double y){
        int pixX = (int)x, pixY = (int)y;
        for (int i = -radius;i <= radius;i++)
            for (int j = -radius;j <= radius;j++)
                paintingData.setPixel(pixX + i, pixY + j, 0x00000000);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double d2){
        debugText = "Scroll: " + x + ", " + y + ", " + d2;
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
                return true;
            }
        }
        return false;
    }

    private boolean isOnOpacityPicker(double x, double y){
        double dx = x - this.leftPos - OPACITY_LEFT;
        double dy = y - this.topPos - OPACITY_TOP;
        return dx >= 0.0D && dy >= 0.0D && dx <= OPACITY_WIDTH && dy <= OPACITY_HEIGHT;
    }

    private void getOpacity(double x, double y){
        opacity = (int)((1.0D - (y - this.topPos - OPACITY_TOP) / (double)OPACITY_HEIGHT) * 255.0D);
        this.opacityPos = (int)y - this.topPos;
    }

    @Override
    public boolean isPauseScreen() {
        return false; // returns true by default... interesting...
    }
}