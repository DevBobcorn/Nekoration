package io.devbobcorn.nekoration.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import io.devbobcorn.nekoration.client.gui.screen.CustomBlockScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Draws the shape of the entry a player is pointing at in white, and the
 * shapes of the other entries of the same Custom Block in black. While the
 * editor screen is open all entries are drawn in black.
 */
public final class CustomBlockSelectionOutlineRenderer {
    private static final float WHITE_ALPHA = 0.5F;
    private static final float BLACK_ALPHA = 0.4F;

    private CustomBlockSelectionOutlineRenderer() {
    }

    public static boolean render(BlockPos pos, Vec3 cameraPos, PoseStack poseStack, MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Level level = minecraft.level;
        if (player == null || level == null) {
            return false;
        }
        if (!(level.getBlockEntity(pos) instanceof CustomBlockEntity customBlock) || customBlock.entryCount() == 0) {
            return false;
        }

        // While the editor is open every entry is drawn alike; the selected one
        // is marked by the yellow outline in the block entity renderer instead.
        boolean editing = minecraft.screen instanceof CustomBlockScreen screen
                && screen.getMenu().getCustomBlock().getBlockPos().equals(pos);
        int pointed = editing ? -1 : customBlock.pointedEntry(level, pos, player);
        double x = pos.getX() - cameraPos.x;
        double y = pos.getY() - cameraPos.y;
        double z = pos.getZ() - cameraPos.z;

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        CollisionContext context = CollisionContext.of(player);
        for (int index = 0; index < CustomBlockEntity.MAX_ENTRIES; index++) {
            if (index == pointed) {
                continue;
            }
            CustomBlockEntity.CustomEntry entry = customBlock.entry(index);
            if (entry != null) {
                drawShapeOutline(poseStack, consumer, entry.displayState().getShape(level, pos, context), x, y, z,
                        0.0F, 0.0F, 0.0F, BLACK_ALPHA);
            }
        }
        CustomBlockEntity.CustomEntry pointedEntry = customBlock.entry(pointed);
        if (pointedEntry != null) {
            drawShapeOutline(poseStack, consumer, pointedEntry.displayState().getShape(level, pos, context), x, y, z,
                    1.0F, 1.0F, 1.0F, WHITE_ALPHA);
        }
        return true;
    }

    private static void drawShapeOutline(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape,
            double originX, double originY, double originZ, float red, float green, float blue, float alpha) {
        PoseStack.Pose pose = poseStack.last();
        shape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
            float dx = (float) (x1 - x0);
            float dy = (float) (y1 - y0);
            float dz = (float) (z1 - z0);
            float length = Mth.sqrt(dx * dx + dy * dy + dz * dz);
            dx /= length;
            dy /= length;
            dz /= length;
            consumer.addVertex(pose, (float) (x0 + originX), (float) (y0 + originY), (float) (z0 + originZ))
                    .setColor(red, green, blue, alpha).setNormal(pose, dx, dy, dz);
            consumer.addVertex(pose, (float) (x1 + originX), (float) (y1 + originY), (float) (z1 + originZ))
                    .setColor(red, green, blue, alpha).setNormal(pose, dx, dy, dz);
        });
    }
}
