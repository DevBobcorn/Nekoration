package io.devbobcorn.nekoration.client.creative;

import io.devbobcorn.nekoration.Nekoration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Small 20×20 control with an icon from {@code textures/gui/icons.png} (legacy Nekoration sheet).
 */
public final class CreativeTabIconButton extends Button {
    private static final ResourceLocation ICONS = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "textures/gui/icons.png");

    private final int iconU;
    private final int iconV;
    private boolean filterUiActive = true;

    public CreativeTabIconButton(int x, int y, Component tooltip, Runnable action, int iconU, int iconV) {
        super(x, y, 20, 20, Component.empty(), button -> action.run(), DEFAULT_NARRATION);
        this.iconU = iconU;
        this.iconV = iconV;
        setTooltip(Tooltip.create(tooltip));
    }

    public void setFilterUiActive(boolean active) {
        this.filterUiActive = active;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!filterUiActive) {
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!filterUiActive) {
            return;
        }
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        graphics.blit(ICONS, getX() + 2, getY() + 2, iconU, iconV, 16, 16, 256, 256);
    }
}
