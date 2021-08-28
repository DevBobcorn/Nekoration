package com.devbobcorn.nekoration.items;

import java.util.function.Consumer;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.client.rendering.items.WallPaperItemRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    // For Block Items, see CommonModEventSubscriber...
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Nekoration.MODID);

    public static final RegistryObject<Item> PAW = ITEMS.register("paw", () -> new Item(new Item.Properties().tab(ModItemTabs.TOOL_GROUP)));
    // Meow ~
    public static final RegistryObject<Item> PAW_UP = ITEMS.register("paw_up", () -> new Item(new Item.Properties().tab(ModItemTabs.TOOL_GROUP)));
    public static final RegistryObject<Item> PAW_DOWN = ITEMS.register("paw_down", () -> new Item(new Item.Properties().tab(ModItemTabs.TOOL_GROUP)));
    public static final RegistryObject<Item> PAW_LEFT = ITEMS.register("paw_left", () -> new Item(new Item.Properties().tab(ModItemTabs.TOOL_GROUP)));
    public static final RegistryObject<Item> PAW_RIGHT = ITEMS.register("paw_right", () -> new Item(new Item.Properties().tab(ModItemTabs.TOOL_GROUP)));
    public static final RegistryObject<Item> PAW_NEAR = ITEMS.register("paw_near", () -> new Item(new Item.Properties().tab(ModItemTabs.TOOL_GROUP)));
    public static final RegistryObject<Item> PAW_FAR = ITEMS.register("paw_far", () -> new Item(new Item.Properties().tab(ModItemTabs.TOOL_GROUP)));
    public static final RegistryObject<Item> PAW_15 = ITEMS.register("paw_15", () -> new Item(new Item.Properties().tab(ModItemTabs.TOOL_GROUP)));
    public static final RegistryObject<Item> PAW_90 = ITEMS.register("paw_90", () -> new Item(new Item.Properties().tab(ModItemTabs.TOOL_GROUP)));

    public static final RegistryObject<Item> PALETTE = ITEMS.register("palette", () -> new PaletteItem(new Item.Properties().tab(ModItemTabs.TOOL_GROUP)));

    public static final RegistryObject<Item> PAINTING = ITEMS.register("painting", () -> new PaintingItem(new Item.Properties().tab(ModItemTabs.DECOR_GROUP)));

    public static final RegistryObject<Item> WALLPAPER = ITEMS.register("wallpaper", () -> new WallPaperItem(new Item.Properties().tab(ModItemTabs.DECOR_GROUP)){
        @Override
        public void initializeClient(Consumer<IItemRenderProperties> consumer) {
            consumer.accept(new IItemRenderProperties(){
                @Override
                public BlockEntityWithoutLevelRenderer getItemStackRenderer(){
                    return new WallPaperItemRenderer(null, Minecraft.getInstance().getEntityModels());
                }
            });
        }
    });
}
