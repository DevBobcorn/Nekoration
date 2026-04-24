package io.devbobcorn.nekoration.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.devbobcorn.nekoration.blocks.containers.EaselMenuBlock;
import io.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.DyeColor;
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
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }

        for (int side = 0; side < 2; side++) {
            renderSideItems(blockEntity, state, side, level, poseStack, buffer, packedLight);
            renderSideText(blockEntity, state, side, poseStack, buffer, packedLight);
        }
    }

    private static void renderSideItems(EaselMenuBlockEntity blockEntity, BlockState state, int side, Level level,
            PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.getValue(EaselMenuBlock.FACING).toYRot() + side * 180.0F));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-22.5F));

        int seedBase = (int) blockEntity.getBlockPos().asLong();
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        int slotBase = side * 4;

        // Match legacy order/offsets: 0,1 top row; 2,3 bottom row.
        poseStack.translate(-0.3D, 0.0D, 0.4D);
        itemRenderer.renderStatic(blockEntity.getItem(slotBase), ItemDisplayContext.GROUND, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, buffer, level, seedBase + 1);

        poseStack.translate(0.6D, 0.0D, 0.0D);
        itemRenderer.renderStatic(blockEntity.getItem(slotBase + 1), ItemDisplayContext.GROUND, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, buffer, level, seedBase + 2);

        poseStack.translate(0.0D, -0.6D, 0.0D);
        itemRenderer.renderStatic(blockEntity.getItem(slotBase + 3), ItemDisplayContext.GROUND, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, buffer, level, seedBase + 3);

        poseStack.translate(-0.6D, 0.0D, 0.0D);
        itemRenderer.renderStatic(blockEntity.getItem(slotBase + 2), ItemDisplayContext.GROUND, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, buffer, level, seedBase + 4);
        poseStack.popPose();
    }

    private static void renderSideText(EaselMenuBlockEntity blockEntity, BlockState state, int side, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {
        Font font = Minecraft.getInstance().font;
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.getValue(EaselMenuBlock.FACING).toYRot() + side * 180.0F));
        poseStack.translate(-0.3D, 0.4D, 0.08D);
        poseStack.mulPose(Axis.XP.rotationDegrees(-22.5F));
        poseStack.scale(0.015F, -0.015F, 0.015F);

        DyeColor[] colors = blockEntity.getColors();
        int slotBase = side * 4;
        int light = blockEntity.isGlowing() ? LightTexture.FULL_BRIGHT : packedLight;
        Font.DisplayMode mode = blockEntity.isGlowing() ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL;

        for (int i = 0; i < 4; i++) {
            int slot = slotBase + i;
            font.drawInBatch(blockEntity.getMessage(slot).getString(), 1.0F, 1.0F, colors[slot].getTextColor(), false,
                    poseStack.last().pose(), buffer, mode, 0, light);
            poseStack.translate(0.0F, 12.0F, 0.0F);
        }
        poseStack.popPose();
    }
}
