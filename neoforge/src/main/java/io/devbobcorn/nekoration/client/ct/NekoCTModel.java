package io.devbobcorn.nekoration.client.ct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.devbobcorn.nekoration.client.ct.NekoConnectedTextureBehaviour.CTContext;
import io.devbobcorn.nekoration.blocks.WindowPaneBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class NekoCTModel extends NekoBakedModelWrapperWithData {
    private static final ModelProperty<CTData> CT_PROPERTY = new ModelProperty<>();

    private final NekoConnectedTextureBehaviour behaviour;

    public NekoCTModel(BakedModel originalModel, NekoConnectedTextureBehaviour behaviour) {
        super(originalModel);
        this.behaviour = behaviour;
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state,
            ModelData blockEntityData) {
        return builder.with(CT_PROPERTY, createCTData(world, pos, state));
    }

    private CTData createCTData(BlockAndTintGetter world, BlockPos pos, BlockState state) {
        CTData data = new CTData();
        MutableBlockPos mutablePos = new MutableBlockPos();
        for (Direction face : Direction.values()) {
            boolean shouldRenderFace = Block.shouldRenderFace(state, world, pos, face, mutablePos.setWithOffset(pos, face));
            if (shouldCullPaneVerticalFace(state, face)) {
                data.putPaneVerticalFaceCulled(face, isPaneVerticalFaceFullyOccluded(world, pos, state, face, mutablePos));
            }
            if (!behaviour.buildContextForOccludedDirections() && !shouldRenderFace) {
                continue;
            }
            NekoCTType dataType = behaviour.getDataType(world, pos, state, face);
            if (dataType == null) {
                continue;
            }
            CTContext context = behaviour.buildContext(world, pos, state, face, dataType.getContextRequirement());
            data.put(face, dataType.getTextureIndex(context));
        }
        return data;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
        List<BakedQuad> quads = super.getQuads(state, side, rand, extraData, renderType);
        if (!extraData.has(CT_PROPERTY)) {
            return quads;
        }

        CTData data = extraData.get(CT_PROPERTY);
        quads = new ArrayList<>(quads);
        for (int i = 0; i < quads.size(); i++) {
            BakedQuad quad = quads.get(i);
            if (shouldCullPaneVerticalFace(state, quad.getDirection()) && data.isPaneVerticalFaceCulled(quad.getDirection())) {
                quads.remove(i--);
                continue;
            }

            int index = data.get(quad.getDirection());
            if (index == -1) {
                continue;
            }

            NekoCTSpriteShiftEntry spriteShift = behaviour.getShift(state, quad.getDirection(), quad.getSprite());
            if (spriteShift == null) {
                continue;
            }
            if (quad.getSprite() != spriteShift.getOriginal()) {
                continue;
            }

            BakedQuad newQuad = NekoBakedQuadHelper.clone(quad);
            int[] vertexData = newQuad.getVertices();
            for (int vertex = 0; vertex < 4; vertex++) {
                float u = NekoBakedQuadHelper.getU(vertexData, vertex);
                float v = NekoBakedQuadHelper.getV(vertexData, vertex);
                NekoBakedQuadHelper.setU(vertexData, vertex, spriteShift.getTargetU(u, index));
                NekoBakedQuadHelper.setV(vertexData, vertex, spriteShift.getTargetV(v, index));
            }
            quads.set(i, newQuad);
        }
        return quads;
    }

    private static boolean shouldCullPaneVerticalFace(BlockState state, Direction face) {
        return state != null
                && state.getBlock() instanceof WindowPaneBlock
                && face.getAxis().isVertical();
    }

    private static boolean isPaneVerticalFaceFullyOccluded(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction face,
            MutableBlockPos mutablePos) {
        BlockPos neighborPos = mutablePos.setWithOffset(pos, face);
        BlockState neighborState = world.getBlockState(neighborPos);

        VoxelShape selfFace = state.getOcclusionShape(world, pos).getFaceShape(face);
        if (selfFace.isEmpty()) {
            return false;
        }

        VoxelShape neighborFace = neighborState.getOcclusionShape(world, neighborPos).getFaceShape(face.getOpposite());
        return !Shapes.joinIsNotEmpty(selfFace, neighborFace, BooleanOp.ONLY_FIRST);
    }

    private static class CTData {
        private final int[] indices = new int[6];
        private final boolean[] paneVerticalFaceCulled = new boolean[6];

        private CTData() {
            Arrays.fill(indices, -1);
            Arrays.fill(paneVerticalFaceCulled, false);
        }

        private void put(Direction face, int texture) {
            indices[face.get3DDataValue()] = texture;
        }

        private int get(Direction face) {
            return indices[face.get3DDataValue()];
        }

        private void putPaneVerticalFaceCulled(Direction face, boolean culled) {
            paneVerticalFaceCulled[face.get3DDataValue()] = culled;
        }

        private boolean isPaneVerticalFaceCulled(Direction face) {
            return paneVerticalFaceCulled[face.get3DDataValue()];
        }
    }
}
