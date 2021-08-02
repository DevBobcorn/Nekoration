package com.devbobcorn.nekoration.entities;

import net.minecraft.nbt.CompoundNBT;

public class PaintingData {
    private short width;
    private short height;

    private int[] pixels;

    public PaintingData(short w, short h){
        width = w;
        height = h;
        pixels = new int[w * h];
        for (int i = 0;i < w;i++)
            for (int j = 0;j < h;j++)
                pixels[i + j * w] = (16 * (i % 16) << 16) + (16 * (j % 16) << 8) + 255;
    }

    public PaintingData(short w, short h, int[] pix){
        width = w;
        height = h;
        pixels = pix;
    }

    public static void writeTo(PaintingData data, CompoundNBT tag){
        tag.putShort("Width", data.width);
        tag.putShort("Height", data.height);
        tag.putIntArray("Pixels", data.pixels);
    }

    public static PaintingData readFrom(CompoundNBT tag){
        return new PaintingData(tag.getShort("Width"), tag.getShort("Height"), tag.getIntArray("Pixels"));
    }

    public int[] getPixels(){
        return pixels;
    }

    public void setPixels(int[] pixels){
        if (pixels.length == this.pixels.length)
            this.pixels = pixels;
    }

    public int getPixel(int x, int y){
        return pixels[x + y * width];
    }

    public short getWidth(){
        return width;
    }

    public short getHeight(){
        return height;
    }
}