package io.devbobcorn.nekoration.client.ct;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;
import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import io.devbobcorn.nekoration.client.ct.NekoCTModel.CTData;

/**
 * NeoForge shell for the connected-textures core: per-position CT data rides
 * NeoForge's model data mechanism, as before the multiplatform split.
 */
public class NeoForgeCTModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<CTData> CT_PROPERTY = new ModelProperty<>();

    private final NekoCTModel core;

    public NeoForgeCTModel(BakedModel originalModel, NekoConnectedTextureBehaviour behaviour) {
        super(originalModel);
        this.core = new NekoCTModel(originalModel, behaviour);
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        return blockEntityData.derive().with(CT_PROPERTY, core.createCTData(world, pos, state)).build();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData,
            @Nullable RenderType renderType) {
        List<BakedQuad> quads = super.getQuads(state, side, rand, extraData, renderType);
        if (extraData == null || !extraData.has(CT_PROPERTY)) {
            return quads;
        }
        return core.applyCT(state, quads, extraData.get(CT_PROPERTY));
    }
}
