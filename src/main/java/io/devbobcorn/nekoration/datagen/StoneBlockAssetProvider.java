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
            }
            generateStoneCubeAllAssets(cachedOutput, "polished_smooth", true, writes, stone.id());
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

    private void generateStoneDecoAssets(CachedOutput cachedOutput, String variant, boolean prefixedId, List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = prefixedId ? variant + "_" + stoneId : stoneId + "_" + variant;
        // String textureId = stoneId + "_" + variant;

        writeJson(cachedOutput, writes, blockstatePathProvider, variantId,
            Map.of("variants", Map.of("", Map.of("model", modLoc("block/stone/" + variantId)))));

        writeJson(cachedOutput, writes, itemModelPathProvider, variantId,
                Map.of("parent", modLoc("block/stone/" + variantId)));
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
