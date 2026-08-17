package io.devbobcorn.nekoration.datagen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.Nekoration;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public final class CementBlockAssetProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final List<String> CONNECTION_IDS = List.of("s0", "d0", "d1", "t0", "t1", "t2");

    private final PackOutput.PathProvider blockstatePathProvider;
    private final PackOutput.PathProvider blockModelPathProvider;
    private final PackOutput.PathProvider itemModelPathProvider;

    public CementBlockAssetProvider(PackOutput output) {
        blockstatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        blockModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
        itemModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        List<CompletableFuture<?>> writes = new ArrayList<>();
        generateConnected(cachedOutput, writes, "cement", "cement", false);
        generateStandalone(cachedOutput, writes, "trimmed_cement", "trimmed_cement", "cement_top");
        generateConnected(cachedOutput, writes, "paneled_cement", "paneled_cement", true);
        generateStandalone(cachedOutput, writes, "layered_cement", "layered_cement", "cement_top");
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    private void generateConnected(CachedOutput output, List<CompletableFuture<?>> writes, String blockId,
            String textureBase, boolean hasStandaloneTexture) {
        for (String part : List.of("s0", "t0", "t1", "t2")) {
            String textureSuffix = "s0".equals(part) ? "" : "_" + part;
            writeColumnModel(output, writes, blockId + "_" + part, textureBase + textureSuffix, "cement_top");
        }

        Map<String, Object> variants = new LinkedHashMap<>();
        for (EnumNekoColor color : EnumNekoColor.values()) {
            for (String connection : CONNECTION_IDS) {
                String part = switch (connection) {
                    case "d0", "t0" -> "t0";
                    case "t1" -> "t1";
                    case "s0" -> hasStandaloneTexture ? "s0" : "t2";
                    default -> "t2";
                };
                variants.put("color=" + color.getSerializedName() + ",vertical_connection=" + connection,
                        Map.of("model", modLoc("block/cement/" + blockId + "_" + part)));
            }
        }
        writeJson(output, writes, blockstatePathProvider, blockId, Map.of("variants", variants));
        writeJson(output, writes, itemModelPathProvider, blockId,
                Map.of("parent", modLoc("block/cement/" + blockId + (hasStandaloneTexture ? "_s0" : "_t2"))));
    }

    private void generateStandalone(CachedOutput output, List<CompletableFuture<?>> writes, String blockId,
            String sideTexture, String endTexture) {
        writeColumnModel(output, writes, blockId, sideTexture, endTexture);
        Map<String, Object> variants = new LinkedHashMap<>();
        for (EnumNekoColor color : EnumNekoColor.values()) {
            variants.put("color=" + color.getSerializedName(),
                    Map.of("model", modLoc("block/cement/" + blockId)));
        }
        writeJson(output, writes, blockstatePathProvider, blockId, Map.of("variants", variants));
        writeJson(output, writes, itemModelPathProvider, blockId,
                Map.of("parent", modLoc("block/cement/" + blockId)));
    }

    private void writeColumnModel(CachedOutput output, List<CompletableFuture<?>> writes, String modelId,
            String sideTexture, String endTexture) {
        Map<String, Object> textures = new LinkedHashMap<>();
        textures.put("side", modLoc("block/cement/" + sideTexture));
        textures.put("end", modLoc("block/cement/" + endTexture));
        writeJson(output, writes, blockModelPathProvider, "cement/" + modelId,
                Map.of("parent", modLoc("block/cement/tintable_column"), "textures", textures));
    }

    private static String modLoc(String path) {
        return Nekoration.MODID + ":" + path;
    }

    private static void writeJson(CachedOutput output, List<CompletableFuture<?>> writes,
            PackOutput.PathProvider pathProvider, String path, Map<String, Object> body) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, path);
        writes.add(DataProvider.saveStable(output, GSON.toJsonTree(body), pathProvider.json(id)));
    }

    @Override
    public String getName() {
        return "Nekoration cement block assets";
    }
}