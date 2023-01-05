package com.devbobcorn.nekoration.client.rendering.blockentities;

import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import com.devbobcorn.nekoration.items.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class CustomRenderer implements BlockEntityRenderer<CustomBlockEntity> {
    private final static HashMap<Level, CustomRendererTintGetter> tintGetters = new HashMap<Level, CustomRendererTintGetter>();
    private static ItemStack arrow;

    final double frac = 1.0D / 32.0D;

    public CustomRenderer(BlockEntityRendererProvider.Context tileEntityRendererDispatcher) { }

    @Override
    public void render(CustomBlockEntity tileEntity, float partialTicks, PoseStack stack, MultiBufferSource buffers, int combinedLight, int combinedOverlay) {
        // Render block model with our TE renderer...
        stack.pushPose();
        // Translate a bit so that it can rotate around the center of this block
        stack.translate(0.5, 0.0, 0.5);
        stack.mulPose(Axis.YP.rotationDegrees(tileEntity.dir * 15F));
        
        stack.translate(tileEntity.offset[0] * frac, tileEntity.offset[1] * frac, tileEntity.offset[2] * frac);

        if (arrow != null) {
            if (tileEntity.showHint) {
                // Render Arrow
                stack.translate(0F, 0.5F, -1F);
                stack.mulPose(Axis.XP.rotationDegrees(90F));

                ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
                BakedModel bakedmodel = ir.getModel(arrow, tileEntity.getLevel(), (LivingEntity)null, 0);

                // p_115149_ is packedLight / p_115_150_ is packedOverlay
                ir.render(arrow, TransformType.FIXED, true, stack, buffers, 255, 0, bakedmodel);

                stack.mulPose(Axis.XP.rotationDegrees(-90F));
                stack.translate(0F, -0.5F, 1F);
            }
        } else {
            arrow = new ItemStack(ModItems.ARROW_HINT.get());
        }

        stack.translate(-0.5, 0.0, -0.5); // Then just get it back...

        BlockState state;
        if (tileEntity.displayState != CustomBlockEntity.defaultState) {
            state = tileEntity.displayState;
        } else {
            state = ModBlocks.DREAM_WAS_TAKEN.get().defaultBlockState();
        }

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        var model = dispatcher.getBlockModel(state);

        for (var type : model.getRenderTypes(state, RandomSource.create(state.getSeed(tileEntity.getBlockPos())), ModelData.EMPTY)) {
            // Magic starts...
            Level world = tileEntity.getLevel();
            long seed = state.getSeed(tileEntity.getBlockPos());

            if (tileEntity.retint) {
                if (!tintGetters.containsKey(world))
                    tintGetters.put(world, new CustomRendererTintGetter(world));

                // Do re-tint (override tint color)...
                int rgb = (tileEntity.color[0] << 16) + (tileEntity.color[1] << 8) + tileEntity.color[2];
                CustomRendererTintGetter tintGetter = tintGetters.get(world);
                tintGetter.SetCustomTint(rgb);
                dispatcher.getModelRenderer().tesselateBlock(tintGetter, dispatcher.getBlockModel(state), state, tileEntity.getBlockPos(), stack, buffers.getBuffer(type), false, RandomSource.create(), seed, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, type);
            } else {
                // Don't re-tint...
                dispatcher.getModelRenderer().tesselateBlock(world, dispatcher.getBlockModel(state), state, tileEntity.getBlockPos(), stack, buffers.getBuffer(type), false, RandomSource.create(), seed, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, type);
            }

        }
        
        stack.popPose();
        
    }

    @Override
    public boolean shouldRenderOffScreen(CustomBlockEntity tileEntity) {
        return true;
    }
}
