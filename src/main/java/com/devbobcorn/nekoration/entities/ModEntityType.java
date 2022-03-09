package com.devbobcorn.nekoration.entities;

import com.devbobcorn.nekoration.Nekoration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

@EventBusSubscriber(modid = Nekoration.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntityType {
    public static final Logger LOGGER = LogManager.getLogger();

    public static EntityType<PaintingEntity> PAINTING_TYPE;

    public static EntityType<WallPaperEntity> WALLPAPER_TYPE;

    public static EntityType<SeatEntity> SEAT_TYPE;

    @SubscribeEvent
    public static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
        LOGGER.debug("Manually registering entity types");

        PAINTING_TYPE = EntityType.Builder.<PaintingEntity>of(PaintingEntity::new, MobCategory.MISC).setCustomClientFactory(PaintingEntity::new).sized(0.5F, 0.5F).updateInterval(Integer.MAX_VALUE).build(Nekoration.MODID + ":painting");
        WALLPAPER_TYPE = EntityType.Builder.<WallPaperEntity>of(WallPaperEntity::new, MobCategory.MISC).setCustomClientFactory(WallPaperEntity::new).sized(0.5F, 0.5F).updateInterval(Integer.MAX_VALUE).build(Nekoration.MODID + ":wallpaper");
        SEAT_TYPE = EntityType.Builder.<SeatEntity>of((type, world) -> new SeatEntity(world), MobCategory.MISC).setCustomClientFactory((spawnEntity, world) -> new SeatEntity(world)).sized(0.0F, 0.0F).updateInterval(Integer.MAX_VALUE).build(Nekoration.MODID + ":seat");

        var reg = event.getRegistry();
        reg.register(PAINTING_TYPE.setRegistryName(Nekoration.MODID, "painting"));
        reg.register(WALLPAPER_TYPE.setRegistryName(Nekoration.MODID, "wallpaper"));
        reg.register(SEAT_TYPE.setRegistryName(Nekoration.MODID, "seat"));
    }

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
