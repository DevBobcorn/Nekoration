package io.devbobcorn.nekoration.datagen;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import com.google.common.hash.HashCode;

import io.devbobcorn.nekoration.Nekoration;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

/**
 * Datagen equivalent of the legacy Python wooden texture generators.
 */
public final class WoodenTextureAssetProvider implements DataProvider {
    private static final String[] TINT_TEXTURE_FOLDERS = {"half_timber", "window"};

    private static final Map<String, Integer> WOOD_COLORS = new LinkedHashMap<>();

    static {
        WOOD_COLORS.put("oak", 0xB9955B);
        WOOD_COLORS.put("spruce", 0x886541);
        WOOD_COLORS.put("birch", 0xE8D699);
        WOOD_COLORS.put("jungle", 0xB38564);
        WOOD_COLORS.put("acacia", 0xB4653A);
        WOOD_COLORS.put("dark_oak", 0x5C3C1B);
        WOOD_COLORS.put("mangrove", 0x753630);
        WOOD_COLORS.put("cherry", 0xE2B2AC);
        WOOD_COLORS.put("bamboo", 0xC1AD50);
        WOOD_COLORS.put("crimson", 0x873468);
        WOOD_COLORS.put("warped", 0x389A99);
    }

    private final Path templateTextureRoot;
    private final Path generatedTextureRoot;
    private int writtenTextureCount;

    public WoodenTextureAssetProvider(PackOutput output) {
        this.templateTextureRoot = resolveTemplateTextureRoot();
        this.generatedTextureRoot = templateTextureRoot.getParent()
                .resolve("src/generated/resources/assets/" + Nekoration.MODID + "/textures/block");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        writtenTextureCount = 0;
        Nekoration.LOGGER.info("Generating textures from {} to {}", templateTextureRoot, generatedTextureRoot);
        try {
            generateTintedTextures(cachedOutput);
            generateHalfTimberBackTextures(cachedOutput);
        } catch (IOException e) {
            throw new IllegalStateException("Failed generating wooden textures", e);
        }
        if (writtenTextureCount == 0) {
            throw new IllegalStateException("Texture provider wrote no files; check script template paths.");
        }
        Nekoration.LOGGER.info("Generated {} texture files", writtenTextureCount);
        return CompletableFuture.completedFuture(null);
    }

    private void generateTintedTextures(CachedOutput cachedOutput) throws IOException {
        for (String textureFolder : TINT_TEXTURE_FOLDERS) {
            Path templateFolder = templateTextureRoot.resolve(textureFolder + "_template");
            if (!Files.isDirectory(templateFolder)) {
                continue;
            }

            try (var templates = Files.list(templateFolder)) {
                templates
                        .filter(path -> path.getFileName().toString().endsWith(".png"))
                        .forEach(templatePath -> {
                            try {
                                BufferedImage source = ImageIO.read(templatePath.toFile());
                                if (source == null) {
                                    return;
                                }

                                String fileName = templatePath.getFileName().toString();
                                String stem = fileName.substring(0, fileName.length() - 4);
                                for (Map.Entry<String, Integer> woodAndColor : WOOD_COLORS.entrySet()) {
                                    BufferedImage tinted = tintMultiply(source, woodAndColor.getValue());
                                    writeTexture(cachedOutput,
                                            textureFolder + "/" + woodAndColor.getKey() + "/" + stem, tinted);
                                }
                            } catch (IOException e) {
                                throw new IllegalStateException("Failed tinting texture " + templatePath, e);
                            }
                        });
            }
        }
    }

    private void generateHalfTimberBackTextures(CachedOutput cachedOutput) throws IOException {
        Path basePath = templateTextureRoot.resolve("white_concrete.png");
        if (!Files.exists(basePath)) {
            return;
        }

        BufferedImage baseImage = ImageIO.read(basePath.toFile());
        if (baseImage == null) {
            return;
        }

        Path overlayFolder = templateTextureRoot.resolve("half_timber_shadow");
        if (!Files.isDirectory(overlayFolder)) {
            return;
        }

        try (var overlays = Files.list(overlayFolder)) {
            overlays
                    .filter(path -> path.getFileName().toString().endsWith(".png"))
                    .forEach(overlayPath -> {
                        try {
                            BufferedImage overlay = ImageIO.read(overlayPath.toFile());
                            if (overlay == null) {
                                return;
                            }

                            BufferedImage composed = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(),
                                    BufferedImage.TYPE_INT_ARGB);
                            Graphics2D graphics = composed.createGraphics();
                            try {
                                graphics.drawImage(baseImage, 0, 0, null);
                                graphics.drawImage(overlay, 0, 0, null);
                            } finally {
                                graphics.dispose();
                            }

                            String fileName = overlayPath.getFileName().toString();
                            String stem = fileName.substring(0, fileName.length() - 4);
                            writeTexture(cachedOutput, "half_timber_back/" + stem, composed);
                        } catch (IOException e) {
                            throw new IllegalStateException("Failed composing half-timber back texture " + overlayPath, e);
                        }
                    });
        }
    }

    private void writeTexture(CachedOutput cachedOutput, String texturePath, BufferedImage image) throws IOException {
        Path outPath = generatedTextureRoot.resolve(texturePath + ".png");
        Files.createDirectories(outPath.getParent());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] data = baos.toByteArray();
        cachedOutput.writeIfNeeded(outPath, data, HashCode.fromBytes(data));
        writtenTextureCount++;
    }

    private static BufferedImage tintMultiply(BufferedImage source, int rgbTint) {
        int tintR = (rgbTint >> 16) & 0xFF;
        int tintG = (rgbTint >> 8) & 0xFF;
        int tintB = rgbTint & 0xFF;

        BufferedImage out = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                int r = (argb >>> 16) & 0xFF;
                int g = (argb >>> 8) & 0xFF;
                int b = argb & 0xFF;

                int outR = (r * tintR) / 255;
                int outG = (g * tintG) / 255;
                int outB = (b * tintB) / 255;
                int tintedArgb = (a << 24) | (outR << 16) | (outG << 8) | outB;
                out.setRGB(x, y, tintedArgb);
            }
        }
        return out;
    }

    private static Path resolveTemplateTextureRoot() {
        Path probe = Path.of("").toAbsolutePath();
        for (Path current = probe; current != null; current = current.getParent()) {
            Path candidate = current.resolve("generator_files");
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not locate generator_files from " + probe);
    }

    @Override
    public String getName() {
        return "Nekoration wooden textures";
    }
}
