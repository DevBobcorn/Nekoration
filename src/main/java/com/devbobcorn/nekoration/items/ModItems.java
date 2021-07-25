package com.devbobcorn.nekoration.items;

import com.devbobcorn.nekoration.Nekoration;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
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
}
