package io.devbobcorn.nekoration.client;

import com.mojang.blaze3d.vertex.PoseStack;

import io.devbobcorn.nekoration.blocks.states.FrameConnection;
import io.devbobcorn.nekoration.blocks.stone.FrameSideBlock;
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
 * Draws a wireframe hint of the frame side strip that a held frame side item would place,
 * but only when the strip's back face would be covered by the block behind it.
 */
public final class FrameSidePlacementHintRenderer {
    private FrameSidePlacementHintRenderer() {
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
        if (!isFrameSideStack(stack)) {
            stack = player.getOffhandItem();
            hand = InteractionHand.OFF_HAND;
            if (!isFrameSideStack(stack)) {
                return;
            }
        }

        FrameSideBlock block = (FrameSideBlock) ((BlockItem) stack.getItem()).getBlock();
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

        if (!PlacementHintGeometry.isBackFaceCovered(hinted, minecraft.level, pos)) {
            return;
        }
        PlacementHintGeometry.renderShape(hinted, minecraft.level, pos, cameraPos, poseStack, bufferSource);
    }

    private static boolean isFrameSideStack(ItemStack stack) {
        return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof FrameSideBlock;
    }
}
