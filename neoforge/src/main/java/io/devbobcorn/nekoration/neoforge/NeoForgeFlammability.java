package io.devbobcorn.nekoration.neoforge;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import io.devbobcorn.nekoration.registry.OrnamentRegistration;
import io.devbobcorn.nekoration.registry.WoodenBlockRegistration;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;

/**
 * NeoForge flammability registration (vanilla {@link FireBlock} odds maps),
 * matching vanilla planks (5/20), wool and leaves (30/60).
 */
public final class NeoForgeFlammability {
    private NeoForgeFlammability() {
    }

    public static void register() {
        FireBlock fire = (FireBlock) Blocks.FIRE;
        for (var entry : WoodenBlockRegistration.blocksByWoodView().entrySet()) {
            if (!entry.getKey().isFlammable()) {
                continue;
            }
            for (RegistrySupplier<Block> holder : entry.getValue()) {
                fire.setFlammable(holder.get(), 5, 20);
            }
        }
        for (RegistrySupplier<Block> holder : OrnamentRegistration.awningBlocksView()) {
            fire.setFlammable(holder.get(), 30, 60);
        }
        fire.setFlammable(OrnamentRegistration.windowPlantBlock().get(), 30, 60);
    }
}
