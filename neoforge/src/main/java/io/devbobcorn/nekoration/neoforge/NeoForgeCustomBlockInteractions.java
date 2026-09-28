package io.devbobcorn.nekoration.neoforge;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.CustomBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * NeoForge wiring for Custom Block entries placed against a neighboring surface
 * and broken one at a time.
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

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof Level level
                && !CustomBlock.beforeBlockBreak(level, event.getPlayer(), event.getPos(), event.getState(),
                        level.getBlockEntity(event.getPos()))) {
            event.setCanceled(true);
        }
    }
}
