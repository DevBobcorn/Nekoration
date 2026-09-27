package io.devbobcorn.nekoration.client;

import java.awt.Color;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.devbobcorn.nekoration.NekoColors;
import io.devbobcorn.nekoration.items.PaletteItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class PaletteSelectionOutlineRenderer {
    private static final float ALPHA = 0.5F;

    private PaletteSelectionOutlineRenderer() {
    }

    public static boolean render(BlockPos pos, Vec3 cameraPos, PoseStack poseStack,
            MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Level level = minecraft.level;
        if (player == null || level == null) {
            return false;
        }

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof PaletteItem)) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || !level.getWorldBorder().isWithinBounds(pos)) {
            return false;
        }

        Color color = getActiveColor(stack);
        VoxelShape shape = state.getShape(level, pos, CollisionContext.of(player));

        double x = pos.getX() - cameraPos.x;
        double y = pos.getY() - cameraPos.y;
        double z = pos.getZ() - cameraPos.z;

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        drawShapeOutline(poseStack, consumer, shape, x, y, z,
                color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, ALPHA);
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

    private static Color getActiveColor(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CompoundTag tag = data.copyTag();
            if (tag.contains(PaletteItem.ACTIVE)) {
                byte active = tag.getByte(PaletteItem.ACTIVE);
                int[] colors = tag.getIntArray(PaletteItem.COLORS);
                if (active >= 0 && active < colors.length) {
                    return NekoColors.getRGBColor(colors[active]);
                }
            }
        }
        return PaletteItem.DEFAULT_COLOR_SET[0];
    }
}
