package io.devbobcorn.nekoration.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.devbobcorn.nekoration.blocks.CustomBlock;
import io.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
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
 * Draws a wireframe hint of the entry that a held block item would add to the
 * Custom Block being looked at, but only when the entry's interaction and
 * collision shapes would not overlap the ones already stored. Reports whether
 * a hint was drawn so other placement hints are skipped while it is shown.
 */
public final class CustomBlockPlacementHintRenderer {
    private static final float RED = 0.3F;
    private static final float GREEN = 0.85F;
    private static final float BLUE = 1.0F;
    private static final float ALPHA = 0.65F;

    private CustomBlockPlacementHintRenderer() {
    }

    /**
     * Renders the hint, if applicable, and reports whether it was drawn. Platform
     * listeners call this from their block-highlight render event with the
     * event's data.
     */
    public static boolean render(BlockHitResult hit, Vec3 cameraPos, PoseStack poseStack,
            net.minecraft.client.renderer.MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return false;
        }

        ItemStack stack = player.getMainHandItem();
        InteractionHand hand = InteractionHand.MAIN_HAND;
        if (!isEntryStack(stack)) {
            stack = player.getOffhandItem();
            hand = InteractionHand.OFF_HAND;
            if (!isEntryStack(stack)) {
                return false;
            }
        }

        BlockItem blockItem = (BlockItem) stack.getItem();
        BlockPlaceContext placeContext = new BlockPlaceContext(player, hand, stack, hit);
        // The entry goes to the Custom Block at the placement position when one
        // is there, and to the clicked Custom Block otherwise.
        BlockPos placement = placeContext.getClickedPos();
        boolean atPlacement = minecraft.level.getBlockEntity(placement) instanceof CustomBlockEntity;
        BlockPos pos = atPlacement ? placement : hit.getBlockPos();
        if (!(minecraft.level.getBlockEntity(pos) instanceof CustomBlockEntity customBlock)) {
            return false;
        }
        // Sneaking bypasses the clicked Custom Block and places the held block
        // normally instead; only a Custom Block at the placement position still
        // receives the entry.
        if (!atPlacement && player.isSecondaryUseActive()) {
            return false;
        }

        BlockState displayState = blockItem.getBlock().getStateForPlacement(placeContext);
        if (displayState == null) {
            displayState = blockItem.getBlock().defaultBlockState();
        }

        CollisionContext context = CollisionContext.of(player);
        if (!customBlock.canAddEntry(displayState, minecraft.level, pos, context)) {
            return false;
        }
        VoxelShape shape = displayState.getShape(minecraft.level, pos, context);

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
        return true;
    }

    private static boolean isEntryStack(ItemStack stack) {
        return stack.getItem() instanceof BlockItem item && !(item.getBlock() instanceof CustomBlock);
    }
}
