package com.devbobcorn.nekoration.misc;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

import com.devbobcorn.nekoration.Nekoration;

import java.util.function.Supplier;

public class ModItemTabs {
    public static final ItemGroup NEKORATION_GROUP = new ModItemGroup("nekoration", () -> new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Nekoration.MODID, "stone_base_bottom"))));
	//TODO: Why write as above?

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
