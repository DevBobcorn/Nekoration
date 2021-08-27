package com.devbobcorn.nekoration.entities;

import com.devbobcorn.nekoration.Nekoration;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

@EventBusSubscriber(modid = Nekoration.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntityType {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, Nekoration.MODID);

    public static final EntityType<PaintingEntity> PAINTING_TYPE = EntityType.Builder.<PaintingEntity>of(PaintingEntity::new, MobCategory.MISC).setCustomClientFactory(PaintingEntity::new).sized(0.5F, 0.5F).updateInterval(Integer.MAX_VALUE).build(Nekoration.MODID + ":painting");
    public static final RegistryObject<EntityType<PaintingEntity>> $PAINTING_TYPE = ENTITY_TYPES.register("painting", () -> PAINTING_TYPE);

    public static final EntityType<WallPaperEntity> WALLPAPER_TYPE = EntityType.Builder.<WallPaperEntity>of(WallPaperEntity::new, MobCategory.MISC).setCustomClientFactory(WallPaperEntity::new).sized(0.5F, 0.5F).updateInterval(Integer.MAX_VALUE).build(Nekoration.MODID + ":wallpaper");
    public static final RegistryObject<EntityType<WallPaperEntity>> $WALLPAPER_TYPE = ENTITY_TYPES.register("wallpaper", () -> WALLPAPER_TYPE);

    public static final EntityType<SeatEntity> SEAT_TYPE = EntityType.Builder.<SeatEntity>of((type, world) -> new SeatEntity(world), MobCategory.MISC).setCustomClientFactory((spawnEntity, world) -> new SeatEntity(world)).sized(0.0F, 0.0F).updateInterval(Integer.MAX_VALUE).build(Nekoration.MODID + ":seat");
    public static final RegistryObject<EntityType<SeatEntity>> $SEAT_TYPE = ENTITY_TYPES.register("seat", () -> SEAT_TYPE);

    @SubscribeEvent
    public static void registerPlacements(FMLCommonSetupEvent event) {
        /*
        EntitySpawnPlacementRegistry.register(CHOX, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
            (entityType, world, reason, pos, random) -> (world.getBlockState(pos.below()).getMaterial() == Material.GRASS && world.getMaxLocalRawBrightness(pos, 0) > 8));
        */
        //LOGGER.info("Entity Placement Registered.");
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        //event.put($CHOX, MobEntity.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.MAX_HEALTH, 10).add(Attributes.ARMOR, 0).add(Attributes.ATTACK_DAMAGE, 3).build());
        //LOGGER.info("Entity Attributes Registered.");
    }
}
