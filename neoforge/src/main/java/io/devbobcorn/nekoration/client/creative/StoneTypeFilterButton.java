package io.devbobcorn.nekoration.client.creative;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.systems.RenderSystem;

import org.joml.Quaternionf;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.NekoStone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Toggle for one stone type in the creative stone-blocks filter (same chrome as {@link WoodTypeFilterButton}).
 */
public final class StoneTypeFilterButton extends AbstractButton {
    private static final ResourceLocation TABS = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "textures/gui/tabs.png");

    private @Nullable NekoStone stone;
    private ItemStack icon = ItemStack.EMPTY;
    private boolean toggled = true;
    private boolean filterUiActive = true;
    private final BiConsumer<NekoStone, Boolean> onChanged;

    public StoneTypeFilterButton(int x, int y, BiConsumer<NekoStone, Boolean> onChanged) {
        super(x, y, 32, 28, Component.empty());
        this.onChanged = onChanged;
    }

    public void bind(@Nullable NekoStone type, boolean enabled, int x, int y) {
        this.stone = type;
        setPosition(x, y);
        if (type == null) {
            this.icon = ItemStack.EMPTY;
            setTooltip((Tooltip) null);
            setToggledVisual(true);
            return;
        }
        this.icon = new ItemStack(type.vanillaStoneBlock().asItem());
        setTooltip(Tooltip.create(Component.translatable(type.vanillaStoneDescriptionId())));
        setToggledVisual(enabled);
    }

    public boolean isBound() {
        return stone != null;
    }

    public @Nullable NekoStone stoneType() {
        return stone;
    }

    public void setToggledVisual(boolean on) {
        this.toggled = on;
    }

    public void setFilterUiActive(boolean active) {
        this.filterUiActive = active;
        this.visible = active;
        this.active = active;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!filterUiActive || stone == null) {
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!filterUiActive || stone == null) {
            return;
        }
        RenderSystem.enableBlend();
        int drawW = toggled ? 32 : 28;
        int textureY = toggled ? 32 : 0;
        graphics.pose().pushPose();
        graphics.pose().translate(getX() + drawW / 2f, getY() + 14f, 0f);
        graphics.pose().mulPose(new Quaternionf().rotationZ((float) (Math.PI * 3 / 2)));
        graphics.pose().translate(-14f, -drawW / 2f, 0f);
        graphics.blit(TABS, 0, 0, 28, textureY, 28, drawW, 256, 256);
        graphics.pose().popPose();
        if (!icon.isEmpty()) {
            graphics.renderItem(icon, getX() + 8, getY() + 6);
            graphics.renderItemDecorations(Minecraft.getInstance().font, icon, getX() + 8, getY() + 6);
        }
    }

    @Override
    public void onPress() {
        if (stone == null) {
            return;
        }
        toggled = !toggled;
        onChanged.accept(stone, toggled);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {
        defaultButtonNarrationText(narration);
    }
}
