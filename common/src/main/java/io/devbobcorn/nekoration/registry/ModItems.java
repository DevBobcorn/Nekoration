package io.devbobcorn.nekoration.registry;

import net.minecraft.world.item.Item;

import io.devbobcorn.nekoration.items.PaintingItem;
import io.devbobcorn.nekoration.items.PaletteItem;
import io.devbobcorn.nekoration.items.WallpaperItem;
import io.devbobcorn.nekoration.xplat.NekoRegistrar;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;

public final class ModItems {
    public static RegistrySupplier<WallpaperItem> WALLPAPER;
    public static RegistrySupplier<PaintingItem> PAINTING;
    public static RegistrySupplier<PaletteItem> PALETTE;

    private ModItems() {
    }

    public static void register(NekoRegistrar registrar) {
        WALLPAPER = registrar.item("wallpaper", () -> new WallpaperItem(new Item.Properties()));
        PAINTING = registrar.item("painting", () -> new PaintingItem(new Item.Properties()));
        PALETTE = registrar.item("palette", () -> new PaletteItem(new Item.Properties()));
    }
}
