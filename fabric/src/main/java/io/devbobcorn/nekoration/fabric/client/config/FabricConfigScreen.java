package io.devbobcorn.nekoration.fabric.client.config;

import io.devbobcorn.nekoration.BopDisplayMode;
import io.devbobcorn.nekoration.fabric.xplat.FabricConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/** Client settings backed by Fabric's JSON config. */
public final class FabricConfigScreen extends Screen {
    private final Screen parent;
    private boolean useImageRendering;
    private boolean simplifyRendering;
    private boolean debugMode;
    private int maxUndoLimit;
    private BopDisplayMode bopDisplayMode;

    public FabricConfigScreen(Screen parent) {
        super(Component.translatable("nekoration.configuration.title", Component.literal("Nekoration")));
        this.parent = parent;
        FabricConfig config = FabricConfig.get();
        useImageRendering = config.useImageRendering();
        simplifyRendering = config.simplifyRendering();
        debugMode = config.debugMode();
        maxUndoLimit = config.maxUndoLimit();
        bopDisplayMode = config.bopDisplayMode();
    }

    @Override
    protected void init() {
        int x = width / 2 - 100;
        int y = height / 2 - 78;
        addRenderableWidget(Button.builder(booleanLabel("useImageRendering", useImageRendering), button -> {
            useImageRendering = !useImageRendering;
            button.setMessage(booleanLabel("useImageRendering", useImageRendering));
            save();
        }).bounds(x, y, 200, 20).build());
        addRenderableWidget(Button.builder(booleanLabel("simplifyRendering", simplifyRendering), button -> {
            simplifyRendering = !simplifyRendering;
            button.setMessage(booleanLabel("simplifyRendering", simplifyRendering));
            save();
        }).bounds(x, y + 24, 200, 20).build());
        addRenderableWidget(Button.builder(booleanLabel("debugMode", debugMode), button -> {
            debugMode = !debugMode;
            button.setMessage(booleanLabel("debugMode", debugMode));
            save();
        }).bounds(x, y + 48, 200, 20).build());
        addRenderableWidget(Button.builder(undoLabel(), button -> {
            maxUndoLimit = maxUndoLimit == 30 ? 2 : maxUndoLimit + 1;
            button.setMessage(undoLabel());
            save();
        }).bounds(x, y + 72, 200, 20).build());
        addRenderableWidget(Button.builder(bopLabel(), button -> {
            bopDisplayMode = BopDisplayMode.VALUES[(bopDisplayMode.ordinal() + 1) % BopDisplayMode.VALUES.length];
            button.setMessage(bopLabel());
            save();
        }).bounds(x, y + 96, 200, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds(x, y + 130, 200, 20).build());
    }

    private static Component booleanLabel(String key, boolean value) {
        return Component.translatable("nekoration.configuration." + key).append(": ")
                .append(value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }

    private Component undoLabel() {
        return Component.translatable("nekoration.configuration.maxUndoLimit").append(": " + maxUndoLimit);
    }

    private Component bopLabel() {
        return Component.translatable("nekoration.configuration.bopVariants").append(": ")
                .append(Component.translatable("nekoration.configuration.creative.bopVariants." + bopDisplayMode.getSerializedName()));
    }

    private void save() {
        FabricConfig.update(useImageRendering, simplifyRendering, debugMode, maxUndoLimit, bopDisplayMode);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, height / 2 - 105, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}
