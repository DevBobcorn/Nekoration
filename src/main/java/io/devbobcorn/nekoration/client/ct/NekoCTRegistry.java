package io.devbobcorn.nekoration.client.ct;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.NekoWood;
import io.devbobcorn.nekoration.registry.WoodenBlockRegistration.WindowVariant;
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

        WindowCTBehaviour behaviour = new WindowCTBehaviour();
        for (NekoWood wood : NekoWood.values()) {
            for (WindowVariant variant : WindowVariant.values()) {
                String id = wood.id() + "_window_" + variant.id();
                ResourceLocation blockId = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, id);
                CT_MODELS.put(blockId, model -> new NekoCTModel(model, behaviour));
            }
        }
    }
}
