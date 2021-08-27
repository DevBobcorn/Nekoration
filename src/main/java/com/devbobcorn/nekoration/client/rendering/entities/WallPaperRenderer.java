package com.devbobcorn.nekoration.client.rendering.entities;

import java.util.List;

import com.devbobcorn.nekoration.client.event.ClientModEventSubscriber;
import com.devbobcorn.nekoration.entities.WallPaperEntity;
import com.devbobcorn.nekoration.entities.WallPaperEntity.Part;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;

public class WallPaperRenderer extends EntityRenderer<WallPaperEntity> {
    public final ModelPart paperFull;
	public final ModelPart paperUpper;
	public final ModelPart paperLower;

	public WallPaperRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
		ModelPart modelpart = ctx.bakeLayer(ClientModEventSubscriber.WALLPAPER);
    	this.paperFull = modelpart.getChild("full");
    	this.paperUpper = modelpart.getChild("upper");
    	this.paperLower = modelpart.getChild("lower");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("upper", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, 0.0F, 0.0F, 20.0F, 20.0F, 1.0F), PartPose.offset(0.0F, -10.0F, -0.5F));
		partdefinition.addOrReplaceChild("lower", CubeListBuilder.create().texOffs(0, 20).addBox(-10.0F, 0.0F, 0.0F, 20.0F, 20.0F, 1.0F), PartPose.offset(0.0F, -10.0F, -0.5F));
		partdefinition.addOrReplaceChild("full", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, 0.0F, 0.0F, 20.0F, 40.0F, 1.0F), PartPose.offset(0.0F, -20.0F, -0.5F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	public ModelPart getPaper(Part part) {
		switch (part){
			case FULL:
				return paperFull;
			case LOWER:
				return paperLower;
			default:
				return paperUpper;
		}
	}

	public void render(WallPaperEntity entity, float rotation, float partialTicks, PoseStack stack, MultiBufferSource buffers, int packedLight) {
		stack.pushPose();
		stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - rotation));

		renderWallPaper(stack, buffers, entity, entity.getHeight(), packedLight);
		stack.popPose();
		super.render(entity, rotation, partialTicks, stack, buffers, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(WallPaperEntity entity) {
		return null;
	}

    public static List<Pair<BannerPattern, DyeColor>> getBlankPattern(DyeColor base){
        List<Pair<BannerPattern, DyeColor>> list = Lists.newArrayList();
        list.add(Pair.of(BannerPattern.BASE, base));
        return list;
    }

	private void renderWallPaper(PoseStack stack, MultiBufferSource buffers, WallPaperEntity entity, int height, int light) {
        // Then render the wallpaper
		float sc = 0.8F;
		stack.scale(sc, sc, sc);
		stack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));

		BannerRenderer.renderPatterns(stack, buffers, light, 0xFFFFFF, getPaper(entity.getPart()), ModelBakery.BANNER_BASE, true, (entity.getPatterns() == null) ? getBlankPattern(entity.getBaseColor()) : entity.getPatterns());
	}
}
