package io.devbobcorn.nekoration.client;

import java.awt.Color;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.devbobcorn.nekoration.NekoColors;
import io.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import io.devbobcorn.nekoration.client.gui.screen.CustomBlockScreen;
import io.devbobcorn.nekoration.items.PaletteItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Draws the shape of the entry a player is pointing at, and the shapes of the
 * other entries of the same Custom Block in black. The pointed entry is drawn
 * in the active color of the held palette, or in white when no palette is
 * held. While the editor screen is open all entries are drawn in black, except
 * the active entry when the yellow outline drawn by the block entity renderer
 * covers its black outline.
 */
public final class CustomBlockSelectionOutlineRenderer {
    private static final float WHITE_ALPHA = 1.0F;
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

        // While the editor is open the active entry is marked by the yellow
        // outline in the block entity renderer instead of a black outline.
        boolean editing = minecraft.screen instanceof CustomBlockScreen screen
                && screen.getMenu().getCustomBlock().getBlockPos().equals(pos);
        int pointed = editing ? -1 : customBlock.pointedEntry(level, pos, player);
        int active = editing ? customBlock.activeIndex() : -1;
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
            if (entry == null) {
                continue;
            }
            VoxelShape shape = entry.displayState().getShape(level, pos, context);
            if (index == active && outlineCoveredByYellow(entry, shape)) {
                continue;
            }
            drawShapeOutline(poseStack, consumer, shape, x, y, z, 0.0F, 0.0F, 0.0F, BLACK_ALPHA);
        }
        CustomBlockEntity.CustomEntry pointedEntry = customBlock.entry(pointed);
        if (pointedEntry != null) {
            Color paletteColor = heldPaletteColor(player);
            float red = paletteColor == null ? 1.0F : paletteColor.getRed() / 255.0F;
            float green = paletteColor == null ? 1.0F : paletteColor.getGreen() / 255.0F;
            float blue = paletteColor == null ? 1.0F : paletteColor.getBlue() / 255.0F;
            drawShapeOutline(poseStack, consumer, pointedEntry.displayState().getShape(level, pos, context), x, y, z,
                    red, green, blue, WHITE_ALPHA);
        }
        return true;
    }

    /** The active color of the held palette, or null when neither hand holds one. */
    private static Color heldPaletteColor(LocalPlayer player) {
        for (ItemStack stack : new ItemStack[] { player.getMainHandItem(), player.getOffhandItem() }) {
            if (stack.getItem() instanceof PaletteItem) {
                return NekoColors.getRGBColor(PaletteItem.getActiveColor(stack));
            }
        }
        return null;
    }

    /**
     * True when the entry's transform maps its outline onto itself, i.e. the
     * entry is not offset and its Y rotation is a right angle that leaves every
     * AABB of the shape in place. In that case the black outline is exactly
     * covered by the yellow active-entry outline of the block entity renderer.
     */
    private static boolean outlineCoveredByYellow(CustomBlockEntity.CustomEntry entry, VoxelShape shape) {
        if (entry.offset(0) != 0 || entry.offset(1) != 0 || entry.offset(2) != 0) {
            return false;
        }
        if (entry.dir() % 6 != 0) {
            return false;
        }
        int quarterTurns = entry.dir() / 6 % 4;
        if (quarterTurns == 0) {
            return true;
        }
        List<AABB> boxes = shape.toAabbs();
        boolean[] matched = new boolean[boxes.size()];
        for (AABB box : boxes) {
            AABB rotated = rotateQuarterTurns(box, quarterTurns);
            boolean found = false;
            for (int index = 0; index < boxes.size(); index++) {
                if (!matched[index] && sameBox(boxes.get(index), rotated)) {
                    matched[index] = true;
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    /** Rotates an AABB around the vertical center line of the block (x=z=0.5). */
    private static AABB rotateQuarterTurns(AABB box, int quarterTurns) {
        double minX = box.minX;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxZ = box.maxZ;
        for (int turn = 0; turn < quarterTurns; turn++) {
            double nextMinX = minZ;
            double nextMinZ = 1.0D - maxX;
            double nextMaxX = maxZ;
            double nextMaxZ = 1.0D - minX;
            minX = nextMinX;
            minZ = nextMinZ;
            maxX = nextMaxX;
            maxZ = nextMaxZ;
        }
        return new AABB(minX, box.minY, minZ, maxX, box.maxY, maxZ);
    }

    private static boolean sameBox(AABB first, AABB second) {
        return Math.abs(first.minX - second.minX) < 1.0E-6D
                && Math.abs(first.minY - second.minY) < 1.0E-6D
                && Math.abs(first.minZ - second.minZ) < 1.0E-6D
                && Math.abs(first.maxX - second.maxX) < 1.0E-6D
                && Math.abs(first.maxY - second.maxY) < 1.0E-6D
                && Math.abs(first.maxZ - second.maxZ) < 1.0E-6D;
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
