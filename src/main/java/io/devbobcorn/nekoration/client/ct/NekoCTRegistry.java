package io.devbobcorn.nekoration.client.ct;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

public final class NekoCTRegistry {
    private static final Map<ResourceLocation, Function<BakedModel, ? extends BakedModel>> CT_MODELS = new HashMap<>();
    private static boolean bootstrapped;

    private NekoCTRegistry() {
    }

    public static Map<ResourceLocation, Function<BakedModel, ? extends BakedModel>> getModelFactories() {
        bootstrap();
        return CT_MODELS;
    }

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;

        /*
        TestCTBehaviour behaviour = new TestCTBehaviour();
        ResourceLocation blockId = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "ct_test_block");
        CT_MODELS.put(blockId, model -> new NekoCTModel(model, behaviour));
        */
    }
}
