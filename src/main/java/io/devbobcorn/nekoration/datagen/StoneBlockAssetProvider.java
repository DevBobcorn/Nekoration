package io.devbobcorn.nekoration.datagen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.NekoStone;
import io.devbobcorn.nekoration.blocks.states.VerticalConnection;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

/**
 * Blockstate/Block/Item model generator for all stone blocks.
 */
public final class StoneBlockAssetProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final List<String> VERTICAL_CONNECTION_IDS = List.of("s0", "d0", "d1", "t0", "t1", "t2");

    private final PackOutput.PathProvider blockstatePathProvider;
    private final PackOutput.PathProvider blockModelPathProvider;
    private final PackOutput.PathProvider itemModelPathProvider;

    public StoneBlockAssetProvider(PackOutput output) {
        this.blockstatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.blockModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
        this.itemModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        List<CompletableFuture<?>> writes = new ArrayList<>();
        for (NekoStone stone : NekoStone.values()) {
            String stoneId = stone.id();
            if (stone.needsSmoothVariant()) {
                generateStoneCubeAllAssets(cachedOutput, "smooth", true, writes, stoneId);
                generateStoneStairAssets(cachedOutput, "smooth", true, writes, stoneId);
                generateStoneSlabAssets(cachedOutput, "smooth", true, false, writes, stoneId);
            }
            generateStoneCubeAllAssets(cachedOutput, "polished_smooth", true, writes, stoneId);
            generateStoneStairAssets(cachedOutput, "polished_smooth", true, writes, stoneId);
            generateStoneSlabAssets(cachedOutput, "polished_smooth", true, true, writes, stoneId);
            generateVerticalConnectedStoneAssets(cachedOutput, "chiseled_smooth", true, writes, stoneId);

            generateStoneBaseAssets(cachedOutput, writes, stoneId);
            generateStoneColumnAssets(cachedOutput, "column_doric", writes, stoneId);
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    private void generateStoneCubeAllAssets(CachedOutput cachedOutput, String variant, boolean prefixedId, List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = prefixedId ? variant + "_" + stoneId : stoneId + "_" + variant;
        String textureId = stoneId + "_" + variant;

        Map<String, Object> blockModelTextures = new LinkedHashMap<>();
        blockModelTextures.put("all", modLoc("block/stone/" + textureId));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId,
                Map.of("parent", "block/cube_all", "textures", blockModelTextures));

        writeJson(cachedOutput, writes, blockstatePathProvider, variantId,
                Map.of("variants", Map.of("", Map.of("model", modLoc("block/stone/" + variantId)))));

        writeJson(cachedOutput, writes, itemModelPathProvider, variantId,
                Map.of("parent", modLoc("block/stone/" + variantId)));
    }

    private void generateStoneStairAssets(CachedOutput cachedOutput, String variant, boolean prefixedId,
            List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = prefixedId ? variant + "_" + stoneId : stoneId + "_" + variant;
        String textureId = stoneId + "_" + variant;
        String stairId = variantId + "_stairs";

        Map<String, Object> stairTextures = new LinkedHashMap<>();
        stairTextures.put("bottom", modLoc("block/stone/" + textureId));
        stairTextures.put("top", modLoc("block/stone/" + textureId));
        stairTextures.put("side", modLoc("block/stone/" + textureId));

        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + stairId,
                Map.of("parent", "block/stairs", "textures", stairTextures));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + stairId + "_inner",
                Map.of("parent", "block/inner_stairs", "textures", stairTextures));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + stairId + "_outer",
                Map.of("parent", "block/outer_stairs", "textures", stairTextures));

        Map<String, Object> variants = new LinkedHashMap<>();
        for (String facing : List.of("north", "east", "south", "west")) {
            for (String half : List.of("bottom", "top")) {
                for (String shape : List.of("straight", "inner_left", "inner_right", "outer_left", "outer_right")) {
                    String variantKey = "facing=" + facing + ",half=" + half + ",shape=" + shape;
                    variants.put(variantKey, stairVariant(stairId, facing, half, shape));
                }
            }
        }
        writeJson(cachedOutput, writes, blockstatePathProvider, stairId, Map.of("variants", variants));
        writeJson(cachedOutput, writes, itemModelPathProvider, stairId,
                Map.of("parent", modLoc("block/stone/" + stairId)));
    }

    private void generateStoneSlabAssets(CachedOutput cachedOutput, String variant, boolean prefixedId, boolean hasSlabTexture,
            List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = prefixedId ? variant + "_" + stoneId : stoneId + "_" + variant;
        String textureId = stoneId + "_" + variant;
        String slabId = variantId + "_slab";
        String sideTextureId = hasSlabTexture ? textureId + "_slab" : textureId;

        Map<String, Object> slabTextures = new LinkedHashMap<>();
        slabTextures.put("bottom", modLoc("block/stone/" + textureId));
        slabTextures.put("top", modLoc("block/stone/" + textureId));
        slabTextures.put("side", modLoc("block/stone/" + sideTextureId));

        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + slabId,
                Map.of("parent", "block/slab", "textures", slabTextures));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + slabId + "_top",
                Map.of("parent", "block/slab_top", "textures", slabTextures));
        
        // If the slab hasSlabTexture, generate a double slab model
        if (hasSlabTexture) {
            Map<String, Object> doubleSlabTextures = new LinkedHashMap<>();
            doubleSlabTextures.put("end", modLoc("block/stone/" + textureId));
            doubleSlabTextures.put("side", modLoc("block/stone/" + sideTextureId));

            writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + slabId + "_double",
                Map.of("parent", "block/cube_column", "textures", doubleSlabTextures));
        }

        Map<String, Object> variants = new LinkedHashMap<>();
        variants.put("type=bottom", Map.of("model", modLoc("block/stone/" + slabId)));
        variants.put("type=top", Map.of("model", modLoc("block/stone/" + slabId + "_top")));
        variants.put("type=double", Map.of("model", hasSlabTexture ?
                modLoc("block/stone/" + slabId + "_double") : modLoc("block/stone/" + variantId)));
        writeJson(cachedOutput, writes, blockstatePathProvider, slabId, Map.of("variants", variants));
        writeJson(cachedOutput, writes, itemModelPathProvider, slabId,
                Map.of("parent", modLoc("block/stone/" + slabId)));
    }

    private void generateVerticalConnectedStoneAssets(CachedOutput cachedOutput, String variant, boolean prefixedId,
            List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = prefixedId ? variant + "_" + stoneId : stoneId + "_" + variant;
        String textureId = stoneId + "_" + variant;

        Map<String, Object> blockModelTextures = new LinkedHashMap<>();
        blockModelTextures.put("side", modLoc("block/stone/" + textureId));
        blockModelTextures.put("end", modLoc("block/stone/" + textureId));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId,
                Map.of("parent", "block/cube_column", "textures", blockModelTextures));

        for (String connectionId : VERTICAL_CONNECTION_IDS) {
            if ("s0".equals(connectionId)) {
                continue;
            }
            String connectionModelName = variantId + "_" + connectionId;
            String sideSuffix = chiseledSideSuffixForConnection(connectionId);
            Map<String, Object> connectedTextures = new LinkedHashMap<>();
            connectedTextures.put("side", modLoc("block/stone/" + textureId + "_" + sideSuffix));
            connectedTextures.put("end", modLoc("block/stone/" + textureId));
            writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + connectionModelName,
                    Map.of("parent", "block/cube_column", "textures", connectedTextures));
        }

        Map<String, Object> variants = new LinkedHashMap<>();
        for (String connectionId : VERTICAL_CONNECTION_IDS) {
            String modelName = "s0".equals(connectionId) ? variantId : variantId + "_" + connectionId;
            variants.put("vertical_connection=" + connectionId, Map.of("model", modLoc("block/stone/" + modelName)));
        }
        writeJson(cachedOutput, writes, blockstatePathProvider, variantId, Map.of("variants", variants));

        writeJson(cachedOutput, writes, itemModelPathProvider, variantId,
                Map.of("parent", modLoc("block/stone/" + variantId)));
    }

    private void generateStoneBaseAssets(CachedOutput cachedOutput, List<CompletableFuture<?>> writes, String stoneId) {
        Map<String, Object> baseTextures = new LinkedHashMap<>();
        baseTextures.put("0", modLoc("block/stone/" + stoneId + "_chiseled_smooth"));
        baseTextures.put("1", modLoc("block/stone/" + stoneId + "_polished_smooth"));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + stoneId + "_base",
                Map.of("parent", modLoc("block/stone/base"), "textures", baseTextures));

        Map<String, Object> variants = new LinkedHashMap<>();
        variants.put("", Map.of("model", modLoc("block/stone/" + stoneId + "_base")));
        writeJson(cachedOutput, writes, blockstatePathProvider, stoneId + "_base", Map.of("variants", variants));

        writeJson(cachedOutput, writes, itemModelPathProvider, stoneId + "_base",
                Map.of("parent", modLoc("block/stone/" + stoneId + "_base")));
    }

    private void generateStoneColumnAssets(CachedOutput cachedOutput, String variant,
        List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = stoneId + "_" + variant;

        Map<String, Object> t0Textures = new LinkedHashMap<>();
        t0Textures.put("0", modLoc("block/stone/" + stoneId + "/column"));
        t0Textures.put("1", modLoc("block/stone/" + stoneId + "_chiseled_smooth"));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId + "_t0",
                Map.of("parent", modLoc("block/stone/column_t0"), "textures", t0Textures));

        Map<String, Object> t1Textures = new LinkedHashMap<>();
        t1Textures.put("0", modLoc("block/stone/" + stoneId + "/column"));
        t1Textures.put("1", modLoc("block/stone/" + stoneId + "_chiseled_smooth"));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId + "_t1",
                Map.of("parent", modLoc("block/stone/column_t1"), "textures", t1Textures));
        
        Map<String, Object> t2Textures = new LinkedHashMap<>();
        t2Textures.put("0", modLoc("block/stone/" + stoneId + "/" + variant));
        t2Textures.put("1", modLoc("block/stone/" + stoneId + "_chiseled_smooth"));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId + "_t2",
                Map.of("parent", modLoc("block/stone/" + variant + "_t2"), "textures", t2Textures));

        Map<String, Object> variants = new LinkedHashMap<>();
        for (VerticalConnection connection : VerticalConnection.values()) {
            String modelName = switch (connection) {
                case S0 -> variantId + "_t0";
                case D0 -> variantId + "_t0";
                case D1 -> variantId + "_t2";
                default -> variantId + "_" + connection.getSerializedName();
            };
            variants.put("vertical_connection=" + connection.getSerializedName(), Map.of("model", modLoc("block/stone/" + modelName)));
        }
        writeJson(cachedOutput, writes, blockstatePathProvider, variantId, Map.of("variants", variants));

        writeJson(cachedOutput, writes, itemModelPathProvider, variantId,
                Map.of("parent", modLoc("block/stone/" + variantId + "_t2")));
    }

    private static String chiseledSideSuffixForConnection(String connectionId) {
        return switch (connectionId) {
            case "d0", "t0" -> "t0";
            case "d1", "t2" -> "t2";
            default -> "t1";
        };
    }

    private static Map<String, Object> stairVariant(String stairId, String facing, String half, String shape) {
        int facingY = switch (facing) {
            case "east" -> 90;
            case "south" -> 180;
            case "west" -> 270;
            default -> 0;
        };
        int baseY = (facingY + 270) % 360;

        String modelName = stairId;
        if ("inner_left".equals(shape) || "inner_right".equals(shape)) {
            modelName = stairId + "_inner";
        } else if ("outer_left".equals(shape) || "outer_right".equals(shape)) {
            modelName = stairId + "_outer";
        }

        int yOffset = 0;
        if ("inner_left".equals(shape) || "outer_left".equals(shape)) {
            yOffset = half.equals("top") ? 0 : 270;
        } else if ("inner_right".equals(shape) || "outer_right".equals(shape)) {
            yOffset = half.equals("top") ? 90 : 0;
        }
        int yRotation = (baseY + yOffset) % 360;

        Map<String, Object> variant = new LinkedHashMap<>();
        variant.put("model", modLoc("block/stone/" + modelName));
        if (half.equals("top")) {
            variant.put("x", 180);
            variant.put("uvlock", true);
        } else if (yRotation != 0) {
            variant.put("uvlock", true);
        }
        if (yRotation != 0) {
            variant.put("y", yRotation);
        }
        return variant;
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

    @Override
    public String getName() {
        return "Nekoration stone block assets";
    }
}
