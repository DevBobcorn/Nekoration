package io.devbobcorn.nekoration.mixin;

import java.nio.file.Path;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.devbobcorn.nekoration.world.upgrade.LegacyWorldUpgrader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.LevelStorageSource;

@Mixin(LevelStorageSource.class)
abstract class LevelStorageSourceMixin {
    @Inject(method = "readLevelDataTagRaw", at = @At("RETURN"))
    private static void nekoration$upgradeLegacyItems(Path levelPath,
            CallbackInfoReturnable<CompoundTag> callback) {
        LegacyWorldUpgrader.upgradeItemStacks(callback.getReturnValue());
    }
}
