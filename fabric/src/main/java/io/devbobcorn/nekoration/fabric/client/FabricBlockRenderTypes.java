package io.devbobcorn.nekoration.fabric.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import io.devbobcorn.nekoration.Nekoration;

/**
 * Fabric equivalent of NeoForge's block model {@code "render_type"} field.
 *
 * <p>NeoForge reads {@code render_type} from block model JSONs while baking;
 * Fabric has no per-model hook for it, so without this listener every block
 * model is chunked into the solid (opaque) layer, breaking cutout and
 * translucent visuals. On every client resource (re)load this listener scans
 * the mod's blockstates and block models, resolves {@code render_type} through
 * the model {@code parent} chains, and registers each affected block with
 * Fabric's {@link BlockRenderLayerMap} (which is per block, so when a block
 * mixes layers the strongest one wins).</p>
 */
final class FabricBlockRenderTypes implements SimpleSynchronousResourceReloadListener {
    private static final ResourceLocation LISTENER_ID = ResourceLocation
            .fromNamespaceAndPath(Nekoration.MODID, "block_render_types");
    /** Folder prefix in the listed resource ids; everything after it is the model path. */
    private static final String MODELS_PREFIX = "models/";
    private static final String STATES_PREFIX = "blockstates/";
    /** Chunk layer for each aggregation rank (index = rank). */
    private static final RenderType[] BY_RANK = { RenderType.solid(), RenderType.cutout(),
            RenderType.cutoutMipped(), RenderType.translucent() };

    FabricBlockRenderTypes() {
    }

    static void register() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricBlockRenderTypes());
    }

    @Override
    public ResourceLocation getFabricId() {
        return LISTENER_ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        try {
            apply(manager);
        } catch (Exception e) {
            Nekoration.LOGGER.error("Failed to assign block render layers from model render_type", e);
        }
    }

    private void apply(ResourceManager manager) {
        // Collect this mod's block models with their parent + render_type data.
        Map<ResourceLocation, Resource> modelFiles = listModResources(manager, "models");
        Map<ResourceLocation, ModelInfo> models = new HashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : modelFiles.entrySet()) {
            String path = entry.getKey().getPath();
            if (path.startsWith(MODELS_PREFIX) && path.endsWith(".json")) {
                ModelInfo info = readModel(entry.getKey(), entry.getValue());
                if (info != null) {
                    models.put(modelId(entry.getKey()), info);
                }
            }
        }

        // Resolve each blockstate's referenced models and aggregate per block.
        Map<ResourceLocation, Resource> stateFiles = listModResources(manager, "blockstates");
        Map<ResourceLocation, Integer> ranks = new HashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : stateFiles.entrySet()) {
            String path = entry.getKey().getPath();
            if (!path.startsWith(STATES_PREFIX) || !path.endsWith(".json")) {
                continue;
            }
            String blockPath = path.substring(STATES_PREFIX.length(), path.length() - ".json".length());
            if (blockPath.isEmpty()) {
                continue;
            }
            int best = 0;
            for (ResourceLocation referenced : readBlockstateModels(entry.getKey(), entry.getValue())) {
                RenderType type = resolveRenderType(referenced, models, new HashSet<>());
                if (type != null) {
                    best = Math.max(best, rankOf(type));
                }
            }
            if (best > 0) {
                ranks.put(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, blockPath), best);
            }
        }

        int registered = 0;
        for (Map.Entry<ResourceLocation, Integer> entry : ranks.entrySet()) {
            Block block = BuiltInRegistries.BLOCK.getOptional(entry.getKey()).orElse(null);
            if (block == null) {
                Nekoration.LOGGER.warn("No block found for blockstate {}, skipping render layer", entry.getKey());
                continue;
            }
            BlockRenderLayerMap.INSTANCE.putBlock(block, BY_RANK[entry.getValue()]);
            registered++;
        }
        Nekoration.LOGGER.info("Assigned block render layers to {} blocks from model render_type", registered);
    }

    private static Map<ResourceLocation, Resource> listModResources(ResourceManager manager, String folder) {
        return manager.listResources(folder, id -> id.getNamespace().equals(Nekoration.MODID));
    }

    private static ResourceLocation modelId(ResourceLocation file) {
        String path = file.getPath();
        return ResourceLocation.fromNamespaceAndPath(file.getNamespace(),
                path.substring(MODELS_PREFIX.length(), path.length() - ".json".length()));
    }

    private record ModelInfo(ResourceLocation parent, String renderType) {
    }

    private static ModelInfo readModel(ResourceLocation id, Resource resource) {
        JsonElement json = readJson(id, resource);
        if (json == null || !json.isJsonObject()) {
            return null;
        }
        var object = json.getAsJsonObject();
        String renderType = object.has("render_type") && object.get("render_type").isJsonPrimitive()
                ? object.get("render_type").getAsString()
                : null;
        ResourceLocation parent = object.has("parent") && object.get("parent").isJsonPrimitive()
                ? ResourceLocation.tryParse(object.get("parent").getAsString())
                : null;
        return new ModelInfo(parent, renderType);
    }

    private static Set<ResourceLocation> readBlockstateModels(ResourceLocation id, Resource resource) {
        JsonElement json = readJson(id, resource);
        if (json == null || !json.isJsonObject()) {
            return Set.of();
        }
        Set<ResourceLocation> refs = new HashSet<>();
        collectModelRefs(json, refs);
        return refs;
    }

    /** Gathers every "model" value from variants, multipart "apply" entries and arrays. */
    private static void collectModelRefs(JsonElement element, Set<ResourceLocation> refs) {
        if (element == null || !element.isJsonObject()) {
            return;
        }
        var object = element.getAsJsonObject();
        if (object.has("model") && object.get("model").isJsonPrimitive()) {
            ResourceLocation id = ResourceLocation.tryParse(object.get("model").getAsString());
            if (id != null) {
                refs.add(id);
            }
        }
        collectModelRefs(object.get("apply"), refs);
        if (object.has("variants") && object.get("variants").isJsonObject()) {
            for (var variant : object.getAsJsonObject("variants").entrySet()) {
                collectModelRefs(variant.getValue(), refs);
            }
        }
        if (object.has("multipart") && object.get("multipart").isJsonArray()) {
            for (var part : object.getAsJsonArray("multipart")) {
                collectModelRefs(part, refs);
            }
        }
    }

    private static JsonElement readJson(ResourceLocation id, Resource resource) {
        try (BufferedReader reader = resource.openAsReader()) {
            return JsonParser.parseReader(reader);
        } catch (IOException | JsonParseException e) {
            Nekoration.LOGGER.warn("Could not read model data from {}", id, e);
            return null;
        }
    }

    /**
     * Follows the model's parent chain within this mod's models; a model's own
     * {@code render_type} always wins over inherited values (NeoForge behavior).
     */
    private static RenderType resolveRenderType(ResourceLocation modelId, Map<ResourceLocation, ModelInfo> models,
            Set<ResourceLocation> visited) {
        if (!visited.add(modelId)) {
            return null;
        }
        ModelInfo model = models.get(modelId);
        if (model == null) {
            return null;
        }
        if (model.renderType() != null) {
            return renderType(model.renderType());
        }
        return model.parent() == null ? null : resolveRenderType(model.parent(), models, visited);
    }

    /** Maps a JSON render_type value (plain or minecraft-namespaced) to a chunk layer. */
    private static RenderType renderType(String value) {
        String path = value;
        int separator = value.indexOf(':');
        if (separator >= 0) {
            if (!value.substring(0, separator).equals(ResourceLocation.DEFAULT_NAMESPACE)) {
                return null;
            }
            path = value.substring(separator + 1);
        }
        return switch (path) {
            case "solid" -> RenderType.solid();
            case "cutout" -> RenderType.cutout();
            case "cutout_mipped" -> RenderType.cutoutMipped();
            case "translucent" -> RenderType.translucent();
            default -> null;
        };
    }

    private static int rankOf(RenderType type) {
        if (type == RenderType.translucent()) {
            return 3;
        }
        if (type == RenderType.cutoutMipped()) {
            return 2;
        }
        if (type == RenderType.cutout()) {
            return 1;
        }
        return 0;
    }
}
