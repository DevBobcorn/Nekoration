package com.devbobcorn.nekoration.client.rendering.items;

import java.util.List;

import com.devbobcorn.nekoration.client.event.ClientModEventSubscriber;
import com.devbobcorn.nekoration.client.rendering.entities.WallPaperRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;


public class WallPaperItemRenderer extends BlockEntityWithoutLevelRenderer {
    protected EntityModelSet modelSet;
    public final ModelPart paperFull;

    public WallPaperItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet set) {
        super(dispatcher, set);
        this.modelSet = set;
        this.paperFull = modelSet.bakeLayer(ClientModEventSubscriber.WALLPAPER).getChild("full");
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        boolean flag = stack.getTagElement("BlockEntityTag") != null;
        matrixStack.pushPose();
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));

        List<Pair<BannerPattern, DyeColor>> list = flag ? BannerBlockEntity.createPatterns(ShieldItem.getColor(stack), BannerBlockEntity.getItemPatterns(stack)) : WallPaperRenderer.getBlankPattern(DyeColor.WHITE);
        BannerRenderer.renderPatterns(matrixStack, buffer, combinedLight, combinedOverlay, paperFull, ModelBakery.BANNER_BASE, true, list, false);

        matrixStack.popPose();
    }
}
