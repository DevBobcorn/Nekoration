package io.devbobcorn.nekoration.registry;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.items.WallpaperItem;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ModItems {
    public static final DeferredItem<WallpaperItem> WALLPAPER = Nekoration.ITEMS.registerItem(
            "wallpaper", WallpaperItem::new);

    private ModItems() {
    }

    public static void register() {
    }
}
