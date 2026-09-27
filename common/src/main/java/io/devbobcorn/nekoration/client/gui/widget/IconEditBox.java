package io.devbobcorn.nekoration.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IconEditBox extends EditBox {
    public static final int ICON_SIZE = 16;
    public static final int ICON_GAP = 1;

    private final ResourceLocation iconResource;
    private final int iconU;
    private final int iconV;
    private final int iconX;
    private final int iconY;

    public IconEditBox(Font font, int x, int y, int width, int height, Component message, ResourceLocation iconResource, int iconU, int iconV) {
        super(font, x + ICON_SIZE + ICON_GAP, y, width, height, message);
        this.iconResource = iconResource;
        this.iconU = iconU;
        this.iconV = iconV;
        this.iconX = x;
        this.iconY = y + (height - ICON_SIZE) / 2;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (this.visible && mouseX >= this.iconX && mouseX < this.iconX + ICON_SIZE && mouseY >= this.iconY && mouseY < this.iconY + ICON_SIZE)
            return true;
        return super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(this.iconResource, this.iconX, this.iconY, this.iconU, this.iconV, ICON_SIZE, ICON_SIZE, 256, 256);
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
    }
}
