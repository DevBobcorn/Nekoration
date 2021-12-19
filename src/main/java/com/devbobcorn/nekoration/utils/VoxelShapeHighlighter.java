package com.devbobcorn.nekoration.utils;

import java.awt.Color;
import java.lang.reflect.Field;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.items.PaletteItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.api.distmarker.Dist;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class VoxelShapeHighlighter {
    public static final Logger LOGGER = LogManager.getLogger();

	// Used on Client-Side only...
    private static Field worldField;
    private static boolean loggedReflectionError = false;
	private static boolean HoldingPalette = false;
	public static Color PaletteColor = Color.RED;
	private static int lastHolding = 1202;

	@SubscribeEvent
	public static void onDrawBlockHighlightEvent(DrawSelectionEvent.HighlightBlock event) {
		HitResult rayTraceResult = event.getTarget();
		Minecraft mc = Minecraft.getInstance();
		if (rayTraceResult.getType() != HitResult.Type.BLOCK)
			return;
		Level world;

		try {
			world = getPrivateWorldFromWorldRenderer(event.getLevelRenderer());
		} catch (IllegalAccessException | ObfuscationReflectionHelper.UnableToFindFieldException e) {
			if (!loggedReflectionError)
				LOGGER.error("Could not find WorldRenderer.world");
			loggedReflectionError = true;
			return;
		}
		
		BlockPos blockpos = ((BlockHitResult) rayTraceResult).getBlockPos();
		BlockState blockstate = world.getBlockState(blockpos);
		if (blockstate.isAir() || !world.getWorldBorder().isWithinBounds(blockpos))
			return;

		ItemStack itemStack = mc.player.getMainHandItem();
		// Palette Color Preview...
		if (itemStack.getItem() == ModItems.PALETTE.get()){
			int holding = mc.player.getInventory().selected;
			// Render Frame in that color...
			if (!HoldingPalette || lastHolding != holding){ // Update palette color...
				CompoundTag nbt = itemStack.getOrCreateTag();
				if (nbt.contains(PaletteItem.ACTIVE, TagTypes.BYTE_NBT_ID)){
					byte a = nbt.getByte(PaletteItem.ACTIVE);
					int[] c = nbt.getIntArray(PaletteItem.COLORS);
					PaletteColor = NekoColors.getRGBColor(c[a]);
				} else {
					PaletteColor = PaletteItem.DEFAULT_COLOR_SET[0];
				};
				lastHolding = holding;
				HoldingPalette = true;
			}
			Camera ari = event.getCamera();
			VoxelShape shape = blockstate.getShape(world, blockpos, CollisionContext.of(ari.getEntity()));
			drawSelectionBox(event.getLevelRenderer(), event.getMultiBufferSource(), event.getPoseStack(), blockpos, ari, shape, PaletteColor);

            event.setCanceled(true);
		} else if (HoldingPalette) HoldingPalette = false;
	}

	private static void drawSelectionBox(LevelRenderer worldRenderer, MultiBufferSource renderTypeBuffers,
			PoseStack matrixStack, BlockPos blockPos, Camera activeRenderInfo, VoxelShape shape,
			Color color) {
		RenderType renderType = RenderType.lines();
		VertexConsumer vertexBuilder = renderTypeBuffers.getBuffer(renderType);

		double eyeX = activeRenderInfo.getPosition().x();
		double eyeY = activeRenderInfo.getPosition().y();
		double eyeZ = activeRenderInfo.getPosition().z();
		final float ALPHA = 0.5f;
		drawShapeOutline(matrixStack, vertexBuilder, shape, blockPos.getX() - eyeX, blockPos.getY() - eyeY,
				blockPos.getZ() - eyeZ, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, ALPHA);
	}

	private static void drawShapeOutline(PoseStack matrixStack, VertexConsumer vertexBuilder, VoxelShape voxelShape, double originX, double originY, double originZ, float red, float green, float blue, float alpha) {
		PoseStack.Pose matrix4f = matrixStack.last();
		voxelShape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
		   float f = (float)(x1 - x0);
		   float f1 = (float)(y1 - y0);
		   float f2 = (float)(z1 - z0);
		   float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
		   f = f / f3;
		   f1 = f1 / f3;
		   f2 = f2 / f3;
		   vertexBuilder.vertex(matrix4f.pose(), (float)(x0 + originX), (float)(y0 + originY), (float)(z0 + originZ)).color(red, green, blue, alpha).normal(matrix4f.normal(), f, f1, f2).endVertex();
		   vertexBuilder.vertex(matrix4f.pose(), (float)(x1 + originX), (float)(y1 + originY), (float)(z1 + originZ)).color(red, green, blue, alpha).normal(matrix4f.normal(), f, f1, f2).endVertex();
		});
	 }

	// The world field is private so we need a trick to get access to it
	// we need to use the srg name for it to work robustly:
	// see here: https://mcp.thiakil.com/#/search
	// and here: https://jamieswhiteshirt.github.io/resources/know-your-tools/
	private static Level getPrivateWorldFromWorldRenderer(LevelRenderer worldRenderer)
			throws IllegalAccessException, ObfuscationReflectionHelper.UnableToFindFieldException {
		if (worldField == null) {
			worldField = ObfuscationReflectionHelper.findField(LevelRenderer.class, "f_109465_");
		}
		return (Level) worldField.get(worldRenderer);
	}    
}
