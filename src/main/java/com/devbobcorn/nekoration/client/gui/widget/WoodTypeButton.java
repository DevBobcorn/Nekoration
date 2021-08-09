package com.devbobcorn.nekoration.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.client.event.CreativeInventoryEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class WoodTypeButton extends Button
{
    private static final ResourceLocation TABS = new ResourceLocation(Nekoration.MODID, "textures/gui/tabs.png");

    private CreativeInventoryEvents.WoodFilter category;
    private ItemStack stack;
    private boolean toggled;

    public WoodTypeButton(int x, int y, CreativeInventoryEvents.WoodFilter category, OnPress pressable)
    {
        super(x, y, 32, 28, TextComponent.EMPTY, pressable);
        this.category = category;
        this.stack = category.getIcon();
        this.toggled = category.isEnabled();
    }

    public CreativeInventoryEvents.WoodFilter getCategory()
    {
        return this.category;
    }

    @Override
    public void onPress()
    {
        this.toggled = !this.toggled;
        this.category.setEnabled(this.toggled);
        super.onPress();
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bindForSetup(TABS);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        // RenderSystem.disableLighting(); TODO & below
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        int width = this.toggled ? 32 : 28;
        int textureX = 28;
        int textureY = this.toggled ? 32 : 0;
        this.drawRotatedTexture(this.x, this.y, textureX, textureY, width, 28);

        // RenderSystem.enableRescaleNormal();
        ItemRenderer renderer = mc.getItemRenderer();
        renderer.blitOffset = 100.0F;
        renderer.renderGuiItem(this.stack, x + 8, y + 6);
        renderer.renderGuiItemDecorations(mc.font, this.stack, x + 8, y + 6);
        renderer.blitOffset = 0.0F;
    }

    private void drawRotatedTexture(int x, int y, int textureX, int textureY, int width, int height)
    {
        float scaleX = 0.00390625F;
        float scaleY = 0.00390625F;
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(x, y + height, 0.0).uv(((float) (textureX + height) * scaleX), ((float) (textureY) * scaleY)).endVertex();
        buffer.vertex(x + width, y + height, 0.0).uv(((float) (textureX + height) * scaleX), ((float) (textureY + width) * scaleY)).endVertex();
        buffer.vertex(x + width, y, 0.0).uv(((float) (textureX) * scaleX), ((float) (textureY + width) * scaleY)).endVertex();
        buffer.vertex(x, y, 0.0).uv(((float) (textureX) * scaleX), ((float) (textureY) * scaleY)).endVertex();
        tessellator.end();
    }

    public void updateState()
    {
        this.toggled = this.category.isEnabled();
    }
}
