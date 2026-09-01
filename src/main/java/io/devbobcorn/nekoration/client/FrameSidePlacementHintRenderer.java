package io.devbobcorn.nekoration.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.states.FrameConnection;
import io.devbobcorn.nekoration.blocks.stone.FrameSideBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

/**
 * Draws a wireframe hint of the frame side strip that a held frame side item would place,
 * but only when the strip's back face would be covered by the block behind it.
 */
@EventBusSubscriber(modid = Nekoration.MODID, value = Dist.CLIENT)
public final class FrameSidePlacementHintRenderer {
    private static final float RED = 0.3F;
    private static final float GREEN = 0.85F;
    private static final float BLUE = 1.0F;
    private static final float ALPHA = 0.65F;

    /** How far past the strip's back face the neighbor shape must reach to count as covering it. */
    private static final double COVER_DELTA = 1.0 / 128.0;

    private FrameSidePlacementHintRenderer() {
    }

    @SubscribeEvent
    public static void onRenderBlockHighlight(RenderHighlightEvent.Block event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        InteractionHand hand = InteractionHand.MAIN_HAND;
        if (!isFrameSideStack(stack)) {
            stack = player.getOffhandItem();
            hand = InteractionHand.OFF_HAND;
            if (!isFrameSideStack(stack)) {
                return;
            }
        }

        FrameSideBlock block = (FrameSideBlock) ((BlockItem) stack.getItem()).getBlock();
        BlockHitResult hit = event.getTarget();
        BlockPlaceContext context = new BlockPlaceContext(player, hand, stack, hit);
        BlockState placed = block.getStateForPlacement(context);
        if (placed == null) {
            return;
        }

        BlockPos pos = context.getClickedPos();
        if (!context.canPlace()
                || !placed.canSurvive(minecraft.level, pos)
                || !minecraft.level.isUnobstructed(placed, pos, CollisionContext.of(player))) {
            return;
        }

        BlockState hinted = placed;
        BlockState existing = minecraft.level.getBlockState(pos);
        if (existing.getBlock() == block && existing.getValue(FrameSideBlock.CONNECTION) != FrameConnection.BOTH) {
            hinted = placed.setValue(FrameSideBlock.CONNECTION,
                    existing.getValue(FrameSideBlock.CONNECTION) == FrameConnection.LEFT
                            ? FrameConnection.RIGHT
                            : FrameConnection.LEFT);
        }

        if (!isBackFaceCovered(hinted, minecraft.level, pos)) {
            return;
        }
        VoxelShape shape = hinted.getShape(minecraft.level, pos);

        Vec3 cameraPos = event.getCamera().getPosition();
        double x = pos.getX() - cameraPos.x;
        double y = pos.getY() - cameraPos.y;
        double z = pos.getZ() - cameraPos.z;

        PoseStack poseStack = event.getPoseStack();
        VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.lines());
        for (AABB aabb : shape.toAabbs()) {
            LevelRenderer.renderLineBox(poseStack, consumer,
                    x + aabb.minX, y + aabb.minY, z + aabb.minZ,
                    x + aabb.maxX, y + aabb.maxY, z + aabb.maxZ,
                    RED, GREEN, BLUE, ALPHA);
        }
    }

    private static boolean isFrameSideStack(ItemStack stack) {
        return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof FrameSideBlock;
    }

    /**
     * Checks that every strip of the hinted placement has its back face flush against material
     * in the cell behind it.
     */
    private static boolean isBackFaceCovered(BlockState placed, BlockGetter level, BlockPos pos) {
        Direction behind = placed.getValue(FrameSideBlock.FACING).getOpposite();
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
