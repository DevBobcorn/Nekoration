package io.devbobcorn.nekoration.neoforge.xplat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.xplat.NekoRegistrar;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;

/**
 * NeoForge registration backed by {@link DeferredRegister}s.
 */
public final class NeoForgeRegistrar implements NekoRegistrar {
    private final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> registers = new HashMap<>();
    private final List<DeferredRegister<?>> ordered = new ArrayList<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    private DeferredRegister<?> registrarFor(ResourceKey<? extends Registry<?>> registryKey) {
        DeferredRegister<?> existing = registers.get(registryKey);
        if (existing != null) {
            return existing;
        }
        DeferredRegister<?> created = DeferredRegister.create((ResourceKey) registryKey, Nekoration.MODID);
        registers.put(registryKey, created);
        ordered.add(created);
        return created;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T, R extends T> RegistrySupplier<R> register(ResourceKey<Registry<T>> registry, String name,
            Supplier<R> factory) {
        DeferredRegister register = registrarFor((ResourceKey) registry);
        DeferredHolder<T, R> holder = register.register(name, factory);
        return new NeoForgeRegistrySupplier<>(holder);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <M extends net.minecraft.world.inventory.AbstractContainerMenu> RegistrySupplier<net.minecraft.world.inventory.MenuType<M>> menuType(
            String name, MenuTypeFactory<M> factory) {
        return register(Registries.MENU, name,
                () -> net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create(
                        (containerId, inventory, data) -> factory.create(containerId, inventory, data.readBlockPos())));
    }

    /** Binds every DeferredRegister created so far to the mod event bus. */
    public void registerModEventBus(IEventBus modEventBus) {
        for (DeferredRegister<?> register : ordered) {
            register.register(modEventBus);
        }
    }
}
