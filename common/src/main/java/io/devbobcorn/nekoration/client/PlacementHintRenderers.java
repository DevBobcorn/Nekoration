package io.devbobcorn.nekoration.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Picks the placement hint to draw for the surface the player is looking at.
 * The Custom Block entry hint takes priority; when the held block cannot be
 * stored in a Custom Block there, the hints of that block's own placement
 * rules (awning, frame side) are checked instead.
 */
public final class PlacementHintRenderers {
    private PlacementHintRenderers() {
    }

    public static void render(BlockHitResult hit, Vec3 cameraPos, PoseStack poseStack, MultiBufferSource bufferSource) {
        if (CustomBlockPlacementHintRenderer.render(hit, cameraPos, poseStack, bufferSource)) {
            return;
        }
        AwningPlacementHintRenderer.render(hit, cameraPos, poseStack, bufferSource);
        FrameSidePlacementHintRenderer.render(hit, cameraPos, poseStack, bufferSource);
    }
}
