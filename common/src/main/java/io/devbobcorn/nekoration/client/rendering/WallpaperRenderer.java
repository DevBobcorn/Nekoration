package io.devbobcorn.nekoration.client.rendering;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.entities.WallpaperEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class WallpaperRenderer extends EntityRenderer<WallpaperEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "wallpaper"), "main");

    private final ModelPart full;
    private final ModelPart upper;
    private final ModelPart lower;

    public WallpaperRenderer(EntityRendererProvider.Context context) {
        super(context);
        ModelPart root = context.bakeLayer(LAYER);
        full = root.getChild("full");
        upper = root.getChild("upper");
        lower = root.getChild("lower");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("upper", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-10.0F, 0.0F, 0.0F, 20.0F, 20.0F, 1.0F), PartPose.offset(0.0F, -10.0F, -0.5F));
        root.addOrReplaceChild("lower", CubeListBuilder.create().texOffs(0, 20)
                .addBox(-10.0F, 0.0F, 0.0F, 20.0F, 20.0F, 1.0F), PartPose.offset(0.0F, -10.0F, -0.5F));
        root.addOrReplaceChild("full", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-10.0F, 0.0F, 0.0F, 20.0F, 40.0F, 1.0F), PartPose.offset(0.0F, -20.0F, -0.5F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void render(WallpaperEntity entity, float yaw, float partialTick, PoseStack poseStack,
            MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(0.8F, 0.8F, 0.8F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        renderPaper(entity.getItem(), paperFor(entity), poseStack, buffers, packedLight, 0);
        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffers, packedLight);
    }

    static void renderPaper(ItemStack stack, ModelPart model, PoseStack poseStack, MultiBufferSource buffers,
            int light, int overlay) {
        BannerRenderer.renderPatterns(poseStack, buffers, light, overlay, model, ModelBakery.BANNER_BASE, true,
                stack.getOrDefault(DataComponents.BASE_COLOR, DyeColor.WHITE),
                stack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY), stack.hasFoil());
    }

    private ModelPart paperFor(WallpaperEntity entity) {
        return switch (entity.getPart()) {
            case FULL -> full;
            case LOWER -> lower;
            case UPPER -> upper;
        };
    }

    @Override
    public ResourceLocation getTextureLocation(WallpaperEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
