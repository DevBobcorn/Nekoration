package io.devbobcorn.nekoration.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * An icon button which draws a 16x16 icon on a vanilla widgets.png button base.
 * Author: MrCrayfish (original concept), ported to 1.21.1.
 */
public class IconButton extends AbstractButton {
    public static final ResourceLocation WIDGETS_LOCATION =
            ResourceLocation.withDefaultNamespace("textures/gui/widgets.png");

    public interface OnPress {
        void onPress(IconButton button);
    }

    private ResourceLocation iconResource;
    private int iconU;
    private int iconV;
    private final OnPress pressable;

    public IconButton(int x, int y, Component message, OnPress pressable, ResourceLocation iconResource, int iconU, int iconV) {
        super(x, y, 20, 20, message);
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
        graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        int offset = !this.active ? 0 : (this.isHoveredOrFocused() ? 2 : 1); // 0: Disabled, 1: Normal, 2: Hovered...

        int x = this.getX();
        int y = this.getY();

        graphics.blit(WIDGETS_LOCATION, x, y, 0, 46 + offset * 20, width / 2, height);
        graphics.blit(WIDGETS_LOCATION, x + width / 2, y, 200 - width / 2, 46 + offset * 20, width / 2, height);
        if (!this.active) {
            graphics.setColor(0.5F, 0.5F, 0.5F, 1.0F);
        }
        graphics.blit(this.iconResource, x + 2, y + 2, iconU, iconV, 16, 16);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
