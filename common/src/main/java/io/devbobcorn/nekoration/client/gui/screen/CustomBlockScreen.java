package io.devbobcorn.nekoration.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.CustomBlock;
import io.devbobcorn.nekoration.blocks.containers.CustomBlockMenu;
import io.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import io.devbobcorn.nekoration.client.rendering.CustomRendererTintGetter;
import io.devbobcorn.nekoration.client.rendering.ColorTintVertexConsumer;
import io.devbobcorn.nekoration.common.ComponentCompat;
import io.devbobcorn.nekoration.network.CustomBlockClearPayload;
import io.devbobcorn.nekoration.network.CustomBlockEditPayload;
import io.devbobcorn.nekoration.network.CustomBlockUpdatePayload;
import io.devbobcorn.nekoration.xplat.NekoPlatform;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Entry selection and editing screen for a Custom Block.
 */
public class CustomBlockScreen extends AbstractContainerScreen<CustomBlockMenu> {
    private static final int LIST_X = 6;
    private static final int PANEL_Y = 6;
    private static final int LIST_Y = 25;
    private static final int PANEL_COLOR = 0xB9000000;
    private static final int PANEL_BORDER = 0x805C5C5C;
    private static final int PANEL_WIDTH = 170;
    private static final int CONTROL_HEIGHT = 224;
    private static final int ROW_HEIGHT = 18;
    private static final int PREVIEW_SIZE = 16;
    private static final int LABEL_X = LIST_X + PREVIEW_SIZE + 4;
    private static final int LABEL_MAX_WIDTH = 160;
    private static final int ACTIVE_BACKGROUND = 0x50000000;
    private static final int ACTIVE_ACCENT = 0xFFF0C000;
    private static final int ACTIVE_TEXT = 0xFFFFD700;
    private static final int TEXT = 0xFFFFFFFF;

    private int scrollRow;
    private EditBox colorInput;
    private Button tintButton;
    private Button clearTintButton;
    private Button tintAllFacesButton;
    private Button aoButton;
    private final List<Button> entryButtons = new ArrayList<>();
    private final List<Button> lightButtons = new ArrayList<>();

    public CustomBlockScreen(CustomBlockMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        this.imageWidth = 0;
        this.imageHeight = 0;
        super.init();
        this.leftPos = 0;
        this.topPos = 0;
        entryButtons.clear();
        lightButtons.clear();

        int x = controlsX();
        int w = controlsWidth();
        addStepButtons(x, w, 39, CustomBlockEditPayload.MOVE_X, entryButtons);
        addStepButtons(x, w, 60, CustomBlockEditPayload.MOVE_Y, entryButtons);
        addStepButtons(x, w, 81, CustomBlockEditPayload.MOVE_Z, entryButtons);
        addStepButtons(x, w, 102, CustomBlockEditPayload.ROTATE, entryButtons);

        colorInput = new EditBox(font, x + 8, PANEL_Y + 132, Math.max(36, w - 73), 18,
                Component.translatable("gui.nekoration.custom_block.color"));
        colorInput.setMaxLength(7);
        colorInput.setFilter(value -> value.matches("#?[0-9a-fA-F]*"));
        loadActiveColor();
        addRenderableWidget(colorInput);
        tintButton = addRenderableWidget(Button.builder(Component.translatable("gui.nekoration.custom_block.apply"),
                button -> applyColor()).bounds(x + w - 60, PANEL_Y + 132, 52, 18).build());
        clearTintButton = addRenderableWidget(Button.builder(Component.translatable("gui.nekoration.custom_block.clear_color"),
                button -> edit(CustomBlockEditPayload.CLEAR_TINT, 0))
                .bounds(x + 8, PANEL_Y + 154, (w - 20) / 2, 18).build());
        tintAllFacesButton = addRenderableWidget(Button.builder(Component.empty(),
                button -> edit(CustomBlockEditPayload.TOGGLE_TINT_ALL_FACES, 0))
                .bounds(x + 10 + (w - 20) / 2, PANEL_Y + 154, (w - 20) / 2, 18).build());
        aoButton = addRenderableWidget(Button.builder(Component.empty(), button -> edit(CustomBlockEditPayload.TOGGLE_AO, 0))
                .bounds(x + 8, PANEL_Y + 177, w - 16, 18).build());
        addStepButtons(x, w, 203, CustomBlockEditPayload.CHANGE_LIGHT, lightButtons);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Keep the world visible around the two editor panels.
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        panel(graphics, LIST_X - 3, PANEL_Y, LIST_X - 3 + listWidth(), height - 5);
        int x = controlsX();
        int w = controlsWidth();
        panel(graphics, x, PANEL_Y, x + w, PANEL_Y + CONTROL_HEIGHT);
        graphics.drawString(font, Component.translatable("gui.nekoration.custom_block.entries"), LIST_X, 11, TEXT, false);
        graphics.drawString(font, Component.translatable("gui.nekoration.custom_block.controls"), x + 8, 11, TEXT, false);

        CustomBlockEntity customBlock = menu.getCustomBlock();
        CustomBlockEntity.CustomEntry active = customBlock.activeEntry();
        String selected = active == null ? "-" : font.plainSubstrByWidth(entryName(active).getString(), w - 17);
        graphics.drawString(font, selected, x + 8, 25, ACTIVE_TEXT, false);
        if (active != null) {
            graphics.drawString(font, "X: " + active.offset(0), x + 8, 45, TEXT, false);
            graphics.drawString(font, "Y: " + active.offset(1), x + 8, 66, TEXT, false);
            graphics.drawString(font, "Z: " + active.offset(2), x + 8, 87, TEXT, false);
            graphics.drawString(font, Component.translatable("gui.nekoration.custom_block.rotation", active.dir() * 15),
                    x + 8, 108, TEXT, false);
        }
        graphics.drawString(font, Component.translatable("gui.nekoration.custom_block.color"), x + 8, 126, TEXT, false);
        int light = customBlock.getBlockState().getValue(CustomBlock.LIGHT);
        graphics.drawString(font, Component.translatable("gui.nekoration.custom_block.light", light),
                x + 8, 211, TEXT, false);
        boolean ao = customBlock.getBlockState().getValue(CustomBlock.AMBIENT_OCCLUSION);
        aoButton.setMessage(Component.translatable("gui.nekoration.custom_block.ao", Component.translatable(
                ao ? "options.on" : "options.off")));
        tintAllFacesButton.setMessage(Component.translatable("gui.nekoration.custom_block.all_faces",
                Component.translatable(active != null && active.tintAllFaces() ? "options.on" : "options.off")));
        boolean hasEntry = active != null;
        for (Button button : entryButtons) button.active = hasEntry;
        tintButton.active = hasEntry;
        clearTintButton.active = hasEntry && active.tinted();
        tintAllFacesButton.active = hasEntry;
        colorInput.active = hasEntry;
    }

    private static void panel(GuiGraphics graphics, int x1, int y1, int x2, int y2) {
        graphics.fill(x1, y1, x2, y2, PANEL_COLOR);
        graphics.fill(x1, y1, x2, y1 + 1, PANEL_BORDER);
        graphics.fill(x1, y2 - 1, x2, y2, PANEL_BORDER);
    }

    private void addStepButtons(int x, int w, int y, int action, List<Button> buttons) {
        int buttonY = PANEL_Y + y - 6;
        for (int step : new int[]{-1, 1}) {
            int buttonX = x + w - (step < 0 ? 57 : 32);
            Button button = addRenderableWidget(Button.builder(Component.literal(step < 0 ? "-" : "+"),
                    ignored -> edit(action, step)).bounds(buttonX, buttonY, 23, 17).build());
            buttons.add(button);
        }
    }

    private void edit(int action, int value) {
        CustomBlockEntity customBlock = menu.getCustomBlock();
        CustomBlockEntity.CustomEntry active = customBlock.activeEntry();
        if ((action <= CustomBlockEditPayload.CLEAR_TINT || action == CustomBlockEditPayload.TOGGLE_TINT_ALL_FACES)
                && active == null) return;
        if (active != null) {
            switch (action) {
                case CustomBlockEditPayload.MOVE_X, CustomBlockEditPayload.MOVE_Y, CustomBlockEditPayload.MOVE_Z ->
                        customBlock.moveActive(action, value);
                case CustomBlockEditPayload.ROTATE -> customBlock.rotateActive(value);
                case CustomBlockEditPayload.TINT -> customBlock.tintActive(value);
                case CustomBlockEditPayload.CLEAR_TINT -> customBlock.clearActiveTint();
                case CustomBlockEditPayload.TOGGLE_TINT_ALL_FACES -> customBlock.toggleActiveTintAllFaces();
                default -> { }
            }
        }
        NekoPlatform.sendToServer(new CustomBlockEditPayload(customBlock.getBlockPos(), customBlock.activeIndex(), action, value));
    }

    private void applyColor() {
        String value = colorInput.getValue().replaceFirst("^#", "");
        if (value.length() == 6) {
            edit(CustomBlockEditPayload.TINT, Integer.parseInt(value, 16));
        }
    }

    private void loadActiveColor() {
        CustomBlockEntity.CustomEntry active = menu.getCustomBlock().activeEntry();
        colorInput.setValue(String.format("%06X", active == null ? 0xFFFFFF : active.color()));
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
        if (isWithinList(mouseX) && row >= 0 && scrollRow + row < entries.size()) {
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
            String label = font.plainSubstrByWidth(entryName(entry).getString(), labelWidth());
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
        // The world block layer keeps the tessellator's face shading from being
        // applied a second time by an entity shader.
        RenderType renderType = ItemBlockRenderTypes.getChunkRenderType(state);
        CustomRendererTintGetter tintGetter = new CustomRendererTintGetter(level);
        tintGetter.setFullBright(true);
        tintGetter.setTintPos(menu.getCustomBlock().getBlockPos());
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + PREVIEW_SIZE / 2.0F, y + PREVIEW_SIZE / 2.0F + 2.0F, 150.0F);
        pose.scale(8.0F, -8.0F, 8.0F);
        pose.mulPose(Axis.XP.rotationDegrees(30.0F));
        pose.mulPose(Axis.YP.rotationDegrees(225.0F));
        pose.translate(-0.5F, -0.5F, -0.5F);
        var consumer = graphics.bufferSource().getBuffer(renderType);
        dispatcher.getModelRenderer().tesselateBlock(tintGetter, model, state, BlockPos.ZERO, pose,
                entry.tinted() ? new ColorTintVertexConsumer(consumer, entry.color(), entry.tintAllFaces()) : consumer,
                false, RandomSource.create(), 42L, OverlayTexture.NO_OVERLAY);
        graphics.flush();
        pose.popPose();
    }

    private void renderEntryTooltip(GuiGraphics graphics, int index, int mouseX, int mouseY) {
        CustomBlockEntity.CustomEntry entry = menu.getCustomBlock().entry(index);
        if (entry == null) {
            return;
        }
        List<Component> lines = new ArrayList<>();
        BlockState state = entry.displayState();
        lines.add(Component.literal(String.format("#%d %s", index, BuiltInRegistries.BLOCK.getKey(state.getBlock()))));
        lines.addAll(propertyLabels(state));
        lines.add(Component.translatable("gui.nekoration.custom_block.select").withStyle(ChatFormatting.AQUA));
        lines.add(Component.translatable("gui.nekoration.custom_block.clear").withStyle(ChatFormatting.AQUA));
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
                loadActiveColor();
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
        if (isWithinList(mouseX) && maxScroll > 0 && scrollY != 0) {
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
        return Math.max(1, (height - LIST_Y - 7) / ROW_HEIGHT);
    }

    private int rowAt(double mouseY) {
        int row = (int) Math.floor((mouseY - LIST_Y) / ROW_HEIGHT);
        return row >= 0 && row < visibleRowCount() ? row : -1;
    }

    private int labelWidth() {
        return Math.max(20, listWidth() - (LABEL_X - LIST_X) - 8);
    }

    private int listWidth() {
        return Math.min(LABEL_MAX_WIDTH + LABEL_X - LIST_X + 8, Math.max(105, (width - 22) / 2));
    }

    private int controlsWidth() {
        return Math.min(PANEL_WIDTH, Math.max(105, (width - 22) / 2));
    }

    private int controlsX() {
        return width - controlsWidth() - 6;
    }

    private boolean isWithinList(double mouseX) {
        return mouseX >= LIST_X - 3 && mouseX <= LIST_X - 3 + listWidth();
    }

    /** Localized entry name, resolving the dye color placeholder in name translations. */
    private static Component entryName(CustomBlockEntity.CustomEntry entry) {
        BlockState state = entry.displayState();
        Block block = state.getBlock();
        if (state.hasProperty(DyeableBlock.COLOR)) {
            String colorKey = "color.nekoration." + state.getValue(DyeableBlock.COLOR).getSerializedName();
            return Component.translatable(block.getDescriptionId(), ComponentCompat.interpolationArg(colorKey));
        }
        return block.getName();
    }

    private static List<Component> propertyLabels(BlockState state) {
        List<Component> lines = new ArrayList<>();
        for (Property<?> property : state.getProperties()) {
            lines.add(Component.literal(String.format("%s=%s", property.getName(), propertyValue(state, property))).withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }

    private static <T extends Comparable<T>> String propertyValue(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }
}
