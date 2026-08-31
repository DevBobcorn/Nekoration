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

/** Blockstate and item model generator for ornaments. */
public final class OrnamentAssetProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<String> COLORS = java.util.Arrays.stream(EnumNekoColor.values())
            .map(EnumNekoColor::getSerializedName).toList();
    private static final List<String> MINERAL_MATERIALS = List.of("iron", "gold", "quartz");
    private static final List<String> DIRECTIONS = List.of("north", "east", "south", "west");
    private final PackOutput.PathProvider blockstates;
    private final PackOutput.PathProvider models;
    private final PackOutput.PathProvider items;

    public OrnamentAssetProvider(PackOutput output) {
        blockstates = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        models = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
        items = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        List<CompletableFuture<?>> writes = new ArrayList<>();
        generateAwning(cachedOutput, writes, "awning_pure", false, false);
        generateAwning(cachedOutput, writes, "awning_stripe", false, true);
        generateAwning(cachedOutput, writes, "awning_pure_short", true, false);
        generateAwning(cachedOutput, writes, "awning_stripe_short", true, true);
        for (String material : MINERAL_MATERIALS) {
            generateLampPost(cachedOutput, writes, material);
            generateCandleHolder(cachedOutput, writes, material);
            generateFlowerBasket(cachedOutput, writes, material);
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    private void generateCandleHolder(CachedOutput output, List<CompletableFuture<?>> writes, String material) {
        String modelPath = "candle_holder/" + material + "_candle_holder";
        String texturePrefix = "block/mineral/" + material + "/";

        writeModel(output, writes, modelPath, "candle_holder/candle_holder",
                Map.of("0", modLoc(texturePrefix + "candle_holder"),
                        "1", modLoc(texturePrefix + "face")));

        Map<String, Object> variants = new LinkedHashMap<>();
        variants.put("", facingVariant(modLoc("block/" + modelPath), 0));
        write(output, writes, blockstates, material + "_candle_holder", Map.of("variants", variants));

        Map<String, Object> itemBody = new LinkedHashMap<>();
        itemBody.put("parent", modLoc("block/" + modelPath));
        write(output, writes, items, material + "_candle_holder", itemBody);
    }

    private void generateFlowerBasket(CachedOutput output, List<CompletableFuture<?>> writes, String material) {
        String modelPath = "flower_basket/" + material + "_flower_basket";
        String texturePrefix = "block/mineral/" + material + "/";

        writeModel(output, writes, modelPath, "flower_basket/flower_basket",
                Map.of("0", modLoc(texturePrefix + "flower_basket"),
                        "1", modLoc("block/flowers"),
                        "2", modLoc(texturePrefix + "face")));

        Map<String, Object> variants = new LinkedHashMap<>();
        variants.put("", facingVariant(modLoc("block/" + modelPath), 0));
        write(output, writes, blockstates, material + "_flower_basket", Map.of("variants", variants));

        Map<String, Object> itemBody = new LinkedHashMap<>();
        itemBody.put("parent", "item/generated");
        itemBody.put("textures", Map.of("layer0", modLoc(texturePrefix + "flower_basket")));
        write(output, writes, items, material + "_flower_basket", itemBody);
    }

    private void generateLampPost(CachedOutput output, List<CompletableFuture<?>> writes, String material) {
        String modelPrefix = "lamp_post/" + material + "_lamp_post";
        String texturePrefix = "block/mineral/" + material + "/";

        writeModel(output, writes, modelPrefix + "_base", "lamp_post/base",
                Map.of("0", modLoc(texturePrefix + "face")));
        writeModel(output, writes, modelPrefix + "_pole", "lamp_post/pole",
                Map.of("0", modLoc(texturePrefix + "face")));
        writeModel(output, writes, modelPrefix + "_top_pole", "lamp_post/top_pole",
                Map.of("0", modLoc(texturePrefix + "face")));
        writeModel(output, writes, modelPrefix + "_pole_side", "lamp_post/plane_half",
                Map.of("0", modLoc(texturePrefix + "lamp_post_pole_side")));
        writeModel(output, writes, modelPrefix + "_side_down", "lamp_post/plane_full",
                Map.of("0", modLoc(texturePrefix + "lamp_post_side_down")));
        writeModel(output, writes, modelPrefix + "_side_up", "lamp_post/plane_full",
                Map.of("0", modLoc(texturePrefix + "lamp_post_side_up")));
        writeModel(output, writes, modelPrefix + "_top_side", "lamp_post/plane_half",
                Map.of("0", modLoc(texturePrefix + "lamp_post_top_side")));

        List<Map<String, Object>> multipart = new ArrayList<>();
        multipart.add(lampPostVariant("base", null, modelPrefix + "_base", 0));
        multipart.add(lampPostVariant("pole", null, modelPrefix + "_pole", 0));
        addLampPostSideVariants(multipart, "pole", modelPrefix + "_pole_side", false);
        multipart.add(lampPostVariant("top", null, modelPrefix + "_top_pole", 0));
        addLampPostSideVariants(multipart, "top", modelPrefix + "_top_side", false);
        addLampPostSideVariants(multipart, "side_down", modelPrefix + "_side_down", true);
        addLampPostSideVariants(multipart, "side_up", modelPrefix + "_side_up", true);
        write(output, writes, blockstates, material + "_lamp_post", Map.of("multipart", multipart));

        Map<String, Object> itemBody = new LinkedHashMap<>();
        itemBody.put("parent", modLoc("block/" + modelPrefix + "_base"));
        write(output, writes, items, material + "_lamp_post", itemBody);
    }

    private static void addLampPostSideVariants(List<Map<String, Object>> multipart, String postType,
            String model, boolean flipped) {
        for (String direction : DIRECTIONS) {
            multipart.add(lampPostVariant(postType, direction, model, lampPostSideRotation(direction, flipped)));
        }
    }

    private static Map<String, Object> lampPostVariant(String postType, String direction, String model, int yRotation) {
        Map<String, Object> when = new LinkedHashMap<>();
        when.put("post_type", postType);
        if (direction != null) {
            when.put(direction, "true");
        }
        Map<String, Object> apply = new LinkedHashMap<>();
        apply.put("model", modLoc("block/" + model));
        if (yRotation != 0) {
            apply.put("y", yRotation);
        }
        return Map.of("when", when, "apply", apply);
    }

    private static int lampPostSideRotation(String direction, boolean flipped) {
        int y = switch (direction) {
            case "east" -> 90;
            case "south" -> 180;
            case "west" -> 270;
            default -> 0;
        };
        return flipped ? (y + 180) % 360 : y;
    }

    private void generateAwning(CachedOutput output, List<CompletableFuture<?>> writes, String id, boolean shortAwning,
            boolean stripe) {
        Map<String, Object> variants = new LinkedHashMap<>();
        for (String color : COLORS) {
            writeModel(output, writes, colorModelPath(id, color), "awning/" + (shortAwning ? "awning_short" : "awning"),
                    stripe ? Map.of("0", modLoc("block/awning/" + color + "_stripe_top"),
                            "1", modLoc("block/awning/" + color + "_stripe"))
                            : Map.of("0", "block/" + color + "_wool",
                                    "1", modLoc("block/awning/" + color + "_pure")));
            if (!shortAwning) {
                writeModel(output, writes, colorEndModelPath(id, color), "awning/awning_end",
                        stripe ? Map.of("0", modLoc("block/awning/" + color + "_stripe_top"),
                                "1", modLoc("block/awning/" + color + "_stripe"))
                                : Map.of("0", "block/" + color + "_wool",
                                        "1", modLoc("block/awning/" + color + "_pure")));
            }

            for (String facing : List.of("north", "east", "south", "west")) {
                String key = "color=" + color + ",facing=" + facing;
                if (!shortAwning) {
                    key += ",bottom=false";
                }
                variants.put(key, facingVariant(modLoc("block/" + colorModelPath(id, color)), rotation(facing)));
                if (!shortAwning) {
                    variants.put("color=" + color + ",facing=" + facing + ",bottom=true",
                            facingVariant(modLoc("block/" + colorEndModelPath(id, color)), rotation(facing)));
                }
            }
        }
        write(output, writes, blockstates, id, Map.of("variants", variants));
        Map<String, Object> itemBody = new LinkedHashMap<>();
        itemBody.put("parent", modLoc("block/" + (shortAwning ? colorModelPath(id, "white") : colorEndModelPath(id, "white"))));
        List<Map<String, Object>> overrides = new ArrayList<>();
        for (EnumNekoColor color : EnumNekoColor.values()) {
            Map<String, Object> override = new LinkedHashMap<>();
            override.put("predicate", Map.of("nekoration:color", (double) color.getNbtId()));
            override.put("model", modLoc("block/" + (shortAwning ? colorModelPath(id, color.getSerializedName())
                    : colorEndModelPath(id, color.getSerializedName()))));
            overrides.add(override);
        }
        itemBody.put("overrides", overrides);
        write(output, writes, items, id, itemBody);
    }

    private void writeModel(CachedOutput output, List<CompletableFuture<?>> writes, String path, String parent,
            Map<String, String> textures) {
        write(output, writes, models, path,
                Map.of("parent", modLoc("block/" + parent), "textures", textures));
    }

    private static Map<String, Object> facingVariant(String model, int rotation) {
        Map<String, Object> variant = new LinkedHashMap<>();
        variant.put("model", model);
        if (rotation != 0) {
            variant.put("y", rotation);
        }
        return variant;
    }

    private static int rotation(String facing) {
        return switch (facing) {
            case "east" -> 90;
            case "south" -> 180;
            case "west" -> 270;
            default -> 0;
        };
    }

    private static String colorModelPath(String id, String color) {
        return "awning/" + id + "_" + color;
    }

    private static String colorEndModelPath(String id, String color) {
        return "awning/" + id + "_end_" + color;
    }

    private static String modLoc(String path) {
        return Nekoration.MODID + ":" + path;
    }

    private static void write(CachedOutput output, List<CompletableFuture<?>> writes, PackOutput.PathProvider provider,
            String path, Map<String, Object> body) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, path);
        writes.add(DataProvider.saveStable(output, GSON.toJsonTree(body), provider.json(id)));
    }

    @Override
    public String getName() {
        return "Nekoration ornament assets";
    }
}
