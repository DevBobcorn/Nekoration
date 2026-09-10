package io.devbobcorn.nekoration.fabric.xplat;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;

/**
 * Immutable registry supplier for Fabric registrations.
 */
record FabricRegistrySupplier<T>(ResourceLocation id, ResourceKey<T> key, Holder<T> holder)
        implements RegistrySupplier<T> {
    @Override
    public T get() {
        return holder.value();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public ResourceKey<T> getKey() {
        return key;
    }

    @Override
    public Holder<T> holder() {
        return holder;
    }
}
