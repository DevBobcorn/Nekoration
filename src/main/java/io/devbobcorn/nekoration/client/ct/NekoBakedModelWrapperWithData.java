package io.devbobcorn.nekoration.client.ct;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;

public abstract class NekoBakedModelWrapperWithData extends BakedModelWrapper<BakedModel> {

    protected NekoBakedModelWrapperWithData(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public final ModelData getModelData(BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        ModelData.Builder builder = ModelData.builder();
        if (originalModel instanceof NekoBakedModelWrapperWithData wrapper) {
            wrapper.gatherModelData(builder, world, pos, state, blockEntityData);
        }
        gatherModelData(builder, world, pos, state, blockEntityData);
        return builder.build();
    }

    protected abstract ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world,
            BlockPos pos, BlockState state, ModelData blockEntityData);
}
