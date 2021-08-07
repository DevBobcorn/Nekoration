package com.devbobcorn.nekoration.entities;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Arrays;
import java.util.Random;

import java.awt.image.BufferedImage;

import com.devbobcorn.nekoration.NekoColors;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;

public class PaintingData {
    private short width;
    private short height;
    private int seed;

    public final boolean isClient;

    private int[] canvas;    // Fully Opaque, client-only
    private int[] pixels;    // Allows Transparency, for both sides
    private int[] composite; // The final painting, fully Opaque, client-only, for rendering

    private int paintingHash;// Client-only, used to get the corresponding PaintingImageRenderer of a painting

    public boolean imageReady = false;

    public PaintingData(short w, short h, boolean client, int seed){
        width = w;
        height = h;
        pixels = new int[w * h];
        this.seed = seed;
        isClient = client;
        if (isClient){
            Random random = new Random(seed); // Use the entity's id as a seed to ensure that each PaintingEntity's wooden frame is unique and constant...
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
            updatePaintingHash();
            save(String.valueOf(getPaintingHash()), true);
        }
    }

    public PaintingData(short w, short h, int[] pix, boolean client, int seed){
        width = w;
        height = h;
        pixels = pix;
        this.seed = seed;
        isClient = client;
        if (isClient){
            Random random = new Random(seed); // Use the entity's id as a seed to ensure that each PaintingEntity's wooden frame is unique and constant...
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
            updatePaintingHash();
            imageReady = true;
        }
    }
    
    public boolean save(String name, boolean composite){
        try {
            Minecraft minecraft = Minecraft.getInstance();
        
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0;i < width;i++)
                for (int j = 0;j < height;j++) {
                    // The composite layer does not contain alpha values, and we need to make it fully opaque here...
                    image.setRGB(i, j, composite ? 0xff000000 + getCompositeAt(i, j) : getPixelAt(i, j));
                }
            File file = new File(new File(minecraft.gameDirectory, "paintings"), composite ? "composite" : "pixels");
            if (!file.exists() && !file.mkdir())
                throw new IOException("Could not create folder");
            final File finalFile = new File(file, name + ".png");
            if (!ImageIO.write(image, "png", finalFile))
                throw new IOException("Could not encode image as png!");
            /*
            IFormattableTextComponent component = new StringTextComponent(finalFile.getName());
            component = component.withStyle(TextFormatting.UNDERLINE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, finalFile.getAbsolutePath())));
            minecraft.player.displayClientMessage(new TranslationTextComponent("gui.nekoration.message." + (composite ? "painting_saved" : "painting_content_saved"), component), false);
            */
            imageReady = true;
            return true;
        } catch (IOException e) {
            imageReady = false;
            return false;
        }
    }

    private void updatePaintingHash(){
        paintingHash = Arrays.hashCode(composite);
    }

    public int getPaintingHash(){
        return paintingHash;
    }

    public static void writeTo(PaintingData data, CompoundNBT tag){
        tag.putShort("Width", data.width);
        tag.putShort("Height", data.height);
        tag.putIntArray("Pixels", data.pixels);
        tag.putInt("Seed", data.seed);
    }

    public static PaintingData readFrom(CompoundNBT tag){
        // Used on server to initialize a Painting...
        return new PaintingData(tag.getShort("Width"), tag.getShort("Height"), tag.getIntArray("Pixels"), false, tag.getInt("Seed"));
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
        updatePaintingHash();
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
        updatePaintingHash();
    }

    public int getCompositeAt(int x, int y){
        return composite[x + y * width];
    }

    public int getPixelAt(int x, int y){
        return pixels[x + y * width];
    }

    public short getWidth(){
        return width;
    }

    public short getHeight(){
        return height;
    }
}