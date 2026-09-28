package io.devbobcorn.nekoration.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import io.devbobcorn.nekoration.blocks.containers.CustomBlockMenu;
import io.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import io.devbobcorn.nekoration.client.rendering.CustomRendererTintGetter;
import io.devbobcorn.nekoration.network.CustomBlockClearPayload;
import io.devbobcorn.nekoration.network.CustomBlockUpdatePayload;
import io.devbobcorn.nekoration.xplat.NekoPlatform;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Entry selection screen for a Custom Block. Entries are listed vertically on
 * the left of the screen over the world: left click selects the entry that the
 * palette and paw tools edit, right click clears it.
 */
public class CustomBlockScreen extends AbstractContainerScreen<CustomBlockMenu> {
    private static final int LIST_X = 6;
    private static final int LIST_Y = 6;
    private static final int ROW_HEIGHT = 18;
    private static final int PREVIEW_SIZE = 16;
    private static final int LABEL_X = LIST_X + PREVIEW_SIZE + 4;
    private static final int LABEL_MAX_WIDTH = 140;
    private static final int ACTIVE_BACKGROUND = 0x50000000;
    private static final int ACTIVE_ACCENT = 0xFFF0C000;
    private static final int ACTIVE_TEXT = 0xFFFFD700;
    private static final int TEXT = 0xFFFFFFFF;

    private int scrollRow;

    public CustomBlockScreen(CustomBlockMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.imageWidth = 0;
        this.imageHeight = 0;
        this.leftPos = 0;
        this.topPos = 0;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Transparent: the entry list floats directly over the world.
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        List<Integer> entries = visibleEntries();
        int visibleRows = visibleRowCount();
        scrollRow = Mth.clamp(scrollRow, 0, Math.max(0, entries.size() - visibleRows));
        renderEntries(graphics, entries, visibleRows);
        int row = rowAt(mouseY);
        if (row >= 0 && scrollRow + row < entries.size()) {
            renderEntryTooltip(graphics, entries.get(scrollRow + row), mouseX, mouseY);
        }
    }

    private void renderEntries(GuiGraphics graphics, List<Integer> entries, int visibleRows) {
        CustomBlockEntity customBlock = menu.getCustomBlock();
        int active = customBlock.activeIndex();
        int count = Math.min(entries.size() - scrollRow, visibleRows);
        for (int row = 0; row < count; row++) {
            int index = entries.get(scrollRow + row);
            CustomBlockEntity.CustomEntry entry = customBlock.entry(index);
            if (entry == null) {
                continue;
            }
            int y = LIST_Y + row * ROW_HEIGHT;
            String label = font.plainSubstrByWidth(stateLabel(entry.displayState()).getString(), labelWidth());
            boolean isActive = index == active;
            if (isActive) {
                graphics.fill(LIST_X - 3, y - 1, LABEL_X + font.width(label) + 3, y + ROW_HEIGHT - 1,
                        ACTIVE_BACKGROUND);
                graphics.fill(LIST_X - 3, y - 1, LIST_X - 1, y + ROW_HEIGHT - 1, ACTIVE_ACCENT);
            }
            renderPreview(graphics, entry, LIST_X, y);
            graphics.drawString(font, label, LABEL_X, y + 5, isActive ? ACTIVE_TEXT : TEXT, true);
        }
    }

    @SuppressWarnings("deprecation")
    private void renderPreview(GuiGraphics graphics, CustomBlockEntity.CustomEntry entry, int x, int y) {
        Level level = minecraft != null ? minecraft.level : null;
        if (level == null) {
            return;
        }
        BlockState state = entry.displayState();
        if (state.isAir()) {
            return;
        }
        BlockRenderDispatcher dispatcher = minecraft.getBlockRenderer();
        BakedModel model = dispatcher.getBlockModel(state);
        RenderType renderType = ItemBlockRenderTypes.getRenderType(state, false);
        CustomRendererTintGetter tintGetter = new CustomRendererTintGetter(level);
        tintGetter.setFullBright(true);
        tintGetter.setTintPos(menu.getCustomBlock().getBlockPos());
        if (entry.tinted()) {
            tintGetter.setCustomTint(entry.color());
        }
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + PREVIEW_SIZE / 2.0F, y + PREVIEW_SIZE / 2.0F + 2.0F, 150.0F);
        pose.scale(8.0F, -8.0F, 8.0F);
        pose.mulPose(Axis.XP.rotationDegrees(30.0F));
        pose.mulPose(Axis.YP.rotationDegrees(225.0F));
        pose.translate(-0.5F, -0.5F, -0.5F);
        dispatcher.getModelRenderer().tesselateBlock(tintGetter, model, state, BlockPos.ZERO, pose,
                graphics.bufferSource().getBuffer(renderType), false, RandomSource.create(), 42L,
                OverlayTexture.NO_OVERLAY);
        graphics.flush();
        pose.popPose();
    }

    private void renderEntryTooltip(GuiGraphics graphics, int index, int mouseX, int mouseY) {
        CustomBlockEntity.CustomEntry entry = menu.getCustomBlock().entry(index);
        if (entry == null) {
            return;
        }
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("gui.nekoration.custom_block.entry", index + 1));
        lines.add(stateLabel(entry.displayState()));
        lines.add(Component.translatable("gui.nekoration.custom_block.select"));
        lines.add(Component.translatable("gui.nekoration.custom_block.clear"));
        graphics.renderTooltip(font, lines, Optional.empty(), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int row = rowAt(mouseY);
        List<Integer> entries = visibleEntries();
        if (row >= 0 && scrollRow + row < entries.size() && isWithinList(mouseX)) {
            int index = entries.get(scrollRow + row);
            CustomBlockEntity customBlock = menu.getCustomBlock();
            if (button == 0) {
                customBlock.setActive(index);
                sendUpdate();
            } else if (button == 1 && customBlock.entry(index) != null) {
                customBlock.removeEntry(index);
                NekoPlatform.sendToServer(new CustomBlockClearPayload(customBlock.getBlockPos(), index));
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxScroll = Math.max(0, visibleEntries().size() - visibleRowCount());
        if (maxScroll > 0 && scrollY != 0) {
            scrollRow = Mth.clamp(scrollRow - (int) Math.signum(scrollY), 0, maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void sendUpdate() {
        CustomBlockEntity customBlock = menu.getCustomBlock();
        NekoPlatform.sendToServer(new CustomBlockUpdatePayload(customBlock.getBlockPos(),
                customBlock.activeIndex(), customBlock.showHint()));
    }

    private List<Integer> visibleEntries() {
        CustomBlockEntity customBlock = menu.getCustomBlock();
        List<Integer> entries = new ArrayList<>();
        for (int index = 0; index < CustomBlockEntity.MAX_ENTRIES; index++) {
            if (customBlock.entry(index) != null) {
                entries.add(index);
            }
        }
        return entries;
    }

    private int visibleRowCount() {
        return Math.max(1, (height - LIST_Y - 4) / ROW_HEIGHT);
    }

    private int rowAt(double mouseY) {
        int row = (int) ((mouseY - LIST_Y) / ROW_HEIGHT);
        return row >= 0 && row < visibleRowCount() ? row : -1;
    }

    private int labelWidth() {
        return Math.min(LABEL_MAX_WIDTH, Math.max(20, width - LABEL_X - 6));
    }

    private static boolean isWithinList(double mouseX) {
        return mouseX >= LIST_X - 3 && mouseX <= LABEL_X + LABEL_MAX_WIDTH + 3;
    }

    private static Component stateLabel(BlockState state) {
        Component name = state.getBlock().getName();
        if (state.getProperties().isEmpty()) {
            return name;
        }
        StringBuilder builder = new StringBuilder(name.getString()).append(" (");
        boolean first = true;
        for (Property<?> property : state.getProperties()) {
            if (!first) {
                builder.append(", ");
            }
            first = false;
            builder.append(property.getName()).append('=').append(propertyValue(state, property));
        }
        return Component.literal(builder.append(')').toString());
    }

    private static <T extends Comparable<T>> String propertyValue(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }
}
