package io.devbobcorn.nekoration.client.ct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        for (Map.Entry<ResourceLocation, NekoConnectedTextureBehaviour> entry : NekoCTRegistry.getBehaviours()
                .entrySet()) {
            List<ModelResourceLocation> locations = getAllBlockStateModelLocations(entry.getKey());
            for (ModelResourceLocation location : locations) {
                BakedModel current = modelRegistry.get(location);
                if (current == null) {
                    continue;
                }
                modelRegistry.put(location, new NeoForgeCTModel(current, entry.getValue()));
            }
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
