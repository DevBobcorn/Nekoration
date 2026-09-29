package io.devbobcorn.nekoration.neoforge;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.CustomBlock;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * NeoForge wiring for Custom Block entries placed against a neighboring surface.
 */
@EventBusSubscriber(modid = Nekoration.MODID)
public final class NeoForgeCustomBlockInteractions {
    private NeoForgeCustomBlockInteractions() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = CustomBlock.addEntryFromPlacement(event.getEntity(), event.getLevel(),
                event.getHand(), event.getHitVec());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }
}
