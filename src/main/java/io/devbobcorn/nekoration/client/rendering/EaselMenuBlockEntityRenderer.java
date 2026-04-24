package io.devbobcorn.nekoration.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.devbobcorn.nekoration.blocks.containers.EaselMenuBlock;
import io.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renders easel display items and text directly on the block.
 */
public class EaselMenuBlockEntityRenderer implements BlockEntityRenderer<EaselMenuBlockEntity> {
    public EaselMenuBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(EaselMenuBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
            int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        if (!state.hasProperty(EaselMenuBlock.FACING)) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.getValue(EaselMenuBlock.FACING).toYRot()));

        renderItems(blockEntity, poseStack, buffer, packedLight);
        renderText(blockEntity, poseStack, buffer, packedLight);

        poseStack.popPose();
    }

    private static void renderItems(EaselMenuBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }
        int seedBase = blockEntity.getBlockPos().hashCode();

        for (int i = 0; i < EaselMenuBlockEntity.NUMBER_OF_SLOTS; i++) {
            poseStack.pushPose();
            int column = i < 4 ? 0 : 1;
            int row = i % 4;
            float x = column == 0 ? -0.29F : 0.29F;
            float y = 0.24F - row * 0.19F;

            poseStack.translate(x, y, -0.26D);
            poseStack.mulPose(Axis.XP.rotationDegrees(-10.0F));
            poseStack.scale(0.28F, 0.28F, 0.28F);

            itemRenderer.renderStatic(blockEntity.getItem(i), ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY,
                    poseStack, buffer, level, seedBase + i);
            poseStack.popPose();
        }
    }

    private static void renderText(EaselMenuBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Font font = Minecraft.getInstance().font;
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, -0.505D);
        poseStack.scale(0.0105F, -0.0105F, 0.0105F);

        for (int i = 0; i < EaselMenuBlockEntity.NUMBER_OF_SLOTS; i++) {
            Component message = blockEntity.getMessage(i);
            String text = message.getString();
            if (text.isEmpty()) {
                continue;
            }

            int column = i < 4 ? 0 : 1;
            int row = i % 4;
            float centerX = column == 0 ? -27.0F : 27.0F;
            float y = -24.0F + row * 17.0F;
            float x = centerX - font.width(text) / 2.0F;
            int color = blockEntity.getColor(i).getTextColor();

            font.drawInBatch(text, x, y, color, false, poseStack.last().pose(), buffer,
                    blockEntity.isGlowing() ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, 0, packedLight);
        }
        poseStack.popPose();
    }
}
