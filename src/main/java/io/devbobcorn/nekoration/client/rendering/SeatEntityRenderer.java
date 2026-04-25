package io.devbobcorn.nekoration.client.rendering;

import io.devbobcorn.nekoration.entities.SeatEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Intentionally renders nothing; seat entity is invisible.
 */
public class SeatEntityRenderer extends EntityRenderer<SeatEntity> {
    public SeatEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(SeatEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }

    @Override
    public void render(SeatEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {
        // Invisible seat.
    }
}
