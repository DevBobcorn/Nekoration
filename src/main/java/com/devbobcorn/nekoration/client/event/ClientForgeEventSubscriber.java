package com.devbobcorn.nekoration.client.event;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import com.devbobcorn.nekoration.client.rendering.QuadRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

// Client-Side Only Things...
@EventBusSubscriber(modid = Nekoration.MODID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEventSubscriber {
    @SubscribeEvent
    public static boolean onDrawBlockHighlight(DrawHighlightEvent event){
        final double frac = 1.0D / 32.0D; // TODO

        if (!(event.getTarget() instanceof BlockRayTraceResult))
            return false;
        BlockPos blockpos = ((BlockRayTraceResult)event.getTarget()).getBlockPos();
        ClientWorld level = Minecraft.getInstance().level;
        BlockState blockstate = level.getBlockState(blockpos);
        if (blockstate.getBlock() != ModBlocks.CUSTOM.get())
            return false;
        
        TileEntity te = level.getBlockEntity(blockpos);
        if (te == null || !(te instanceof CustomBlockEntity))
            return false;

        CustomBlockEntity tileEntity = (CustomBlockEntity)te;
        MatrixStack stack = event.getMatrix();

        stack.pushPose();
        Minecraft.getInstance().font.draw(stack, "p_238421_2_", 0.0F, 0.0F, 0);

        stack.translate(blockpos.getX() + 0.5, blockpos.getY(), blockpos.getZ() + 0.5); // Change its pivot point before rotation...
        stack.mulPose(Vector3f.YP.rotationDegrees(tileEntity.dir * 15F));
        stack.translate(-0.5, 0.0, -0.5); // Then just get it back...

        // To translate 1 here is to translate 2 meters(blocks), so we translate 1/32 for a single-voxel-long offset(1/16 block)... 
        stack.translate(tileEntity.offset[0] * frac, tileEntity.offset[1] * frac, tileEntity.offset[2] * frac); // Offset by certain voxels (1 block = 16 * 16 * 16 voxels)
        
        //QuadRenderer.renderCubeUsingQuads(tileEntity, event.getPartialTicks(), stack, event.getBuffers(), 0, 0);
        renderShape(stack, event.getBuffers().getBuffer(RenderType.lines()), blockstate.getShape(level, blockpos, ISelectionContext.of(event.getInfo().getEntity())), (double)blockpos.getX() - 0.0, (double)blockpos.getY() - 0.0, (double)blockpos.getZ() - 0.0, 0.0F, 0.0F, 0.0F, 0.4F);
        stack.popPose();

        event.setCanceled(true);
        return true;
    }

    private static void renderShape(MatrixStack stack, IVertexBuilder vertex, VoxelShape shape, double d1, double d2, double d3, float f1, float f2, float f3, float f4) {
        Matrix4f matrix4f = stack.last().pose();
        shape.forAllEdges((n1, n2, n3, n4, n5, n6) -> {
           vertex.vertex(matrix4f, (float)(n1 + d1), (float)(n2 + d2), (float)(n3 + d3)).color(f1, f2, f3, f4).endVertex();
           vertex.vertex(matrix4f, (float)(n4 + d1), (float)(n5 + d2), (float)(n6 + d3)).color(f1, f2, f3, f4).endVertex();
        });
    }
}
