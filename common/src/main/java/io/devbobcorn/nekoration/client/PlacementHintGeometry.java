package io.devbobcorn.nekoration.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.devbobcorn.nekoration.blocks.HorizontalBlock;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Shared drawing and back-face checks for strip-like block placement hints.
 */
final class PlacementHintGeometry {
    private static final float RED = 0.3F;
    private static final float GREEN = 0.85F;
    private static final float BLUE = 1.0F;
    private static final float ALPHA = 0.65F;

    /** How far past a strip's back face the neighbor shape must reach to count as covering it. */
    private static final double COVER_DELTA = 1.0 / 128.0;

    private PlacementHintGeometry() {
    }

    static void renderShape(BlockState placed, BlockGetter level, BlockPos pos, Vec3 cameraPos,
            PoseStack poseStack, MultiBufferSource bufferSource) {
        VoxelShape shape = placed.getShape(level, pos);

        double x = pos.getX() - cameraPos.x;
        double y = pos.getY() - cameraPos.y;
        double z = pos.getZ() - cameraPos.z;

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        for (AABB aabb : shape.toAabbs()) {
            LevelRenderer.renderLineBox(poseStack, consumer,
                    x + aabb.minX, y + aabb.minY, z + aabb.minZ,
                    x + aabb.maxX, y + aabb.maxY, z + aabb.maxZ,
                    RED, GREEN, BLUE, ALPHA);
        }
    }

    /**
     * Checks that every strip of the hinted placement has its back face flush against material
     * in the cell behind it.
     */
    static boolean isBackFaceCovered(BlockState placed, BlockGetter level, BlockPos pos) {
        Direction behind = placed.getValue(HorizontalBlock.FACING).getOpposite();
        BlockPos neighborPos = pos.relative(behind);
        BlockState neighborState = level.getBlockState(neighborPos);
        VoxelShape neighborShape = neighborState.getShape(level, neighborPos)
                .move(behind.getStepX(), behind.getStepY(), behind.getStepZ());

        for (AABB aabb : placed.getShape(level, pos).toAabbs()) {
            VoxelShape needed = backFaceSlice(aabb, behind);
            if (!Shapes.join(needed, neighborShape, BooleanOp.ONLY_FIRST).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /** A thin slice just past a strip's back face, where the block behind must have material. */
    private static VoxelShape backFaceSlice(AABB aabb, Direction behind) {
        double minX = aabb.minX;
        double minY = aabb.minY;
        double minZ = aabb.minZ;
        double maxX = aabb.maxX;
        double maxY = aabb.maxY;
        double maxZ = aabb.maxZ;
        switch (behind) {
            case NORTH -> {
                maxZ = minZ;
                minZ -= COVER_DELTA;
            }
            case SOUTH -> {
                minZ = maxZ;
                maxZ += COVER_DELTA;
            }
            case WEST -> {
                maxX = minX;
                minX -= COVER_DELTA;
            }
            case EAST -> {
                minX = maxX;
                maxX += COVER_DELTA;
            }
            default -> {
            }
        }
        return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
