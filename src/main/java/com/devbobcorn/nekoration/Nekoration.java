package com.devbobcorn.nekoration;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.blocks.containers.ModMenuType;
import com.devbobcorn.nekoration.blocks.entities.ModBlockEntityType;
import com.devbobcorn.nekoration.entities.ModEntityType;
import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.network.C2SUpdateEaselMenuData;
import com.devbobcorn.nekoration.network.C2SUpdatePaintingData;
import com.devbobcorn.nekoration.network.C2SUpdatePaintingSize;
import com.devbobcorn.nekoration.network.C2SUpdatePaletteData;
import com.devbobcorn.nekoration.network.ModPacketHandler;
import com.devbobcorn.nekoration.network.S2CUpdateEaselMenuData;
import com.devbobcorn.nekoration.network.S2CUpdatePaintingData;
import com.devbobcorn.nekoration.recipes.ModRecipes;

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
		
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		ModBlocks.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ModEntityType.ENTITY_TYPES.register(modEventBus);
		ModMenuType.MENU_TYPES.register(modEventBus);
		ModBlockEntityType.TILE_ENTITY_TYPES.register(modEventBus);
		ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);

		// Register Configs
		//modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
		//modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);

		int networkId = 0;
		ModPacketHandler.CHANNEL.registerMessage(networkId++,
			C2SUpdateEaselMenuData.class,
			C2SUpdateEaselMenuData::encode,
			C2SUpdateEaselMenuData::decode,
			C2SUpdateEaselMenuData::handle
		);
		ModPacketHandler.CHANNEL.registerMessage(networkId++,
			S2CUpdateEaselMenuData.class,
			S2CUpdateEaselMenuData::encode,
			S2CUpdateEaselMenuData::decode,
			S2CUpdateEaselMenuData::handle
		);
		ModPacketHandler.CHANNEL.registerMessage(networkId++,
			C2SUpdatePaletteData.class,
			C2SUpdatePaletteData::encode,
			C2SUpdatePaletteData::decode,
			C2SUpdatePaletteData::handle
		);
		ModPacketHandler.CHANNEL.registerMessage(networkId++,
			C2SUpdatePaintingData.class,
			C2SUpdatePaintingData::encode,
			C2SUpdatePaintingData::decode,
			C2SUpdatePaintingData::handle
		);
		ModPacketHandler.CHANNEL.registerMessage(networkId++,
			C2SUpdatePaintingSize.class,
			C2SUpdatePaintingSize::encode,
			C2SUpdatePaintingSize::decode,
			C2SUpdatePaintingSize::handle
		);
		ModPacketHandler.CHANNEL.registerMessage(networkId++,
			S2CUpdatePaintingData.class,
			S2CUpdatePaintingData::encode,
			S2CUpdatePaintingData::decode,
			S2CUpdatePaintingData::handle
		);
    }
}
