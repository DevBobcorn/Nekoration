package io.devbobcorn.nekoration.client.gui.screen;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.containers.EaselMenuMenu;
import io.devbobcorn.nekoration.network.EaselMenuUpdatePayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/**
 * Easel menu GUI with editable text lines, line colors, and glowing toggle.
 */
public class EaselMenuScreen extends AbstractContainerScreen<EaselMenuMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "textures/gui/easel_menu.png");
    private static final int LINE_COUNT = 8;

    private final EditBox[] textInputs = new EditBox[LINE_COUNT];

    private Button glowButton;
    private int selectedColor = DyeColor.WHITE.getId();
    private int editingText = 0;
    private boolean showColorPicker;
    private Component tipMessageOn;
    private Component tipMessageOff;

    public EaselMenuScreen(EaselMenuMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 176;
        imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
        final int extraOffsetX = 14;
        final int extraOffsetY = 10;
        for (int i = 0; i < LINE_COUNT; i++) {
            int x = leftPos + extraOffsetX + (i < 4 ? 8 : 98);
            int y = topPos + extraOffsetY + 36 + (i % 4) * 18;
            EditBox input = new EditBox(font, x, y, 70, 18, Component.empty());
            input.setMaxLength(32);
            int line = i;
            input.setResponder(value -> menu.getEasel().setMessage(line, Component.literal(value)));
            input.setTextColor(menu.getEasel().getColor(i).getTextColor());
            input.setTextColorUneditable(DyeColor.LIGHT_GRAY.getTextColor());
            input.setBordered(false);
            input.setValue(menu.getEasel().getMessage(i).getString());
            addRenderableWidget(input);
            textInputs[i] = input;
        }

        Component enableGlow = Component.translatable("gui.nekoration.button.enable_glow");
        Component disableGlow = Component.translatable("gui.nekoration.button.disable_glow");
        boolean glowing = menu.getEasel().isGlowing();
        glowButton = Button.builder(glowing ? disableGlow : enableGlow, button -> {
            boolean glow = menu.getEasel().toggleGlowing();
            button.setMessage(glow ? disableGlow : enableGlow);
        }).bounds(leftPos + imageWidth + 2, topPos + 4, 62, 20).build();
        addRenderableWidget(glowButton);

        setFocused(textInputs[0]);
        textInputs[0].setFocused(true);
        selectedColor = menu.getEasel().getColor(0).getId();
        tipMessageOn = Component.translatable("gui.nekoration.message.press_key_color_picker_on", "F1");
        tipMessageOff = Component.translatable("gui.nekoration.message.press_key_color_picker_off", "F1");
    }

    @Override
    protected void containerTick() {
        super.containerTick();
    }

    @Override
    public void onClose() {
        String[] texts = new String[LINE_COUNT];
        for (int i = 0; i < LINE_COUNT; i++) {
            texts[i] = menu.getEasel().getMessage(i).getString();
        }
        PacketDistributor.sendToServer(new EaselMenuUpdatePayload(
                menu.getEasel().getBlockPos(),
                texts,
                menu.getEasel().getColors(),
                menu.getEasel().isGlowing()));
        super.onClose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_E) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (minecraft != null && minecraft.player != null) {
                minecraft.player.closeContainer();
            }
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_F1) {
            showColorPicker = !showColorPicker;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result = super.mouseClicked(mouseX, mouseY, button);
        refreshFocusedLine();

        if (showColorPicker) {
            for (int i = 0; i < DyeColor.values().length; i++) {
                if (i == selectedColor) {
                    continue;
                }
                int x = leftPos - 20 - (i == selectedColor ? 4 : 0);
                int y = topPos + 8 + i * 14;
                if (mouseX >= x && mouseX <= x + 12 && mouseY >= y && mouseY <= y + 12) {
                    DyeColor color = DyeColor.byId(i);
                    menu.getEasel().setColor(editingText, color);
                    textInputs[editingText].setTextColor(color.getTextColor());
                    selectedColor = i;
                    setFocused(textInputs[editingText]);
                    textInputs[editingText].setFocused(true);
                    return true;
                }
            }
        }
        return result;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        if (showColorPicker) {
            renderColorPicker(graphics);
        }
        graphics.drawString(font, showColorPicker ? tipMessageOff : tipMessageOn, leftPos + imageWidth + 4, topPos + 30,
                0xFFAAAAAA, false);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 6, 8, DyeColor.GRAY.getTextColor(), false);
    }

    private void renderColorPicker(GuiGraphics graphics) {
        for (int i = 0; i < DyeColor.values().length; i++) {
            int x = leftPos - 20 - (i == selectedColor ? 4 : 0);
            int y = topPos + 8 + i * 14;
            int rgb = DyeColor.byId(i).getTextColor();
            int argb = 0xFF000000 | (rgb & 0x00FFFFFF);
            graphics.fill(x, y, x + 12, y + 12, argb);
            int border = i == selectedColor ? 0xFFFFFFFF : 0xFF444444;
            graphics.renderOutline(x - 1, y - 1, 14, 14, border);
        }
    }

    private void refreshFocusedLine() {
        for (int i = 0; i < textInputs.length; i++) {
            if (textInputs[i].isFocused()) {
                editingText = i;
                selectedColor = Mth.clamp(menu.getEasel().getColor(i).getId(), 0, DyeColor.values().length - 1);
                return;
            }
        }
    }
}
