package io.devbobcorn.nekoration.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * An icon button which draws a 16x16 icon on the current vanilla button.
 * Author: MrCrayfish (original concept), ported to 1.21.1.
 */
public class IconButton extends Button {
    public interface OnPress {
        void onPress(IconButton button);
    }

    private ResourceLocation iconResource;
    private int iconU;
    private int iconV;
    private final OnPress pressable;

    public IconButton(int x, int y, Component message, OnPress pressable, ResourceLocation iconResource, int iconU, int iconV) {
        super(x, y, 20, 20, message, button -> ((IconButton) button).onPress(), DEFAULT_NARRATION);
        this.pressable = pressable;
        this.iconResource = iconResource;
        this.iconU = iconU;
        this.iconV = iconV;
    }

    public void setIcon(ResourceLocation iconResource, int iconU, int iconV) {
        this.iconResource = iconResource;
        this.iconU = iconU;
        this.iconV = iconV;
    }

    @Override
    public void onPress() {
        this.pressable.onPress(this);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        if (!this.active) {
            graphics.setColor(0.5F, 0.5F, 0.5F, this.alpha);
        }
        graphics.blit(this.iconResource, getX() + 2, getY() + 2, iconU, iconV, 16, 16, 256, 256);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void renderString(GuiGraphics graphics, Font font, int color) {
        // The message is used as a tooltip; the 20x20 button only displays its icon.
    }
}
