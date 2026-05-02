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
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

/**
 * Blockstate/Block/Item model generator for all stone blocks.
 */
public final class StoneBlockAssetProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

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
                generateStoneCubeAllAssets(cachedOutput, "smooth", true, writes, stone.id());
                generateStoneStairAssets(cachedOutput, "smooth", true, writes, stone.id());
                generateStoneSlabAssets(cachedOutput, "smooth", true, writes, stone.id());
            }
            generateStoneCubeAllAssets(cachedOutput, "polished_smooth", true, writes, stone.id());
            generateStoneStairAssets(cachedOutput, "polished_smooth", true, writes, stone.id());
            generateStoneSlabAssets(cachedOutput, "polished_smooth", true, writes, stone.id());
            generateStoneCubeAllAssets(cachedOutput, "chiseled", true, writes, stoneId);
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

    private void generateStoneSlabAssets(CachedOutput cachedOutput, String variant, boolean prefixedId,
            List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = prefixedId ? variant + "_" + stoneId : stoneId + "_" + variant;
        String textureId = stoneId + "_" + variant;
        String slabId = variantId + "_slab";

        Map<String, Object> slabTextures = new LinkedHashMap<>();
        slabTextures.put("bottom", modLoc("block/stone/" + textureId));
        slabTextures.put("top", modLoc("block/stone/" + textureId));
        slabTextures.put("side", modLoc("block/stone/" + textureId));

        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + slabId,
                Map.of("parent", "block/slab", "textures", slabTextures));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + slabId + "_top",
                Map.of("parent", "block/slab_top", "textures", slabTextures));

        Map<String, Object> variants = new LinkedHashMap<>();
        variants.put("type=bottom", Map.of("model", modLoc("block/stone/" + slabId)));
        variants.put("type=top", Map.of("model", modLoc("block/stone/" + slabId + "_top")));
        variants.put("type=double", Map.of("model", modLoc("block/stone/" + variantId)));
        writeJson(cachedOutput, writes, blockstatePathProvider, slabId, Map.of("variants", variants));
        writeJson(cachedOutput, writes, itemModelPathProvider, slabId,
                Map.of("parent", modLoc("block/stone/" + slabId)));
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
