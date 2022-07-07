package com.devbobcorn.nekoration.client.gui.screen;

import java.util.Objects;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.containers.EaselMenuMenu;
import com.devbobcorn.nekoration.client.gui.widget.IconButton;
import com.devbobcorn.nekoration.network.C2SUpdateEaselMenuData;
import com.devbobcorn.nekoration.network.ModPacketHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeColor;

public class EaselMenuScreen extends AbstractContainerScreen<EaselMenuMenu> {
    private EditBox[] textInputs = new EditBox[8];

    private IconButton glowButton;

    private static final int COLOR_NUM = DyeColor.values().length;
    private static final float[][] COLOR_SET = new float[COLOR_NUM][3];

    private int selectedColor = DyeColor.WHITE.getId();
    private int editingText = 0;

    public boolean showColorPicker = false;
    private Component tipMessage1;
    private Component tipMessage2;

    public EaselMenuScreen(EaselMenuMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        // Set the width and height of the gui. Should match the size of the texture!
        imageWidth = 176;
        imageHeight = 222;
        for (int i = 0;i < COLOR_NUM;i++){
            int color = DyeColor.byId(i).getTextColor();
            COLOR_SET[i][0] = NekoColors.getRedf(color);
            COLOR_SET[i][1] = NekoColors.getGreenf(color);
            COLOR_SET[i][2] = NekoColors.getBluef(color);
        }
        tipMessage1 = Component.translatable("gui.nekoration.message.press_key_color_picker_on", "'F1'");
        tipMessage2 = Component.translatable("gui.nekoration.message.press_key_color_picker_off", "'F1'");
    }

    @Override
    @SuppressWarnings({"resource"})
    public void init(){
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        Objects.requireNonNull(this.minecraft).keyboardHandler.setSendRepeatsToGui(true);
        final int extraOffsetX = 14;
        final int extraOffsetY = 10;
        for (int i = 0;i < 8;i++) {
            this.textInputs[i] = new EditBox(this.font, this.leftPos + extraOffsetX + (i < 4 ? 8 : 98), this.topPos + extraOffsetY + 36 + (i % 4) * 18, 70, 18, Component.translatable("gui.nekoration.color"));
            this.textInputs[i].setMaxLength(8);
            final int j = i;
            this.textInputs[j].setResponder(input -> {
                this.menu.easel.setMessage(j, Component.nullToEmpty(input));
            });
            this.textInputs[i].setVisible(true);
            this.textInputs[i].setTextColor(this.menu.easel.getColor(i).getTextColor());
            this.textInputs[i].setTextColorUneditable(DyeColor.LIGHT_GRAY.getTextColor());
            this.textInputs[i].setBordered(false);
            this.textInputs[i].setMessage(this.menu.easel.getMessage(i)); // TODO Check if it is correct
            this.addWidget(this.textInputs[i]);
        }
        final Component enableGlow = Component.translatable("gui.nekoration.button.enable_glow");
        final Component disableGlow = Component.translatable("gui.nekoration.button.disable_glow");

        glowButton = new IconButton(leftPos + imageWidth + 2, topPos + 4, menu.easel.getGlowing() ? disableGlow : enableGlow, button -> {
            boolean glow = menu.easel.toggleGlowing();
            button.setMessage(glow ? disableGlow : enableGlow);
            ((IconButton)button).setIcon(ICONS, glow ? 0 : 16, 0);
        }, ICONS, menu.easel.getGlowing() ? 0 : 16, 0);
        this.addWidget(glowButton);
        this.setFocused(this.textInputs[0]);
    }

    @Override
    public void setFocused(GuiEventListener widgeti){
        super.setFocused(widgeti);
        if (widgeti instanceof EditBox){
            EditBox widget = (EditBox) getFocused();
            for (int i = 0;i < textInputs.length;i++){
                if (textInputs[i] == widget){
                    editingText = i;
                    selectedColor = menu.easel.getColor(i).getId();
                } else if (textInputs[i].isFocused()) // Avoid Faulty Multi-Focus...
                    textInputs[i].setFocus(false);
            }
        }
    }

    @Override
    public void containerTick(){
        for (int i = 0;i < 8;i++){
            textInputs[i].tick();
        }
    }

    @Override
    public void onClose() {
        try {
            //Send a packet to the Server tu update data...
            final C2SUpdateEaselMenuData packet = new C2SUpdateEaselMenuData(menu.easel.getBlockPos(), menu.easel.getMessages(), menu.easel.getColors(), menu.easel.getGlowing());
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
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Objects.requireNonNull(Objects.requireNonNull(this.minecraft).player).closeContainer();
            //return true;
        } else if (keyCode == GLFW.GLFW_KEY_F1) {
            showColorPicker = !showColorPicker;
            return true;
        }
        boolean res = super.keyPressed(keyCode, scanCode, modifier);
        for (int i = 0;i < 8;i++){
            //res |= textInputs[i].keyPressed(keyCode, scanCode, modifier);
            res |= textInputs[i].canConsumeInput();
        }
        return res;
    }

    @Override
    public boolean mouseClicked(double x, double y, int type){
        // Call the super type's first because it changes focused control...
        boolean res = super.mouseClicked(x, y, type);
        if (showColorPicker)
            for (int i = 0;i < COLOR_NUM;i++) {
                if (i == selectedColor)
                    continue;
                if (isOn(x + 16, y - 8 - i * 14, 12, 12)){
                    menu.easel.setColor(editingText, DyeColor.byId(i));
                    textInputs[editingText].setTextColor(DyeColor.byId(i).getTextColor());
                    selectedColor = i;
                    setFocused(textInputs[editingText]);
                    textInputs[editingText].setFocus(true);
                }
            }
        return res;
    }

    private boolean isOn(double x, double y, double w, double h){
        double dx = x - this.leftPos;
        double dy = y - this.topPos;
        return dx >= 0.0D && dy >= 0.0D && dx <= w && dy <= h;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, menu.easel.white ? WHITE_BACKGROUND : BACKGROUND);
        if (showColorPicker){
            for (int i = 0;i < COLOR_NUM;i++) {
                RenderSystem.setShaderColor(COLOR_SET[i][0], COLOR_SET[i][1], COLOR_SET[i][2], 1.0F);
                this.blit(stack, leftPos - 16 - (i == selectedColor ? 4 : 0), topPos + 8 + i * 14, 0, 240, 12, 12);
            }
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        super.render(stack, mouseX, mouseY, partialTicks);
        for (int i = 0;i < 8;i++)
            this.textInputs[i].render(stack, mouseX, mouseY, partialTicks);
        glowButton.render(stack, mouseX, mouseY, partialTicks);
        if (glowButton.isMouseOver(mouseX, mouseY)){
            renderTooltip(stack, glowButton.getMessage(), mouseX, mouseY);
        }
        this.renderTooltip(stack, mouseX, mouseY);
        // Render Tip Text...
        stack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
        stack.translate(topPos + 30, -leftPos - 192, 0);
        this.font.draw(stack, showColorPicker ? tipMessage2 : tipMessage1, 1.0F, 1.0F, (150 << 24) + (255 << 16) + (255 << 8) + 255);
    }

    // Draw the foreground layer for the GuiContainer (everything in front of the
    // items) Taken directly from ContainerScreen
    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        final float LABEL_XPOS = 6;
        final float FONT_Y_SPACING = 10;
        final float CHEST_LABEL_YPOS = EaselMenuMenu.TILE_INVENTORY_YPOS - FONT_Y_SPACING;
        this.font.draw(stack, this.title, LABEL_XPOS, CHEST_LABEL_YPOS, DyeColor.GRAY.getTextColor());
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        this.blit(stack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    // This is the resource location for the background image for the GUI
    private static final ResourceLocation ICONS = new ResourceLocation(Nekoration.MODID, "textures/gui/icons.png");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Nekoration.MODID, "textures/gui/easel_menu.png");
    private static final ResourceLocation WHITE_BACKGROUND = new ResourceLocation(Nekoration.MODID, "textures/gui/easel_menu_white.png");
}