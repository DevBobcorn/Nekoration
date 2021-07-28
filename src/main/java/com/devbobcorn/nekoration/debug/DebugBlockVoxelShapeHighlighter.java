package com.devbobcorn.nekoration.debug;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.exp.ExpNBTTypes;
import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.items.PaletteItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.lang.reflect.Field;

/**
 * Orginally Created by TGG on 27/06/2019, Adapted by DevBobcorn on 27/07/2021.
 */
public class DebugBlockVoxelShapeHighlighter {
	public static final String Param1 = "showshape";
	public static final String Param2 = "showrendershape";
	public static final String Param3 = "showcollisionshape";
	public static final String Param4 = "showraytraceshape";


	@SubscribeEvent
	public static void onDrawBlockHighlightEvent(DrawHighlightEvent.HighlightBlock event) {
		RayTraceResult rayTraceResult = event.getTarget();
		if (rayTraceResult.getType() != RayTraceResult.Type.BLOCK)
			return;
		World world;

		try {
			world = getPrivateWorldFromWorldRenderer(event.getContext());
		} catch (IllegalAccessException | ObfuscationReflectionHelper.UnableToFindFieldException e) {
			if (!loggedReflectionError)
				LOGGER.error("Could not find WorldRenderer.world");
			loggedReflectionError = true;
			return;
		}

		BlockPos blockpos = ((BlockRayTraceResult) rayTraceResult).getBlockPos();
		BlockState blockstate = world.getBlockState(blockpos);
		if (blockstate.isAir(world, blockpos) || !world.getWorldBorder().isWithinBounds(blockpos))
			return;

		ItemStack itemStack = Minecraft.getInstance().player.getMainHandItem();
		// Palette Color Preview...
		if (itemStack.getItem() == ModItems.PALETTE.get()){
			// Render Frame in that color...
			CompoundNBT nbt = itemStack.getTag();

            if (nbt != null && nbt.contains(PaletteItem.ACTIVE, ExpNBTTypes.BYTE_NBT_ID)){
                byte a = nbt.getByte(PaletteItem.ACTIVE);
                int[] c = nbt.getIntArray(PaletteItem.COLORS);
				
				ActiveRenderInfo ari = event.getInfo();
				VoxelShape shape = blockstate.getShape(world, blockpos, ISelectionContext.of(ari.getEntity()));
				drawSelectionBox(event.getContext(), event.getBuffers(), event.getMatrix(), blockpos, ari, shape, NekoColors.getRGBColor(c[a]));
				return;
            };
		}

		boolean showshape = DebugSettings.getDebugParameter(Param1).isPresent() || DebugSettings.getDebugParameterVec3d(Param1).isPresent();
		boolean showrendershape = DebugSettings.getDebugParameter(Param2).isPresent() || DebugSettings.getDebugParameterVec3d(Param2).isPresent();
		boolean showcollisionshape = DebugSettings.getDebugParameter(Param3).isPresent() || DebugSettings.getDebugParameterVec3d(Param3).isPresent();
		boolean showraytraceshape = DebugSettings.getDebugParameter(Param4).isPresent() || DebugSettings.getDebugParameterVec3d(Param4).isPresent();

		if (!(showshape || showrendershape || showcollisionshape || showraytraceshape))
			return;

		ActiveRenderInfo activeRenderInfo = event.getInfo();
		ISelectionContext iSelectionContext = ISelectionContext.of(activeRenderInfo.getEntity());
		IRenderTypeBuffer renderTypeBuffers = event.getBuffers();
		MatrixStack matrixStack = event.getMatrix();
		// Use the param values as colors, instead of just hard-coding it...
		final Color SHAPE_COLOR; // = Color.RED;
		final Color RENDERSHAPE_COLOR; // = Color.BLUE;
		final Color COLLISIONSHAPE_COLOR; // = Color.GREEN;
		final Color RAYTRACESHAPE_COLOR; // = Color.MAGENTA;

		if (showshape) {
			if (DebugSettings.getDebugParameterVec3d(Param1).isPresent())
				SHAPE_COLOR = NekoColors.getRGBColor(DebugSettings.getDebugParameterVec3d(Param1).get());
			else SHAPE_COLOR = NekoColors.getRGBColor(DebugSettings.getDebugParameter(Param1).get());
			VoxelShape shape = blockstate.getShape(world, blockpos, iSelectionContext);
			drawSelectionBox(event.getContext(), renderTypeBuffers, matrixStack, blockpos, activeRenderInfo, shape, SHAPE_COLOR);
		}
		if (showrendershape) {
			if (DebugSettings.getDebugParameterVec3d(Param2).isPresent())
				RENDERSHAPE_COLOR = NekoColors.getRGBColor(DebugSettings.getDebugParameterVec3d(Param2).get());
			else RENDERSHAPE_COLOR = NekoColors.getRGBColor(DebugSettings.getDebugParameter(Param2).get());
			VoxelShape shape = blockstate.getShape(world, blockpos);
			drawSelectionBox(event.getContext(), renderTypeBuffers, matrixStack, blockpos, activeRenderInfo, shape, RENDERSHAPE_COLOR);
		}
		if (showcollisionshape) {
			if (DebugSettings.getDebugParameterVec3d(Param3).isPresent())
				COLLISIONSHAPE_COLOR = NekoColors.getRGBColor(DebugSettings.getDebugParameterVec3d(Param3).get());
			else COLLISIONSHAPE_COLOR = NekoColors.getRGBColor(DebugSettings.getDebugParameter(Param3).get());
			VoxelShape shape = blockstate.getCollisionShape(world, blockpos, iSelectionContext);
			drawSelectionBox(event.getContext(), renderTypeBuffers, matrixStack, blockpos, activeRenderInfo, shape, COLLISIONSHAPE_COLOR);
		}
		if (showraytraceshape) {
			if (DebugSettings.getDebugParameterVec3d(Param4).isPresent())
				RAYTRACESHAPE_COLOR = NekoColors.getRGBColor(DebugSettings.getDebugParameterVec3d(Param4).get());
			else RAYTRACESHAPE_COLOR = NekoColors.getRGBColor(DebugSettings.getDebugParameter(Param4).get());
			VoxelShape shape = blockstate.getVisualShape(world, blockpos, iSelectionContext);
			drawSelectionBox(event.getContext(), renderTypeBuffers, matrixStack, blockpos, activeRenderInfo, shape, RAYTRACESHAPE_COLOR);
		}
		event.setCanceled(true);
	}

	// The world field is private so we need a trick to get access to it
	// we need to use the srg name for it to work robustly:
	// see here: https://mcp.thiakil.com/#/search
	// and here: https://jamieswhiteshirt.github.io/resources/know-your-tools/
	private static World getPrivateWorldFromWorldRenderer(WorldRenderer worldRenderer)
			throws IllegalAccessException, ObfuscationReflectionHelper.UnableToFindFieldException {
		if (worldField == null) {
			worldField = ObfuscationReflectionHelper.findField(WorldRenderer.class, "field_72769_h");
		}
		return (World) worldField.get(worldRenderer);
	}

	private static Field worldField;
	private static boolean loggedReflectionError = false;

	/**
	 * copied from WorldRenderer; starting from the code marked with
	 * iprofiler.endStartSection("outline");
	 *
	 * @param activeRenderInfo
	 */
	private static void drawSelectionBox(WorldRenderer worldRenderer, IRenderTypeBuffer renderTypeBuffers,
			MatrixStack matrixStack, BlockPos blockPos, ActiveRenderInfo activeRenderInfo, VoxelShape shape,
			Color color) {
		RenderType renderType = RenderType.lines();
		IVertexBuilder vertexBuilder = renderTypeBuffers.getBuffer(renderType);

		double eyeX = activeRenderInfo.getPosition().x();
		double eyeY = activeRenderInfo.getPosition().y();
		double eyeZ = activeRenderInfo.getPosition().z();
		final float ALPHA = 0.5f;
		drawShapeOutline(matrixStack, vertexBuilder, shape, blockPos.getX() - eyeX, blockPos.getY() - eyeY,
				blockPos.getZ() - eyeZ, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, ALPHA);

	}

	private static void drawShapeOutline(MatrixStack matrixStack, IVertexBuilder vertexBuilder, VoxelShape voxelShape,
			double originX, double originY, double originZ, float red, float green, float blue, float alpha) {

		Matrix4f matrix4f = matrixStack.last().pose();
		voxelShape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
			vertexBuilder.vertex(matrix4f, (float) (x0 + originX), (float) (y0 + originY), (float) (z0 + originZ))
					.color(red, green, blue, alpha).endVertex();
			vertexBuilder.vertex(matrix4f, (float) (x1 + originX), (float) (y1 + originY), (float) (z1 + originZ))
					.color(red, green, blue, alpha).endVertex();
		});
	}

	private static final Logger LOGGER = LogManager.getLogger();
}
