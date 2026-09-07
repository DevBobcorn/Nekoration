package io.devbobcorn.nekoration.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class WallpaperItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final ModelPart paper;

    public WallpaperItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
        paper = modelSet.bakeLayer(WallpaperRenderer.LAYER).getChild("full");
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
            MultiBufferSource buffers, int light, int overlay) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        WallpaperRenderer.renderPaper(stack, paper, poseStack, buffers, light, overlay);
        poseStack.popPose();
    }
}
