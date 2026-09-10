package io.devbobcorn.nekoration.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.devbobcorn.nekoration.world.upgrade.LegacyWorldUpgrader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;

@Mixin(SimpleRegionStorage.class)
abstract class SimpleRegionStorageMixin {
    @Inject(method = "upgradeChunkTag(Lnet/minecraft/nbt/CompoundTag;I)Lnet/minecraft/nbt/CompoundTag;", at = @At("HEAD"))
    private void nekoration$upgradeLegacyItems(CompoundTag data, int version,
            CallbackInfoReturnable<CompoundTag> callback) {
        LegacyWorldUpgrader.upgradeItemStacks(data);
    }

    @Inject(method = "upgradeChunkTag(Lnet/minecraft/nbt/CompoundTag;I)Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"))
    private void nekoration$finalizeLegacyItems(CompoundTag data, int version,
            CallbackInfoReturnable<CompoundTag> callback) {
        LegacyWorldUpgrader.finalizeItemStacks(callback.getReturnValue());
    }
}
