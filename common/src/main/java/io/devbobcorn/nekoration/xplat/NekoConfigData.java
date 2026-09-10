package io.devbobcorn.nekoration.xplat;

/**
 * Loader-agnostic view of the client config values.
 * Replaces direct access to {@code NekoConfig.CLIENT} (a NeoForge
 * {@code ModConfigSpec}).
 */
public interface NekoConfigData {
    boolean useImageRendering();

    boolean simplifyRendering();

    boolean debugMode();

    int maxUndoLimit();
}
