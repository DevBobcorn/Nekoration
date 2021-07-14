package com.devbobcorn.nekoration;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;

public class NekoColors {
	public static int getBlockColorAt(int value,int min,int max,int minColor,int maxColor) {
		if (value >= max) return maxColor;
		else if (value <= min) return minColor;
		double frac = ((double)value - (double)min) / ((double)max - (double)min);
		
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
	
    // Neko Colors... Meow~
	public static int getNekoColorOrWhite(int id) {
		return EnumNekoColor.getColorValueFromID((byte)id);
	}

	public enum EnumNekoColor implements IStringSerializable {
		BLACK((byte)0, "black", 0x393939),
		BLUE((byte)1, "blue" , 0x2891ff),
		BROWN((byte)2, "brown", 0x673400),
		CYAN((byte)3, "cyan", 0x94e2ff),
		GRAY((byte)4, "gray", 0x757575),
		GREEN((byte)5, "green", 0x33b54c),
		LIGHT_BLUE((byte)6, "light_blue", 0x75aaff),
		LIGHT_GRAY((byte)7, "light_gray", 0xbebebe),
		LIME((byte)8, "lime", 0x7aff8f), //7df494
		MAGENTA((byte)9, "magenta", 0xf976ff),
		ORANGE((byte)10, "orange", 0xff7700), //ff9500
		PINK((byte)11, "pink", 0xffa3e0), //ff3baf
		PURPLE((byte)12, "purple", 0xbc61ff),
		RED((byte)13, "red", 0xe03f3f),
		WHITE((byte)14, "white", 0xffffff),
		YELLOW((byte)15, "yellow", 0xffc80a); //ffd54f

		@Override
		public String toString() {
			return this.name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}

		public int getColor() {
			return color;
		}
		
		public int getNBTId() {
			return (int)nbtID;
		}

		public static EnumNekoColor fromNBT(CompoundNBT compoundNBT, String tagname) {
			byte flavorID = 0; // default in case of error
			if (compoundNBT != null && compoundNBT.contains(tagname)) {
				flavorID = compoundNBT.getByte(tagname);
			}
			EnumNekoColor color = getColorEnumFromID(flavorID);
			return color; // default is white
		}

		/**
		 * Write this enum to NBT
		 * 
		 * @param compoundNBT
		 * @param tagname
		 */
		public void putIntoNBT(CompoundNBT compoundNBT, String tagname) {
			compoundNBT.putByte(tagname, nbtID);
		}

		private final byte nbtID;
		private final String name;
		private final int color;

		EnumNekoColor(byte i_NBT_ID, String i_name, int i_color) {
			this.nbtID = (byte) i_NBT_ID;
			this.name = i_name;
			this.color = i_color;
		}

		public static EnumNekoColor getColorEnumFromID(byte ID) {
			for (EnumNekoColor c : EnumNekoColor.values()) {
				if (c.nbtID == ID)
					return c;
			}
			return EnumNekoColor.WHITE;
		}

		public static int getColorValueFromID(byte ID) {
			for (EnumNekoColor c : EnumNekoColor.values()) {
				if (c.nbtID == ID)
					return c.color;
			}
			return WHITE.color;
		}

		public float getPropertyOverrideValue() {
			return nbtID;
		}
	}

	// Wooden Colors... Woof~
	public static int getWoodenColorOrBrown(int id) {
		return EnumWoodenColor.getColorValueFromID((byte)id);
	}

	public enum EnumWoodenColor implements IStringSerializable {
		BLACK((byte)0, "black", 0x5c3c1b), // dark_oak
		BLUE((byte)1, "blue" , 0x53b6ff),
		BROWN((byte)2, "brown", 0x886541), // spruce
		CYAN((byte)3, "cyan", 0x389a99), // warped
		GRAY((byte)4, "gray", 0xa27d64), // jungle
		GREEN((byte)5, "green", 0x33b54c),
		LIGHT_BLUE((byte)6, "light_blue", 0x75aaff),
		LIGHT_GRAY((byte)7, "light_gray", 0xb9955b), // oak
		LIME((byte)8, "lime", 0x7df494),
		MAGENTA((byte)9, "magenta", 0xbd0bab), // crimson
		ORANGE((byte)10, "orange", 0xf0973d), // acacia
		PINK((byte)11, "pink", 0xff97d7),
		PURPLE((byte)12, "purple", 0x873468),
		RED((byte)13, "red", 0xe03f3f),
		WHITE((byte)14, "white", 0xe8d699), // birch
		YELLOW((byte)15, "yellow", 0xffd54f); // oak

		@Override
		public String toString() {
			return this.name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}

		public int getColor() {
			return color;
		}

		public int getNBTId() {
			return (int)nbtID;
		}

		public static EnumWoodenColor fromNBT(CompoundNBT compoundNBT, String tagname) {
			byte flavorID = 0; // default in case of error
			if (compoundNBT != null && compoundNBT.contains(tagname)) {
				flavorID = compoundNBT.getByte(tagname);
			}
			EnumWoodenColor color = getColorEnumFromID(flavorID);
			return color; // default is wooden brown
		}

		public void putIntoNBT(CompoundNBT compoundNBT, String tagname) {
			compoundNBT.putByte(tagname, nbtID);
		}

		private final byte nbtID;
		private final String name;
		private final int color;

		EnumWoodenColor(byte i_NBT_ID, String i_name, int i_color) {
			this.nbtID = (byte) i_NBT_ID;
			this.name = i_name;
			this.color = i_color;
		}

		public static EnumWoodenColor getColorEnumFromID(byte ID) {
			for (EnumWoodenColor c : EnumWoodenColor.values()) {
				if (c.nbtID == ID)
					return c;
			}
			return EnumWoodenColor.BROWN;
		}

		public static int getColorValueFromID(byte ID) {
			for (EnumWoodenColor c : EnumWoodenColor.values()) {
				if (c.nbtID == ID)
					return c.color;
			}
			return BROWN.color;
		}

		public float getPropertyOverrideValue() {
			return nbtID;
		}
	}
}
