package com.devbobcorn.nekoration.common.event;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import com.devbobcorn.nekoration.NekoColors.EnumWoodenColor;
import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.common.VanillaCompat;
import com.devbobcorn.nekoration.items.DyeableBlockItem;
import com.devbobcorn.nekoration.items.DyeableWoodenBlockItem;
import com.devbobcorn.nekoration.items.HalfTimberBlockItem;
import com.devbobcorn.nekoration.items.ModItems;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = Nekoration.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class CommonModEventSubscriber {
    private static final Logger LOGGER = LogManager.getLogger(Nekoration.MODID + " Mod Event Subscriber");

    @SubscribeEvent
    public static void onFMLCommonSetupEvent(final FMLCommonSetupEvent event) {
        LOGGER.info("Initializing Vanilla Compat");
        VanillaCompat.Initialize();
    }

    @SubscribeEvent
    public static void registerCreativeTabs(CreativeModeTabEvent.Register event) {
        LOGGER.debug("Registering neko creative tabs!");

        event.registerCreativeModeTab(new ResourceLocation(Nekoration.MODID, "neko_stone"), builder -> builder
                .title(Component.translatable("itemGroup.neko_stone"))
                .icon(() -> {
                    var icoStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "stone_frame_bottom")));
                    DyeableBlockItem.setColor(icoStack, EnumNekoColor.WHITE);
                    return icoStack;
                })
                .displayItems((featureFlag, output, operator) -> {
                    output.accept(ModBlocks.STONE_LAYERED.get());
                    output.accept(ModBlocks.STONE_BASE_BOTTOM.get());
                    output.accept(ModBlocks.STONE_FRAME_BOTTOM.get());
                    output.accept(ModBlocks.STONE_PILLAR_BOTTOM.get());
                    output.accept(ModBlocks.STONE_BASE.get());
                    output.accept(ModBlocks.STONE_FRAME.get());
                    output.accept(ModBlocks.STONE_PILLAR.get());
                    output.accept(ModBlocks.STONE_DORIC.get());
                    output.accept(ModBlocks.STONE_IONIC.get());
                    output.accept(ModBlocks.STONE_CORINTHIAN.get());

                    output.accept(ModBlocks.STONE_BOTTOM_THIN.get());
                    output.accept(ModBlocks.STONE_PILLAR_THIN.get());
                    output.accept(ModBlocks.STONE_DORIC_THIN.get());
                    output.accept(ModBlocks.STONE_IONIC_THIN.get());
                    output.accept(ModBlocks.STONE_CORINTHIAN_THIN.get());

                })
        );
        
        event.registerCreativeModeTab(new ResourceLocation(Nekoration.MODID, "neko_wooden"), builder -> builder
                .title(Component.translatable("itemGroup.neko_wooden"))
                .icon(() -> {
                    var icoStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "half_timber_p1")));
                    HalfTimberBlockItem.setColor1(icoStack, EnumNekoColor.WHITE);
                    HalfTimberBlockItem.setColor0(icoStack, EnumWoodenColor.BROWN);
                    return icoStack;
                })
                .displayItems((featureFlag, output, operator) -> {
                    output.accept(ModBlocks.HALF_TIMBER_P0.get());
                    output.accept(ModBlocks.HALF_TIMBER_P1.get());
                    output.accept(ModBlocks.HALF_TIMBER_P2.get());
                    output.accept(ModBlocks.HALF_TIMBER_P3.get());
                    output.accept(ModBlocks.HALF_TIMBER_P4.get());
                    output.accept(ModBlocks.HALF_TIMBER_P5.get());
                    output.accept(ModBlocks.HALF_TIMBER_P6.get());
                    output.accept(ModBlocks.HALF_TIMBER_P7.get());
                    output.accept(ModBlocks.HALF_TIMBER_P8.get());
                    output.accept(ModBlocks.HALF_TIMBER_P9.get());

                    output.accept(ModBlocks.HALF_TIMBER_PILLAR_P0.get());
                    output.accept(ModBlocks.HALF_TIMBER_PILLAR_P1.get());
                    output.accept(ModBlocks.HALF_TIMBER_PILLAR_P2.get());

                })
        );

        event.registerCreativeModeTab(new ResourceLocation(Nekoration.MODID, "neko_window_n_door"), builder -> builder
                .title(Component.translatable("itemGroup.neko_window_n_door"))
                .icon(() -> {
                    var icoStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "window_lancet")));
                    DyeableWoodenBlockItem.setColor(icoStack, EnumWoodenColor.BROWN);
                    return icoStack;
                })
                .displayItems((featureFlag, output, operator) -> {
                    output.accept(ModBlocks.WINDOW_TOP.get());
                    output.accept(ModBlocks.WINDOW_SILL.get());
                    output.accept(ModBlocks.WINDOW_PLANT.get());

                    output.accept(ModBlocks.WINDOW_SIMPLE.get());
                    output.accept(ModBlocks.WINDOW_ARCH.get());
                    output.accept(ModBlocks.WINDOW_CROSS.get());
                    output.accept(ModBlocks.WINDOW_SHADE.get());
                    output.accept(ModBlocks.WINDOW_LANCET.get());

                    output.accept(ModBlocks.DOOR_1.get());
                    output.accept(ModBlocks.DOOR_2.get());
                    output.accept(ModBlocks.DOOR_3.get());

                    output.accept(ModBlocks.DOOR_TALL_1.get());
                    output.accept(ModBlocks.DOOR_TALL_2.get());
                    output.accept(ModBlocks.DOOR_TALL_3.get());
                })
        );

        event.registerCreativeModeTab(new ResourceLocation(Nekoration.MODID, "neko_decor"), builder -> builder
                .title(Component.translatable("itemGroup.neko_decor"))
                .icon(() -> {
                    var icoStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "easel_menu")));
                    DyeableWoodenBlockItem.setColor(icoStack, EnumWoodenColor.BROWN);
                    return icoStack;
                })
                .displayItems((featureFlag, output, operator) -> {
                    output.accept(ModBlocks.STONE_POT.get());
                    output.accept(ModBlocks.STONE_PLANTER.get());

                    output.accept(ModBlocks.AWNING_PURE.get());
                    output.accept(ModBlocks.AWNING_STRIPE.get());
                    output.accept(ModBlocks.AWNING_PURE_SHORT.get());
                    output.accept(ModBlocks.AWNING_STRIPE_SHORT.get());

                    output.accept(ModBlocks.LAMP_POST_IRON.get());
                    output.accept(ModBlocks.LAMP_POST_GOLD.get());
                    output.accept(ModBlocks.LAMP_POST_QUARTZ.get());

                    output.accept(ModBlocks.CANDLE_HOLDER_IRON.get());
                    output.accept(ModBlocks.CANDLE_HOLDER_GOLD.get());
                    output.accept(ModBlocks.CANDLE_HOLDER_QUARTZ.get());

                    output.accept(ModBlocks.FLOWER_BASKET_IRON.get());
                    output.accept(ModBlocks.FLOWER_BASKET_GOLD.get());
                    output.accept(ModBlocks.FLOWER_BASKET_QUARTZ.get());

                    output.accept(ModBlocks.EASEL_MENU.get());
                    output.accept(ModBlocks.EASEL_MENU_WHITE.get());

                    output.accept(ModBlocks.PHONOGRAPH.get());
                    output.accept(ModBlocks.CUSTOM.get());

                    //output.accept(ModBlocks.DREAM_WAS_TAKEN.get()); No item form
                    output.accept(ModBlocks.PRISMAP_TABLE.get());
                    

                    output.accept(ModBlocks.PUMPKIN_TABLE.get());
                    output.accept(ModBlocks.PUMPKIN_CHAIR.get());

                    output.accept(ModBlocks.OAK_TABLE.get());
                    output.accept(ModBlocks.OAK_ROUND_TABLE.get());
                    output.accept(ModBlocks.OAK_CHAIR.get());
                    output.accept(ModBlocks.JUNGLE_TABLE.get());
                    output.accept(ModBlocks.JUNGLE_ROUND_TABLE.get());
                    output.accept(ModBlocks.JUNGLE_CHAIR.get());
                    output.accept(ModBlocks.ACACIA_TABLE.get());
                    output.accept(ModBlocks.ACACIA_ROUND_TABLE.get());
                    output.accept(ModBlocks.ACACIA_CHAIR.get());
                    output.accept(ModBlocks.BIRCH_TABLE.get());
                    output.accept(ModBlocks.BIRCH_ROUND_TABLE.get());
                    output.accept(ModBlocks.BIRCH_CHAIR.get());
                    output.accept(ModBlocks.DARK_OAK_TABLE.get());
                    output.accept(ModBlocks.DARK_OAK_ROUND_TABLE.get());
                    output.accept(ModBlocks.DARK_OAK_CHAIR.get());
                    output.accept(ModBlocks.SPRUCE_TABLE.get());
                    output.accept(ModBlocks.SPRUCE_ROUND_TABLE.get());
                    output.accept(ModBlocks.SPRUCE_CHAIR.get());
                    output.accept(ModBlocks.CRIMSON_TABLE.get());
                    output.accept(ModBlocks.CRIMSON_ROUND_TABLE.get());
                    output.accept(ModBlocks.CRIMSON_CHAIR.get());
                    output.accept(ModBlocks.WARPED_TABLE.get());
                    output.accept(ModBlocks.WARPED_ROUND_TABLE.get());
                    output.accept(ModBlocks.WARPED_CHAIR.get());
                    output.accept(ModBlocks.MANGROVE_TABLE.get());
                    output.accept(ModBlocks.MANGROVE_ROUND_TABLE.get());
                    output.accept(ModBlocks.MANGROVE_CHAIR.get());

                    output.accept(ModBlocks.GLASS_TABLE.get());
                    output.accept(ModBlocks.GLASS_ROUND_TABLE.get());
                    output.accept(ModBlocks.ARM_CHAIR.get());
                    output.accept(ModBlocks.BENCH.get());

                    output.accept(ModBlocks.DRAWER.get());
                    output.accept(ModBlocks.CABINET.get());
                    output.accept(ModBlocks.DRAWER_CHEST.get());

                    output.accept(ModBlocks.CUPBOARD.get());
                    output.accept(ModBlocks.SHELF.get());
                    output.accept(ModBlocks.WALL_SHELF.get());

                    output.accept(ModItems.PAINTING.get());
                    output.accept(ModItems.WALLPAPER.get());
                })
        );

        event.registerCreativeModeTab(new ResourceLocation(Nekoration.MODID, "neko_tool"), builder -> builder
                .title(Component.translatable("itemGroup.neko_tool"))
                .icon(() -> new ItemStack(ModItems.PAW.get()))
                .displayItems((featureFlag, output, operator) -> {
                    output.accept(ModItems.PAW.get());
                    output.accept(ModItems.PAW_UP.get());
                    output.accept(ModItems.PAW_DOWN.get());
                    output.accept(ModItems.PAW_LEFT.get());
                    output.accept(ModItems.PAW_RIGHT.get());
                    output.accept(ModItems.PAW_NEAR.get());
                    output.accept(ModItems.PAW_FAR.get());
                    output.accept(ModItems.PAW_15.get());
                    output.accept(ModItems.PAW_90.get());

                    output.accept(ModItems.PALETTE.get());

                    // TODO output.accept(ModItems.CAMERA.get());

                })
        );
        
    }
}