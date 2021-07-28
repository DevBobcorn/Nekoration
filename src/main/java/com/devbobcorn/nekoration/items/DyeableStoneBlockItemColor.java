package com.devbobcorn.nekoration.items;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

import java.awt.*;

import com.devbobcorn.nekoration.NekoColors;

public class DyeableStoneBlockItemColor implements IItemColor {
	@Override
	public int getColor(ItemStack stack, int tintIndex) {
		// when rendering, choose the colour multiplier based on the contents
		// we want layer 0 (the bottle glass) to be unaffected (return white as the
		// multiplier)
		// layer 1 will change colour depending on the contents, which is stored in NBT.
		{
			switch (tintIndex) {
				case 0:
					NekoColors.EnumStoneColor color = DyeableStoneBlockItem.getColor(stack);
					return color.getColor();
				default: {
					// oops! should never get here.
					return Color.BLACK.getRGB();
				}
			}
		}
	}
}
