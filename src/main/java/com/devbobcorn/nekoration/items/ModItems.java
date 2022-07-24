package com.devbobcorn.nekoration.items;

import java.util.function.Consumer;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.client.rendering.items.WallPaperItemRenderer;
import com.devbobcorn.nekoration.items.TweakItem.Aspect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    // For Block Items, see CommonModEventSubscriber...
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Nekoration.MODID);

    public static final RegistryObject<Item> PAW = ITEMS.register("paw", () -> new Item(new Item.Properties().tab(ModItemTabs.TOOL_GROUP)));
    // Meow ~
    public static final RegistryObject<Item> PAW_UP = ITEMS.register("paw_up", () -> new TweakItem(new Item.Properties().tab(ModItemTabs.TOOL_GROUP), Aspect.PosY, 1));
    public static final RegistryObject<Item> PAW_DOWN = ITEMS.register("paw_down", () -> new TweakItem(new Item.Properties().tab(ModItemTabs.TOOL_GROUP), Aspect.PosY, -1));
    public static final RegistryObject<Item> PAW_LEFT = ITEMS.register("paw_left", () -> new TweakItem(new Item.Properties().tab(ModItemTabs.TOOL_GROUP), Aspect.PosX, 1));
    public static final RegistryObject<Item> PAW_RIGHT = ITEMS.register("paw_right", () -> new TweakItem(new Item.Properties().tab(ModItemTabs.TOOL_GROUP), Aspect.PosX, -1));
    public static final RegistryObject<Item> PAW_NEAR = ITEMS.register("paw_near", () -> new TweakItem(new Item.Properties().tab(ModItemTabs.TOOL_GROUP), Aspect.PosZ, -1));
    public static final RegistryObject<Item> PAW_FAR = ITEMS.register("paw_far", () -> new TweakItem(new Item.Properties().tab(ModItemTabs.TOOL_GROUP), Aspect.PosZ, 1));
    public static final RegistryObject<Item> PAW_15 = ITEMS.register("paw_15", () -> new TweakItem(new Item.Properties().tab(ModItemTabs.TOOL_GROUP), Aspect.Rotation, 1));
    public static final RegistryObject<Item> PAW_90 = ITEMS.register("paw_90", () -> new TweakItem(new Item.Properties().tab(ModItemTabs.TOOL_GROUP), Aspect.Rotation, 6));

    public static final RegistryObject<Item> ARROW_HINT = ITEMS.register("arrow_hint", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PALETTE = ITEMS.register("palette", () -> new PaletteItem(new Item.Properties().tab(ModItemTabs.TOOL_GROUP)));
    
    public static final RegistryObject<Item> CAMERA = ITEMS.register("camera", () -> new CameraItem(new Item.Properties())); // TODO: .tab(ModItemTabs.TOOL_GROUP)

    public static final RegistryObject<Item> PAINTING = ITEMS.register("painting", () -> new PaintingItem(new Item.Properties().tab(ModItemTabs.DECOR_GROUP)));

    public static final RegistryObject<Item> WALLPAPER = ITEMS.register("wallpaper", () -> new WallPaperItem(new Item.Properties().tab(ModItemTabs.DECOR_GROUP)){
        @Override
        public void initializeClient(Consumer<IClientItemExtensions> consumer) {
            consumer.accept(new IClientItemExtensions(){
                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer(){
                    return new WallPaperItemRenderer(null, Minecraft.getInstance().getEntityModels());
                }
            });
        }
    });
}
