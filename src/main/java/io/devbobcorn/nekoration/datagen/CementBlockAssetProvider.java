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
    private static final List<String> FRAME_CONNECTION_IDS = List.of("left", "right", "both");
    private static final List<String> FACINGS = List.of("north", "east", "south", "west");

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
        generateFrame(cachedOutput, writes, "cement_frame_head", "frame_head", false);
        generateFrame(cachedOutput, writes, "cement_frame_peak", "frame_peak", true);
        generateFrame(cachedOutput, writes, "cement_frame_sill", "frame_sill", false);
        generateFrameSide(cachedOutput, writes, "cement_frame_side");
        generatePot(cachedOutput, writes, "cement_pot", "pot");
        generatePot(cachedOutput, writes, "cement_planter", "planter");
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

    private void generateFrame(CachedOutput output, List<CompletableFuture<?>> writes, String blockId,
            String part, boolean hasBackTexture) {
        Map<String, Object> textures = new LinkedHashMap<>();
        textures.put("0", modLoc("block/cement/" + blockId));
        if (hasBackTexture) {
            textures.put("1", modLoc("block/cement/cement_top"));
        }

        writeJson(output, writes, blockModelPathProvider, "cement/" + blockId,
                Map.of("parent", modLoc("block/cement/" + part), "textures", textures));

        for (String connectionId : CONNECTION_IDS) {
            if ("s0".equals(connectionId) || "d0".equals(connectionId) || "d1".equals(connectionId)) {
                continue;
            }
            writeJson(output, writes, blockModelPathProvider, "cement/" + blockId + "_" + connectionId,
                    Map.of("parent", modLoc("block/cement/" + part + "_" + connectionId), "textures", textures));
        }

        Map<String, Object> variants = new LinkedHashMap<>();
        for (EnumNekoColor color : EnumNekoColor.values()) {
            for (String connectionId : CONNECTION_IDS) {
                String modelName = "s0".equals(connectionId) ? blockId : blockId + "_" + tripleConnectionSuffixForConnection(connectionId);
                putFacingVariants(variants, "color=" + color.getSerializedName() + ",horizontal_connection=" + connectionId,
                        modLoc("block/cement/" + modelName));
            }
        }
        writeJson(output, writes, blockstatePathProvider, blockId, Map.of("variants", variants));
        writeJson(output, writes, itemModelPathProvider, blockId,
                Map.of("parent", modLoc("block/cement/" + blockId)));
    }

    private void generateFrameSide(CachedOutput output, List<CompletableFuture<?>> writes, String blockId) {
        Map<String, Object> textures = new LinkedHashMap<>();
        textures.put("0", modLoc("block/cement/" + blockId));

        for (String connectionId : FRAME_CONNECTION_IDS) {
            writeJson(output, writes, blockModelPathProvider, "cement/" + blockId + "_" + connectionId,
                    Map.of("parent", modLoc("block/cement/frame_side_" + connectionId), "textures", textures));
        }

        Map<String, Object> variants = new LinkedHashMap<>();
        for (EnumNekoColor color : EnumNekoColor.values()) {
            for (String connectionId : FRAME_CONNECTION_IDS) {
                putFacingVariants(variants, "color=" + color.getSerializedName() + ",frame_connection=" + connectionId,
                        modLoc("block/cement/" + blockId + "_" + connectionId));
            }
        }
        writeJson(output, writes, blockstatePathProvider, blockId, Map.of("variants", variants));
        writeJson(output, writes, itemModelPathProvider, blockId,
                Map.of("parent", modLoc("block/cement/" + blockId + "_both")));
    }

    private void generatePot(CachedOutput output, List<CompletableFuture<?>> writes, String blockId, String part) {
        Map<String, Object> textures = new LinkedHashMap<>();
        textures.put("0", modLoc("block/cement/" + blockId));
        textures.put("1", modLoc("block/cement/cement_frame"));

        writeJson(output, writes, blockModelPathProvider, "cement/" + blockId,
                Map.of("parent", modLoc("block/cement/" + part), "textures", textures));

        Map<String, Object> variants = new LinkedHashMap<>();
        for (EnumNekoColor color : EnumNekoColor.values()) {
            variants.put("color=" + color.getSerializedName(),
                    Map.of("model", modLoc("block/cement/" + blockId)));
        }
        writeJson(output, writes, blockstatePathProvider, blockId, Map.of("variants", variants));
        writeJson(output, writes, itemModelPathProvider, blockId,
                Map.of("parent", modLoc("block/cement/" + blockId)));
    }

    private static void putFacingVariants(Map<String, Object> variants, String keyPrefix, String model) {
        int i = 0;
        for (String facing : FACINGS) {
            Map<String, Object> variant = new LinkedHashMap<>();
            variant.put("model", model);
            if (i != 0) {
                variant.put("y", i * 90);
            }
            variants.put(keyPrefix + ",facing=" + facing, variant);
            i++;
        }
    }

    private static String tripleConnectionSuffixForConnection(String connectionId) {
        return switch (connectionId) {
            case "d0", "t0" -> "t0";
            case "d1", "t2" -> "t2";
            default -> "t1";
        };
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