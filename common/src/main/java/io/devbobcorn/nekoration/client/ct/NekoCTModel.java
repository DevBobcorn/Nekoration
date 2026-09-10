package io.devbobcorn.nekoration.client.ct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.devbobcorn.nekoration.blocks.WindowPaneBlock;
import io.devbobcorn.nekoration.client.ct.NekoConnectedTextureBehaviour.CTContext;
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

/**
 * Loader-agnostic connected-textures core: gathers the per-position CT context
 * and rewrites quad UVs for it. Platform modules wrap this in their own model
 * shells (NeoForge: model data wrapper, Fabric: renderer API) since the vanilla
 * {@code BakedModel#getQuads} contract has no per-position hook.
 */
public final class NekoCTModel {
    private final BakedModel originalModel;
    private final NekoConnectedTextureBehaviour behaviour;

    public NekoCTModel(BakedModel originalModel, NekoConnectedTextureBehaviour behaviour) {
        this.originalModel = originalModel;
        this.behaviour = behaviour;
    }

    public BakedModel originalModel() {
        return originalModel;
    }

    public NekoConnectedTextureBehaviour behaviour() {
        return behaviour;
    }

    /** Quads of the original model, for the vanilla contract. */
    public List<BakedQuad> originalQuads(BlockState state, Direction side, RandomSource rand) {
        return originalModel.getQuads(state, side, rand);
    }

    public CTData createCTData(BlockAndTintGetter world, BlockPos pos, BlockState state) {
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

    /** Applies the CT texture shifts to a copy of the given quads. */
    public List<BakedQuad> applyCT(BlockState state, List<BakedQuad> quads, CTData data) {
        List<BakedQuad> result = new ArrayList<>(quads);
        for (int i = 0; i < result.size(); i++) {
            BakedQuad quad = result.get(i);
            if (shouldCullPaneVerticalFace(state, quad.getDirection()) && data.isPaneVerticalFaceCulled(quad.getDirection())) {
                result.remove(i--);
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
            result.set(i, newQuad);
        }
        return result;
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

    /** Per-face CT texture indices for one block position. */
    public static final class CTData {
        private final int[] indices = new int[6];
        private final boolean[] paneVerticalFaceCulled = new boolean[6];

        private CTData() {
            Arrays.fill(indices, -1);
            Arrays.fill(paneVerticalFaceCulled, false);
        }

        private void put(Direction face, int texture) {
            indices[face.get3DDataValue()] = texture;
        }

        public int get(Direction face) {
            return indices[face.get3DDataValue()];
        }

        private void putPaneVerticalFaceCulled(Direction face, boolean culled) {
            paneVerticalFaceCulled[face.get3DDataValue()] = culled;
        }

        public boolean isPaneVerticalFaceCulled(Direction face) {
            return paneVerticalFaceCulled[face.get3DDataValue()];
        }
    }
}
