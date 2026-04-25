package io.devbobcorn.nekoration.registry;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.entities.SeatEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Entity type registrations.
 */
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTER =
            DeferredRegister.create(Registries.ENTITY_TYPE, Nekoration.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<SeatEntity>> SEAT = REGISTER.register("seat",
            () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                    .sized(0.0F, 0.0F)
                    .clientTrackingRange(1)
                    .updateInterval(Integer.MAX_VALUE)
                    .build(Nekoration.MODID + ":seat"));

    private ModEntities() {
    }
}
