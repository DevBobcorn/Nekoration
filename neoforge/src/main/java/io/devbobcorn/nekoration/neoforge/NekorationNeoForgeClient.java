package io.devbobcorn.nekoration.neoforge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.client.NekoClientSetup;
import io.devbobcorn.nekoration.client.ct.NekoModelSwapper;
import io.devbobcorn.nekoration.client.rendering.WallpaperItemRenderer;
import io.devbobcorn.nekoration.client.rendering.PaintingTooltipRenderer;
import io.devbobcorn.nekoration.items.PaintingTooltipComponent;
import io.devbobcorn.nekoration.registry.ModItems;

/**
 * NeoForge client entrypoint. Wires the common client setup into NeoForge
 * events and registers the NeoForge-only client extensions.
 */
@Mod(value = Nekoration.MODID, dist = Dist.CLIENT)
public class NekorationNeoForgeClient {
    public NekorationNeoForgeClient(IEventBus modEventBus, ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(NekorationNeoForgeClient::onClientSetup);
        modEventBus.addListener(NekorationNeoForgeClient::registerRenderers);
        modEventBus.addListener(NekorationNeoForgeClient::registerScreens);
        modEventBus.addListener(NekorationNeoForgeClient::registerLayerDefinitions);
        modEventBus.addListener(NekorationNeoForgeClient::registerClientExtensions);
        modEventBus.addListener(NekorationNeoForgeClient::registerTooltipComponents);
        NekoModelSwapper.registerListeners(modEventBus);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> NekoClientSetup.registerItemProperties(net.minecraft.client.renderer.item.ItemProperties::register));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        NekoClientSetup.registerRenderers(
                (type, provider) -> event.registerBlockEntityRenderer((net.minecraft.world.level.block.entity.BlockEntityType) type, provider),
                (type, provider) -> event.registerEntityRenderer((net.minecraft.world.entity.EntityType) type, provider));
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(io.devbobcorn.nekoration.registry.ModMenuTypes.EASEL_MENU.get(),
                io.devbobcorn.nekoration.client.gui.screen.EaselMenuScreen::new);
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        NekoClientSetup.registerLayerDefinitions(event::registerLayerDefinition);
    }

    private static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private WallpaperItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    Minecraft minecraft = Minecraft.getInstance();
                    renderer = new WallpaperItemRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
                }
                return renderer;
            }
        }, ModItems.WALLPAPER.get());
    }

    private static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        // The painting content preview in a painted painting item's tooltip...
        event.register(PaintingTooltipComponent.class, PaintingTooltipRenderer::new);
    }
}
