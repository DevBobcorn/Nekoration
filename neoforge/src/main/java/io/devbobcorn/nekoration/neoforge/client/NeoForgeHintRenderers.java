package io.devbobcorn.nekoration.neoforge.client;

import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.client.PlacementHintRenderers;

/**
 * NeoForge wiring for the placement hint renderers.
 */
@EventBusSubscriber(modid = Nekoration.MODID, value = Dist.CLIENT)
public final class NeoForgeHintRenderers {
    private NeoForgeHintRenderers() {
    }

    @SubscribeEvent
    public static void onRenderBlockHighlight(RenderHighlightEvent.Block event) {
        BlockHitResult hit = event.getTarget();
        PlacementHintRenderers.render(hit, event.getCamera().getPosition(), event.getPoseStack(),
                event.getMultiBufferSource());
    }
}
