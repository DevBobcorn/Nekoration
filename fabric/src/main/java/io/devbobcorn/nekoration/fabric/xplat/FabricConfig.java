package io.devbobcorn.nekoration.fabric.xplat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.ExtraCodecs;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.xplat.NekoConfigData;

/**
 * Minimal client config for the Fabric build: a small JSON file with values
 * only (no spec machinery). Loaded lazily on first access.
 */
public final class FabricConfig implements NekoConfigData {
    private static final Codec<FabricConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("useImageRendering").forGetter(c -> c.useImageRendering),
            Codec.BOOL.fieldOf("simplifyRendering").forGetter(c -> c.simplifyRendering),
            Codec.BOOL.fieldOf("debugMode").forGetter(c -> c.debugMode),
            ExtraCodecs.intRange(2, 30).fieldOf("maxUndoLimit").forGetter(c -> c.maxUndoLimit))
            .apply(instance, FabricConfig::new));

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "nekoration-client.json";

    private static volatile FabricConfig instance;

    private final boolean useImageRendering;
    private final boolean simplifyRendering;
    private final boolean debugMode;
    private final int maxUndoLimit;

    private FabricConfig(boolean useImageRendering, boolean simplifyRendering, boolean debugMode, int maxUndoLimit) {
        this.useImageRendering = useImageRendering;
        this.simplifyRendering = simplifyRendering;
        this.debugMode = debugMode;
        this.maxUndoLimit = maxUndoLimit;
    }

    public static FabricConfig get() {
        if (instance == null) {
            synchronized (FabricConfig.class) {
                if (instance == null) {
                    instance = load();
                }
            }
        }
        return instance;
    }

    private static FabricConfig load() {
        FabricConfig defaults = new FabricConfig(true, true, false, 15);
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        if (!Files.exists(path)) {
            save(path, defaults);
            return defaults;
        }
        try {
            JsonElement element = JsonParser.parseReader(Files.newBufferedReader(path));
            FabricConfig config = CODEC.parse(JsonOps.INSTANCE, element).result().orElse(null);
            return config != null ? config : defaults;
        } catch (Exception e) {
            Nekoration.LOGGER.warn("Failed to read {}, using defaults", FILE_NAME, e);
            return defaults;
        }
    }

    private static void save(Path path, FabricConfig config) {
        try {
            JsonElement element = CODEC.encodeStart(JsonOps.INSTANCE, config).getOrThrow();
            Files.writeString(path, GSON.toJson(element));
        } catch (IOException | IllegalStateException e) {
            Nekoration.LOGGER.warn("Failed to write {}", FILE_NAME, e);
        }
    }

    @Override
    public boolean useImageRendering() {
        return useImageRendering;
    }

    @Override
    public boolean simplifyRendering() {
        return simplifyRendering;
    }

    @Override
    public boolean debugMode() {
        return debugMode;
    }

    @Override
    public int maxUndoLimit() {
        return maxUndoLimit;
    }
}
