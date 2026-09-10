package io.devbobcorn.nekoration.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import io.devbobcorn.nekoration.blocks.containers.ItemDisplayBlock;
import io.devbobcorn.nekoration.blocks.entities.ItemDisplayBlockEntity;

/**
 * In-world item quads for cupboards and wall shelves (ported from legacy {@code CupboardRenderer}).
 */
public class ItemDisplayBlockEntityRenderer implements BlockEntityRenderer<ItemDisplayBlockEntity> {

    public ItemDisplayBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ItemDisplayBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
            int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        // Legacy CupboardRenderer always drew items; do not skip when OPEN (avoids client state desync hiding items).
        poseStack.pushPose();
        if (blockEntity.wallShelf) {
            renderShelfItems(blockEntity, state, poseStack, buffer, packedLight);
        } else {
            renderCabinetItems(blockEntity, state, poseStack, buffer, packedLight);
        }
        poseStack.popPose();
    }

    private static void renderShelfItems(ItemDisplayBlockEntity tileEntity, BlockState state, PoseStack stack,
            MultiBufferSource buffers, int combinedLight) {
        stack.translate(0.5D, 0.5D, 0.5D);
        stack.mulPose(Axis.YP.rotationDegrees(-state.getValue(ItemDisplayBlock.FACING).toYRot()));
        float sc = 0.5F;
        stack.scale(sc, sc, sc);
        stack.mulPose(Axis.XP.rotationDegrees(-10));

        stack.translate(-1.35D, 0.2D, -0.5D);
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        Level level = renderLevel(tileEntity);
        if (level == null) {
            return;
        }
        int seedBase = tileEntity.getBlockPos().hashCode();
        for (int i = 0; i < 4; i++) {
            stack.translate(0.55D, 0.0D, 0.0D);
            itemRenderer.renderStatic(tileEntity.getRenderStacks().get(i), ItemDisplayContext.GROUND, combinedLight,
                    OverlayTexture.NO_OVERLAY, stack, buffers, level, seedBase + i);
        }
    }

    private static void renderCabinetItems(ItemDisplayBlockEntity tileEntity, BlockState state, PoseStack stack,
            MultiBufferSource buffers, int combinedLight) {
        stack.translate(0.5D, 0.5D, 0.5D);
        stack.mulPose(Axis.YP.rotationDegrees(-state.getValue(ItemDisplayBlock.FACING).toYRot()));
        float sc = 0.5F;
        stack.scale(sc, sc, sc);
        stack.mulPose(Axis.XP.rotationDegrees(-10));

        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        var stacks = tileEntity.getRenderStacks();
        Level level = renderLevel(tileEntity);
        if (level == null) {
            return;
        }
        int seedBase = tileEntity.getBlockPos().hashCode();

        stack.translate(-0.4D, 0.2D, -0.5D);
        itemRenderer.renderStatic(stacks.get(0), ItemDisplayContext.GROUND, combinedLight, OverlayTexture.NO_OVERLAY, stack,
                buffers, level, seedBase);

        stack.translate(0.8D, 0.0D, 0.0D);
        itemRenderer.renderStatic(stacks.get(1), ItemDisplayContext.GROUND, combinedLight, OverlayTexture.NO_OVERLAY, stack,
                buffers, level, seedBase + 1);

        stack.translate(0.0D, -0.7D, -0.2D);
        itemRenderer.renderStatic(stacks.get(3), ItemDisplayContext.GROUND, combinedLight, OverlayTexture.NO_OVERLAY, stack,
                buffers, level, seedBase + 3);

        stack.translate(-0.8D, 0.0D, 0.0D);
        itemRenderer.renderStatic(stacks.get(2), ItemDisplayContext.GROUND, combinedLight, OverlayTexture.NO_OVERLAY, stack,
                buffers, level, seedBase + 2);
    }

    private static Level renderLevel(ItemDisplayBlockEntity tileEntity) {
        Level fromBe = tileEntity.getLevel();
        if (fromBe != null) {
            return fromBe;
        }
        return Minecraft.getInstance().level;
    }
}
