package io.devbobcorn.nekoration.client;

import com.mojang.blaze3d.vertex.PoseStack;

import io.devbobcorn.nekoration.blocks.HorizontalConnectedBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

/**
 * Draws a wireframe hint of the frame head or sill strip that a held item would place,
 * but only when the strip's back face would be covered by the block behind it.
 */
public final class FrameHeadSillPlacementHintRenderer {
    private FrameHeadSillPlacementHintRenderer() {
    }

    public static void render(BlockHitResult hit, Vec3 cameraPos, PoseStack poseStack,
            net.minecraft.client.renderer.MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        InteractionHand hand = InteractionHand.MAIN_HAND;
        if (!isFrameStack(stack)) {
            stack = player.getOffhandItem();
            hand = InteractionHand.OFF_HAND;
            if (!isFrameStack(stack)) {
                return;
            }
        }

        HorizontalConnectedBlock block = (HorizontalConnectedBlock) ((BlockItem) stack.getItem()).getBlock();
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

        if (!PlacementHintGeometry.isBackFaceCovered(placed, minecraft.level, pos)) {
            return;
        }
        PlacementHintGeometry.renderShape(placed, minecraft.level, pos, cameraPos, poseStack, bufferSource);
    }

    private static boolean isFrameStack(ItemStack stack) {
        return stack.getItem() instanceof BlockItem item
                && item.getBlock() instanceof HorizontalConnectedBlock block
                && block.hasFrameConnection();
    }
}
