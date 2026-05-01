package io.devbobcorn.nekoration.datagen;

import io.devbobcorn.nekoration.Nekoration;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Nekoration.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class NekorationDataGenerators {
    private NekorationDataGenerators() {
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeClient(),
                new StoneBlockAssetProvider(event.getGenerator().getPackOutput()));
        event.getGenerator().addProvider(
                event.includeClient(),
                new WoodenBlockAssetProvider(event.getGenerator().getPackOutput()));
        event.getGenerator().addProvider(
                event.includeClient(),
                new WoodenTextureAssetProvider(event.getGenerator().getPackOutput()));
    }
}
