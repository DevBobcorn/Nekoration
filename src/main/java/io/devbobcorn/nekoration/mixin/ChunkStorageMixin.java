package io.devbobcorn.nekoration.mixin;

import java.util.Optional;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.MapCodec;

import io.devbobcorn.nekoration.world.upgrade.LegacyWorldUpgrader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.storage.DimensionDataStorage;

@Mixin(ChunkStorage.class)
abstract class ChunkStorageMixin {
    @Inject(method = "upgradeChunkTag", at = @At("HEAD"))
    private void nekoration$upgradeLegacyData(ResourceKey<Level> levelKey,
            Supplier<DimensionDataStorage> storage, CompoundTag chunkData,
            Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> chunkGeneratorKey,
            CallbackInfoReturnable<CompoundTag> callback) {
        LegacyWorldUpgrader.upgradeChunk(chunkData);
    }
}
