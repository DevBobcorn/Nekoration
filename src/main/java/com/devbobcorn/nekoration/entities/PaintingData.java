package com.devbobcorn.nekoration.entities;

import java.util.Random;

import com.devbobcorn.nekoration.NekoColors;

import net.minecraft.nbt.CompoundNBT;

public class PaintingData {
    private short width;
    private short height;

    public final boolean isClient;

    private int[] canvas;    // Fully Opaque, client-only
    private int[] pixels;    // Allows Transparency, for both sides
    private int[] composite; // The final painting, fully Opaque, client-only, for rendering

    public PaintingData(short w, short h, boolean client, int entityId){
        width = w;
        height = h;
        pixels = new int[w * h];
        isClient = client;
        if (isClient){
            Random random = new Random(entityId); // Use the entity's id as a seed to ensure that each PaintingEntity's wooden frame is unique and constant...
            // Initialize canvas layer and composite layer as an empty canvas...
            canvas = new int[w * h];
            composite = new int[w * h];
            for (int i = 0;i < w;i++)
                for (int j = 0;j < h;j++)
                    composite[i + j * w] = canvas[i + j * w] = (i % 16 == 0 || j % 16 == 0) ? 0xBAA080 : 0xEAD6B0;
            int bottom = (h - 1) * w;
            int right = w - 1;
            for (int i = 0;i < w;i++){ // Wooden: 187 131 53 random range:30
                composite[i] = canvas[i] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
                composite[i + bottom] = canvas[i + bottom] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
            }
            for (int j = 1;j < h - 1;j++){
                composite[j * w] = canvas[j * w] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
                composite[right + j * w] = canvas[right + j * w] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
            }
        }
    }

    public PaintingData(short w, short h, int[] pix, boolean client, int entityId){
        width = w;
        height = h;
        pixels = pix;
        isClient = client;
        if (isClient){
            Random random = new Random(entityId); // Use the entity's id as a seed to ensure that each PaintingEntity's wooden frame is unique and constant...
            // Initialize the canvas layer as an empty canvas...
            canvas = new int[w * h];
            for (int i = 0;i < w;i++)
                for (int j = 0;j < h;j++)
                    canvas[i + j * w] = (i % 16 == 0 || j % 16 == 0) ? 0xBAA080 : 0xEAD6B0;
            int bottom = (h - 1) * w;
            int right = w - 1;
            for (int i = 0;i < w;i++){ // Wooden: 187 131 53 random range:30
                canvas[i] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
                canvas[i + bottom] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
            }
            for (int j = 1;j < h - 1;j++){
                canvas[j * w] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
                canvas[right + j * w] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
            }
            // Intialize the composite layer...
            composite = new int[w * h];
            recalculateComposite();
        }
    }

    public static void writeTo(PaintingData data, CompoundNBT tag){
        tag.putShort("Width", data.width);
        tag.putShort("Height", data.height);
        tag.putIntArray("Pixels", data.pixels);
    }

    public static PaintingData readFrom(CompoundNBT tag){
        // Used on server to initialize a Painting...
        return new PaintingData(tag.getShort("Width"), tag.getShort("Height"), tag.getIntArray("Pixels"), false, 20021222);
    }

    private boolean isLegal(int x, int y){
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public int[] getPixels(){
        return pixels;
    }

    public int[] getComposite(){
        return composite;
    }

    public void setPixels(int[] pixels){
        if (pixels.length == this.pixels.length)
            this.pixels = pixels;
        if (isClient)
            recalculateComposite();
    }

    private void recalculateComposite(){
        for (int x = 0;x < width;x++)
            for (int y = 0;y < height;y++)
                composite[y * width + x] = NekoColors.getRGBColorBetween(((pixels[y * width + x] >> 24) & 0xff) / 255.0D, canvas[y * width + x] , pixels[y * width + x]);
    }

    public void setPixel(int x, int y, int color){
        if (isLegal(x, y)){
            pixels[y * width + x] = color;
            //System.out.println("Pixel set @[" + x + ", " + y + "] Opacity: " + ((pixels[y * width + x] & 0xff000000)));
            //System.out.printf("%x\n", ((pixels[y * width + x] >> 24) & 0xff));
            if (isClient)
                recalculateCompositeAt(x, y);
        }
    }

    private void recalculateCompositeAt(int x, int y){
        // double opacity = (pixels[y * width + x] >> 24) / 255.0D;
        composite[y * width + x] = NekoColors.getRGBColorBetween(((pixels[y * width + x] >> 24) & 0xff) / 255.0D, canvas[y * width + x] , pixels[y * width + x]);
    }

    public int getCompositeAt(int x, int y){
        return composite[x + y * width];
    }

    public short getWidth(){
        return width;
    }

    public short getHeight(){
        return height;
    }
}