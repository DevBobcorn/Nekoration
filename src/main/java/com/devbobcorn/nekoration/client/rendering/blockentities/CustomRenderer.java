package com.devbobcorn.nekoration.client.rendering.blockentities;

import com.devbobcorn.nekoration.blocks.CustomBlock;
import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;

public class CustomRenderer implements BlockEntityRenderer<CustomBlockEntity> {
    private final static HashMap<Level, CustomRendererTintGetter> tintGetters = new HashMap<Level, CustomRendererTintGetter>();

    final double frac = 1.0D / 32.0D;
    final double frac2 = 10.0D / 32.0D;

    public CustomRenderer(BlockEntityRendererProvider.Context tileEntityRendererDispatcher) {
        
    }

    @Override
    public void render(CustomBlockEntity tileEntity, float partialTicks, PoseStack stack, MultiBufferSource buffers, int combinedLight, int combinedOverlay) {
        // Make sure the blockstate is rendering nothing now...
        if (tileEntity.getBlockState().getValue(CustomBlock.MODEL) != 0)
            return;
        
        // and then we rendering models with our TE renderer...
        if (tileEntity.model > 0 && tileEntity.model <= 15) {
            stack.pushPose();

            stack.translate(0.5, 0.0, 0.5);

            // BED stack.translate(0.0F, -0.4375, 0.0F);
            // CARPET stack.translate(0.0F, -0.875, 0.0F);
            stack.mulPose(Vector3f.YP.rotationDegrees(tileEntity.dir * 15F));
            stack.scale(0.1F, 0.1F, 0.1F);
            stack.translate(tileEntity.offset[0] * frac2, tileEntity.offset[1] * frac2, tileEntity.offset[2] * frac2);

            BlockState state = ModBlocks.CUSTOM.get().defaultBlockState().setValue(CustomBlock.MODEL, tileEntity.model);
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            BakedModel model = dispatcher.getBlockModel(state);

            PoseStack.Pose currentMatrix = stack.last();

            VertexConsumer vertexBuffer = buffers.getBuffer(RenderType.solid());
            int[] theColor = tileEntity.color;
            dispatcher.getModelRenderer().renderModel(currentMatrix, vertexBuffer, null, model, (float)theColor[0] / 255.0F, (float)theColor[1] / 255.0F, (float)theColor[2] / 255.0F, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

            stack.popPose();
        } else {
            stack.pushPose();
            stack.translate(0.5, 0.0, 0.5);
            stack.mulPose(Vector3f.YP.rotationDegrees(tileEntity.dir * 15F));
            stack.translate(-0.5, 0.0, -0.5); // Then just get it back...
            stack.translate(tileEntity.offset[0] * frac, tileEntity.offset[1] * frac, tileEntity.offset[2] * frac);

            BlockState state;
            if (tileEntity.model <= 1) // 0 or 1, just apply default block model
                state = ModBlocks.CUSTOM.get().defaultBlockState().setValue(CustomBlock.MODEL, 1);
            else state = tileEntity.displayBlock;
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

            for (RenderType type : RenderType.chunkBufferLayers()) {
                if (ItemBlockRenderTypes.canRenderInLayer(state, type)) {
                    ForgeHooksClient.setRenderType(type);
                    // Magic starts...
                    Level world = tileEntity.getLevel();
                    long seed = state.getSeed(tileEntity.getBlockPos());

                    if (!tintGetters.containsKey(world))
                    {
                        tintGetters.put(world, new CustomRendererTintGetter(world));
                    }
                    else
                    {
                        // Do re-tint...
                        int rgb = (tileEntity.color[0] << 16) + (tileEntity.color[1] << 8) + tileEntity.color[2];
                        CustomRendererTintGetter tintGetter = tintGetters.get(world);
                        tintGetter.SetCustomTint(rgb);
                        dispatcher.getModelRenderer().tesselateBlock(tintGetter, dispatcher.getBlockModel(state), state, tileEntity.getBlockPos(), stack, buffers.getBuffer(type), false, new Random(), seed, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
                    }

                    // Don't re-tint...
                    //dispatcher.getModelRenderer().tesselateBlock(tileEntity.getLevel(), dispatcher.getBlockModel(state), state, tileEntity.getBlockPos(), stack, buffers.getBuffer(type), false, new Random(), seed, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
                }
            }
            ForgeHooksClient.setRenderType(null);
            
            stack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(CustomBlockEntity tileEntity) {
        return true;
    }
}
