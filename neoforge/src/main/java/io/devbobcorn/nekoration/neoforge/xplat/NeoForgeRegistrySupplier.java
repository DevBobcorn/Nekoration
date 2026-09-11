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
    private final DeferredHolder<T, ? extends T> holder;

    NeoForgeRegistrySupplier(DeferredHolder<T, ? extends T> holder) {
        this.holder = holder;
    }

    @Override
    public T get() {
        return holder.get();
    }

    @Override
    public ResourceLocation getId() {
        return holder.getId();
    }

    @Override
    public ResourceKey<T> getKey() {
        return holder.getKey();
    }

    @Override
    public Holder<T> holder() {
        // DeferredHolder<T, ? extends T> is a Holder<T> already; the cast is
        // only needed because of the wildcard.
        return (Holder<T>) holder;
    }
}
