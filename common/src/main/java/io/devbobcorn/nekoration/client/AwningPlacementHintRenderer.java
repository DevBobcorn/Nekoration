package io.devbobcorn.nekoration.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.devbobcorn.nekoration.blocks.AwningBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Draws a wireframe hint of the awning that a held awning item would place right below the
 * position in front of the awning being looked at, visually connecting the two, when pointing at
 * the front side of that awning's bottom step.
 */
public final class AwningPlacementHintRenderer {
    private static final float RED = 0.3F;
    private static final float GREEN = 0.85F;
    private static final float BLUE = 1.0F;
    private static final float ALPHA = 0.65F;

    private AwningPlacementHintRenderer() {
    }

    /**
     * Renders the hint, if applicable. Platform listeners call this from their
     * block-highlight render event with the event's data.
     */
    public static void render(BlockHitResult hit, Vec3 cameraPos, PoseStack poseStack,
            net.minecraft.client.renderer.MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        InteractionHand hand = InteractionHand.MAIN_HAND;
        if (!isAwningStack(stack)) {
            stack = player.getOffhandItem();
            hand = InteractionHand.OFF_HAND;
            if (!isAwningStack(stack)) {
                return;
            }
        }

        AwningBlock block = (AwningBlock) ((BlockItem) stack.getItem()).getBlock();
        BlockPos pos = AwningBlock.getConnectedPlacementPos(minecraft.level, hit.getBlockPos(),
                hit.getDirection(), hit.getLocation());
        if (pos == null) {
            return;
        }

        BlockPlaceContext context = new BlockPlaceContext(player, hand, stack, hit);
        if (!minecraft.level.getBlockState(pos).canBeReplaced(context)) {
            return;
        }
        context = BlockPlaceContext.at(context, pos, hit.getDirection());

        BlockState placed = block.getStateForPlacement(context);
        if (placed == null
                || !placed.canSurvive(minecraft.level, pos)
                || !minecraft.level.isUnobstructed(placed, pos, CollisionContext.of(player))) {
            return;
        }
        VoxelShape shape = placed.getShape(minecraft.level, pos);

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

    private static boolean isAwningStack(ItemStack stack) {
        return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof AwningBlock;
    }
}
