package io.devbobcorn.nekoration.neoforge.xplat;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;

/**
 * Registry supplier wrapping a NeoForge {@link DeferredHolder}.
 */
final class NeoForgeRegistrySupplier<T> implements RegistrySupplier<T> {
    @SuppressWarnings("rawtypes")
    private final DeferredHolder holder;

    @SuppressWarnings("rawtypes")
    NeoForgeRegistrySupplier(DeferredHolder holder) {
        this.holder = holder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        return (T) holder.get();
    }

    @Override
    public ResourceLocation getId() {
        return holder.getId();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ResourceKey<T> getKey() {
        return (ResourceKey<T>) holder.getKey();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Holder<T> holder() {
        return (Holder<T>) holder;
    }
}
