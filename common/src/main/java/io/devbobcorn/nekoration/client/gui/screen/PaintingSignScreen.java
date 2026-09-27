package io.devbobcorn.nekoration.client.gui.screen;

import java.util.List;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.network.PaintingSignUpdatePayload;
import io.devbobcorn.nekoration.xplat.NekoPlatform;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;

/**
 * The signing screen for painted paintings. The author is always the signing
 * player; only the title is editable here.
 */
public class PaintingSignScreen extends Screen {
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "textures/gui/painting_signing.png");
    public static final int BACKGROUND_WIDTH = 256;
    public static final int BACKGROUND_HEIGHT = 128;

    private static final int TITLE_MAX_LENGTH = 32;
    private static final int WARNING_WIDTH = 200;
    private static final int LABEL_COLOR = 0xFFFFFFFF;
    private static final int TITLE_COLOR = 0xFFFFFFFF;
    private static final int OWNER_COLOR = 0xFFAAAAAA;
    private static final int WARNING_COLOR = 0xFFFF8080;
    private static final Component EDIT_TITLE_LABEL = Component.translatable("gui.nekoration.message.sign_painting_title");
    private static final Component FINALIZE_WARNING_LABEL = Component.translatable("gui.nekoration.message.sign_painting_warning");
    private static final FormattedCharSequence BRIGHT_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.WHITE));
    private static final FormattedCharSequence DIM_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.GRAY));

    private final InteractionHand hand;
    private final Component ownerText;
    private String title = "";
    private int frameTick;
    private Button finalizeButton;
    private final TextFieldHelper titleEdit = new TextFieldHelper(() -> this.title, text -> this.title = text,
            this::getClipboard, this::setClipboard, text -> text.length() < TITLE_MAX_LENGTH);

    public PaintingSignScreen(InteractionHand hand) {
        super(Component.translatable("gui.nekoration.message.sign_painting"));
        this.hand = hand;
        var player = Minecraft.getInstance().player;
        this.ownerText = Component.translatable("book.byAuthor", player == null ? Component.empty() : player.getName());
    }

    @Override
    protected void init() {
        super.init();
        int leftPos = (this.width - BACKGROUND_WIDTH) / 2;
        int topPos = (this.height - BACKGROUND_HEIGHT) / 2;
        this.finalizeButton = this.addRenderableWidget(Button.builder(Component.translatable("book.finalizeButton"), button -> {
            if (!this.title.isEmpty()) {
                this.signPainting();
                this.minecraft.setScreen(null);
            }
        }).bounds(leftPos + 28, topPos + 96, 98, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(null))
                .bounds(leftPos + 130, topPos + 96, 98, 20).build());
        this.updateButtonVisibility();
    }

    private void signPainting() {
        NekoPlatform.sendToServer(new PaintingSignUpdatePayload(this.hand, this.title));
    }

    private void updateButtonVisibility() {
        this.finalizeButton.active = !this.title.isBlank();
    }

    private String getClipboard() {
        return this.minecraft == null ? "" : TextFieldHelper.getClipboardContents(this.minecraft);
    }

    private void setClipboard(String contents) {
        if (this.minecraft != null)
            TextFieldHelper.setClipboardContents(this.minecraft, contents);
    }

    @Override
    public void tick() {
        super.tick();
        this.frameTick++;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        boolean blink = this.frameTick / 6 % 2 == 0;
        FormattedCharSequence titleWithCursor = FormattedCharSequence.composite(
                FormattedCharSequence.forward(this.title, Style.EMPTY), blink ? BRIGHT_CURSOR : DIM_CURSOR);
        graphics.drawCenteredString(this.font, EDIT_TITLE_LABEL, centerX, centerY - 40, LABEL_COLOR);
        graphics.drawString(this.font, titleWithCursor, centerX - this.font.width(titleWithCursor) / 2, centerY - 24, TITLE_COLOR, false);
        graphics.drawCenteredString(this.font, this.ownerText, centerX, centerY - 12, OWNER_COLOR);
        List<FormattedCharSequence> warningLines = this.font.split(FINALIZE_WARNING_LABEL, WARNING_WIDTH);
        for (int idx = 0; idx < warningLines.size(); idx++) {
            FormattedCharSequence line = warningLines.get(idx);
            graphics.drawString(this.font, line, centerX - this.font.width(line) / 2, centerY + 4 + idx * 10, WARNING_COLOR, false);
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(graphics);
        graphics.blit(BACKGROUND, (this.width - BACKGROUND_WIDTH) / 2, (this.height - BACKGROUND_HEIGHT) / 2, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == 257 || keyCode == 335) { // Enter
            if (!this.title.isEmpty()) {
                this.signPainting();
                this.minecraft.setScreen(null);
            }
            return true;
        } else if (keyCode == 259) { // Backspace
            this.titleEdit.removeCharsFromCursor(-1);
            this.updateButtonVisibility();
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (super.charTyped(codePoint, modifiers)) {
            return true;
        } else if (this.titleEdit.charTyped(codePoint)) {
            this.updateButtonVisibility();
            return true;
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
