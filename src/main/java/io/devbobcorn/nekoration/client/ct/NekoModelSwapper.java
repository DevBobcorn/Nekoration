package io.devbobcorn.nekoration.client.ct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;

public final class NekoModelSwapper {
    private NekoModelSwapper() {
    }

    public static void registerListeners(IEventBus modEventBus) {
        NekoCTRegistry.bootstrap();
        modEventBus.addListener(NekoModelSwapper::onModelBake);
    }

    private static void onModelBake(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();
        for (Map.Entry<ResourceLocation, Function<BakedModel, ? extends BakedModel>> entry : NekoCTRegistry.getModelFactories()
                .entrySet()) {
            swapModels(modelRegistry, getAllBlockStateModelLocations(entry.getKey()), entry.getValue());
        }
    }

    private static void swapModels(Map<ModelResourceLocation, BakedModel> modelRegistry, List<ModelResourceLocation> locations,
            Function<BakedModel, ? extends BakedModel> factory) {
        for (ModelResourceLocation location : locations) {
            BakedModel current = modelRegistry.get(location);
            if (current == null) {
                continue;
            }
            modelRegistry.put(location, factory.apply(current));
        }
    }

    private static List<ModelResourceLocation> getAllBlockStateModelLocations(ResourceLocation blockId) {
        List<ModelResourceLocation> models = new ArrayList<>();
        Block block = BuiltInRegistries.BLOCK.get(blockId);
        if (block == net.minecraft.world.level.block.Blocks.AIR) {
            return models;
        }
        block.getStateDefinition().getPossibleStates()
                .forEach(state -> models.add(BlockModelShaper.stateToModelLocation(blockId, state)));
        return models;
    }
}
