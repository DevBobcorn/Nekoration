package io.devbobcorn.nekoration.neoforge.client;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.client.PaletteSelectionOutlineRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

@EventBusSubscriber(modid = Nekoration.MODID, value = Dist.CLIENT)
public final class NeoForgePaletteOutlineRenderer {
    private NeoForgePaletteOutlineRenderer() {
    }

    @SubscribeEvent
    public static void onRenderBlockHighlight(RenderHighlightEvent.Block event) {
        if (PaletteSelectionOutlineRenderer.render(event.getTarget().getBlockPos(),
                event.getCamera().getPosition(), event.getPoseStack(), event.getMultiBufferSource())) {
            event.setCanceled(true);
        }
    }
}
