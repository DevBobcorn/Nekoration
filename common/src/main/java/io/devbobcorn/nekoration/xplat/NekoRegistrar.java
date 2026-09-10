package io.devbobcorn.nekoration.xplat;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Loader-agnostic registration facade. Each platform provides an
 * implementation whose {@link #register} enqueues entries into its own
 * registration machinery (NeoForge: {@code DeferredRegister}s, Fabric:
 * {@code Registry.register}).
 */
public interface NekoRegistrar {
    <T, R extends T> RegistrySupplier<R> register(ResourceKey<net.minecraft.core.Registry<T>> registry, String name,
            Supplier<R> factory);

    default <B extends Block> RegistrySupplier<B> block(String name, Supplier<B> factory) {
        return register(Registries.BLOCK, name, factory);
    }

    default <I extends Item> RegistrySupplier<I> item(String name, Supplier<I> factory) {
        return register(Registries.ITEM, name, factory);
    }

    /**
     * Creates an extended menu type whose extra opening data is a
     * {@link BlockPos}. Callers open it via {@link NekoPlatform#openEaselMenu}.
     */
    <M extends AbstractContainerMenu> RegistrySupplier<MenuType<M>> menuType(String name, MenuTypeFactory<M> factory);

    /** Creates the menu on both sides from the opening position. */
    @FunctionalInterface
    interface MenuTypeFactory<M extends AbstractContainerMenu> {
        M create(int containerId, Inventory playerInventory, BlockPos pos);
    }
}
