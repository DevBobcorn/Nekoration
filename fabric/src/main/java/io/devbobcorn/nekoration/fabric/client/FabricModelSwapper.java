package io.devbobcorn.nekoration.fabric.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import io.devbobcorn.nekoration.client.ct.NekoConnectedTextureBehaviour;
import io.devbobcorn.nekoration.client.ct.NekoCTRegistry;

/**
 * Wraps the baked models of CT-capable blocks with {@link FabricCTModel}s.
 */
public final class FabricModelSwapper implements ModelLoadingPlugin {
    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        NekoCTRegistry.bootstrap();
        pluginContext.modifyModelAfterBake().register((model, context) -> {
            if (context.topLevelId() instanceof ModelResourceLocation location) {
                NekoConnectedTextureBehaviour behaviour = matchBehaviour(location);
                if (behaviour != null) {
                    return new FabricCTModel(model, behaviour);
                }
            }
            return model;
        });
    }

    private static NekoConnectedTextureBehaviour matchBehaviour(ModelResourceLocation location) {
        ResourceLocation modelId = location.id();
        for (var entry : NekoCTRegistry.getBehaviours().entrySet()) {
            ResourceLocation blockId = entry.getKey();
            if (!modelId.getNamespace().equals(blockId.getNamespace())) {
                continue;
            }
            // Blockstate models use "<block>/<variant>" paths.
            if (modelId.getPath().equals(blockId.getPath())
                    || modelId.getPath().startsWith(blockId.getPath() + "/")) {
                return entry.getValue();
            }
        }
        return null;
    }
}
