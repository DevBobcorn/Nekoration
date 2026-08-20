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
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    private void generateAwning(CachedOutput output, List<CompletableFuture<?>> writes, String id, boolean shortAwning,
            boolean stripe) {
        Map<String, Object> variants = new LinkedHashMap<>();
        for (String color : COLORS) {
            writeModel(output, writes, colorModelPath(id, color), shortAwning ? "awning_short" : "awning",
                    stripe ? Map.of("0", modLoc("block/awning/" + color + "_stripe_top"),
                            "1", modLoc("block/awning/" + color + "_stripe"))
                            : Map.of("0", "block/" + color + "_wool"));
            if (!shortAwning) {
                writeModel(output, writes, colorEndModelPath(id, color), "awning_end",
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
                variants.put(key, facingVariant(modLoc("block/awning/" + colorModelPath(id, color)), rotation(facing)));
                if (!shortAwning) {
                    variants.put("color=" + color + ",facing=" + facing + ",bottom=true",
                            facingVariant(modLoc("block/awning/" + colorEndModelPath(id, color)), rotation(facing)));
                }
            }
        }
        write(output, writes, blockstates, id, Map.of("variants", variants));
        String itemModel = shortAwning ? colorModelPath(id, "white") : colorEndModelPath(id, "white");
        write(output, writes, items, id, Map.of("parent", modLoc("block/" + itemModel)));
    }

    private void writeModel(CachedOutput output, List<CompletableFuture<?>> writes, String path, String parent,
            Map<String, String> textures) {
        write(output, writes, models, path,
                Map.of("parent", modLoc("block/awning/" + parent), "textures", textures));
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
