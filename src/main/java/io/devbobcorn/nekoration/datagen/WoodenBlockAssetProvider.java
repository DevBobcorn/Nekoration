package io.devbobcorn.nekoration.datagen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.NekoWood;
import io.devbobcorn.nekoration.registry.WoodenBlockRegistration;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

/**
 * Datagen equivalent of the legacy Python wooden block asset scripts.
 */
public final class WoodenBlockAssetProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final List<String> CONNECTION_IDS = List.of("s0", "d0", "d1", "t0", "t1", "t2");
    private static final List<String> COLOR_IDS = Arrays.stream(EnumNekoColor.values())
            .map(EnumNekoColor::getSerializedName)
            .sorted()
            .toList();

    private final PackOutput.PathProvider blockstatePathProvider;
    private final PackOutput.PathProvider blockModelPathProvider;
    private final PackOutput.PathProvider itemModelPathProvider;
    private final Path containerTemplateModelRoot;

    public WoodenBlockAssetProvider(PackOutput output) {
        this.blockstatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.blockModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
        this.itemModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");
        this.containerTemplateModelRoot = resolveContainerTemplateModelRoot();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        List<CompletableFuture<?>> writes = new ArrayList<>();
        for (NekoWood wood : NekoWood.values()) {
            String woodId = wood.id();
            generateContainerAssets(cachedOutput, writes, woodId);
            generateFurnitureAssets(cachedOutput, writes, woodId);
            generateWindowAssets(cachedOutput, writes, woodId);
            generateHalfTimberAssets(cachedOutput, writes, woodId);
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    private void generateFurnitureAssets(CachedOutput cachedOutput, List<CompletableFuture<?>> writes, String woodId) {
        String tableId = woodId + "_table";
        writeJson(cachedOutput, writes, blockModelPathProvider, "furniture/" + woodId + "/table",
                Map.of(
                        "parent", modLoc("block/furniture/table"),
                        "textures", Map.of(
                                "top", modLoc("block/furniture/" + woodId + "_top"),
                                "particle", "minecraft:block/" + woodId + "_planks")));
        writeJson(cachedOutput, writes, blockstatePathProvider, tableId,
                Map.of("variants", Map.of("", Map.of("model", modLoc("block/furniture/" + woodId + "/table")))));
        writeJson(cachedOutput, writes, itemModelPathProvider, tableId,
                Map.of("parent", modLoc("block/furniture/" + woodId + "/table")));

        String chairId = woodId + "_chair";
        writeJson(cachedOutput, writes, blockModelPathProvider, "furniture/" + woodId + "/chair",
                Map.of(
                        "parent", modLoc("block/furniture/chair"),
                        "textures", Map.of(
                                "top", modLoc("block/furniture/" + woodId + "_top"),
                                "side", "minecraft:block/" + woodId + "_planks",
                                "particle", "minecraft:block/" + woodId + "_planks")));
        Map<String, Object> chairVariants = new LinkedHashMap<>();
        chairVariants.put("facing=north", Map.of("model", modLoc("block/furniture/" + woodId + "/chair")));
        chairVariants.put("facing=east", Map.of("model", modLoc("block/furniture/" + woodId + "/chair"), "y", 90));
        chairVariants.put("facing=south", Map.of("model", modLoc("block/furniture/" + woodId + "/chair"), "y", 180));
        chairVariants.put("facing=west", Map.of("model", modLoc("block/furniture/" + woodId + "/chair"), "y", 270));
        writeJson(cachedOutput, writes, blockstatePathProvider, chairId, Map.of("variants", chairVariants));
        writeJson(cachedOutput, writes, itemModelPathProvider, chairId,
                Map.of("parent", modLoc("block/furniture/" + woodId + "/chair")));
    }

    private void generateContainerAssets(CachedOutput cachedOutput, List<CompletableFuture<?>> writes, String woodId) {
        for (WoodenBlockRegistration.ContainerVariant variant : WoodenBlockRegistration.ContainerVariant.values()) {
            String variantId = variant.id();
            switch (variant) {
                case CABINET -> {
                    writeContainerModelFromTemplate(cachedOutput, writes, woodId, "cabinet", "cabinet");
                    writeContainerModelFromTemplate(cachedOutput, writes, woodId, "cabinet_open", "cabinet_open");

                    Map<String, Object> blockstateVariants = new LinkedHashMap<>();
                    for (String facing : List.of("north", "east", "south", "west")) {
                        int y = horizontalRotationY(facing);
                        for (boolean open : List.of(false, true)) {
                            String modelName = open ? "cabinet_open" : "cabinet";
                            String key = "facing=" + facing + ",open=" + open;
                            blockstateVariants.put(key, horizontalFacingVariant("block/container/" + woodId + "/" + modelName, y));
                        }
                    }

                    writeJson(cachedOutput, writes, blockstatePathProvider, woodId + "_" + variantId,
                            Map.of("variants", blockstateVariants));
                    writeJson(cachedOutput, writes, itemModelPathProvider, woodId + "_" + variantId,
                            Map.of("parent", modLoc("block/container/" + woodId + "/cabinet")));
                }
                case CUPBOARD -> {
                    writeContainerModelFromTemplate(cachedOutput, writes, woodId, "cupboard_d0", "cupboard_d0");
                    writeContainerModelFromTemplate(cachedOutput, writes, woodId, "cupboard_d1", "cupboard_d1");

                    Map<String, Object> blockstateVariants = new LinkedHashMap<>();
                    for (String facing : List.of("north", "east", "south", "west")) {
                        int y = horizontalRotationY(facing);
                        for (boolean bottom : List.of(false, true)) {
                            for (boolean open : List.of(false, true)) {
                                String modelName = bottom ? "cupboard_d0" : "cupboard_d1";
                                String key = "bottom=" + bottom + ",facing=" + facing + ",open=" + open;
                                blockstateVariants.put(key,
                                        horizontalFacingVariant("block/container/" + woodId + "/" + modelName, y));
                            }
                        }
                    }

                    writeJson(cachedOutput, writes, blockstatePathProvider, woodId + "_" + variantId,
                            Map.of("variants", blockstateVariants));
                    writeJson(cachedOutput, writes, itemModelPathProvider, woodId + "_" + variantId,
                            Map.of("parent", modLoc("block/container/" + woodId + "/cupboard_d1")));
                }
                case DRAWER -> {
                    writeContainerModelFromTemplate(cachedOutput, writes, woodId, "drawer", "drawer");
                    writeContainerModelFromTemplate(cachedOutput, writes, woodId, "drawer_open", "drawer_open");

                    Map<String, Object> blockstateVariants = new LinkedHashMap<>();
                    for (String facing : List.of("north", "east", "south", "west")) {
                        int y = horizontalRotationY(facing);
                        for (boolean open : List.of(false, true)) {
                            String modelName = open ? "drawer_open" : "drawer";
                            String key = "facing=" + facing + ",open=" + open;
                            blockstateVariants.put(key, horizontalFacingVariant("block/container/" + woodId + "/" + modelName, y));
                        }
                    }

                    writeJson(cachedOutput, writes, blockstatePathProvider, woodId + "_" + variantId,
                            Map.of("variants", blockstateVariants));
                    writeJson(cachedOutput, writes, itemModelPathProvider, woodId + "_" + variantId,
                            Map.of("parent", modLoc("block/container/" + woodId + "/drawer")));
                }
                case DRAWER_CHEST -> {
                    writeContainerModelFromTemplate(cachedOutput, writes, woodId, "drawer_chest", "drawer_chest");
                    writeContainerModelFromTemplate(cachedOutput, writes, woodId, "drawer_chest_open", "drawer_chest_open");

                    Map<String, Object> blockstateVariants = new LinkedHashMap<>();
                    for (String facing : List.of("north", "east", "south", "west")) {
                        int y = horizontalRotationY(facing);
                        for (boolean open : List.of(false, true)) {
                            String modelName = open ? "drawer_chest_open" : "drawer_chest";
                            String key = "facing=" + facing + ",open=" + open;
                            blockstateVariants.put(key, horizontalFacingVariant("block/container/" + woodId + "/" + modelName, y));
                        }
                    }

                    writeJson(cachedOutput, writes, blockstatePathProvider, woodId + "_" + variantId,
                            Map.of("variants", blockstateVariants));
                    writeJson(cachedOutput, writes, itemModelPathProvider, woodId + "_" + variantId,
                            Map.of("parent", modLoc("block/container/" + woodId + "/drawer_chest")));
                }
                case EASEL_MENU -> {
                    writeContainerModelFromTemplate(cachedOutput, writes, woodId, "easel_menu", "easel_menu");

                    Map<String, Object> blockstateVariants = new LinkedHashMap<>();
                    for (String facing : List.of("north", "east", "south", "west")) {
                        int y = horizontalRotationY(facing);
                        String key = "facing=" + facing;
                        blockstateVariants.put(key, horizontalFacingVariant("block/container/" + woodId + "/easel_menu", y));
                    }

                    writeJson(cachedOutput, writes, blockstatePathProvider, woodId + "_" + variantId,
                            Map.of("variants", blockstateVariants));
                    writeJson(cachedOutput, writes, itemModelPathProvider, woodId + "_" + variantId,
                            Map.of("parent", modLoc("block/container/" + woodId + "/easel_menu")));
                }
                case WALL_SHELF -> {
                    writeContainerModelFromTemplate(cachedOutput, writes, woodId, "wall_shelf_s0", "wall_shelf_s0");
                    writeContainerModelFromTemplate(cachedOutput, writes, woodId, "wall_shelf_t0", "wall_shelf_t0");
                    writeContainerModelFromTemplate(cachedOutput, writes, woodId, "wall_shelf_t1", "wall_shelf_t1");
                    writeContainerModelFromTemplate(cachedOutput, writes, woodId, "wall_shelf_t2", "wall_shelf_t2");

                    Map<String, Object> blockstateVariants = new LinkedHashMap<>();
                    for (String facing : List.of("north", "east", "south", "west")) {
                        int y = horizontalRotationY(facing);
                        for (String connectionId : CONNECTION_IDS) {
                            for (boolean open : List.of(false, true)) {
                                String modelName = "wall_shelf_" + wallShelfModelSuffix(connectionId);
                                String key = "facing=" + facing + ",horizontal_connection=" + connectionId + ",open=" + open;
                                blockstateVariants.put(key,
                                        horizontalFacingVariant("block/container/" + woodId + "/" + modelName, y));
                            }
                        }
                    }

                    writeJson(cachedOutput, writes, blockstatePathProvider, woodId + "_" + variantId,
                            Map.of("variants", blockstateVariants));
                    writeJson(cachedOutput, writes, itemModelPathProvider, woodId + "_" + variantId,
                            Map.of("parent", modLoc("block/container/" + woodId + "/wall_shelf_s0")));
                }
            }
        }
    }

    private void generateWindowAssets(CachedOutput cachedOutput, List<CompletableFuture<?>> writes, String woodId) {
        for (WoodenBlockRegistration.WindowVariant variant : WoodenBlockRegistration.WindowVariant.values()) {
            String variantId = variant.id();
            String style = "window_" + variantId;

            for (String connectionId : CONNECTION_IDS) {
                String modelName = style + ("s0".equals(connectionId) ? "" : "_" + connectionId);
                Map<String, Object> model = new LinkedHashMap<>();
                model.put("parent", modLoc("block/window/window"));

                Map<String, Object> textures = new LinkedHashMap<>();
                textures.put("side", modLoc("block/window/" + woodId + "/" + windowTextureStem(style, connectionId)));
                textures.put("end", modLoc("block/window/" + woodId + "/window_top"));
                model.put("textures", textures);

                writeJson(cachedOutput, writes, blockModelPathProvider, "window/" + woodId + "/" + modelName, model);
            }

            Map<String, Object> blockstateVariants = new LinkedHashMap<>();
            for (String connectionId : CONNECTION_IDS) {
                String modelName = style + ("s0".equals(connectionId) ? "" : "_" + connectionId);
                blockstateVariants.put("vertical_connection=" + connectionId,
                        Map.of("model", modLoc("block/window/" + woodId + "/" + modelName)));
            }

            writeJson(cachedOutput, writes, blockstatePathProvider, woodId + "_window_" + variantId,
                    Map.of("variants", blockstateVariants));
            writeJson(cachedOutput, writes, itemModelPathProvider, woodId + "_window_" + variantId,
                    Map.of("parent", modLoc("block/window/" + woodId + "/" + style)));
        }
    }

    private void generateHalfTimberAssets(CachedOutput cachedOutput, List<CompletableFuture<?>> writes, String woodId) {
        for (int patternIndex = 0; patternIndex <= 9; patternIndex++) {
            String baseModelName = "half_timber_p" + patternIndex;
            writeJson(cachedOutput, writes, blockModelPathProvider, "half_timber/" + woodId + "/" + baseModelName,
                    halfTimberCubeModel(woodId, patternIndex));

            Map<String, Object> blockstateVariants = new LinkedHashMap<>();
            for (String colorId : COLOR_IDS) {
                blockstateVariants.put("color=" + colorId,
                        Map.of("model", modLoc("block/half_timber/" + woodId + "/" + baseModelName)));
            }

            String blockId = woodId + "_" + baseModelName;
            writeJson(cachedOutput, writes, blockstatePathProvider, blockId, Map.of("variants", blockstateVariants));
            writeJson(cachedOutput, writes, itemModelPathProvider, blockId,
                    Map.of("parent", modLoc("block/half_timber/" + woodId + "/" + baseModelName)));
        }

        for (int pillarSlot = 0; pillarSlot <= 2; pillarSlot++) {
            String baseModelName = "half_timber_p" + pillarSlot;

            for (String connectionId : CONNECTION_IDS) {
                if ("s0".equals(connectionId)) {
                    continue;
                }
                String connectedModelName = baseModelName + "_" + connectionId;
                writeJson(cachedOutput, writes, blockModelPathProvider, "half_timber/" + woodId + "/" + connectedModelName,
                        halfTimberPillarModel(woodId, pillarSlot, connectionId));
            }

            Map<String, Object> blockstateVariants = new LinkedHashMap<>();
            for (String colorId : COLOR_IDS) {
                for (String connectionId : CONNECTION_IDS) {
                    String modelName = baseModelName + ("s0".equals(connectionId) ? "" : "_" + connectionId);
                    String variantKey = "color=" + colorId + ",vertical_connection=" + connectionId;
                    blockstateVariants.put(variantKey,
                            Map.of("model", modLoc("block/half_timber/" + woodId + "/" + modelName)));
                }
            }

            String blockId = woodId + "_half_timber_pillar_p" + pillarSlot;
            writeJson(cachedOutput, writes, blockstatePathProvider, blockId, Map.of("variants", blockstateVariants));
            writeJson(cachedOutput, writes, itemModelPathProvider, blockId,
                    Map.of("parent", modLoc("block/half_timber/" + woodId + "/" + baseModelName)));
        }
    }

    private static String windowTextureStem(String style, String connectionId) {
        return switch (connectionId) {
            case "s0" -> style;
            case "d0", "t0" -> style + "_t0";
            case "d1", "t2" -> style + "_t2";
            case "t1" -> style + "_t1";
            default -> style;
        };
    }

    private void writeContainerModelFromTemplate(CachedOutput cachedOutput, List<CompletableFuture<?>> writes, String woodId,
            String templateModelName, String outputModelName) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("parent", modLoc("block/container/" + templateModelName));
        model.put("textures", readContainerTemplateTextures(templateModelName, woodId));
        writeJson(cachedOutput, writes, blockModelPathProvider, "container/" + woodId + "/" + outputModelName, model);
    }

    private Map<String, Object> readContainerTemplateTextures(String templateModelName, String woodId) {
        Path templatePath = containerTemplateModelRoot.resolve(templateModelName + ".json");
        if (!Files.isRegularFile(templatePath)) {
            throw new IllegalStateException("Missing container model template: " + templatePath);
        }
        try {
            String raw = Files.readString(templatePath, StandardCharsets.UTF_8);
            JsonObject templateJson = JsonParser.parseString(raw).getAsJsonObject();
            JsonObject texturesJson = templateJson.getAsJsonObject("textures");
            if (texturesJson == null) {
                throw new IllegalStateException("Container model template has no textures object: " + templatePath);
            }

            Map<String, Object> textures = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : texturesJson.entrySet()) {
                String textureValue = entry.getValue().getAsString();
                textures.put(entry.getKey(),
                        textureValue.replace("block/container/oak/", "block/container/" + woodId + "/"));
            }
            return textures;
        } catch (IOException e) {
            throw new IllegalStateException("Failed reading container model template: " + templatePath, e);
        }
    }

    private static int horizontalRotationY(String facing) {
        return switch (facing) {
            case "east" -> 90;
            case "south" -> 180;
            case "west" -> 270;
            default -> 0;
        };
    }

    private static Map<String, Object> horizontalFacingVariant(String modelPath, int y) {
        Map<String, Object> variant = new LinkedHashMap<>();
        variant.put("model", modLoc(modelPath));
        if (y != 0) {
            variant.put("y", y);
        }
        return variant;
    }

    private static String wallShelfModelSuffix(String connectionId) {
        return switch (connectionId) {
            case "d0" -> "t0";
            case "d1" -> "t2";
            default -> connectionId;
        };
    }

    private static Map<String, Object> halfTimberCubeModel(String woodId, int patternIndex) {
        Map<String, Object> textures = new LinkedHashMap<>();
        textures.put("side", modLoc("block/half_timber_back/halftimber_frame_p" + patternIndex));
        textures.put("overlay", modLoc("block/half_timber/" + woodId + "/halftimber_frame_p" + patternIndex));

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("parent", modLoc("block/half_timber/half_timber"));
        model.put("textures", textures);
        return model;
    }

    private static Map<String, Object> halfTimberPillarModel(String woodId, int patternIndex, String connectionId) {
        String suffix = "_" + connectionId;
        Map<String, Object> textures = new LinkedHashMap<>();
        textures.put("side", modLoc("block/half_timber_back/halftimber_frame_p" + patternIndex + suffix));
        textures.put("overlay", modLoc("block/half_timber/" + woodId + "/halftimber_frame_p" + patternIndex + suffix));
        textures.put("end", modLoc("block/half_timber_back/halftimber_frame_p" + patternIndex));
        textures.put("end_overlay", modLoc("block/half_timber/" + woodId + "/halftimber_frame_p" + patternIndex));

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("parent", modLoc("block/half_timber/half_timber_pillar"));
        model.put("textures", textures);
        return model;
    }

    private static String modLoc(String path) {
        return Nekoration.MODID + ":" + path;
    }

    private static void writeJson(CachedOutput cachedOutput, List<CompletableFuture<?>> writes,
            PackOutput.PathProvider pathProvider, String path, Map<String, Object> jsonBody) {
        writeJson(cachedOutput, writes, pathProvider, path, GSON.toJsonTree(jsonBody));
    }

    private static void writeJson(CachedOutput cachedOutput, List<CompletableFuture<?>> writes,
            PackOutput.PathProvider pathProvider, String path, JsonElement jsonBody) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, path);
        writes.add(DataProvider.saveStable(cachedOutput, jsonBody, pathProvider.json(id)));
    }

    private static Path resolveContainerTemplateModelRoot() {
        Path probe = Path.of("").toAbsolutePath();
        for (Path current = probe; current != null; current = current.getParent()) {
            Path candidate = current.resolve("src/main/resources/assets/" + Nekoration.MODID + "/models/block/container");
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not locate container template models from " + probe);
    }

    @Override
    public String getName() {
        return "Nekoration wooden block assets";
    }
}
