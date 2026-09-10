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
    private static final List<String> FRAME_CONNECTION_IDS = List.of("left", "right", "both");
    private static final List<String> VERTICAL_CONNECTION_IDS = List.of("s0", "d0", "d1", "t0", "t1", "t2");
    private static final List<String> HORIZONTAL_CONNECTION_IDS = List.of("s0", "d0", "d1", "t0", "t1", "t2");

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
            if (stone.vanillaWallBlock() == null) {
                String plainTextureId = switch (stone) {
                    case DRIPSTONE -> "block/dripstone_block";
                    default -> "block/" + stoneId;
                };
                generateStoneWallAssets(cachedOutput, stoneId + "_wall", plainTextureId, writes);
            }
            if (stone.needsSmoothVariant()) {
                generateStoneCubeAllAssets(cachedOutput, "smooth", true, writes, stoneId);
                generateStoneStairAssets(cachedOutput, "smooth", true, writes, stoneId);
                generateStoneSlabAssets(cachedOutput, "smooth", true, false, writes, stoneId);
            } else if (stone == NekoStone.STONE) {
                generateStoneStairAssets(cachedOutput, "smooth", true, writes, stoneId);
            }
            if (stone.needsPolishedVariant()) {
                generateStoneCubeAllAssets(cachedOutput, "polished", true, writes, stoneId);
                generateStoneStairAssets(cachedOutput, "polished", true, writes, stoneId);
                generateStoneSlabAssets(cachedOutput, "polished", true, false, writes, stoneId);
            }
            if (stone.vanillaPolishedWallBlock() == null) {
                generateStoneWallAssets(cachedOutput, "polished_" + stoneId + "_wall",
                        modLoc("block/stone/" + stoneId + "_polished"), writes);
            }
            generateStoneCubeAllAssets(cachedOutput, "polished_smooth", true, writes, stoneId);
            generateStoneStairAssets(cachedOutput, "polished_smooth", true, writes, stoneId);
            generateStoneSlabAssets(cachedOutput, "polished_smooth", true, true, writes, stoneId);
            if (stone.needsChiseledVariant()) {
                generateChiseledStoneColumnAssets(cachedOutput, "chiseled_" + stoneId, stoneId + "_chiseled", stoneId, writes);
            }
            if (stone.needsChiseledBricksVariant()) {
                generateChiseledStoneColumnAssets(cachedOutput, "chiseled_" + stoneId + "_bricks", stoneId + "_chiseled_bricks", stoneId, writes);
            }
            if (stone.needsBricksVariant()) {
                generateStoneCubeAllAssets(cachedOutput, "bricks", false, writes, stoneId);
                generateStoneStairAssets(cachedOutput, "bricks", false, writes, stoneId);
                generateStoneSlabAssets(cachedOutput, "bricks", false, false, writes, stoneId);
            }
            if (stone.vanillaBrickWallBlock() == null) {
                generateStoneWallAssets(cachedOutput, stoneId + "_brick_wall",
                        modLoc("block/stone/" + stoneId + "_bricks"), writes);
            }
            generateStoneCubeAllAssets(cachedOutput, "tiles", false, writes, stoneId);
            generateStoneStairAssets(cachedOutput, "tiles", false, writes, stoneId);
            generateStoneSlabAssets(cachedOutput, "tiles", false, false, writes, stoneId);
            generateStoneWallAssets(cachedOutput, stoneId + "_tile_wall",
                    modLoc("block/stone/" + stoneId + "_tiles"), writes);
            generateVerticalConnectedStoneCubeAssets(cachedOutput, "chiseled_smooth", true, writes, stoneId);
            generateHorizontalConnectedStoneCubeAssets(cachedOutput, "horizontal_chiseled_smooth", "chiseled_smooth", true, writes, stoneId);

            generateStonePedestalAssets(cachedOutput, writes, stoneId);
            generateStoneColumnAssets(cachedOutput, "column_doric", false, writes, stoneId);
            generateStoneColumnAssets(cachedOutput, "column_ionic", true, writes, stoneId);
            generateStoneColumnAssets(cachedOutput, "column_corinthian", false, writes, stoneId);

            generateStonePotAssets(cachedOutput, "pot", writes, stoneId);
            generateStonePotAssets(cachedOutput, "planter", writes, stoneId);

            generateStoneFrameAssets(cachedOutput, "frame_head", writes, stoneId);
            generateStoneFrameAssets(cachedOutput, "frame_peak", writes, stoneId);
            generateStoneFrameAssets(cachedOutput, "frame_sill", writes, stoneId);
            generateStoneFrameSideAssets(cachedOutput, "frame_side", writes, stoneId);
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    private void generateStoneCubeAllAssets(CachedOutput cachedOutput, String variant, boolean prefixedId, List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = prefixedId ? variant + "_" + stoneId : stoneId + "_" + variant;
        String textureId = stoneId + "_" + variant;
        generateStoneCubeAllAssets(cachedOutput, variantId, textureId, writes);
    }

    private void generateStoneCubeAllAssets(CachedOutput cachedOutput, String variantId, String textureId, List<CompletableFuture<?>> writes) {
        Map<String, Object> blockModelTextures = new LinkedHashMap<>();
        blockModelTextures.put("all", modLoc("block/stone/" + textureId));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId,
                Map.of("parent", "block/cube_all", "textures", blockModelTextures));

        writeJson(cachedOutput, writes, blockstatePathProvider, variantId,
                Map.of("variants", Map.of("", Map.of("model", modLoc("block/stone/" + variantId)))));

        writeJson(cachedOutput, writes, itemModelPathProvider, variantId,
                Map.of("parent", modLoc("block/stone/" + variantId)));
    }

    private void generateStoneWallAssets(CachedOutput cachedOutput, String variantId, String textureId,
            List<CompletableFuture<?>> writes) {
        Map<String, Object> textures = new LinkedHashMap<>();
        textures.put("wall", textureId);

        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId + "_wall_post",
                Map.of("parent", "block/template_wall_post", "textures", textures));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId + "_wall_side",
                Map.of("parent", "block/template_wall_side", "textures", textures));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId + "_wall_side_tall",
                Map.of("parent", "block/template_wall_side_tall", "textures", textures));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId + "_wall_inventory",
                Map.of("parent", "block/wall_inventory", "textures", textures));

        List<Map<String, Object>> parts = new ArrayList<>();
        parts.add(Map.of("apply", Map.of("model", modLoc("block/stone/" + variantId + "_wall_post")),
                "when", Map.of("up", "true")));
        for (String direction : List.of("north", "east", "south", "west")) {
            int yRotation = switch (direction) {
                case "east" -> 90;
                case "south" -> 180;
                case "west" -> 270;
                default -> 0;
            };
            for (String side : List.of("low", "tall")) {
                Map<String, Object> apply = new LinkedHashMap<>();
                apply.put("model", modLoc("block/stone/" + variantId
                        + ("tall".equals(side) ? "_wall_side_tall" : "_wall_side")));
                apply.put("uvlock", true);
                if (yRotation != 0) {
                    apply.put("y", yRotation);
                }
                parts.add(Map.of("apply", apply, "when", Map.of(direction, side)));
            }
        }
        writeJson(cachedOutput, writes, blockstatePathProvider, variantId, Map.of("multipart", parts));

        writeJson(cachedOutput, writes, itemModelPathProvider, variantId,
                Map.of("parent", modLoc("block/stone/" + variantId + "_wall_inventory")));
    }

    private void generateChiseledStoneColumnAssets(CachedOutput cachedOutput, String variantId, String sideTextureId,
            String stoneId, List<CompletableFuture<?>> writes) {
        Map<String, Object> blockModelTextures = new LinkedHashMap<>();
        blockModelTextures.put("end", modLoc("block/stone/" + stoneId + "_chiseled_smooth"));
        blockModelTextures.put("side", modLoc("block/stone/" + sideTextureId));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId,
                Map.of("parent", "block/cube_column", "textures", blockModelTextures));

        writeJson(cachedOutput, writes, blockstatePathProvider, variantId,
                Map.of("variants", Map.of("", Map.of("model", modLoc("block/stone/" + variantId)))));

        writeJson(cachedOutput, writes, itemModelPathProvider, variantId,
                Map.of("parent", modLoc("block/stone/" + variantId)));
    }

    private void generateStoneStairAssets(CachedOutput cachedOutput, String variant, boolean prefixedId,
            List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = prefixedId ? variant + "_" + stoneId : stoneId + "_" + variant;
        String textureId = stoneId + "_" + variant;
        generateStoneStairAssets(cachedOutput, NekoStone.singularizedSetId(variantId), textureId, writes);
    }

    private void generateStoneStairAssets(CachedOutput cachedOutput, String variantId, String textureId,
            List<CompletableFuture<?>> writes) {
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
        generateStoneSlabAssets(cachedOutput, NekoStone.singularizedSetId(variantId), textureId, hasSlabTexture,
                writes, variantId);
    }

    private void generateStoneSlabAssets(CachedOutput cachedOutput, String slabVariantId, String textureId,
            boolean hasSlabTexture, List<CompletableFuture<?>> writes, String fullBlockId) {
        String slabId = slabVariantId + "_slab";
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
                modLoc("block/stone/" + slabId + "_double") : modLoc("block/stone/" + fullBlockId)));
        writeJson(cachedOutput, writes, blockstatePathProvider, slabId, Map.of("variants", variants));
        writeJson(cachedOutput, writes, itemModelPathProvider, slabId,
                Map.of("parent", modLoc("block/stone/" + slabId)));
    }

    private void generateStonePotAssets(CachedOutput cachedOutput, String variant, List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = stoneId + "_" + variant;

        Map<String, Object> textures = new LinkedHashMap<>();
        textures.put("0", modLoc("block/stone/" + stoneId + "/" + variant));
        textures.put("1", modLoc("block/stone/" + stoneId + "_chiseled_smooth"));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId,
                Map.of("parent", modLoc("block/stone/" + variant), "textures", textures));

        Map<String, Object> variants = new LinkedHashMap<>();
        variants.put("", Map.of("model", modLoc("block/stone/" + variantId)));
        writeJson(cachedOutput, writes, blockstatePathProvider, variantId, Map.of("variants", variants));

        writeJson(cachedOutput, writes, itemModelPathProvider, variantId,
                Map.of("parent", modLoc("block/stone/" + variantId)));
    }

    private void generateVerticalConnectedStoneCubeAssets(CachedOutput cachedOutput, String variant, boolean prefixedId,
            List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = prefixedId ? variant + "_" + stoneId : stoneId + "_" + variant;
        String textureId = stoneId + "_" + variant;

        Map<String, Object> blockModelTextures = new LinkedHashMap<>();
        blockModelTextures.put("side", modLoc("block/stone/" + textureId));
        blockModelTextures.put("end", modLoc("block/stone/" + textureId));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId,
                Map.of("parent", "block/cube_column", "textures", blockModelTextures));

        for (String connectionId : VERTICAL_CONNECTION_IDS) {
            if ("s0".equals(connectionId) || "d0".equals(connectionId) || "d1".equals(connectionId)) {
                continue;
            }
            String connectionModelName = variantId + "_" + connectionId;
            String sideSuffix = tripleConnectionSuffixForConnection(connectionId);
            Map<String, Object> connectedTextures = new LinkedHashMap<>();
            connectedTextures.put("side", modLoc("block/stone/" + textureId + "_" + sideSuffix));
            connectedTextures.put("end", modLoc("block/stone/" + textureId));
            writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + connectionModelName,
                    Map.of("parent", "block/cube_column", "textures", connectedTextures));
        }

        Map<String, Object> variants = new LinkedHashMap<>();
        for (String connectionId : VERTICAL_CONNECTION_IDS) {
            String modelName = "s0".equals(connectionId) ? variantId : variantId + "_" + tripleConnectionSuffixForConnection(connectionId);
            variants.put("vertical_connection=" + connectionId, Map.of("model", modLoc("block/stone/" + modelName)));
        }
        writeJson(cachedOutput, writes, blockstatePathProvider, variantId, Map.of("variants", variants));

        writeJson(cachedOutput, writes, itemModelPathProvider, variantId,
                Map.of("parent", modLoc("block/stone/" + variantId)));
    }

    private void generateHorizontalConnectedStoneCubeAssets(CachedOutput cachedOutput, String variant, String textureVariant, boolean prefixedId,
            List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = prefixedId ? variant + "_" + stoneId : stoneId + "_" + variant;
        String textureId = stoneId + "_" + textureVariant;

        Map<String, Object> blockModelTextures = new LinkedHashMap<>();
        blockModelTextures.put("front", modLoc("block/stone/" + textureId));
        blockModelTextures.put("back", modLoc("block/stone/" + textureId));
        blockModelTextures.put("end", modLoc("block/stone/" + textureId));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId,
                Map.of("parent", modLoc("block/stone/cube_horizontal_column"), "textures", blockModelTextures));

        for (String connectionId : HORIZONTAL_CONNECTION_IDS) {
            if ("s0".equals(connectionId) || "d0".equals(connectionId) || "d1".equals(connectionId)) {
                continue;
            }
            String connectionModelName = variantId + "_" + connectionId;
            String reversedSideSuffix = reversedTripleConnectionSuffixForConnection(connectionId);
            Map<String, Object> connectedTextures = new LinkedHashMap<>();
            connectedTextures.put("front", modLoc("block/stone/" + textureId + "_" + reversedSideSuffix));
            connectedTextures.put("back", modLoc("block/stone/" + textureId + "_" + connectionId));
            connectedTextures.put("end", modLoc("block/stone/" + textureId));
            writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + connectionModelName,
                    Map.of("parent", modLoc("block/stone/cube_horizontal_column"), "textures", connectedTextures));
        }

        Map<String, Object> variants = new LinkedHashMap<>();
        for (String connectionId : HORIZONTAL_CONNECTION_IDS) {
            String modelName = "s0".equals(connectionId) ? variantId : variantId + "_" + tripleConnectionSuffixForConnection(connectionId);
            variants.put("horizontal_connection=" + connectionId + ",facing=north", Map.of("model", modLoc("block/stone/" + modelName)));
            variants.put("horizontal_connection=" + connectionId + ",facing=east", Map.of("model", modLoc("block/stone/" + modelName), "y", 90));
            variants.put("horizontal_connection=" + connectionId + ",facing=south", Map.of("model", modLoc("block/stone/" + modelName), "y", 180));
            variants.put("horizontal_connection=" + connectionId + ",facing=west", Map.of("model", modLoc("block/stone/" + modelName), "y", 270));
        }
        writeJson(cachedOutput, writes, blockstatePathProvider, variantId, Map.of("variants", variants));

        writeJson(cachedOutput, writes, itemModelPathProvider, variantId,
                Map.of("parent", modLoc("block/stone/" + variantId)));
    }

    private void generateStonePedestalAssets(CachedOutput cachedOutput, List<CompletableFuture<?>> writes, String stoneId) {
        Map<String, Object> pedestalTextures = new LinkedHashMap<>();
        pedestalTextures.put("0", modLoc("block/stone/" + stoneId + "_chiseled_smooth"));
        pedestalTextures.put("1", modLoc("block/stone/" + stoneId + "_polished_smooth"));
        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + stoneId + "_pedestal",
                Map.of("parent", modLoc("block/stone/pedestal"), "textures", pedestalTextures));

        Map<String, Object> variants = new LinkedHashMap<>();
        variants.put("", Map.of("model", modLoc("block/stone/" + stoneId + "_pedestal")));
        writeJson(cachedOutput, writes, blockstatePathProvider, stoneId + "_pedestal", Map.of("variants", variants));

        writeJson(cachedOutput, writes, itemModelPathProvider, stoneId + "_pedestal",
                Map.of("parent", modLoc("block/stone/" + stoneId + "_pedestal")));
    }

    private void generateStoneColumnAssets(CachedOutput cachedOutput, String variant,
        boolean hasHorizontalAxis, List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = stoneId + "_" + variant;

        Map<String, Object> t0Textures = new LinkedHashMap<>();
        t0Textures.put("0", modLoc("block/stone/" + stoneId + "/column_bottom"));
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
                case S0 -> variantId + "_t2";
                case D0 -> variantId + "_t0";
                case D1 -> variantId + "_t2";
                default -> variantId + "_" + connection.getSerializedName();
            };
            if (hasHorizontalAxis && (connection == VerticalConnection.S0 || connection == VerticalConnection.D1 || connection == VerticalConnection.T2)) {
                variants.put("vertical_connection=" + connection.getSerializedName() + ",axis=z", Map.of("model", modLoc("block/stone/" + modelName)));
                variants.put("vertical_connection=" + connection.getSerializedName() + ",axis=x", Map.of("model", modLoc("block/stone/" + modelName), "y", 90, "uvlock", true));
            } else {
                variants.put("vertical_connection=" + connection.getSerializedName(), Map.of("model", modLoc("block/stone/" + modelName)));
            }
        }
        writeJson(cachedOutput, writes, blockstatePathProvider, variantId, Map.of("variants", variants));

        writeJson(cachedOutput, writes, itemModelPathProvider, variantId,
                Map.of("parent", modLoc("block/stone/" + variantId + "_t2")));
    }

    private void generateStoneFrameAssets(CachedOutput cachedOutput, String variant,
        List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = stoneId + "_" + variant;

        Map<String, Object> textures = new LinkedHashMap<>();
        textures.put("0", modLoc("block/stone/" + stoneId + "/" + variant));
        if (variant == "frame_peak") {
            String smoothTextureId;
            if (stoneId == "sandstone") {
                smoothTextureId = "block/smooth_sandstone";
            } else if (stoneId == "red_sandstone") {
                smoothTextureId = "block/red_smooth_sandstone";
            } else {
                smoothTextureId = modLoc("block/stone/" + stoneId + "_smooth");
            }
            textures.put("1", smoothTextureId);
        }

        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId,
        Map.of("parent", modLoc("block/stone/" + variant), "textures", textures));

        for (String connectionId : HORIZONTAL_CONNECTION_IDS) {
            if ("s0".equals(connectionId) || "d0".equals(connectionId) || "d1".equals(connectionId)) {
                continue;
            }
            String connectionModelName = variantId + "_" + connectionId;
            writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + connectionModelName,
                    Map.of("parent", modLoc("block/stone/" + variant + "_" + connectionId), "textures", textures));
        }

        Map<String, Object> variants = new LinkedHashMap<>();
        for (String connectionId : HORIZONTAL_CONNECTION_IDS) {
            String modelName = "s0".equals(connectionId) ? variantId : variantId + "_" + tripleConnectionSuffixForConnection(connectionId);
            variants.put("horizontal_connection=" + connectionId + ",facing=north", Map.of("model", modLoc("block/stone/" + modelName)));
            variants.put("horizontal_connection=" + connectionId + ",facing=east", Map.of("model", modLoc("block/stone/" + modelName), "y", 90));
            variants.put("horizontal_connection=" + connectionId + ",facing=south", Map.of("model", modLoc("block/stone/" + modelName), "y", 180));
            variants.put("horizontal_connection=" + connectionId + ",facing=west", Map.of("model", modLoc("block/stone/" + modelName), "y", 270));
        }
        writeJson(cachedOutput, writes, blockstatePathProvider, variantId, Map.of("variants", variants));

        writeJson(cachedOutput, writes, itemModelPathProvider, variantId,
                Map.of("parent", modLoc("block/stone/" + variantId)));
    }

    private void generateStoneFrameSideAssets(CachedOutput cachedOutput, String variant,
        List<CompletableFuture<?>> writes, String stoneId) {
        String variantId = stoneId + "_" + variant;

        Map<String, Object> textures = new LinkedHashMap<>();
        textures.put("0", modLoc("block/stone/" + stoneId + "/" + variant));

        writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + variantId,
        Map.of("parent", modLoc("block/stone/" + variant), "textures", textures));

        for (String connectionId : FRAME_CONNECTION_IDS) {
            String connectionModelName = variantId + "_" + connectionId;
            writeJson(cachedOutput, writes, blockModelPathProvider, "stone/" + connectionModelName,
                    Map.of("parent", modLoc("block/stone/" + variant + "_" + connectionId), "textures", textures));
        }

        Map<String, Object> variants = new LinkedHashMap<>();
        for (String connectionId : FRAME_CONNECTION_IDS) {
            String modelName = variantId + "_" + connectionId;
            variants.put("frame_connection=" + connectionId + ",facing=north", Map.of("model", modLoc("block/stone/" + modelName)));
            variants.put("frame_connection=" + connectionId + ",facing=east", Map.of("model", modLoc("block/stone/" + modelName), "y", 90));
            variants.put("frame_connection=" + connectionId + ",facing=south", Map.of("model", modLoc("block/stone/" + modelName), "y", 180));
            variants.put("frame_connection=" + connectionId + ",facing=west", Map.of("model", modLoc("block/stone/" + modelName), "y", 270));
        }
        writeJson(cachedOutput, writes, blockstatePathProvider, variantId, Map.of("variants", variants));

        writeJson(cachedOutput, writes, itemModelPathProvider, variantId,
                Map.of("parent", modLoc("block/stone/" + variantId + "_both")));
    }

    private static String tripleConnectionSuffixForConnection(String connectionId) {
        return switch (connectionId) {
            case "d0", "t0" -> "t0";
            case "d1", "t2" -> "t2";
            default -> "t1";
        };
    }

    private static String reversedTripleConnectionSuffixForConnection(String connectionId) {
        return switch (connectionId) {
            case "d0", "t0" -> "t2";
            case "d1", "t2" -> "t0";
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
