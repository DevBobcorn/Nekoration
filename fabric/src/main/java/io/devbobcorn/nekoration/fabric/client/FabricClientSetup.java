package io.devbobcorn.nekoration.fabric.client;

import java.lang.reflect.Proxy;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.phys.BlockHitResult;
import io.devbobcorn.nekoration.client.NekoClientSetup;
import io.devbobcorn.nekoration.client.NekoColorHandlers;
import io.devbobcorn.nekoration.client.creative.NekoCreativeTabFilterClient;
import io.devbobcorn.nekoration.client.gui.screen.EaselMenuScreen;
import io.devbobcorn.nekoration.client.rendering.PaintingTooltipRenderer;
import io.devbobcorn.nekoration.client.rendering.WallpaperItemRenderer;
import io.devbobcorn.nekoration.fabric.client.ct.FabricModelSwapper;
import io.devbobcorn.nekoration.fabric.client.network.FabricClientNetwork;
import io.devbobcorn.nekoration.fabric.client.config.FabricConfigScreen;
import io.devbobcorn.nekoration.blocks.containers.EaselMenuMenu;
import io.devbobcorn.nekoration.items.PaintingTooltipComponent;
import io.devbobcorn.nekoration.registry.ModItems;

/**
 * Fabric client wiring for the common client setup.
 */
public final class FabricClientSetup {
    private FabricClientSetup() {
    }

    public static void initialize() {
        // Renderers: block-entity renderers through vanilla's registry (widened
        // by Fabric's transitive access wideners), entity renderers through
        // Fabric's own registry hook.
        NekoClientSetup.registerRenderers(
                net.minecraft.client.renderer.blockentity.BlockEntityRenderers::register,
                EntityRendererRegistry::register);
        // Model layers.
        NekoClientSetup.registerLayerDefinitions((layer, supplier) -> EntityModelLayerRegistry.registerModelLayer(layer, supplier::get));
        // Item model overrides (on Fabric the public overload takes a clamped provider).
        NekoClientSetup.registerItemProperties((item, id, property) ->
                net.minecraft.client.renderer.item.ItemProperties.register(item, id,
                        (stack, level, entity, seed) -> property.call(stack, level, entity, seed)));
        // CT models.
        ModelLoadingPlugin.register(new FabricModelSwapper());
        // Block render layers from model "render_type" (Fabric has no per-model
        // render type support; NeoForge reads that field on its own).
        FabricBlockRenderTypes.register();
        // Colors (Fabric's registries accept the vanilla provider interfaces).
        NekoColorHandlers.registerBlockColors((color, blocks) -> ColorProviderRegistry.BLOCK.register(color,
                blocks.toArray(new net.minecraft.world.level.block.Block[0])));
        NekoColorHandlers.registerItemColors((color, items) -> ColorProviderRegistry.ITEM.register(color,
                items.toArray(new net.minecraft.world.item.Item[0])));
        // Wallpaper item renderer (builtin item renderer registry).
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WALLPAPER.get(), (stack, mode, matrices, consumers,
                light, overlay) -> wallpaperRenderer().renderByItem(stack, mode, matrices, consumers, light, overlay));
        // Painting tooltip preview.
        TooltipComponentCallback.EVENT.register(component -> component instanceof PaintingTooltipComponent painting
                ? new PaintingTooltipRenderer(painting) : null);
        // Placement hint renderers (the event hands us the vanilla block hit result).
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((worldRenderContext, hitResult) -> {
            if (hitResult instanceof BlockHitResult hit) {
                io.devbobcorn.nekoration.client.PlacementHintRenderers.render(hit,
                        worldRenderContext.camera().getPosition(), worldRenderContext.matrixStack(),
                        worldRenderContext.consumers());
            }
            return true;
        });
        // Custom block entry outline (replaces the vanilla outline).
        WorldRenderEvents.BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> {
            net.minecraft.world.phys.Vec3 cameraPos = new net.minecraft.world.phys.Vec3(
                    blockOutlineContext.cameraX(), blockOutlineContext.cameraY(), blockOutlineContext.cameraZ());
            return !io.devbobcorn.nekoration.client.CustomBlockSelectionOutlineRenderer.render(
                    blockOutlineContext.blockPos(), cameraPos, worldRenderContext.matrixStack(),
                    worldRenderContext.consumers());
        });
        // Creative tab filters.
        registerCreativeTabFilter();
        registerConfigScreen();
        // Networking (client-to-server sending + server->client payloads + client receivers).
        FabricClientNetwork.register();
        // Menu screens (vanilla register API is private: reflect once).
        registerScreens();
    }

    private static volatile BlockEntityWithoutLevelRenderer wallpaperRendererInstance;

    private static BlockEntityWithoutLevelRenderer wallpaperRenderer() {
        if (wallpaperRendererInstance == null) {
            Minecraft minecraft = Minecraft.getInstance();
            wallpaperRendererInstance = new WallpaperItemRenderer(minecraft.getBlockEntityRenderDispatcher(),
                    minecraft.getEntityModels());
        }
        return wallpaperRendererInstance;
    }

    private static void registerCreativeTabFilter() {
        ScreenEvents.AFTER_INIT.register((minecraft, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof CreativeModeInventoryScreen creative) {
                NekoCreativeTabFilterClient.onScreenInit(creative, Screens.getButtons(screen)::add);
                ScreenEvents.beforeRender(creative).register((rendered, guiGraphics, mouseX, mouseY, tickDelta) ->
                        NekoCreativeTabFilterClient.onRender(creative));
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> NekoCreativeTabFilterClient.reset());
    }

    private static void registerConfigScreen() {
        ScreenEvents.AFTER_INIT.register((minecraft, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof OptionsScreen) {
                Screens.getButtons(screen).add(Button.builder(
                        Component.literal("Nekoration"),
                        button -> minecraft.setScreen(new FabricConfigScreen(screen)))
                        .bounds(scaledWidth - 104, 6, 98, 20).build());
            }
        });
    }

    /**
     * Registers the easel menu screen. Vanilla's {@code MenuScreens.register}
     * and its constructor interface are not accessible from mod code outside
     * the minecraft package, so the registration happens reflectively (the
     * NeoForge build uses its own event instead).
     */
    private static void registerScreens() {
        try {
            java.lang.reflect.Method register = null;
            for (java.lang.reflect.Method method : net.minecraft.client.gui.screens.MenuScreens.class
                    .getDeclaredMethods()) {
                Class<?>[] params = method.getParameterTypes();
                if (method.getName().equals("register") && params.length == 2
                        && params[0] == MenuType.class && params[1].isInterface()) {
                    register = method;
                    register.setAccessible(true);
                    break;
                }
            }
            if (register == null) {
                throw new IllegalStateException("MenuScreens.register(MenuType, ScreenConstructor) not found");
            }
            Class<?> constructorInterface = register.getParameterTypes()[1];
            Object screenConstructor = Proxy.newProxyInstance(FabricClientSetup.class.getClassLoader(),
                    new Class<?>[]{constructorInterface},
                    (proxy, method, args) -> {
                        if (method.getName().equals("create") && args != null && args.length == 3) {
                            if (args[0] instanceof io.devbobcorn.nekoration.blocks.containers.CustomBlockMenu customBlockMenu) {
                                return new io.devbobcorn.nekoration.client.gui.screen.CustomBlockScreen(customBlockMenu,
                                        (Inventory) args[1], (Component) args[2]);
                            }
                            return new EaselMenuScreen((EaselMenuMenu) args[0], (Inventory) args[1],
                                    (Component) args[2]);
                        }
                        return defaultValue(method);
                    });
            register.invoke(null, io.devbobcorn.nekoration.registry.ModMenuTypes.EASEL_MENU.get(), screenConstructor);
            register.invoke(null, io.devbobcorn.nekoration.registry.ModMenuTypes.CUSTOM_BLOCK.get(), screenConstructor);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to register easel menu screen", e);
        }
    }

    private static Object defaultValue(java.lang.reflect.Method method) {
        Class<?> type = method.getReturnType();
        if (type == boolean.class) {
            return false;
        }
        if (type.isPrimitive() && type != void.class) {
            return 0;
        }
        return null;
    }
}
