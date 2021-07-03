package com.devbobcorn.nekoration;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class NekoColors {
	public static int getBlockColorAt(int height,int min,int max,int minColor,int maxColor) {
		if (height >= max) return maxColor;
		else if (height <= min) return minColor;
		double frac = ((double)height - (double)min) / ((double)max - (double)min);
		
		return getColorBetween(frac, minColor, maxColor);
	}
	
	public static int getItemColor(ItemStack stack,int lc,int rc) {
		double frac = (double)stack.getCount() / stack.getMaxStackSize();
		return getColorBetween(frac,lc,rc);
	}
	
	public static int getColorBetween(double frac,int lc,int rc) {
		int red1 = (lc & 0xff0000) >> 16;
    	int green1 = (lc & 0xff00) >> 8;
    	int blue1 = lc & 0xff;
    	
    	int red2 = (rc & 0xff0000) >> 16;
    	int green2 = (rc & 0xff00) >> 8;
    	int blue2 = rc & 0xff;
    	
    	int red3 = (int)MathHelper.lerp(frac,red1,red2);
    	int green3 = (int)MathHelper.lerp(frac,green1,green2);
    	int blue3 = (int)MathHelper.lerp(frac,blue1,blue2);
    	
    	return (red3 << 16) + (green3 << 8) + blue3;
	}
	
	public static int getRed(int c) {
		return (c & 0xff0000) >> 16;
	}
	
	public static int getGreen(int c) {
		return (c & 0xff00) >> 8;
	}
	
	public static int getBlue(int c) {
		return (c & 0xff);
	}
	
	public static float getRedf(int c) {
		return (float)((c & 0xff0000) >> 16) / 255.0F;
	}
	
	public static float getGreenf(int c) {
		return (float)((c & 0xff00) >> 8) / 255.0F;
	}
	
	public static float getBluef(int c) {
		return (float)(c & 0xff) / 255.0F;
	}
	
	public static int getColor(int num) {
		switch(num) {
		case 0: //BLACK_DYE
			return 0x393939;
		case 1: //BLUE_DYE
			//return 0x53b6ff;
			return 0x75aaff; // LIGHT_BLUE <-
		case 2: //BROWN_DYE
			return 0x673400;
		case 3: //CYAN_DYE
			//return 0x94e2ff;
			return 0x53b6ff; // BLUE <-
		case 4: //GRAY_DYE
			return 0x757575;
		case 5: //GREEN_DYE
			return 0x33b54c;
		case 6: //LIGHT_BLUE_DYE
			//return 0x75aaff;
			return 0x94e2ff; // CYAN <-
		case 7: //LIGHT_GRAY_DYE
			return 0xbebebe;
		case 8: //LIME_DYE
			return 0x7df494;
		case 9: //MAGENTA_DYE
			return 0xf976ff;
		case 10: //ORANGE_DYE
			return 0xffa346;
		case 11: //PINK_DYE
			return 0xffc0e7;
		case 12: //PURPLE_DYE
			return 0xbc61ff;
		case 13: //RED_DYE
			return 0xe03f3f;
		case 14: //WHITE_DYE
			return 0xFFFFFF;
		default: //YELLOW_DYE
			return 0xffd54f;
		}
	}
}
