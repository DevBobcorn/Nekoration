package io.devbobcorn.nekoration.client.gui.screen;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.NekoColors;
import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.containers.EaselMenuMenu;
import io.devbobcorn.nekoration.network.EaselMenuUpdatePayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
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
    private static final ResourceLocation ICONS =
            ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "textures/gui/icons.png");
    private static final String FRAME_TEXTURE_PREFIX =
            Nekoration.MODID + ":textures/gui/easel_menu_frame/";
    private static final int LINE_COUNT = 8;
    private static final int MAX_TEXT_WIDTH = 42;

    private final EditBox[] textInputs = new EditBox[LINE_COUNT];

    private ResourceLocation frameTexture;
    private Button glowButton;
    private Component glowEnableTooltip;
    private Component glowDisableTooltip;
    private int selectedColor = DyeColor.WHITE.getId();
    private int editingText = 0;
    private boolean glowing;
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

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(menu.getEasel().getBlockState().getBlock());
        String path = blockId.getPath();
        int suffixPos = path.lastIndexOf("_easel_menu");
        String woodName = suffixPos > 0 ? path.substring(0, suffixPos) : "oak";
        frameTexture = ResourceLocation.parse(FRAME_TEXTURE_PREFIX + woodName + ".png");

        final int extraOffsetX = 14;
        final int extraOffsetY = 10;
        for (int i = 0; i < LINE_COUNT; i++) {
            int x = leftPos + extraOffsetX + (i < 4 ? 8 : 98);
            int y = topPos + extraOffsetY + 36 + (i % 4) * 18;
            EditBox input = new EditBox(font, x, y, 70, 18, Component.empty());
            input.setMaxLength(32);
            input.setFilter(value -> font.width(value) <= MAX_TEXT_WIDTH);
            input.setValue(menu.getEasel().getMessage(i).getString());
            int line = i;
            input.setResponder(value -> {
                menu.getEasel().setMessage(line, Component.literal(value));
                sendUpdateToServer();
            });
            input.setTextColor(menu.getEasel().getColor(i).getTextColor());
            input.setTextColorUneditable(DyeColor.LIGHT_GRAY.getTextColor());
            input.setBordered(false);
            addRenderableWidget(input);
            textInputs[i] = input;
        }

        glowEnableTooltip = Component.translatable("gui.nekoration.button.enable_glow");
        glowDisableTooltip = Component.translatable("gui.nekoration.button.disable_glow");
        glowing = menu.getEasel().isGlowing();
        glowButton = Button.builder(Component.empty(), button -> {
            glowing = menu.getEasel().toggleGlowing();
            updateGlowTooltip();
            sendUpdateToServer();
        }).bounds(leftPos + imageWidth + 2, topPos + 4, 20, 20).build();
        updateGlowTooltip();
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
        sendUpdateToServer();
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
                int x = leftPos - 16;
                int y = topPos + 8 + i * 14;
                if (mouseX >= x && mouseX <= x + 12 && mouseY >= y && mouseY <= y + 12) {
                    DyeColor color = DyeColor.byId(i);
                    menu.getEasel().setColor(editingText, color);
                    textInputs[editingText].setTextColor(color.getTextColor());
                    selectedColor = i;
                    sendUpdateToServer();
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
        renderGlowButtonIcon(graphics);
        graphics.pose().pushPose();
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(90.0F));
        graphics.pose().translate(topPos + 30.0F, -leftPos - 192.0F, 0.0F);
        graphics.drawString(font, showColorPicker ? tipMessageOff : tipMessageOn, 1, 1, 0xFFAAAAAA, false);
        graphics.pose().popPose();
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        graphics.blit(frameTexture, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        BlockState blockState = menu.getEasel().getBlockState();
        if (blockState.hasProperty(DyeableBlock.COLOR)) {
            EnumNekoColor tintColor = blockState.getValue(DyeableBlock.COLOR);
            int rgb = NekoColors.HalfTimberColors.RGB_BY_ORDINAL[tintColor.ordinal()];
            float red   = ((rgb >> 16) & 0xFF) / 255.0F;
            float green = ((rgb >>  8) & 0xFF) / 255.0F;
            float blue  =  (rgb        & 0xFF) / 255.0F;
            graphics.setColor(red, green, blue, 1.0F);
            graphics.blit(BACKGROUND, leftPos + 10,  topPos + 38, 176, 0, 66, 78);
            graphics.blit(BACKGROUND, leftPos + 100, topPos + 38, 176, 0, 66, 78);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    private void renderColorPicker(GuiGraphics graphics) {
        for (int i = 0; i < DyeColor.values().length; i++) {
            int x = leftPos - 16 - (i == selectedColor ? 4 : 0);
            int y = topPos + 8 + i * 14;
            int rgb = DyeColor.byId(i).getTextColor() & 0x00FFFFFF;
            float red = ((rgb >> 16) & 0xFF) / 255.0F;
            float green = ((rgb >> 8) & 0xFF) / 255.0F;
            float blue = (rgb & 0xFF) / 255.0F;
            graphics.setColor(red, green, blue, 1.0F);
            graphics.blit(BACKGROUND, x, y, 0, 240, 12, 12, 256, 256);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
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

    private void sendUpdateToServer() {
        PacketDistributor.sendToServer(new EaselMenuUpdatePayload(
                menu.getEasel().getBlockPos(),
                collectTexts(),
                menu.getEasel().getColors(),
                menu.getEasel().isGlowing()));
    }

    private String[] collectTexts() {
        String[] texts = new String[LINE_COUNT];
        for (int i = 0; i < LINE_COUNT; i++) {
            texts[i] = textInputs[i] != null ? textInputs[i].getValue() : menu.getEasel().getMessage(i).getString();
        }
        return texts;
    }

    private void updateGlowTooltip() {
        if (glowButton != null) {
            glowButton.setTooltip(Tooltip.create(glowing ? glowDisableTooltip : glowEnableTooltip));
        }
    }

    private void renderGlowButtonIcon(GuiGraphics graphics) {
        if (glowButton == null || !glowButton.visible) {
            return;
        }
        int iconU = glowing ? 0 : 16;
        graphics.blit(ICONS, glowButton.getX() + 2, glowButton.getY() + 2, iconU, 0, 16, 16, 256, 256);
    }
}
