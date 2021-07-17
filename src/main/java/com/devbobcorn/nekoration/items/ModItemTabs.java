package com.devbobcorn.nekoration.items;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import com.devbobcorn.nekoration.NekoColors.EnumWoodenColor;

import java.util.function.Supplier;

public class ModItemTabs {
    public static final ItemGroup STONE_GROUP = new ModItemGroup("stone", () -> {
			ItemStack icoStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "stone_frame_bottom")));
			DyeableBlockItem.setColor(icoStack, EnumNekoColor.WHITE);
			return icoStack;
		}
	);

    public static final ItemGroup WOODEN_GROUP = new ModItemGroup("wooden", () -> {
			ItemStack icoStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "half_timber_p1")));
			BiDyeableBlockItem.setColor1(icoStack, EnumNekoColor.WHITE);
			BiDyeableBlockItem.setColor0(icoStack, EnumWoodenColor.BROWN);
			return icoStack;
		}
	);

	public static final ItemGroup WINDOW_GROUP = new ModItemGroup("window", () -> {
			ItemStack icoStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "window_lancet")));
			DyeableWoodenBlockItem.setColor(icoStack, EnumWoodenColor.WHITE);
			return icoStack;
		}
	);

	public static final ItemGroup DOOR_GROUP = new ModItemGroup("door", () -> {
			ItemStack icoStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "door_1")));
			return icoStack;
		}
	);

	public static final ItemGroup FURNITURE_GROUP = new ModItemGroup("furniture", () -> {
			ItemStack icoStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "pumpkin_table")));
			//DyeableBlockItem.setColor(icoStack, EnumNekoColor.WHITE);
			return icoStack;
		}
	);

	public static final ItemGroup DECOR_GROUP = new ModItemGroup("decor", () -> {
			ItemStack icoStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "stone_pot")));
			DyeableBlockItem.setColor(icoStack, EnumNekoColor.WHITE);
			return icoStack;
		}
	);

	public static final class ModItemGroup extends ItemGroup {

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