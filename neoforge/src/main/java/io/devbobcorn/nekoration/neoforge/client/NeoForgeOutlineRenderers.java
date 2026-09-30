package io.devbobcorn.nekoration.neoforge.client;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.client.CustomBlockSelectionOutlineRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

@EventBusSubscriber(modid = Nekoration.MODID, value = Dist.CLIENT)
public final class NeoForgeOutlineRenderers {
    private NeoForgeOutlineRenderers() {
    }

    @SubscribeEvent
    public static void onRenderBlockHighlight(RenderHighlightEvent.Block event) {
        if (CustomBlockSelectionOutlineRenderer.render(event.getTarget().getBlockPos(),
                event.getCamera().getPosition(), event.getPoseStack(), event.getMultiBufferSource())) {
            event.setCanceled(true);
        }
    }
}
