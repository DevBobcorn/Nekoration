package com.devbobcorn.nekoration.client.event;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.DyeableBlock;
import com.devbobcorn.nekoration.blocks.DyeableDoorBlock;
import com.devbobcorn.nekoration.blocks.DyeableHorizontalConnectBlock;
import com.devbobcorn.nekoration.blocks.DyeableVerticalConnectBlock;
import com.devbobcorn.nekoration.blocks.HalfTimberBlock;
import com.devbobcorn.nekoration.blocks.HalfTimberPillarBlock;
import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.blocks.WindowBlock;
import com.devbobcorn.nekoration.blocks.entities.ModBlockEntityType;
import com.devbobcorn.nekoration.items.DyeableBlockItem;
import com.devbobcorn.nekoration.items.DyeableWoodenBlockItem;
import com.devbobcorn.nekoration.items.HalfTimberBlockItem;
import com.devbobcorn.nekoration.client.rendering.blockentities.EaselMenuRenderer;
import com.devbobcorn.nekoration.client.rendering.entities.PaintingRenderer;
import com.devbobcorn.nekoration.entities.ModEntityType;
import com.devbobcorn.nekoration.client.rendering.blockentities.CustomRenderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

// Client-Side Only Things...
@EventBusSubscriber(modid = Nekoration.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEventSubscriber {
	private static final Logger LOGGER = LogManager.getLogger(Nekoration.MODID + " Client Mod Event Subscriber");

	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		LOGGER.info("Client Side Setup.");

		RenderType transparentRenderType = RenderType.cutoutMipped();
		// RenderType cutoutRenderType = RenderType.cutout();
		RenderType translucentRenderType = RenderType.translucent();

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.HALF_TIMBER_P0.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.HALF_TIMBER_P1.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.HALF_TIMBER_P2.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.HALF_TIMBER_P3.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.HALF_TIMBER_P4.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.HALF_TIMBER_P5.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.HALF_TIMBER_P6.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.HALF_TIMBER_P7.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.HALF_TIMBER_P8.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.HALF_TIMBER_P9.get(), translucentRenderType);

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.HALF_TIMBER_PILLAR_P0.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.HALF_TIMBER_PILLAR_P1.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.HALF_TIMBER_PILLAR_P2.get(), translucentRenderType);

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.WINDOW_ARCH.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.WINDOW_CROSS.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.WINDOW_SHADE.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.WINDOW_LANCET.get(), transparentRenderType);

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.WINDOW_PLANT.get(), transparentRenderType);

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLOWER_BASKET_IRON.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLOWER_BASKET_GOLD.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLOWER_BASKET_QUARTZ.get(), transparentRenderType);

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.DOOR_1.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.DOOR_2.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.DOOR_3.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.DOOR_TALL_1.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.DOOR_TALL_2.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.DOOR_TALL_3.get(), translucentRenderType);

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.AWNING_PURE.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.AWNING_PURE_SHORT.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.AWNING_STRIPE.get(), translucentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.AWNING_STRIPE_SHORT.get(), translucentRenderType);

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.LAMP_POST_IRON.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.LAMP_POST_GOLD.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.LAMP_POST_QUARTZ.get(), transparentRenderType);

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.CANDLE_HOLDER_IRON.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.CANDLE_HOLDER_GOLD.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.CANDLE_HOLDER_QUARTZ.get(), transparentRenderType);

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.PHONOGRAPH.get(), transparentRenderType);

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.PUMPKIN_TABLE.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.PUMPKIN_CHAIR.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.OAK_TABLE.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.OAK_CHAIR.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.JUNGLE_TABLE.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.JUNGLE_CHAIR.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.ACACIA_TABLE.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.ACACIA_CHAIR.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.BIRCH_TABLE.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.BIRCH_CHAIR.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.DARK_OAK_TABLE.get(), transparentRenderType);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.DARK_OAK_CHAIR.get(), transparentRenderType);

		LOGGER.info("Block Render Types Registered.");

		BlockEntityRenderers.register(ModBlockEntityType.EASEL_MENU_TYPE.get(), EaselMenuRenderer::new);
		BlockEntityRenderers.register(ModBlockEntityType.CUSTOM_TYPE.get(), CustomRenderer::new);

		LOGGER.info("BlockEntities' Renderer Bound.");

		EntityRenderers.register(ModEntityType.PAINTING_TYPE, PaintingRenderer::new);

		LOGGER.info("Then Entities' Renderer Bound.");

		MinecraftForge.EVENT_BUS.register(new CreativeInventoryEvents());

		LOGGER.info("CreativeInv Events Registered.");

		// we need to attach the fullness PropertyOverride to the Item, but there are
		// two things to be careful of:
		// 1) We should do this on a client installation only, not on a DedicatedServer
		// installation. Hence we need to use
		// FMLClientSetupEvent.
		// 2) FMLClientSetupEvent is multithreaded but ItemModelsProperties is not
		// multithread-safe. So we need to use the enqueueWork method,
		// which lets us register a function for synchronous execution in the main
		// thread after the parallel processing is completed
		event.enqueueWork(ClientModEventSubscriber::registerPropertyOverrides);

		LOGGER.info("Property Overrides Registered.");

		/*
		ScreenManager.register(ModMenuType.EASEL_MENU_TYPE.get(), EaselMenuScreen::new);

		LOGGER.info("Nekoration Screens Registered.");
		*/
	}

	public static void registerPropertyOverrides() {
		ItemProperties.register(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "awning_pure")), new ResourceLocation("color"), DyeableBlockItem::getColorPropertyOverride);
		ItemProperties.register(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "awning_stripe")), new ResourceLocation("color"), DyeableBlockItem::getColorPropertyOverride);
		ItemProperties.register(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "awning_pure_short")), new ResourceLocation("color"), DyeableBlockItem::getColorPropertyOverride);
		ItemProperties.register(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "awning_stripe_short")), new ResourceLocation("color"), DyeableBlockItem::getColorPropertyOverride);
		// use lambda function to link the NBT color value to a suitable property override value
	}

	@SubscribeEvent
	public static void registerBlockColors(ColorHandlerEvent.Block event) {
		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableBlock))
				return NekoColors.getStoneColorOrLightGray(7);
			return NekoColors.getStoneColorOrLightGray(state.getValue(DyeableBlock.COLOR));
		}, ModBlocks.STONE_BASE_BOTTOM.get(), ModBlocks.STONE_FRAME_BOTTOM.get(), ModBlocks.STONE_PILLAR_BOTTOM.get(), ModBlocks.STONE_LAYERED.get(),
				ModBlocks.STONE_POT.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableBlock))
				return NekoColors.getNekoColorOrWhite(14);
			return NekoColors.getNekoColorOrWhite(state.getValue(DyeableBlock.COLOR));
		}, ModBlocks.CANDLE_HOLDER_IRON.get(), ModBlocks.CANDLE_HOLDER_GOLD.get(), ModBlocks.CANDLE_HOLDER_QUARTZ.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableBlock))
				return NekoColors.getWoodenColorOrBrown(2);
			return NekoColors.getWoodenColorOrBrown(state.getValue(DyeableBlock.COLOR));
		}, ModBlocks.EASEL_MENU.get(), ModBlocks.EASEL_MENU_WHITE.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableVerticalConnectBlock))
				return NekoColors.getStoneColorOrLightGray(7);
			return NekoColors.getStoneColorOrLightGray(state.getValue(DyeableVerticalConnectBlock.COLOR));
		}, ModBlocks.STONE_BASE.get(), ModBlocks.STONE_FRAME.get(), ModBlocks.STONE_PILLAR.get(),
				ModBlocks.STONE_DORIC.get(), ModBlocks.STONE_IONIC.get(), ModBlocks.STONE_CORINTHIAN.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableHorizontalConnectBlock))
				return NekoColors.getStoneColorOrLightGray(7);
			return NekoColors.getStoneColorOrLightGray(state.getValue(DyeableHorizontalConnectBlock.COLOR));
		}, ModBlocks.WINDOW_SILL.get(), ModBlocks.WINDOW_TOP.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof HalfTimberBlock))
				return NekoColors.getWoodenColorOrBrown(2);
			return (tintIndex == 0) ? NekoColors.getWoodenColorOrBrown(state.getValue(HalfTimberBlock.COLOR0)) : NekoColors.getNekoColorOrWhite(state.getValue(HalfTimberBlock.COLOR1));
		}, ModBlocks.HALF_TIMBER_P0.get(), ModBlocks.HALF_TIMBER_P1.get(), ModBlocks.HALF_TIMBER_P2.get(),
				ModBlocks.HALF_TIMBER_P3.get(), ModBlocks.HALF_TIMBER_P4.get(), ModBlocks.HALF_TIMBER_P5.get(),
				ModBlocks.HALF_TIMBER_P6.get(), ModBlocks.HALF_TIMBER_P7.get(), ModBlocks.HALF_TIMBER_P8.get(),
				ModBlocks.HALF_TIMBER_P9.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof HalfTimberPillarBlock))
				return (tintIndex == 0) ? NekoColors.getWoodenColorOrBrown(2) : NekoColors.getNekoColorOrWhite(14);
			return (tintIndex == 0) ? NekoColors.getWoodenColorOrBrown(state.getValue(HalfTimberBlock.COLOR0)) : NekoColors.getNekoColorOrWhite(state.getValue(HalfTimberBlock.COLOR1));
		}, ModBlocks.HALF_TIMBER_PILLAR_P0.get(), ModBlocks.HALF_TIMBER_PILLAR_P1.get(),
				ModBlocks.HALF_TIMBER_PILLAR_P2.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableBlock))
				return (tintIndex == 0) ? NekoColors.EnumNekoColor.PURPLE.getColor() : NekoColors.getNekoColorOrWhite(14);
			return (tintIndex == 0) ? BiomeColors.getAverageFoliageColor(view, pos) : NekoColors.getNekoColorOrWhite(state.getValue(DyeableBlock.COLOR));
		}, ModBlocks.WINDOW_PLANT.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof WindowBlock))
				return NekoColors.getWoodenColorOrBrown(2);
			return NekoColors.getWoodenColorOrBrown(state.getValue(WindowBlock.COLOR));
		}, ModBlocks.WINDOW_ARCH.get(), ModBlocks.WINDOW_CROSS.get(), ModBlocks.WINDOW_SHADE.get(),
				ModBlocks.WINDOW_LANCET.get());

		event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableDoorBlock))
				return NekoColors.getNekoColorOrWhite(14);
			return NekoColors.getNekoColorOrWhite(state.getValue(DyeableDoorBlock.COLOR));
		}, ModBlocks.DOOR_1.get(), ModBlocks.DOOR_2.get(), ModBlocks.DOOR_3.get(), ModBlocks.DOOR_TALL_1.get(),
				ModBlocks.DOOR_TALL_2.get(), ModBlocks.DOOR_TALL_3.get());

		LOGGER.info("Block Colors Registered.");
	}

	@SubscribeEvent
	public static void registerItemColors(ColorHandlerEvent.Item event) {
		// Default Light Gray:
		event.getItemColors().register((stack, tintIndex) -> {
			return NekoColors.getStoneFromNeko(DyeableBlockItem.getColor(stack)).getColor();
		},
		ModBlocks.STONE_BASE_BOTTOM.get().asItem(), ModBlocks.STONE_FRAME_BOTTOM.get().asItem(),
		ModBlocks.STONE_PILLAR_BOTTOM.get().asItem(), ModBlocks.STONE_BASE.get().asItem(),
		ModBlocks.STONE_FRAME.get().asItem(), ModBlocks.STONE_PILLAR.get().asItem(),
		ModBlocks.STONE_DORIC.get().asItem(), ModBlocks.STONE_IONIC.get().asItem(),
		ModBlocks.STONE_CORINTHIAN.get().asItem(), ModBlocks.WINDOW_SILL.get().asItem(),
		ModBlocks.WINDOW_TOP.get().asItem(), ModBlocks.STONE_LAYERED.get().asItem(), ModBlocks.STONE_POT.get().asItem());

		// Default White:
		event.getItemColors().register((stack, tintIndex) -> {
			return NekoColors.getStoneFromNeko(DyeableBlockItem.getColor(stack)).getColor();
		},
		ModBlocks.CANDLE_HOLDER_IRON.get().asItem(), ModBlocks.CANDLE_HOLDER_GOLD.get().asItem(),
		ModBlocks.CANDLE_HOLDER_QUARTZ.get().asItem());


		// Default White, Flowering Plants:
		event.getItemColors().register((stack, tintIndex) -> {
			switch (tintIndex) {
				case 0:
				default:
					return NekoColors.EnumNekoColor.PURPLE.getColor();
                case 1:
					return DyeableBlockItem.getColor(stack).getColor();
			}
		},
		ModBlocks.WINDOW_PLANT.get().asItem());

		// Default Wooden Brown:
		event.getItemColors().register((stack, tintIndex) -> {
			return DyeableWoodenBlockItem.getColor(stack).getColor();
		}, 
		ModBlocks.WINDOW_ARCH.get().asItem(), ModBlocks.WINDOW_CROSS.get().asItem(),
		ModBlocks.WINDOW_SHADE.get().asItem(), ModBlocks.WINDOW_LANCET.get().asItem(),
		ModBlocks.EASEL_MENU.get().asItem(), ModBlocks.EASEL_MENU_WHITE.get().asItem());

		// Default Wooden Brown, BiDyeable:
		event.getItemColors().register((stack, tintIndex) -> {
			switch (tintIndex) {
				case 0:
				default:
					return HalfTimberBlockItem.getColor0(stack).getColor();
                case 1:
					return HalfTimberBlockItem.getColor1(stack).getColor();
			}
		},
		ModBlocks.HALF_TIMBER_P0.get().asItem(), ModBlocks.HALF_TIMBER_P1.get().asItem(),
		ModBlocks.HALF_TIMBER_P2.get().asItem(), ModBlocks.HALF_TIMBER_P3.get().asItem(),
		ModBlocks.HALF_TIMBER_P4.get().asItem(), ModBlocks.HALF_TIMBER_P5.get().asItem(),
		ModBlocks.HALF_TIMBER_P6.get().asItem(), ModBlocks.HALF_TIMBER_P7.get().asItem(),
		ModBlocks.HALF_TIMBER_P8.get().asItem(), ModBlocks.HALF_TIMBER_P9.get().asItem(),
		ModBlocks.HALF_TIMBER_PILLAR_P0.get().asItem(), ModBlocks.HALF_TIMBER_PILLAR_P1.get().asItem(),
		ModBlocks.HALF_TIMBER_PILLAR_P2.get().asItem());

		LOGGER.info("Item Colors Registered.");
	}
}
