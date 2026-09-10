package io.devbobcorn.nekoration.neoforge.client;

import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.client.NekoColorHandlers;

/**
 * NeoForge wiring for the common color providers.
 */
@EventBusSubscriber(modid = Nekoration.MODID, value = Dist.CLIENT)
public final class NeoForgeColorHandlers {
    private NeoForgeColorHandlers() {
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        NekoColorHandlers.registerBlockColors((color, blocks) -> {
            Block[] array = blocks.toArray(Block[]::new);
            event.register(color, array);
        });
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        NekoColorHandlers.registerItemColors((color, items) -> {
            net.minecraft.world.item.Item[] array = items.toArray(new net.minecraft.world.item.Item[0]);
            event.register(color, array);
        });
    }
}
