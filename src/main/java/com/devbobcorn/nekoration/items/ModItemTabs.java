package com.devbobcorn.nekoration.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import com.devbobcorn.nekoration.NekoColors.EnumWoodenColor;

import java.util.function.Supplier;

public class ModItemTabs {
    public static final CreativeModeTab STONE_GROUP = new ModItemGroup(Nekoration.MODID + ".stone", () -> {
            ItemStack icoStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "stone_frame_bottom")));
            DyeableBlockItem.setColor(icoStack, EnumNekoColor.WHITE);
            return icoStack;
        }
    );

    public static final CreativeModeTab WOODEN_GROUP = new ModItemGroup(Nekoration.MODID + ".wooden", () -> {
            ItemStack icoStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "half_timber_p1")));
            HalfTimberBlockItem.setColor1(icoStack, EnumNekoColor.WHITE);
            HalfTimberBlockItem.setColor0(icoStack, EnumWoodenColor.BROWN);
            return icoStack;
        }
    );

    public static final CreativeModeTab WINDOW_N_DOOR_GROUP = new ModItemGroup(Nekoration.MODID + ".window_n_door", () -> {
            ItemStack icoStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "window_lancet")));
            DyeableWoodenBlockItem.setColor(icoStack, EnumWoodenColor.BROWN);
            return icoStack;
        }
    );

    public static final CreativeModeTab DECOR_GROUP = new ModItemGroup(Nekoration.MODID + ".decor", () -> {
            ItemStack icoStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "easel_menu")));
            DyeableWoodenBlockItem.setColor(icoStack, EnumWoodenColor.BROWN);
            return icoStack;
        }
    );

    public static final CreativeModeTab TOOL_GROUP = new ModItemGroup(Nekoration.MODID + ".neko_tool", () -> {
            return new ItemStack(ModItems.PAW.get());
        }
    );

    public static final class ModItemGroup extends CreativeModeTab {
        @Nonnull
        private final Supplier<ItemStack> iconSupplier;

        public ModItemGroup(@Nonnull final String name, @Nonnull final Supplier<ItemStack> iconSupplier) {
            super(name);
            this.iconSupplier = iconSupplier;
        }

        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return iconSupplier.get();
        }
    }
}
