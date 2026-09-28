package io.devbobcorn.nekoration.registry;

import net.minecraft.world.item.Item;

import io.devbobcorn.nekoration.items.PaintingItem;
import io.devbobcorn.nekoration.items.PaletteItem;
import io.devbobcorn.nekoration.items.TweakItem;
import io.devbobcorn.nekoration.items.WallpaperItem;
import io.devbobcorn.nekoration.xplat.NekoRegistrar;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;

public final class ModItems {
    public static RegistrySupplier<WallpaperItem> WALLPAPER;
    public static RegistrySupplier<PaintingItem> PAINTING;
    public static RegistrySupplier<PaletteItem> PALETTE;

    public static RegistrySupplier<Item> PAW;
    public static RegistrySupplier<TweakItem> PAW_UP;
    public static RegistrySupplier<TweakItem> PAW_DOWN;
    public static RegistrySupplier<TweakItem> PAW_LEFT;
    public static RegistrySupplier<TweakItem> PAW_RIGHT;
    public static RegistrySupplier<TweakItem> PAW_NEAR;
    public static RegistrySupplier<TweakItem> PAW_FAR;
    public static RegistrySupplier<TweakItem> PAW_15;
    public static RegistrySupplier<TweakItem> PAW_90;
    public static RegistrySupplier<Item> ARROW_HINT;

    private ModItems() {
    }

    public static void register(NekoRegistrar registrar) {
        WALLPAPER = registrar.item("wallpaper", () -> new WallpaperItem(new Item.Properties()));
        PAINTING = registrar.item("painting", () -> new PaintingItem(new Item.Properties()));
        PALETTE = registrar.item("palette", () -> new PaletteItem(new Item.Properties()));

        PAW = registrar.item("paw", () -> new Item(new Item.Properties()));
        PAW_UP = registrar.item("paw_up",
                () -> new TweakItem(new Item.Properties(), TweakItem.Aspect.PosY, 1));
        PAW_DOWN = registrar.item("paw_down",
                () -> new TweakItem(new Item.Properties(), TweakItem.Aspect.PosY, -1));
        PAW_LEFT = registrar.item("paw_left",
                () -> new TweakItem(new Item.Properties(), TweakItem.Aspect.PosX, 1));
        PAW_RIGHT = registrar.item("paw_right",
                () -> new TweakItem(new Item.Properties(), TweakItem.Aspect.PosX, -1));
        PAW_NEAR = registrar.item("paw_near",
                () -> new TweakItem(new Item.Properties(), TweakItem.Aspect.PosZ, -1));
        PAW_FAR = registrar.item("paw_far",
                () -> new TweakItem(new Item.Properties(), TweakItem.Aspect.PosZ, 1));
        PAW_15 = registrar.item("paw_15",
                () -> new TweakItem(new Item.Properties(), TweakItem.Aspect.Rotation, 1));
        PAW_90 = registrar.item("paw_90",
                () -> new TweakItem(new Item.Properties(), TweakItem.Aspect.Rotation, 6));
        ARROW_HINT = registrar.item("arrow_hint", () -> new Item(new Item.Properties()));
    }
}
