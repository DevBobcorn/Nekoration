package io.devbobcorn.nekoration.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.devbobcorn.nekoration.world.upgrade.LegacyWorldUpgrader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.PlayerDataStorage;

@Mixin(PlayerDataStorage.class)
abstract class PlayerDataStorageMixin {
    @Inject(method = "load(Lnet/minecraft/world/entity/player/Player;Ljava/lang/String;)Ljava/util/Optional;", at = @At("RETURN"))
    private void nekoration$upgradeLegacyItems(Player player, String suffix,
            CallbackInfoReturnable<Optional<CompoundTag>> callback) {
        callback.getReturnValue().ifPresent(LegacyWorldUpgrader::upgradeItemStacks);
    }
}
