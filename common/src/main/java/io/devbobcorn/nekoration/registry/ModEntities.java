package io.devbobcorn.nekoration.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.xplat.NekoRegistrar;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;
import io.devbobcorn.nekoration.entities.PaintingEntity;
import io.devbobcorn.nekoration.entities.SeatEntity;
import io.devbobcorn.nekoration.entities.WallpaperEntity;

/**
 * Entity type registrations.
 */
public final class ModEntities {
    public static RegistrySupplier<EntityType<SeatEntity>> SEAT;
    public static RegistrySupplier<EntityType<PaintingEntity>> PAINTING;
    public static RegistrySupplier<EntityType<WallpaperEntity>> WALLPAPER;

    private ModEntities() {
    }

    public static void register(NekoRegistrar registrar) {
        SEAT = registrar.register(Registries.ENTITY_TYPE, "seat",
                () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                        .sized(0.0F, 0.0F)
                        .clientTrackingRange(1)
                        .updateInterval(Integer.MAX_VALUE)
                        .build(Nekoration.MODID + ":seat"));
        PAINTING = registrar.register(Registries.ENTITY_TYPE, "painting",
                () -> EntityType.Builder.<PaintingEntity>of(PaintingEntity::new, MobCategory.MISC)
                        .sized(0.5F, 0.5F)
                        .clientTrackingRange(10)
                        .updateInterval(Integer.MAX_VALUE)
                        .build(Nekoration.MODID + ":painting"));
        WALLPAPER = registrar.register(Registries.ENTITY_TYPE, "wallpaper",
                () -> EntityType.Builder.<WallpaperEntity>of(WallpaperEntity::new, MobCategory.MISC)
                        .sized(0.875F, 2.0F)
                        .clientTrackingRange(10)
                        .updateInterval(Integer.MAX_VALUE)
                        .build(Nekoration.MODID + ":wallpaper"));
    }
}
