package com.devbobcorn.nekoration;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.blocks.containers.ModContainerType;
import com.devbobcorn.nekoration.blocks.entities.ModTileEntityType;
import com.devbobcorn.nekoration.exp.ExpClientOnly;
import com.devbobcorn.nekoration.exp.ExpCommon;
import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.network.C2SUpdateEaselMenuTexts;
import com.devbobcorn.nekoration.network.C2SUpdatePaletteData;
import com.devbobcorn.nekoration.network.ModPacketHandler;
import com.devbobcorn.nekoration.network.S2CUpdateEaselMenuItems;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Nekoration.MODID)
public class Nekoration {
    public static final String MODID = "nekoration";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public Nekoration() {
        LOGGER.info("Meow~~ Miaow~~~"); //Translation: Hello World!
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

		//final ModLoadingContext modLoadingContext = ModLoadingContext.get();
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		// Experimental stuffs...
		final ExpClientOnly ExpClient = new ExpClientOnly(modEventBus);
		final ExpCommon ExpCommon = new ExpCommon();

		ExpCommon.registerCommonEvents(modEventBus);
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ExpClient::registerClientOnlyEvents);

		ModBlocks.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		//ModContainerType.CONTAINER_TYPES.register(modEventBus);
		//ModEntityTypes.ENTITY_TYPES.register(modEventBus);
		ModContainerType.CONTAINER_TYPES.register(modEventBus);
		ModTileEntityType.TILE_ENTITY_TYPES.register(modEventBus);

		// Register Configs
		//modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
		//modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);

		int networkId = 0;
		ModPacketHandler.CHANNEL.registerMessage(networkId++,
			C2SUpdateEaselMenuTexts.class,
			C2SUpdateEaselMenuTexts::encode,
			C2SUpdateEaselMenuTexts::decode,
			C2SUpdateEaselMenuTexts::handle
		);
		ModPacketHandler.CHANNEL.registerMessage(networkId++,
			S2CUpdateEaselMenuItems.class,
			S2CUpdateEaselMenuItems::encode,
			S2CUpdateEaselMenuItems::decode,
			S2CUpdateEaselMenuItems::handle
		);
		ModPacketHandler.CHANNEL.registerMessage(networkId++,
			C2SUpdatePaletteData.class,
			C2SUpdatePaletteData::encode,
			C2SUpdatePaletteData::decode,
			C2SUpdatePaletteData::handle
		);
    }
}
