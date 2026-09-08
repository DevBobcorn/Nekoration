package io.devbobcorn.nekoration.registry;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.items.PaintingItem;
import io.devbobcorn.nekoration.items.PaletteItem;
import io.devbobcorn.nekoration.items.WallpaperItem;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ModItems {
    public static final DeferredItem<WallpaperItem> WALLPAPER = Nekoration.ITEMS.registerItem(
            "wallpaper", WallpaperItem::new);

    public static final DeferredItem<PaintingItem> PAINTING = Nekoration.ITEMS.registerItem(
            "painting", PaintingItem::new);

    public static final DeferredItem<PaletteItem> PALETTE = Nekoration.ITEMS.registerItem(
            "palette", PaletteItem::new);

    private ModItems() {
    }

    public static void register() {
    }
}
