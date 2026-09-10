package io.devbobcorn.nekoration.client.ct;

import java.util.HashMap;
import java.util.Map;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.NekoWood;
import io.devbobcorn.nekoration.registry.CementBlockRegistration;
import io.devbobcorn.nekoration.registry.WoodenBlockRegistration.WindowVariant;
import net.minecraft.resources.ResourceLocation;

/**
 * Assigns a connected-texture behaviour to each CT-capable block. Platform
 * model shells (NeoForge / Fabric) wrap the baked models of these blocks with
 * their own implementations.
 */
public final class NekoCTRegistry {
    private static final Map<ResourceLocation, NekoConnectedTextureBehaviour> CT_BEHAVIOURS = new HashMap<>();
    private static boolean bootstrapped;

    private NekoCTRegistry() {
    }

    public static Map<ResourceLocation, NekoConnectedTextureBehaviour> getBehaviours() {
        bootstrap();
        return CT_BEHAVIOURS;
    }

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;

        NekoConnectedTextureBehaviour cementBehaviour = new CementCTBehaviour();
        for (String blockId : CementBlockRegistration.fullCubeBlockIds()) {
            CT_BEHAVIOURS.put(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, blockId), cementBehaviour);
        }

        NekoConnectedTextureBehaviour windowBehaviour = new WindowCTBehaviour();
        NekoConnectedTextureBehaviour paneBehaviour = new WindowPaneCTBehaviour();
        for (NekoWood wood : NekoWood.values()) {
            for (WindowVariant variant : WindowVariant.values()) {
                String windowId = wood.id() + "_window_" + variant.id();
                ResourceLocation windowBlockId = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, windowId);
                CT_BEHAVIOURS.put(windowBlockId, windowBehaviour);

                String paneId = wood.id() + "_window_pane_" + variant.id();
                ResourceLocation paneBlockId = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, paneId);
                CT_BEHAVIOURS.put(paneBlockId, paneBehaviour);
            }
        }
    }
}
