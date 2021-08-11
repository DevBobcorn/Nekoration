package com.devbobcorn.nekoration.client.rendering;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

public class QuadRenderer {
    public static final ResourceLocation TEXTURE = new ResourceLocation("nekoration:textures/block/custom.png");

	public static void renderCubeUsingQuads(BlockEntity tileEntity, float partialTicks,
			PoseStack stack, MultiBufferSource buffers, int combinedLight, int combinedOverlay) {
		// draw the object as a cube, using quads
		// When render method is called, the origin [0,0,0] is at the current [x,y,z] of
		// the block.
		Color color = Color.WHITE;
		drawCubeQuads(stack, buffers, color, combinedLight);
	}

	/**
	 * Draw a cube from [0,0,0] to [1,1,1], same texture on all sides, using a
	 * supplied texture
	 */
	private static void drawCubeQuads(PoseStack stack, MultiBufferSource buffers, Color color,
			int combinedLight) {
		VertexConsumer vertexBuilderBlockQuads = buffers
				.getBuffer(RenderType.entitySolid(TEXTURE));
		// other typical RenderTypes used by TER are:
		// getEntityCutout, getBeaconBeam (which has translucency),

		Matrix4f matrixPos = stack.last().pose(); // retrieves the current transformation matrix
		Matrix3f matrixNormal = stack.last().normal(); // retrieves the current transformation matrix for the normal vector

		// we use the whole texture
		Vec2 bottomLeftUV = new Vec2(0.0F, 1.0F);
		float UVwidth = 1.0F;
		float UVheight = 1.0F;

		// all faces have the same height and width
		final float WIDTH = 1.0F;
		final float HEIGHT = 1.0F;

		final Vector3d EAST_FACE_MIDPOINT = new Vector3d(1.0, 0.5, 0.5);
		final Vector3d WEST_FACE_MIDPOINT = new Vector3d(0.0, 0.5, 0.5);
		final Vector3d NORTH_FACE_MIDPOINT = new Vector3d(0.5, 0.5, 0.0);
		final Vector3d SOUTH_FACE_MIDPOINT = new Vector3d(0.5, 0.5, 1.0);
		final Vector3d UP_FACE_MIDPOINT = new Vector3d(0.5, 1.0, 0.5);
		final Vector3d DOWN_FACE_MIDPOINT = new Vector3d(0.5, 0.0, 0.5);

		addFace(Direction.EAST, matrixPos, matrixNormal, vertexBuilderBlockQuads, color, EAST_FACE_MIDPOINT, WIDTH,
				HEIGHT, bottomLeftUV, UVwidth, UVheight, combinedLight);
		addFace(Direction.WEST, matrixPos, matrixNormal, vertexBuilderBlockQuads, color, WEST_FACE_MIDPOINT, WIDTH,
				HEIGHT, bottomLeftUV, UVwidth, UVheight, combinedLight);
		addFace(Direction.NORTH, matrixPos, matrixNormal, vertexBuilderBlockQuads, color, NORTH_FACE_MIDPOINT, WIDTH,
				HEIGHT, bottomLeftUV, UVwidth, UVheight, combinedLight);
		addFace(Direction.SOUTH, matrixPos, matrixNormal, vertexBuilderBlockQuads, color, SOUTH_FACE_MIDPOINT, WIDTH,
				HEIGHT, bottomLeftUV, UVwidth, UVheight, combinedLight);
		addFace(Direction.UP, matrixPos, matrixNormal, vertexBuilderBlockQuads, color, UP_FACE_MIDPOINT, WIDTH, HEIGHT,
				bottomLeftUV, UVwidth, UVheight, combinedLight);
		addFace(Direction.DOWN, matrixPos, matrixNormal, vertexBuilderBlockQuads, color, DOWN_FACE_MIDPOINT, WIDTH,
				HEIGHT, bottomLeftUV, UVwidth, UVheight, combinedLight);
	}

	private static void addFace(Direction whichFace, Matrix4f matrixPos, Matrix3f matrixNormal,
			VertexConsumer buffers, Color color, Vector3d centrePos, float width, float height,
			Vec2 bottomLeftUV, float texUwidth, float texVheight, int lightmapValue) {
		// the Direction class has a bunch of methods which can help you rotate quads
		// I've written the calculations out long hand, and based them on a centre
		// position, to make it clearer what
		// is going on.
		// Beware that the Direction class is based on which direction the face is
		// pointing, which is opposite to
		// the direction that the viewer is facing when looking at the face.
		// Eg when drawing the NORTH face, the face points north, but when we're looking
		// at the face, we are facing south,
		// so that the bottom left corner is the eastern-most, not the western-most!

		// calculate the bottom left, bottom right, top right, top left vertices from
		// the VIEWER's point of view (not the
		// face's point of view)

		Vec3 leftToRightDirection, bottomToTopDirection;

		switch (whichFace) {
		case NORTH: { // bottom left is east
			leftToRightDirection = new Vec3(-1, 0, 0); // or alternatively Vec3.XN
			bottomToTopDirection = new Vec3(0, 1, 0); // or alternatively Vec3.YP
			break;
		}
		case SOUTH: { // bottom left is west
			leftToRightDirection = new Vec3(1, 0, 0);
			bottomToTopDirection = new Vec3(0, 1, 0);
			break;
		}
		case EAST: { // bottom left is south
			leftToRightDirection = new Vec3(0, 0, -1);
			bottomToTopDirection = new Vec3(0, 1, 0);
			break;
		}
		case WEST: { // bottom left is north
			leftToRightDirection = new Vec3(0, 0, 1);
			bottomToTopDirection = new Vec3(0, 1, 0);
			break;
		}
		case UP: { // bottom left is southwest by minecraft block convention
			leftToRightDirection = new Vec3(-1, 0, 0);
			bottomToTopDirection = new Vec3(0, 0, 1);
			break;
		}
		case DOWN: { // bottom left is northwest by minecraft block convention
			leftToRightDirection = new Vec3(1, 0, 0);
			bottomToTopDirection = new Vec3(0, 0, 1);
			break;
		}
		default: { // should never get here, but just in case;
			leftToRightDirection = new Vec3(0, 0, 1);
			bottomToTopDirection = new Vec3(0, 1, 0);
			break;
		}
		}
		leftToRightDirection.scale(0.5F * width); // convert to half width
		bottomToTopDirection.scale(0.5F * height); // convert to half height

		// calculate the four vertices based on the centre of the face

		Vec3 bottomLeftPos = new Vec3(centrePos.x, centrePos.y, centrePos.z);
		bottomLeftPos.subtract(leftToRightDirection);
		bottomLeftPos.subtract(bottomToTopDirection);

		Vec3 bottomRightPos = new Vec3(centrePos.x, centrePos.y, centrePos.z);
		bottomRightPos.add(leftToRightDirection);
		bottomRightPos.subtract(bottomToTopDirection);

		Vec3 topRightPos = new Vec3(centrePos.x, centrePos.y, centrePos.z);
		topRightPos.add(leftToRightDirection);
		topRightPos.add(bottomToTopDirection);

		Vec3 topLeftPos = new Vec3(centrePos.x, centrePos.y, centrePos.z);
		topLeftPos.subtract(leftToRightDirection);
		topLeftPos.add(bottomToTopDirection);

		// texture coordinates are "upside down" relative to the face
		// eg bottom left = [U min, V max]
		Vec2 bottomLeftUVpos = new Vec2(bottomLeftUV.x, bottomLeftUV.y);
		Vec2 bottomRightUVpos = new Vec2(bottomLeftUV.x + texUwidth, bottomLeftUV.y);
		Vec2 topLeftUVpos = new Vec2(bottomLeftUV.x + texUwidth, bottomLeftUV.y + texVheight);
		Vec2 topRightUVpos = new Vec2(bottomLeftUV.x, bottomLeftUV.y + texVheight);

		Vector3f normalVector = whichFace.step(); // gives us the normal to the face

		addQuad(matrixPos, matrixNormal, buffers, bottomLeftPos, bottomRightPos, topRightPos, topLeftPos,
				bottomLeftUVpos, bottomRightUVpos, topLeftUVpos, topRightUVpos, normalVector, color, lightmapValue);
	}

	/**
	 * Add a quad. The vertices are added in anti-clockwise order from the VIEWER's
	 * point of view, i.e. bottom left; bottom right, top right, top left If you add
	 * the vertices in clockwise order, the quad will face in the opposite
	 * direction; i.e. the viewer will be looking at the back face, which is usually
	 * culled (not visible) See
	 * http://greyminecraftcoder.blogspot.com/2014/12/the-tessellator-and-worldrenderer-18.html
	 * http://greyminecraftcoder.blogspot.com/2014/12/block-models-texturing-quads-faces.html
	 */
	private static void addQuad(Matrix4f matrixPos, Matrix3f matrixNormal, VertexConsumer buffers, Vec3 blpos,
			Vec3 brpos, Vec3 trpos, Vec3 tlpos, Vec2 blUVpos, Vec2 brUVpos, Vec2 trUVpos,
			Vec2 tlUVpos, Vector3f normalVector, Color color, int lightmapValue) {
		addQuadVertex(matrixPos, matrixNormal, buffers, blpos, blUVpos, normalVector, color, lightmapValue);
		addQuadVertex(matrixPos, matrixNormal, buffers, brpos, brUVpos, normalVector, color, lightmapValue);
		addQuadVertex(matrixPos, matrixNormal, buffers, trpos, trUVpos, normalVector, color, lightmapValue);
		addQuadVertex(matrixPos, matrixNormal, buffers, tlpos, tlUVpos, normalVector, color, lightmapValue);
	}

	// suitable for vertexbuilders using the DefaultVertexFormats.ENTITY format
	private static void addQuadVertex(Matrix4f matrixPos, Matrix3f matrixNormal, VertexConsumer buffers,
			Vec3 pos, Vec2 texUV, Vector3f normalVector, Color color, int lightmapValue) {
		buffers.vertex(pos.x(), pos.y(), pos.z()) // position coordinate
				.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()) // color
				.uv(texUV.x, texUV.y) // texel coordinate
				.overlayCoords(OverlayTexture.NO_OVERLAY) // only relevant for rendering Entities (Living)
				.uv2(lightmapValue) // lightmap with full brightness
				.normal(normalVector.x(), normalVector.y(), normalVector.z()).endVertex();
	}
}
