package io.devbobcorn.nekoration.client.ct;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.NekoWood;
import io.devbobcorn.nekoration.registry.CementBlockRegistration;
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

        CementCTBehaviour cementBehaviour = new CementCTBehaviour();
        for (String blockId : CementBlockRegistration.fullCubeBlockIds()) {
            CT_MODELS.put(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, blockId),
                    model -> new NekoCTModel(model, cementBehaviour));
        }

        WindowCTBehaviour windowBehaviour = new WindowCTBehaviour();
        WindowPaneCTBehaviour paneBehaviour = new WindowPaneCTBehaviour();
        for (NekoWood wood : NekoWood.values()) {
            for (WindowVariant variant : WindowVariant.values()) {
                String windowId = wood.id() + "_window_" + variant.id();
                ResourceLocation windowBlockId = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, windowId);
                CT_MODELS.put(windowBlockId, model -> new NekoCTModel(model, windowBehaviour));

                String paneId = wood.id() + "_window_pane_" + variant.id();
                ResourceLocation paneBlockId = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, paneId);
                CT_MODELS.put(paneBlockId, model -> new NekoCTModel(model, paneBehaviour));
            }
        }
    }
}
