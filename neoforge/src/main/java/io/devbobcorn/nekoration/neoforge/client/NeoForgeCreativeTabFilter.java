package io.devbobcorn.nekoration.neoforge.client;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.client.creative.NekoCreativeTabFilterClient;

/**
 * NeoForge wiring for the creative tab filter UI.
 */
@EventBusSubscriber(modid = Nekoration.MODID, value = Dist.CLIENT)
public final class NeoForgeCreativeTabFilter {
    private NeoForgeCreativeTabFilter() {
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        NekoCreativeTabFilterClient.reset();
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen creative) {
            NekoCreativeTabFilterClient.onScreenInit(creative, event::addListener);
        }
    }

    @SubscribeEvent
    public static void onRenderPre(ScreenEvent.Render.Pre event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen creative) {
            NekoCreativeTabFilterClient.onRender(creative);
        }
    }
}
