package io.devbobcorn.nekoration.entities;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.mojang.logging.LogUtils;

import io.devbobcorn.nekoration.NekoColors;
import io.devbobcorn.nekoration.xplat.NekoPlatform;
import io.devbobcorn.nekoration.utils.PixelPos;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

import org.slf4j.Logger;

public class PaintingData {
    public static final Logger LOGGER = LogUtils.getLogger();

    private short width;
    private short height;
    // This is the 'data id', or to say, the original painting entity's uuid.
    // for a painting entity created from a blank painting item, its 'data id'
    // is the same as its own entity uuid, and for its duplications(or modified
    // version from it) this 'data id' will also stay unchanged.
    // This value is used as a seed to calculate the wooden pattern of a
    // painting's frame, and should therefore keep the same among an original
    // and its duplications, to ensure they're exactly the same, despite of
    // owning different entity uuids. On the other hand, it can be used to
    // indicate a painting's origin, enabling authorship/signiture features...
    private final UUID uuid;

    // Signature data; null until the painting gets signed...
    private String title;
    private String author;

    public final boolean isClient;

    private int[] canvas;    // Fully Opaque, client-only
    private int[] pixels;    // Allows Transparency, for both sides
    private int[] composite; // The final painting, fully Opaque, client-only, for rendering

    private int paintingHash;// Client-only, used to get the corresponding PaintingImageRenderer of a painting

    public boolean imageReady = false;

    public PaintingData(short w, short h, boolean client, UUID seed) {
        width = w;
        height = h;
        pixels = new int[w * h];
        this.uuid = seed;
        isClient = client;
        if (isClient) {
            Random random = new Random(seed.hashCode()); // Use the entity's id as a seed to ensure that each PaintingEntity's wooden frame is unique and constant...
            // Initialize canvas layer and composite layer as an empty canvas...
            canvas = new int[w * h];
            composite = new int[w * h];
            for (int i = 0; i < w; i++)
                for (int j = 0; j < h; j++)
                    composite[i + j * w] = canvas[i + j * w] = (i % 16 == 0 || j % 16 == 0) ? 0xBAA080 : 0xEAD6B0;
            int bottom = (h - 1) * w;
            int right = w - 1;
            for (int i = 0; i < w; i++) { // Wooden: 187 131 53 random range:30
                composite[i] = canvas[i] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
                composite[i + bottom] = canvas[i + bottom] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
            }
            for (int j = 1; j < h - 1; j++) {
                composite[j * w] = canvas[j * w] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
                composite[right + j * w] = canvas[right + j * w] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
            }
            updatePaintingHash();
            if (NekoPlatform.config().useImageRendering())
                cache();
        }
    }

    public PaintingData(short w, short h, int[] pix, boolean client, UUID seed) {
        width = w;
        height = h;
        pixels = pix;
        this.uuid = seed;
        isClient = client;
        if (isClient) {
            Random random = new Random(seed.hashCode()); // Use the entity's id as a seed to ensure that each PaintingEntity's wooden frame is unique and constant...
            // Initialize the canvas layer as an empty canvas...
            canvas = new int[w * h];
            for (int i = 0; i < w; i++)
                for (int j = 0; j < h; j++)
                    canvas[i + j * w] = (i % 16 == 0 || j % 16 == 0) ? 0xBAA080 : 0xEAD6B0;
            int bottom = (h - 1) * w;
            int right = w - 1;
            for (int i = 0; i < w; i++) { // Wooden: 187 131 53 random range:30
                canvas[i] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
                canvas[i + bottom] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
            }
            for (int j = 1; j < h - 1; j++) {
                canvas[j * w] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
                canvas[right + j * w] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
            }
            // Intialize the composite layer...
            composite = new int[w * h];
            recalculateComposite();
            updatePaintingHash();
            if (NekoPlatform.config().useImageRendering())
                imageReady = cache();
        }
    }

    private static final String CACHE_PATH = "nekocache";

    private static final File GAME_DIR = io.devbobcorn.nekoration.xplat.NekoPlatform.gameDir().toFile();

    public boolean cache() {
        // First check if the file's already cached
        final File pathCheck = new File(GAME_DIR, CACHE_PATH);
        if (pathCheck.isDirectory()) {
            final File fileCheck = new File(pathCheck, getPaintingHash() + ".png");
            if (fileCheck.exists()) {
                LOGGER.info("Painting #" + getPaintingHash() + " already cached.");
                return true;
            }
        }
        return (imageReady = save(CACHE_PATH, String.valueOf(getPaintingHash()), true, false));
    }

    @SuppressWarnings("null")
    public boolean save(String path, String name, boolean composite, boolean showMessage) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++) {
                    // The composite layer does not contain alpha values, and we need to make it fully opaque here...
                    image.setRGB(i, j, composite ? 0xff000000 + getCompositeAt(i, j) : getPixelAt(i, j));
                }
            File folder = new File(GAME_DIR, path);
            if (!folder.exists() && !folder.mkdir())
                throw new IOException("Could not create folder");

            List<String> suf = Arrays.asList("png", "jpeg", "jpg");
            String ext = name.toLowerCase().substring(name.lastIndexOf(".") + 1);
            boolean hasExt = suf.contains(ext);
            final File file = new File(folder, hasExt ? name : name + ".png");
            if (!hasExt) ext = "png";

            LOGGER.info("Painting saved to " + file.getAbsolutePath() + " in " + ext + " format.");
            if (!ImageIO.write(image, ext, file))
                throw new IOException("Could not encode image as specified(" + ext + ")!");

            if (showMessage) {
                MutableComponent component = Component.literal(file.getName());
                component = component.withStyle(ChatFormatting.UNDERLINE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
                io.devbobcorn.nekoration.client.ClientHelper.displayClientMessage(
                        Component.translatable("gui.nekoration.message." + (composite ? "painting_saved" : "painting_content_saved"), component));
            }
            return true;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            imageReady = false;
            return false;
        }
    }

    @SuppressWarnings("null")
    public boolean load(String path, String name, int dstLeft, int dstTop, int srcLeft, int srcTop, double scale, int widthLimit, int heightLimit) {
        scale = Mth.clamp(scale, 0.01, 100.0);
        srcLeft = Math.max(srcLeft, 0);
        srcTop = Math.max(srcTop, 0);

        try {
            BufferedImage image;

            if (path.equals("<url>")) { // Load from URL...
                image = ImageIO.read(URI.create(name).toURL());
            } else { // Load from Local Path...
                final File folder = new File(GAME_DIR, path);
                if (!folder.exists())
                    throw new IOException("Could not find folder");

                List<String> suf = Arrays.asList("png", "jpeg", "jpg");
                String ext = name.toLowerCase().substring(name.lastIndexOf(".") + 1);
                boolean hasExt = suf.contains(ext);
                final File file = new File(folder, hasExt ? name : name + ".png");

                if (!file.exists())
                    throw new IOException("Could not find file");
                image = ImageIO.read(file);
            }

            if (image == null) {
                LOGGER.error("Image is not available!");
                return false;
            }

            if ((srcLeft > 0 || srcTop > 0 || widthLimit < image.getWidth() || heightLimit < image.getHeight()) && (srcLeft < image.getWidth() && srcTop < image.getHeight())) {
                int cropW = Math.min(image.getWidth() - srcLeft, widthLimit);
                int cropH = Math.min(image.getHeight() - srcTop, heightLimit);
                if (cropW > 0 && cropH > 0)
                    image = image.getSubimage(srcLeft, srcTop, cropW, cropH);
            }

            if (scale != 1.0) {
                short newW = (short) Math.ceil(image.getWidth() * scale);
                short newH = (short) Math.ceil(image.getHeight() * scale);
                Image scaled = image.getScaledInstance(newW, newH, Image.SCALE_DEFAULT);
                BufferedImage scaledImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaledImage.createGraphics();
                g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
                g2d.drawImage(scaled, 0, 0, newW, newH, null);
                drawImage(scaledImage, dstLeft, dstTop);
            } else {
                drawImage(image, dstLeft, dstTop);
            }
            recalculateComposite();
            LOGGER.info(String.format("Painting '%s' Loaded: %s x %s", name, image.getWidth(), image.getHeight()));
            return true;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            MutableComponent component = Component.literal(name);
            io.devbobcorn.nekoration.client.ClientHelper.displayClientMessage(
                    Component.translatable("gui.nekoration.message.painting_load_failed", component));
            return false;
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
    }

    private void drawImage(BufferedImage image, int dstLeft, int dstTop) {
        for (int i = Math.max(dstLeft, 0); i < Math.min(dstLeft + image.getWidth(), width); i++)
            for (int j = Math.max(dstTop, 0); j < Math.min(dstTop + image.getHeight(), height); j++)
                pixels[j * width + i] = image.getRGB(i - dstLeft, j - dstTop);
    }

    public boolean clearCache(int target) {
        final File path = new File(GAME_DIR, CACHE_PATH);
        if (path.isDirectory()) {
            final File file = new File(path, target + ".png");
            if (file.delete()) {
                LOGGER.info("Painting #" + target + " cache cleared.");
                return true;
            }
        }
        return false;
    }

    private void updatePaintingHash() {
        paintingHash = Arrays.hashCode(composite);
    }

    public UUID getUUID() {
        return this.uuid;
    }

    /** A painting is considered signed once an author has been set. */
    public boolean isSigned() {
        return this.author != null;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setSignature(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public int getPaintingHash() {
        return paintingHash;
    }

    public static void writeTo(PaintingData data, CompoundTag tag) {
        tag.putShort("Width", data.width);
        tag.putShort("Height", data.height);
        tag.putIntArray("Pixels", data.pixels);
        tag.putUUID("DataID", data.uuid);
        if (data.author != null) { // Signed...
            tag.putString("Title", data.title == null ? "" : data.title);
            tag.putString("Author", data.author);
        }
    }

    public static PaintingData readFrom(CompoundTag tag, UUID defaultId) {
        // Used on server to initialize a Painting...
        UUID dataid = tag.hasUUID("DataID") ? tag.getUUID("DataID") : defaultId;
        PaintingData data = new PaintingData(tag.getShort("Width"), tag.getShort("Height"), tag.getIntArray("Pixels"), false, dataid);
        if (tag.contains("Author")) // Signed...
            data.setSignature(tag.getString("Title"), tag.getString("Author"));
        return data;
    }

    private boolean isLegal(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public int[] getPixels() {
        return pixels;
    }

    public int[] getComposite() {
        return composite;
    }

    public void setPixels(int[] pixels) {
        if (pixels.length == this.pixels.length)
            this.pixels = Arrays.copyOf(pixels, pixels.length);
        if (isClient)
            recalculateComposite();
    }

    public void setAreaPixels(byte partX, byte partY, byte partW, byte partH, int[] pixels) {
        for (int i = 0; i < partW * 16; i++)
            for (int j = 0; j < partH * 16; j++) {
                int x = partX * 3 * 16 + i;
                int y = partY * 3 * 16 + j;
                if (!isLegal(x, y))
                    LOGGER.error("Illegal Coordinate: " + x + ", " + y);
                this.pixels[y * width + x] = pixels[j * partW * 16 + i];
                // Painting Coord.         => Part Coord.
            }
        if (isClient)
            recalculateComposite();
    }

    public void setPixel(int x, int y, int color, boolean blend) {
        if (isLegal(x, y)) {
            pixels[y * width + x] = blend ? blendColor(pixels[y * width + x], color) : color;
            if (isClient)
                recalculateCompositeAt(x, y);
        }
    }

    public void clearPixel(int x, int y) {
        if (isLegal(x, y)) {
            pixels[y * width + x] = 0x00000000;
            if (isClient)
                recalculateCompositeAt(x, y);
        }
    }

    private static final int canvasize = 128;
    boolean[][] visited = new boolean[canvasize][canvasize];

    public int fill(int x, int y, int color, int opacity, int threshold, boolean blend) {
        if (!isLegal(x, y))
            return 0;

        for (int i = 0; i < canvasize; i++)
            for (int j = 0; j < canvasize; j++) {
                visited[i][j] = false;
            }

        pixSearch(x, y, threshold);
        int count = 0;
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                if (visited[i][j]) {
                    count++;
                    pixels[i + j * width] = blend ? blendColor(pixels[i + j * width], (opacity << 24) + color) : (opacity << 24) + color;
                }
            }
        recalculateComposite();
        return count;
    }

    private boolean checkAvailable(PixelPos pix) {
        if (!isLegal(pix.x, pix.y))
            return false;
        return !visited[pix.x][pix.y];
    }

    private void setVisited(PixelPos pix) {
        visited[pix.x][pix.y] = true;
    }

    private boolean checkColor(int origin, PixelPos pix, int threshold) {
        int target = getCompositeAt(pix.x, pix.y);
        float ro = (origin & 0xff0000) - (target & 0xff0000);
        float go = (origin & 0xff00) - (target & 0xff00);
        float bo = (origin & 0xff) - (target & 0xff);
        return Mth.sqrt(ro * ro + go * go + bo * bo) < threshold * threshold;
    }

    private static final int[] offsetX = { 1, -1, 0, 0, 1, -1, 1, -1 };
    private static final int[] offsetY = { 0, 0, 1, -1, 1, 1, -1, -1 };
    private boolean connectDiagonal = false;

    private void pixSearch(int x, int y, int threshold) {
        final int originColor = getCompositeAt(x, y);
        // BFS...
        Queue<PixelPos> queue = new LinkedList<PixelPos>();
        queue.add(new PixelPos(x, y));
        visited[x][y] = true;

        while (!queue.isEmpty()) {
            PixelPos pix = queue.poll();
            for (int i = 0; i < (connectDiagonal ? 8 : 4); i++) {
                PixelPos tar = pix.offset(offsetX[i], offsetY[i]);

                if (checkAvailable(tar) && checkColor(originColor, tar, threshold)) {
                    queue.add(tar);
                    setVisited(tar);
                }
            }
        }
    }

    private void recalculateComposite() {
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                composite[y * width + x] = NekoColors.getRGBColorBetween(((pixels[y * width + x] >> 24) & 0xff) / 255.0D, canvas[y * width + x], pixels[y * width + x]);
        updatePaintingHash();
    }

    private void recalculateCompositeAt(int x, int y) {
        composite[y * width + x] = NekoColors.getRGBColorBetween(((pixels[y * width + x] >> 24) & 0xff) / 255.0D, canvas[y * width + x], pixels[y * width + x]);
        updatePaintingHash();
    }

    public int getCompositeAt(int x, int y) {
        return composite[x + y * width];
    }

    public int getPixelAt(int x, int y) {
        return pixels[x + y * width];
    }

    public short getWidth() {
        return width;
    }

    public short getHeight() {
        return height;
    }

    private static int blendColor(int desc, int tarc) {
        // The alpha bits of an int representing a color contains the sign, and we need to use '>>>' to move the bits
        int tara = (tarc & 0xff000000) >>> 24;
        // Transparency Blend
        int desa = (desc & 0xff000000) >>> 24;
        double blendfrac = Mth.clamp((double) tara / (double) (tara + desa), 0.0, 1.0);
        int blenda = Mth.clamp(tara + desa, 0, 255);
        return NekoColors.getRGBColorBetween(blendfrac, desc, tarc) + (blenda << 24);
    }
}
