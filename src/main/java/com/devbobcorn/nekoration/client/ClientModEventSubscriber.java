package com.devbobcorn.nekoration.client;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.api.distmarker.Dist;

// Client-Side Only Things...
@EventBusSubscriber(modid = Nekoration.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEventSubscriber {
    private static final Logger LOGGER = LogManager.getLogger(Nekoration.MODID + " Client Mod Event Subscriber");

    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        LOGGER.info("Client Side Setup.");
        RenderType transparentRenderType = RenderType.cutoutMipped();
        //RenderType cutoutRenderType = RenderType.cutout();
        RenderType translucentRenderType = RenderType.translucent();

        RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P0.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P1.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P2.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P3.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P4.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P5.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P6.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P7.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P8.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_P9.get(), translucentRenderType);

        RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_PILLAR_P0.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_PILLAR_P1.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.HALF_TIMBER_PILLAR_P2.get(), translucentRenderType);

        RenderTypeLookup.setRenderLayer(ModBlocks.WINDOW_ARCH.get(), transparentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.WINDOW_CROSS.get(), transparentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.WINDOW_SHADE.get(), transparentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.WINDOW_LANCET.get(), transparentRenderType);

        RenderTypeLookup.setRenderLayer(ModBlocks.DOOR_1.get(), transparentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.DOOR_2.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.DOOR_3.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.DOOR_TALL_1.get(), transparentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.DOOR_TALL_2.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.DOOR_TALL_3.get(), translucentRenderType);

        RenderTypeLookup.setRenderLayer(ModBlocks.AWNING_PURE.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.AWNING_PURE_SHORT.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.AWNING_STRIPE.get(), translucentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.AWNING_STRIPE_SHORT.get(), translucentRenderType);

        RenderTypeLookup.setRenderLayer(ModBlocks.LAMP_POST_IRON.get(), transparentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.LAMP_POST_GOLD.get(), transparentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.LAMP_POST_QUARTZ.get(), transparentRenderType);

        RenderTypeLookup.setRenderLayer(ModBlocks.CANDLE_HOLDER_IRON.get(), transparentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.CANDLE_HOLDER_GOLD.get(), transparentRenderType);
        RenderTypeLookup.setRenderLayer(ModBlocks.CANDLE_HOLDER_QUARTZ.get(), transparentRenderType);

        LOGGER.info("Block Render Type Registered.");
    }

    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event){
        event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableBlock))
				return NekoColors.getColor(14);
			return NekoColors.getColor(state.getValue(DyeableBlock.COLOR));
		},  ModBlocks.STONE_BASE_BOTTOM.get(),
            ModBlocks.STONE_FRAME_BOTTOM.get(),
            ModBlocks.STONE_PILLAR_BOTTOM.get()
        );

        event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableVerticalConnectBlock))
				return NekoColors.getColor(14);
			return NekoColors.getColor(state.getValue(DyeableVerticalConnectBlock.COLOR));
		},  ModBlocks.STONE_BASE.get(),
            ModBlocks.STONE_FRAME.get(),
            ModBlocks.STONE_PILLAR.get(),
            ModBlocks.STONE_DORIC.get(),
            ModBlocks.STONE_IONIC.get(),
            ModBlocks.STONE_CORINTHIAN.get()
        );

        event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableHorizontalConnectBlock))
				return NekoColors.getColor(14);
			return NekoColors.getColor(state.getValue(DyeableHorizontalConnectBlock.COLOR));
        },
            ModBlocks.WINDOW_SILL.get(),
            ModBlocks.WINDOW_TOP.get()
        );

        event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof HalfTimberBlock))
				return NekoColors.getColor(14);
			return NekoColors.getColor(state.getValue(HalfTimberBlock.COLOR));
		},  ModBlocks.HALF_TIMBER_P0.get(),
            ModBlocks.HALF_TIMBER_P1.get(),
            ModBlocks.HALF_TIMBER_P2.get(),
            ModBlocks.HALF_TIMBER_P3.get(),
            ModBlocks.HALF_TIMBER_P4.get(),
            ModBlocks.HALF_TIMBER_P5.get(),
            ModBlocks.HALF_TIMBER_P6.get(),
            ModBlocks.HALF_TIMBER_P7.get(),
            ModBlocks.HALF_TIMBER_P8.get(),
            ModBlocks.HALF_TIMBER_P9.get()
        );

        event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof HalfTimberPillarBlock))
				return NekoColors.getColor(14);
			return NekoColors.getColor(state.getValue(HalfTimberPillarBlock.COLOR));
		},  ModBlocks.HALF_TIMBER_PILLAR_P0.get(),
            ModBlocks.HALF_TIMBER_PILLAR_P1.get(),
            ModBlocks.HALF_TIMBER_PILLAR_P2.get()
        );

        event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof WindowBlock))
				return NekoColors.getColor(14);
			return NekoColors.getColor(state.getValue(WindowBlock.COLOR));
		},  ModBlocks.WINDOW_ARCH.get(),
            ModBlocks.WINDOW_CROSS.get(),
            ModBlocks.WINDOW_SHADE.get(),
            ModBlocks.WINDOW_LANCET.get()
        );

        event.getBlockColors().register((state, view, pos, tintIndex) -> {
			if (view == null || pos == null || !(state.getBlock() instanceof DyeableDoorBlock))
				return NekoColors.getColor(14);
			return NekoColors.getColor(state.getValue(DyeableDoorBlock.COLOR));
		},  ModBlocks.DOOR_1.get(),
            ModBlocks.DOOR_2.get(),
            ModBlocks.DOOR_3.get(),
            ModBlocks.DOOR_TALL_1.get(),
            ModBlocks.DOOR_TALL_2.get(),
            ModBlocks.DOOR_TALL_3.get()
        );

        LOGGER.info("Block Colors Registered.");
    }

    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event){
        event.getItemColors().register((stack, tintIndex) -> {
			return NekoColors.getColor(14);
		},  ModBlocks.STONE_BASE_BOTTOM.get().asItem(),
            ModBlocks.STONE_FRAME_BOTTOM.get().asItem(),
            ModBlocks.STONE_PILLAR_BOTTOM.get().asItem(),
            ModBlocks.STONE_BASE.get().asItem(),
            ModBlocks.STONE_FRAME.get().asItem(),
            ModBlocks.STONE_PILLAR.get().asItem(),
            ModBlocks.STONE_DORIC.get().asItem(),
            ModBlocks.STONE_IONIC.get().asItem(),
            ModBlocks.STONE_CORINTHIAN.get().asItem(),
            ModBlocks.WINDOW_SILL.get().asItem(),
            ModBlocks.WINDOW_TOP.get().asItem()
        );

        event.getItemColors().register((stack, tintIndex) -> {
			return NekoColors.getColor(2);
		},  ModBlocks.HALF_TIMBER_P0.get().asItem(),
            ModBlocks.HALF_TIMBER_P1.get().asItem(),
            ModBlocks.HALF_TIMBER_P2.get().asItem(),
            ModBlocks.HALF_TIMBER_P3.get().asItem(),
            ModBlocks.HALF_TIMBER_P4.get().asItem(),
            ModBlocks.HALF_TIMBER_P5.get().asItem(),
            ModBlocks.HALF_TIMBER_P6.get().asItem(),
            ModBlocks.HALF_TIMBER_P7.get().asItem(),
            ModBlocks.HALF_TIMBER_P8.get().asItem(),
            ModBlocks.HALF_TIMBER_P9.get().asItem(),
            ModBlocks.HALF_TIMBER_PILLAR_P0.get().asItem(),
            ModBlocks.HALF_TIMBER_PILLAR_P1.get().asItem(),
            ModBlocks.HALF_TIMBER_PILLAR_P2.get().asItem(),
            ModBlocks.WINDOW_ARCH.get().asItem(),
            ModBlocks.WINDOW_CROSS.get().asItem(),
            ModBlocks.WINDOW_SHADE.get().asItem(),
            ModBlocks.WINDOW_LANCET.get().asItem()
        );

        LOGGER.info("Block Colors Registered.");
    }
}
