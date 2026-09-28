package io.devbobcorn.nekoration.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import io.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import io.devbobcorn.nekoration.registry.CustomBlockRegistration;
import io.devbobcorn.nekoration.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
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

/**
 * Renders every display entry of a Custom Block with its own rotation, offset
 * and tint, plus the transform arrow that marks the active entry.
 */
public class CustomBlockEntityRenderer implements BlockEntityRenderer<CustomBlockEntity> {
    private static final double UNIT = 1.0D / 32.0D;

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
        CustomRendererTintGetter tintGetter = new CustomRendererTintGetter(level);
        RandomSource random = RandomSource.create();
        long seed = blockEntity.getBlockPos().asLong();

        boolean hasEntries = false;
        for (int index = 0; index < CustomBlockEntity.MAX_ENTRIES; index++) {
            CustomBlockEntity.CustomEntry entry = blockEntity.entry(index);
            if (entry != null) {
                hasEntries = true;
                renderEntry(blockEntity, entry, index, level, dispatcher, tintGetter, poseStack, buffer, packedLight,
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
    }

    @SuppressWarnings("deprecation")
    private static void renderEntry(CustomBlockEntity blockEntity, CustomBlockEntity.CustomEntry entry, int index,
            Level level, BlockRenderDispatcher dispatcher, CustomRendererTintGetter tintGetter, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay, RandomSource random, long seed) {
        BlockState state = entry.displayState();
        if (state.isAir()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(entry.dir() * 15.0F));
        poseStack.translate(entry.offset(0) * UNIT, entry.offset(1) * UNIT, entry.offset(2) * UNIT);
        poseStack.translate(-0.5D, 0.0D, -0.5D);

        BakedModel model = dispatcher.getBlockModel(state);
        RenderType renderType = ItemBlockRenderTypes.getRenderType(state, false);
        if (entry.tinted()) {
            tintGetter.setCustomTint(entry.color());
            dispatcher.getModelRenderer().tesselateBlock(tintGetter, model, state, blockEntity.getBlockPos(),
                    poseStack, buffer.getBuffer(renderType), false, random, seed + index, packedOverlay);
        } else {
            dispatcher.getModelRenderer().tesselateBlock(level, model, state, blockEntity.getBlockPos(),
                    poseStack, buffer.getBuffer(renderType), false, random, seed + index, packedOverlay);
        }
        poseStack.popPose();
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
