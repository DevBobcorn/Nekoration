package io.devbobcorn.nekoration.datagen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.NekoWood;
import io.devbobcorn.nekoration.registry.WindowRegistration;
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

    public WoodenBlockAssetProvider(PackOutput output) {
        this.blockstatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.blockModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
        this.itemModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        List<CompletableFuture<?>> writes = new ArrayList<>();
        for (NekoWood wood : NekoWood.values()) {
            String woodId = wood.id();
            generateWindowAssets(cachedOutput, writes, woodId);
            generateHalfTimberAssets(cachedOutput, writes, woodId);
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    private void generateWindowAssets(CachedOutput cachedOutput, List<CompletableFuture<?>> writes, String woodId) {
        for (WindowRegistration.WindowVariant variant : WindowRegistration.WindowVariant.values()) {
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
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, path);
        JsonElement json = GSON.toJsonTree(jsonBody);
        writes.add(DataProvider.saveStable(cachedOutput, json, pathProvider.json(id)));
    }

    @Override
    public String getName() {
        return "Nekoration wooden block assets";
    }
}
