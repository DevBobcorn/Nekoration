package io.devbobcorn.nekoration.client;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.client.gui.screen.EaselMenuScreen;
import io.devbobcorn.nekoration.client.gui.screen.MenuScreenConstructor;
import io.devbobcorn.nekoration.client.rendering.EaselMenuBlockEntityRenderer;
import io.devbobcorn.nekoration.client.rendering.ItemDisplayBlockEntityRenderer;
import io.devbobcorn.nekoration.client.rendering.SeatEntityRenderer;
import io.devbobcorn.nekoration.client.rendering.WallpaperRenderer;
import io.devbobcorn.nekoration.client.rendering.entities.PaintingRenderer;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.items.PaintingItem;
import io.devbobcorn.nekoration.registry.ModBlockEntities;
import io.devbobcorn.nekoration.registry.ModEntities;
import io.devbobcorn.nekoration.registry.ModItems;
import io.devbobcorn.nekoration.registry.ModMenuTypes;
import io.devbobcorn.nekoration.registry.OrnamentRegistration;

/**
 * Client-side setup for code shared by every loader. Vanilla's renderer,
 * screen and item-property registration methods are private, so the platform
 * entrypoints pass their own registration hooks in.
 */
public final class NekoClientSetup {
    private NekoClientSetup() {
    }

    /** Registers block-entity and entity renderers through the platform hook. */
    @SuppressWarnings("rawtypes")
    public static void registerRenderers(
            BiConsumer<BlockEntityType<?>, BlockEntityRendererProvider> blockEntityRegistrar,
            BiConsumer<EntityType<?>, EntityRendererProvider> entityRegistrar) {
        blockEntityRegistrar.accept(ModBlockEntities.ITEM_DISPLAY.get(), ItemDisplayBlockEntityRenderer::new);
        blockEntityRegistrar.accept(ModBlockEntities.EASEL_MENU.get(), EaselMenuBlockEntityRenderer::new);
        entityRegistrar.accept(ModEntities.SEAT.get(), SeatEntityRenderer::new);
        entityRegistrar.accept(ModEntities.WALLPAPER.get(), WallpaperRenderer::new);
        entityRegistrar.accept(ModEntities.PAINTING.get(), PaintingRenderer::new);
    }

    /** Registers the model layer definitions through the platform hook. */
    public static void registerLayerDefinitions(
            BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> registrar) {
        registrar.accept(WallpaperRenderer.LAYER, WallpaperRenderer::createBodyLayer);
    }

    /** Registers menu screens through the platform hook. */
    public static void registerScreens(
            BiConsumer<net.minecraft.world.inventory.MenuType<?>, MenuScreenConstructor<?, ?>> registrar) {
        registrar.accept(ModMenuTypes.EASEL_MENU.get(),
                (MenuScreenConstructor<io.devbobcorn.nekoration.blocks.containers.EaselMenuMenu, EaselMenuScreen>) (menu, inventory, title) -> new EaselMenuScreen(
                        menu, inventory, title));
    }

    /** Registers the item model override properties through the platform hook. */
    @SuppressWarnings("deprecation")
    public static void registerItemProperties(ItemPropertyRegistrar registrar) {
        ItemPropertyFunction color = (stack, level, entity, seed) ->
                DyeableBlockItem.getColor(stack).getNbtId();
        ResourceLocation colorId = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "color");
        OrnamentRegistration.awningBlockItemsView().forEach(item -> registrar.register(item.get(), colorId, color));
        // Painting item: blank / painted / magic link model override...
        ItemPropertyFunction paintingType = (stack, level, entity, seed) -> (float) PaintingItem.getType(stack);
        ResourceLocation typeId = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "type");
        registrar.register(ModItems.PAINTING.get(), typeId, paintingType);
    }

    /** Platform hook: item + override id + property function. */
    @FunctionalInterface
    public interface ItemPropertyRegistrar {
        @SuppressWarnings("deprecation")
        void register(Item item, ResourceLocation id, ItemPropertyFunction property);
    }
}
