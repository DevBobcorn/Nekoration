package io.devbobcorn.nekoration.fabric.xplat;

import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.xplat.NekoRegistrar;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;

/**
 * Fabric registration backed by {@link Registry#registerForHolder}.
 */
public final class FabricRegistrar implements NekoRegistrar {
    @Override
    @SuppressWarnings("unchecked")
    public <T, R extends T> RegistrySupplier<R> register(ResourceKey<Registry<T>> registry, String name,
            Supplier<R> factory) {
        Registry<T> reg = (Registry<T>) BuiltInRegistries.REGISTRY.get(registry.location());
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, name);
        R value = factory.get();
        Holder<R> holder = Registry.registerForHolder((Registry<R>) reg, id, value);
        ResourceKey<R> key = (ResourceKey<R>) (ResourceKey<?>) ResourceKey.create(registry, id);
        return new FabricRegistrySupplier<>(id, key, holder);
    }

    @Override
    public <M extends net.minecraft.world.inventory.AbstractContainerMenu> RegistrySupplier<net.minecraft.world.inventory.MenuType<M>> menuType(
            String name, MenuTypeFactory<M> factory) {
        return register(net.minecraft.core.registries.Registries.MENU, name,
                () -> new net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType<>(
                        (syncId, inventory, pos) -> factory.create(syncId, inventory, pos),
                        net.minecraft.core.BlockPos.STREAM_CODEC));
    }
}
