package io.devbobcorn.nekoration.client.creative;

import io.devbobcorn.nekoration.Nekoration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Small 20×20 control with an icon from {@code textures/gui/icons.png} (legacy Nekoration sheet).
 */
public final class CreativeTabIconButton extends AbstractButton {
    private static final ResourceLocation ICONS = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "textures/gui/icons.png");

    private final int iconU;
    private final int iconV;
    private final Runnable action;
    private boolean filterUiActive = true;

    public CreativeTabIconButton(int x, int y, Component tooltip, Runnable action, int iconU, int iconV) {
        super(x, y, 20, 20, Component.empty());
        this.action = action;
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
        int base = isHovered() ? 0xFFAAAAAA : 0xFF888888;
        // When inactive, stay visible (old creative UI used dimmed but still readable controls).
        int fill = active ? base : 0xFF707070;
        int border = active ? 0xFF303030 : 0xFF404040;
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF000000 | border);
        graphics.fill(getX() + 1, getY() + 1, getX() + width - 1, getY() + height - 1, 0xFF000000 | fill);
        graphics.blit(ICONS, getX() + 2, getY() + 2, iconU, iconV, 16, 16, 256, 256);
    }

    @Override
    public void onPress() {
        if (!active) {
            return;
        }
        action.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {
        defaultButtonNarrationText(narration);
    }
}
