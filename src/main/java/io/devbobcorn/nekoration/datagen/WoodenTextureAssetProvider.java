package io.devbobcorn.nekoration.datagen;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private static final Set<String> IMAGE_SUFFIXES = Set.of(".png", ".bmp", ".webp");
    private static final String SOURCE_PALETTE_FILENAME = "grayscale.png";
    private static final Map<String, String> WINDOW_OVERLAYS = Map.of(
            "window_simple.png", "window_glass.png",
            "window_simple_t0.png", "window_glass_t0.png",
            "window_simple_t1.png", "window_glass_t1.png",
            "window_simple_t2.png", "window_glass_t2.png");
    private static final Map<String, String> CONTAINER_OVERLAYS = Map.of(
            "cabinet_front.png", "cabinet_knob.png",
            "cupboard_front_d0.png", "cupboard_glass.png",
            "cupboard_front_d1.png", "cupboard_glass.png",
            "drawer_front.png", "drawer_knob.png",
            "drawer_chest_front.png", "drawer_chest_knob.png",
            "drawer_chest_open_front.png", "drawer_chest_open_knob.png",
            "easel_menu.png", "easel_menu_board.png");

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
            generatePaletteMappedTextures(cachedOutput);
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

    private void generatePaletteMappedTextures(CachedOutput cachedOutput) throws IOException {
        Path paletteDir = templateTextureRoot.resolve("plank_palettes");
        List<Path> palettes = collectImages(paletteDir);
        Path sourcePalettePath = paletteDir.resolve(SOURCE_PALETTE_FILENAME);
        if (!Files.isRegularFile(sourcePalettePath)) {
            throw new IllegalStateException("Configured source palette not found: " + sourcePalettePath);
        }

        List<Path> targetPalettes = palettes.stream()
                .filter(path -> !path.getFileName().toString().equals(SOURCE_PALETTE_FILENAME))
                .collect(Collectors.toList());
        if (targetPalettes.isEmpty()) {
            throw new IllegalStateException("No target palettes found after excluding source palette.");
        }

        generateMappedTextureFolder(cachedOutput, "half_timber", Map.of(), sourcePalettePath, targetPalettes);
        generateMappedTextureFolder(cachedOutput, "window", WINDOW_OVERLAYS, sourcePalettePath, targetPalettes);
        generateMappedTextureFolder(cachedOutput, "container", CONTAINER_OVERLAYS, sourcePalettePath, targetPalettes);
    }

    private void generateMappedTextureFolder(
            CachedOutput cachedOutput,
            String textureFolder,
            Map<String, String> overlaysBySourceFile,
            Path sourcePalettePath,
            List<Path> targetPalettes)
            throws IOException {
        Path sourceDir = templateTextureRoot.resolve(textureFolder + "_template");
        if (!Files.isDirectory(sourceDir)) {
            return;
        }
        Path overlayDir = templateTextureRoot.resolve(textureFolder + "_overlay");
        List<Path> sourceImages = collectImages(sourceDir);
        Palette sourcePalette = loadPalette(sourcePalettePath);

        for (Path sourceImagePath : sourceImages) {
            BufferedImage sourceImage = readImage(sourceImagePath);
            BufferedImage overlayImage = null;
            String overlayName = overlaysBySourceFile.get(sourceImagePath.getFileName().toString());
            if (overlayName != null) {
                Path overlayPath = overlayDir.resolve(overlayName);
                if (!Files.isRegularFile(overlayPath)) {
                    throw new IllegalStateException("Missing overlay '" + overlayName + "' for source '"
                            + sourceImagePath.getFileName() + "' in " + overlayDir);
                }
                overlayImage = readImage(overlayPath);
            }

            for (Path targetPalettePath : targetPalettes) {
                Palette targetPalette = loadPalette(targetPalettePath);
                Map<Integer, Integer> colorMapping = buildColorMapping(
                        sourcePalette, targetPalette, sourcePalettePath, targetPalettePath);
                BufferedImage mapped = remapImage(sourceImage, colorMapping, sourceImagePath);
                if (overlayImage != null) {
                    mapped = composeOverlay(mapped, overlayImage, sourceImagePath);
                }
                String targetWoodName = stripExtension(targetPalettePath.getFileName().toString());
                String textureName = stripExtension(sourceImagePath.getFileName().toString());
                writeTexture(cachedOutput, textureFolder + "/" + targetWoodName + "/" + textureName, mapped);
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

    private Palette loadPalette(Path palettePath) throws IOException {
        BufferedImage paletteImage = readImage(palettePath);
        if (paletteImage.getHeight() != 1) {
            throw new IllegalStateException(
                    "Palette '" + palettePath + "' must be <color_count>x1 but was "
                            + paletteImage.getWidth() + "x" + paletteImage.getHeight());
        }
        List<Integer> colors = new ArrayList<>(paletteImage.getWidth());
        for (int x = 0; x < paletteImage.getWidth(); x++) {
            colors.add(paletteImage.getRGB(x, 0));
        }
        return new Palette(colors, paletteImage.getWidth());
    }

    private Map<Integer, Integer> buildColorMapping(
            Palette sourcePalette,
            Palette targetPalette,
            Path sourcePalettePath,
            Path targetPalettePath) {
        if (sourcePalette.width() != targetPalette.width()) {
            throw new IllegalStateException(
                    "Palette sizes must match. Source " + sourcePalettePath.getFileName() + " has "
                            + sourcePalette.width() + " colors, target " + targetPalettePath.getFileName()
                            + " has " + targetPalette.width());
        }

        Map<Integer, Integer> mapping = new HashMap<>();
        List<Integer> sourceColors = sourcePalette.colors();
        List<Integer> targetColors = targetPalette.colors();
        for (int i = 0; i < sourceColors.size(); i++) {
            int source = sourceColors.get(i);
            int target = targetColors.get(i);
            Integer previous = mapping.putIfAbsent(source, target);
            if (previous != null && previous.intValue() != target) {
                throw new IllegalStateException(
                        "Ambiguous mapping at palette index " + i + " in " + sourcePalettePath.getFileName()
                                + " -> " + targetPalettePath.getFileName());
            }
        }
        return mapping;
    }

    private BufferedImage remapImage(BufferedImage source, Map<Integer, Integer> mapping, Path sourcePath) {
        BufferedImage out = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Set<Integer> unmapped = new LinkedHashSet<>();

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);
                if (((argb >>> 24) & 0xFF) == 0) {
                    out.setRGB(x, y, argb);
                    continue;
                }

                Integer mapped = mapping.get(argb);
                if (mapped == null) {
                    unmapped.add(argb);
                    continue;
                }
                out.setRGB(x, y, mapped);
            }
        }

        if (!unmapped.isEmpty()) {
            String preview = unmapped.stream()
                    .limit(10)
                    .map(WoodenTextureAssetProvider::formatArgb)
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException(
                    "Source image contains colors not present in source palette: " + sourcePath.getFileName()
                            + ". Unmapped color count: " + unmapped.size() + ". Example(s): " + preview);
        }
        return out;
    }

    private BufferedImage composeOverlay(BufferedImage mapped, BufferedImage overlay, Path sourcePath) {
        if (mapped.getWidth() != overlay.getWidth() || mapped.getHeight() != overlay.getHeight()) {
            throw new IllegalStateException(
                    "Overlay size " + overlay.getWidth() + "x" + overlay.getHeight()
                            + " does not match mapped texture size " + mapped.getWidth() + "x" + mapped.getHeight()
                            + " for " + sourcePath.getFileName());
        }

        BufferedImage composed = new BufferedImage(mapped.getWidth(), mapped.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = composed.createGraphics();
        try {
            graphics.setComposite(AlphaComposite.SrcOver);
            graphics.drawImage(mapped, 0, 0, null);
            graphics.drawImage(overlay, 0, 0, null);
        } finally {
            graphics.dispose();
        }
        return composed;
    }

    private static BufferedImage readImage(Path path) throws IOException {
        BufferedImage image = ImageIO.read(path.toFile());
        if (image == null) {
            throw new IOException("Failed to decode image file: " + path);
        }
        return image;
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }

    private static String extensionOf(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        int dot = name.lastIndexOf('.');
        if (dot < 0) {
            return "";
        }
        return name.substring(dot);
    }

    private List<Path> collectImages(Path directory) throws IOException {
        if (!Files.isDirectory(directory)) {
            throw new IllegalStateException("Directory does not exist: " + directory);
        }
        try (Stream<Path> files = Files.list(directory)) {
            List<Path> paths = files
                    .filter(Files::isRegularFile)
                    .filter(path -> IMAGE_SUFFIXES.contains(extensionOf(path)))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .collect(Collectors.toList());
            if (paths.isEmpty()) {
                throw new IllegalStateException("No image files found in directory: " + directory);
            }
            return paths;
        }
    }

    private static String formatArgb(int argb) {
        int a = (argb >>> 24) & 0xFF;
        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = argb & 0xFF;
        return "(" + r + ", " + g + ", " + b + ", " + a + ")";
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

    private record Palette(List<Integer> colors, int width) {
    }
}
