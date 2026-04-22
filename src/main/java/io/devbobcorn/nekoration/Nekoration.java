package io.devbobcorn.nekoration;

import java.util.ArrayList;
import java.util.Comparator;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.registry.OrnamentsRegistration;
import io.devbobcorn.nekoration.registry.WoodenBlocksRegistration;
import io.devbobcorn.nekoration.registry.StoneBlocksRegistration;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Nekoration.MODID)
public class Nekoration {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "nekoration";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // Create a Deferred Register to hold Blocks which will all be registered under the "nekoration" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    // Create a Deferred Register to hold Items which will all be registered under the "nekoration" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "nekoration" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    static {
        WoodenBlocksRegistration.register(BLOCKS, ITEMS);
        StoneBlocksRegistration.register(BLOCKS, ITEMS);
        OrnamentsRegistration.register(BLOCKS, ITEMS);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NEKORATION_STONE_BLOCKS_TAB =
        CREATIVE_MODE_TABS.register("nekoration_stone_blocks", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.nekoration_stone_blocks"))
            .icon(() -> DyeableBlockItem.createCreativeTabStack(StoneBlocksRegistration.iconItem().get()))
            .displayItems((parameters, output) -> {
                ArrayList<ItemStack> stacks = new ArrayList<>();
                StoneBlocksRegistration.blockItemsView().forEach(holder -> {
                    for (EnumNekoColor color : EnumNekoColor.values()) {
                        stacks.add(DyeableBlockItem.createCreativeTabStack(holder.get(), color));
                    }
                });
                stacks.sort(Comparator.<ItemStack>comparingInt(s -> DyeableBlockItem.getColor(s).ordinal())
                        .thenComparingInt(s -> BuiltInRegistries.ITEM.getId(s.getItem())));
                stacks.forEach(output::accept);
            })
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NEKORATION_WOODEN_BLOCKS_TAB =
        CREATIVE_MODE_TABS.register("nekoration_wooden_blocks", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.nekoration_wooden_blocks"))
            .icon(() -> DyeableBlockItem.createCreativeTabStack(WoodenBlocksRegistration.iconItem().get()))
            .displayItems((parameters, output) -> {
                ArrayList<ItemStack> stacks = new ArrayList<>();
                WoodenBlocksRegistration.halfTimberBlockItemsView().forEach(holder -> {
                    stacks.add(DyeableBlockItem.createCreativeTabStack(holder.get(), EnumNekoColor.WHITE));
                });
                stacks.sort(HalfTimberCreativeTabOrdering.stackComparator());
                stacks.forEach(output::accept);
                WoodenBlocksRegistration.windowBlockItemsView().forEach(holder -> output.accept(new ItemStack(holder.get())));
                WoodenBlocksRegistration.furnitureBlockItemsView().forEach(holder -> output.accept(new ItemStack(holder.get())));
            })
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NEKORATION_ORNAMENTS_TAB =
        CREATIVE_MODE_TABS.register("nekoration_ornaments", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.nekoration_ornaments"))
            .icon(() -> DyeableBlockItem.createCreativeTabStack(OrnamentsRegistration.windowPlantBlockItem().get()))
            .displayItems((parameters, output) -> {
                for (EnumNekoColor color : EnumNekoColor.values()) {
                    output.accept(DyeableBlockItem.createCreativeTabStack(OrnamentsRegistration.WINDOW_PLANT_BLOCK_ITEM.get(), color));
                }
            })
            .build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Nekoration(IEventBus modEventBus, ModContainer modContainer) {
        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Nekoration) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.SERVER, NekoConfig.SERVER_SPEC);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
