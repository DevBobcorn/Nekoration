package io.devbobcorn.nekoration.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import io.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import io.devbobcorn.nekoration.client.gui.screen.CustomBlockScreen;
import io.devbobcorn.nekoration.registry.CustomBlockRegistration;
import io.devbobcorn.nekoration.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Renders every display entry of a Custom Block with its own rotation, offset
 * and tint, plus the transform arrow that marks the active entry.
 */
public class CustomBlockEntityRenderer implements BlockEntityRenderer<CustomBlockEntity> {
    private static final double UNIT = 1.0D / 32.0D;
    private static final float OUTLINE_RED = 1.0F;
    private static final float OUTLINE_GREEN = 0.8F;
    private static final float OUTLINE_BLUE = 0.2F;
    private static final float OUTLINE_ALPHA = 1.0F;

    private ItemStack hintStack;
    private ItemStack defaultStack;

    public CustomBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CustomBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
            int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            level = Minecraft.getInstance().level;
        }
        if (level == null) {
            return;
        }
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        RandomSource random = RandomSource.create();
        long seed = blockEntity.getBlockPos().asLong();

        boolean hasEntries = false;
        for (int index = 0; index < CustomBlockEntity.MAX_ENTRIES; index++) {
            CustomBlockEntity.CustomEntry entry = blockEntity.entry(index);
            if (entry != null) {
                hasEntries = true;
                renderEntry(blockEntity, entry, index, level, dispatcher, poseStack, buffer, packedLight,
                        packedOverlay, random, seed);
            }
        }
        if (!hasEntries) {
            if (defaultStack == null) {
                defaultStack = new ItemStack(CustomBlockRegistration.CUSTOM_BLOCK_ITEM.get());
            }
            poseStack.pushPose();
            // ItemRenderer centers item models on the pose origin; undo that to fill the block.
            poseStack.translate(0.5D, 0.5D, 0.5D);
            Minecraft.getInstance().getItemRenderer().renderStatic(defaultStack, ItemDisplayContext.NONE, packedLight,
                    packedOverlay, poseStack, buffer, level, (int) seed);
            poseStack.popPose();
        }
        renderHint(blockEntity, level, poseStack, buffer, packedLight, packedOverlay, seed);
        renderActiveOutline(blockEntity, level, poseStack, buffer);
    }

    @SuppressWarnings("deprecation")
    private static void renderEntry(CustomBlockEntity blockEntity, CustomBlockEntity.CustomEntry entry, int index,
            Level level, BlockRenderDispatcher dispatcher, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay, RandomSource random, long seed) {
        BlockState state = entry.displayState();
        if (state.isAir()) {
            return;
        }
        poseStack.pushPose();
        applyEntryTransform(poseStack, entry);

        BakedModel model = dispatcher.getBlockModel(state);
        // tesselateBlock already applies directional face shading. Entity render
        // layers shade normals again, making the displayed block too dark.
        RenderType renderType = ItemBlockRenderTypes.getChunkRenderType(state);
        var consumer = buffer.getBuffer(renderType);
        int color = entry.tinted() && isInvalidActiveColor(blockEntity, index) ? 0xFFFFFF : entry.color();
        dispatcher.getModelRenderer().tesselateBlock(level, model, state, blockEntity.getBlockPos(),
                poseStack, entry.tinted() ? new ColorTintVertexConsumer(consumer, color, entry.tintAllFaces()) : consumer,
                false, random, seed + index, packedOverlay);
        poseStack.popPose();
    }

    /**
     * True when the entry is the active one of the block whose editor is open
     * with an empty color, so the entry is shown white instead of its previous
     * color while the text is being edited.
     */
    private static boolean isInvalidActiveColor(CustomBlockEntity blockEntity, int index) {
        return Minecraft.getInstance().screen instanceof CustomBlockScreen screen
                && isEditing(blockEntity, screen)
                && index == blockEntity.activeIndex()
                && !screen.hasValidColorInput();
    }

    private static boolean isEditing(CustomBlockEntity blockEntity, CustomBlockScreen screen) {
        return screen.getMenu().getCustomBlock().getBlockPos().equals(blockEntity.getBlockPos());
    }

    /**
     * Draws a yellow wireframe around the active entry while this block's editor
     * screen is open, using the same offset and rotation as the rendered entry.
     */
    private static void renderActiveOutline(CustomBlockEntity blockEntity, Level level, PoseStack poseStack,
            MultiBufferSource buffer) {
        if (!(Minecraft.getInstance().screen instanceof CustomBlockScreen screen) || !isEditing(blockEntity, screen)) {
            return;
        }
        CustomBlockEntity.CustomEntry active = blockEntity.activeEntry();
        if (active == null || active.displayState().isAir()) {
            return;
        }
        poseStack.pushPose();
        applyEntryTransform(poseStack, active);
        VoxelShape shape = active.displayState().getShape(level, blockEntity.getBlockPos());
        VertexConsumer consumer = buffer.getBuffer(RenderType.lines());
        for (AABB aabb : shape.toAabbs()) {
            LevelRenderer.renderLineBox(poseStack, consumer,
                    aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ,
                    OUTLINE_RED, OUTLINE_GREEN, OUTLINE_BLUE, OUTLINE_ALPHA);
        }
        poseStack.popPose();
    }

    private static void applyEntryTransform(PoseStack poseStack, CustomBlockEntity.CustomEntry entry) {
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(entry.dir() * 15.0F));
        poseStack.translate(entry.offset(0) * UNIT, entry.offset(1) * UNIT, entry.offset(2) * UNIT);
        poseStack.translate(-0.5D, 0.0D, -0.5D);
    }

    private void renderHint(CustomBlockEntity blockEntity, Level level, PoseStack poseStack, MultiBufferSource buffer,
            int packedLight, int packedOverlay, long seed) {
        CustomBlockEntity.CustomEntry active = blockEntity.activeEntry();
        if (active == null || !blockEntity.showHint()) {
            return;
        }
        if (hintStack == null) {
            hintStack = new ItemStack(ModItems.ARROW_HINT.get());
        }
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(active.dir() * 15.0F));
        poseStack.translate(active.offset(0) * UNIT, active.offset(1) * UNIT, active.offset(2) * UNIT);
        poseStack.translate(0.0F, 0.5F, -1.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(hintStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer,
                level, (int) seed);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(CustomBlockEntity blockEntity) {
        return true;
    }
}
