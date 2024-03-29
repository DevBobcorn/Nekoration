package com.devbobcorn.nekoration;

import java.awt.Color;

import org.joml.Vector3d;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

public class NekoColors {
    public static int getBlockColorAt(int value,int min,int max,int minColor,int maxColor) {
        if (value >= max) return maxColor;
        else if (value <= min) return minColor;
        double frac = ((double)value - (double)min) / ((double)max - (double)min);
        
        return getRGBColorBetween(frac, minColor, maxColor);
    }
    
    public static int getItemColor(ItemStack stack,int lc,int rc) {
        double frac = (double)stack.getCount() / stack.getMaxStackSize();
        return getRGBColorBetween(frac,lc,rc);
    }
    
    public static final Color getRGBColor(Vec3i vec){
        return new Color(Math.min(Math.max(vec.getX(), 0), 255), Math.min(Math.max(vec.getY(), 0), 255), Math.min(Math.max(vec.getZ(), 0), 255));
    }

    public static final Color getRGBColor(Vector3d vec){
        return new Color(Math.min(Math.max((int)vec.x, 0), 255), Math.min(Math.max((int)vec.y, 0), 255), Math.min(Math.max((int)vec.z, 0), 255));
    }

    public static Color getRGBColor(double col){
        int col1 = (int)col;
        return new Color((col1 & 0xff0000) >> 16, (col1 & 0xff00) >> 8, col1 & 0xff);
    }

    public static Color getRGBColor(int col){
        return new Color((col & 0xff0000) >> 16, (col & 0xff00) >> 8, col & 0xff);
    }

    public static int[] getRGBArray(int col){
        int[] arr = {((col & 0xff0000) >> 16), ((col & 0xff00) >> 8), (col & 0xff)};
        return arr;
    }

    public static int getRGBColorBetween(double frac,int lc,int rc) {
        int red1 = (lc & 0xff0000) >> 16;
        int green1 = (lc & 0xff00) >> 8;
        int blue1 = lc & 0xff;
        
        int red2 = (rc & 0xff0000) >> 16;
        int green2 = (rc & 0xff00) >> 8;
        int blue2 = rc & 0xff;
        
        int red3 = (int)Mth.lerp(frac,red1,red2);
        int green3 = (int)Mth.lerp(frac,green1,green2);
        int blue3 = (int)Mth.lerp(frac,blue1,blue2);
        
        return (red3 << 16) + (green3 << 8) + blue3;
    }

    public static Color getRGBColorBetween(double frac,Color lc,Color rc) {
        return new Color((int)Mth.lerp(frac, lc.getRed(), rc.getRed()), (int)Mth.lerp(frac, lc.getGreen(), rc.getGreen()), (int)Mth.lerp(frac, lc.getBlue(), rc.getBlue()));
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

    public static EnumStoneColor getStoneFromNeko(EnumNekoColor col){
        return EnumStoneColor.getColorEnumFromID(col.nbtID);
    }
    
    // Neko Colors... Meow~
    public static int getNekoColorOrWhite(int id) {
        return EnumNekoColor.getColorValueFromID((byte)id);
    }

    public enum EnumNekoColor implements StringRepresentable {
        BLACK((byte)0, "black", 0x5c5c5c),
        BLUE((byte)1, "blue" , 0x2891ff),
        BROWN((byte)2, "brown", 0x673400),
        CYAN((byte)3, "cyan", 0x94e2ff),
        GRAY((byte)4, "gray", 0x9f9f9f),
        GREEN((byte)5, "green", 0x33b54c),
        LIGHT_BLUE((byte)6, "light_blue", 0x75aaff),
        LIGHT_GRAY((byte)7, "light_gray", 0xbebebe),
        LIME((byte)8, "lime", 0x7aff8f),
        MAGENTA((byte)9, "magenta", 0xf976ff),
        ORANGE((byte)10, "orange", 0xff7700),
        PINK((byte)11, "pink", 0xffa3e0),
        PURPLE((byte)12, "purple", 0xbc61ff),
        RED((byte)13, "red", 0xe03f3f),
        WHITE((byte)14, "white", 0xffffff),
        YELLOW((byte)15, "yellow", 0xffc80a);

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

        public static EnumNekoColor fromNBT(CompoundTag compoundNBT, String tagname) {
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
        public void putIntoNBT(CompoundTag compoundNBT, String tagname) {
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
            return EnumNekoColor.LIGHT_GRAY;
        }

        public static int getColorValueFromID(byte ID) {
            for (EnumNekoColor c : EnumNekoColor.values()) {
                if (c.nbtID == ID)
                    return c.color;
            }
            return LIGHT_GRAY.color;
        }

        public float getPropertyOverrideValue() {
            return nbtID;
        }
    }

    // Stone Colors...
    public static int getStoneColorOrLightGray(int id) {
        return EnumStoneColor.getColorValueFromID((byte)id);
    }

    public enum EnumStoneColor implements StringRepresentable {
        BLACK((byte)0, "black", 0x5c5c5c),
        BLUE((byte)1, "blue" , 0x549ae5),
        BROWN((byte)2, "brown", 0xa37864),
        CYAN((byte)3, "cyan", 0x8ed2ed),
        GRAY((byte)4, "gray", 0x9f9f9f),
        GREEN((byte)5, "green", 0x50ae5f),
        LIGHT_BLUE((byte)6, "light_blue", 0x7eaeff),
        LIGHT_GRAY((byte)7, "light_gray", 0xbebebe),
        LIME((byte)8, "lime", 0x8de996),
        MAGENTA((byte)9, "magenta", 0xe58dea),
        ORANGE((byte)10, "orange", 0xeb9965),
        PINK((byte)11, "pink", 0xe79acd),
        PURPLE((byte)12, "purple", 0xb180d7),
        RED((byte)13, "red", 0xe15252),
        WHITE((byte)14, "white", 0xfef8ec),
        YELLOW((byte)15, "yellow", 0xf1cf7b);

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

        private final byte nbtID;
        private final String name;
        private final int color;

        EnumStoneColor(byte i_NBT_ID, String i_name, int i_color) {
            this.nbtID = (byte) i_NBT_ID;
            this.name = i_name;
            this.color = i_color;
        }

        public static EnumStoneColor getColorEnumFromID(byte ID) {
            for (EnumStoneColor c : EnumStoneColor.values()) {
                if (c.nbtID == ID)
                    return c;
            }
            return EnumStoneColor.LIGHT_GRAY;
        }

        public static int getColorValueFromID(byte ID) {
            for (EnumStoneColor c : EnumStoneColor.values()) {
                if (c.nbtID == ID)
                    return c.color;
            }
            return LIGHT_GRAY.color;
        }

        public float getPropertyOverrideValue() {
            return nbtID;
        }
    }

    // Wooden Colors... Woof~
    public static int getWoodenColorOrBrown(int id) {
        return EnumWoodenColor.getColorValueFromID((byte)id);
    }

    public enum EnumWoodenColor implements StringRepresentable {
        BLACK((byte)0, "black", 0x5c3c1b), // dark_oak
        BLUE((byte)1, "blue" , 0x446184),
        BROWN((byte)2, "brown", 0x886541), // spruce
        CYAN((byte)3, "cyan", 0x389a99), // warped
        GRAY((byte)4, "gray", 0xb38564), // jungle
        GREEN((byte)5, "green", 0x179930),
        LIGHT_BLUE((byte)6, "light_blue", 0x4d71b0), // +magic
        LIGHT_GRAY((byte)7, "light_gray", 0xb9955b), // oak
        LIME((byte)8, "lime", 0x9fad81), // +willow
        MAGENTA((byte)9, "magenta", 0x873468),  // crimson
        ORANGE((byte)10, "orange", 0xb4653a), // acacia
        PINK((byte)11, "pink", 0x9a4a45), // +mangrove prev: mahogany 0xcd8684
        PURPLE((byte)12, "purple", 0x7b688c), // +umbran
        RED((byte)13, "red", 0x9c2525), // +cherry
        WHITE((byte)14, "white", 0xe8d699), // birch
        YELLOW((byte)15, "yellow", 0xcd9144); // +palm

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

        public static EnumWoodenColor fromNBT(CompoundTag compoundNBT, String tagname) {
            byte flavorID = 0; // default in case of error
            if (compoundNBT != null && compoundNBT.contains(tagname)) {
                flavorID = compoundNBT.getByte(tagname);
            }
            EnumWoodenColor color = getColorEnumFromID(flavorID);
            return color; // default is wooden brown
        }

        public void putIntoNBT(CompoundTag compoundNBT, String tagname) {
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
