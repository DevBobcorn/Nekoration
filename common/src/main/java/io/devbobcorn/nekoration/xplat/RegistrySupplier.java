package io.devbobcorn.nekoration.xplat;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;

/**
 * Loader-agnostic handle to a registered object. Replaces the NeoForge
 * {@code DeferredHolder}/{@code DeferredItem}/{@code DeferredBlock} types.
 */
public interface RegistrySupplier<T> extends Supplier<T> {
    ResourceLocation getId();

    ResourceKey<T> getKey();

    Holder<T> holder();

    /**
     * Same as {@link #get()} but allows returning null for very early lookups.
     */
    @Nullable
    default T getOrNull() {
        return get();
    }
}
