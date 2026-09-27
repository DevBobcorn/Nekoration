package io.devbobcorn.nekoration.client.gui.screen;

import io.devbobcorn.nekoration.network.PaintingSignUpdatePayload;
import io.devbobcorn.nekoration.xplat.NekoPlatform;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;

/**
 * The signing screen for painted paintings, styled after vanilla's book
 * signing UI. The author is always the signing player; only the title is
 * editable here.
 */
public class PaintingSignScreen extends Screen {
    private static final int IMAGE_WIDTH = 192;
    private static final int TITLE_MAX_LENGTH = 32;
    private static final Component EDIT_TITLE_LABEL = Component.translatable("book.editTitle");
    private static final Component FINALIZE_WARNING_LABEL = Component.translatable("gui.nekoration.message.sign_painting_warning");
    private static final FormattedCharSequence BLACK_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.BLACK));
    private static final FormattedCharSequence GRAY_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.GRAY));

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
        this.ownerText = Component.translatable("book.byAuthor", player == null ? Component.empty() : player.getName())
                .withStyle(ChatFormatting.DARK_GRAY);
    }

    @Override
    protected void init() {
        this.finalizeButton = this.addRenderableWidget(Button.builder(Component.translatable("book.finalizeButton"), button -> {
            if (!this.title.isEmpty()) {
                this.signPainting();
                this.minecraft.setScreen(null);
            }
        }).bounds(this.width / 2 - 100, 196, 98, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(null))
                .bounds(this.width / 2 + 2, 196, 98, 20).build());
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
        int i = (this.width - IMAGE_WIDTH) / 2;
        boolean blink = this.frameTick / 6 % 2 == 0;
        FormattedCharSequence titleWithCursor = FormattedCharSequence.composite(
                FormattedCharSequence.forward(this.title, Style.EMPTY), blink ? BLACK_CURSOR : GRAY_CURSOR);
        int labelWidth = this.font.width(EDIT_TITLE_LABEL);
        graphics.drawString(this.font, EDIT_TITLE_LABEL, i + 36 + (114 - labelWidth) / 2, 34, 0, false);
        int titleWidth = this.font.width(titleWithCursor);
        graphics.drawString(this.font, titleWithCursor, i + 36 + (114 - titleWidth) / 2, 50, 0, false);
        int ownerWidth = this.font.width(this.ownerText);
        graphics.drawString(this.font, this.ownerText, i + 36 + (114 - ownerWidth) / 2, 60, 0, false);
        graphics.drawWordWrap(this.font, FINALIZE_WARNING_LABEL, i + 36, 82, 114, 0);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(graphics);
        graphics.blit(BookViewScreen.BOOK_LOCATION, (this.width - IMAGE_WIDTH) / 2, 2, 0, 0, 192, 192);
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
