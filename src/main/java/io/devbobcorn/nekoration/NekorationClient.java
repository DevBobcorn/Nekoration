package io.devbobcorn.nekoration;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import io.devbobcorn.nekoration.client.gui.screen.EaselMenuScreen;
import io.devbobcorn.nekoration.client.rendering.EaselMenuBlockEntityRenderer;
import io.devbobcorn.nekoration.client.creative.NekoCreativeTabFilterClient;
import io.devbobcorn.nekoration.client.rendering.ItemDisplayBlockEntityRenderer;
import io.devbobcorn.nekoration.registry.ModBlockEntities;
import io.devbobcorn.nekoration.registry.ModMenuTypes;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Nekoration.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Nekoration.MODID, value = Dist.CLIENT)
public class NekorationClient {
    public NekorationClient(IEventBus modEventBus, ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        // EntityRenderersEvent fires on the mod event bus.
        modEventBus.addListener(NekorationClient::registerRenderers);
        modEventBus.addListener(NekorationClient::registerScreens);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(NekoCreativeTabFilterClient.class);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ITEM_DISPLAY.get(), ItemDisplayBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.EASEL_MENU.get(), EaselMenuBlockEntityRenderer::new);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.EASEL_MENU.get(), EaselMenuScreen::new);
    }
}
