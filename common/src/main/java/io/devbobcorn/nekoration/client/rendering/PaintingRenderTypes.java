package io.devbobcorn.nekoration.client.rendering;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * Custom render types for painting rendering. The render state shards used
 * here are protected in vanilla (NeoForge patches them public), so this class
 * resolves them reflectively; the mojmap names are identical on every loader.
 */
public class PaintingRenderTypes {
    private static final Object LEASH_SHADER;
    private static final Object NO_TEXTURE;
    private static final Object NO_CULL;
    private static final Object LIGHTMAP;
    private static final Object TRANSLUCENT_TRANSPARENCY;
    private static final Object OVERLAY;
    private static final Object ENTITY_SOLID_SHADER;
    private static final Constructor<?> TEXTURE_STATE_CTOR;
    private static final Method COMPOSITE_BUILDER;
    private static final Method CREATE_RENDER_TYPE;

    static {
        try {
            Class<?> shardClass = Class.forName("net.minecraft.client.renderer.RenderStateShard");
            LEASH_SHADER = shard("RENDERTYPE_LEASH_SHADER", shardClass);
            NO_TEXTURE = shard("NO_TEXTURE", shardClass);
            NO_CULL = shard("NO_CULL", shardClass);
            LIGHTMAP = shard("LIGHTMAP", shardClass);
            TRANSLUCENT_TRANSPARENCY = shard("TRANSLUCENT_TRANSPARENCY", shardClass);
            OVERLAY = shard("OVERLAY", shardClass);
            ENTITY_SOLID_SHADER = shard("RENDERTYPE_ENTITY_SOLID_SHADER", shardClass);

            TEXTURE_STATE_CTOR = Class.forName("net.minecraft.client.renderer.RenderStateShard$TextureStateShard")
                    .getDeclaredConstructor(ResourceLocation.class, boolean.class, boolean.class);
            TEXTURE_STATE_CTOR.setAccessible(true);

            Class<?> compositeState = Class.forName("net.minecraft.client.renderer.RenderType$CompositeState");
            COMPOSITE_BUILDER = compositeState.getDeclaredMethod("builder");
            COMPOSITE_BUILDER.setAccessible(true);

            Method createMethod = null;
            for (Method method : RenderType.class.getDeclaredMethods()) {
                Class<?>[] params = method.getParameterTypes();
                if (method.getName().equals("create") && params.length == 7
                        && params[0] == String.class && params[1] == VertexFormat.class
                        && params[2] == VertexFormat.Mode.class && params[3] == int.class
                        && params[4] == boolean.class && params[5] == boolean.class) {
                    createMethod = method;
                    createMethod.setAccessible(true);
                    break;
                }
            }
            if (createMethod == null) {
                throw new IllegalStateException(
                        "RenderType.create(String, VertexFormat, Mode, int, boolean, boolean, CompositeState) not found");
            }
            CREATE_RENDER_TYPE = createMethod;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to initialize painting render types", e);
        }
    }

    private PaintingRenderTypes() {
    }

    private static Object shard(String name, Class<?> shardClass) throws ReflectiveOperationException {
        Field field = shardClass.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(null);
    }

    private static Object builder() {
        try {
            return COMPOSITE_BUILDER.invoke(null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to create composite state builder", e);
        }
    }

    private static Object setState(Object builder, String setter, Object shard) {
        // The shard parameter type can be any supertype of the shard's runtime class,
        // so walk up the hierarchy until the method is found.
        for (Class<?> type = shard.getClass(); type != Object.class; type = type.getSuperclass()) {
            try {
                Method method = builder.getClass().getMethod(setter, type);
                return method.invoke(builder, shard);
            } catch (NoSuchMethodException ignored) {
                // try the supertype
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to configure composite state: " + setter, e);
            }
        }
        throw new IllegalStateException("Composite state setter not found: " + setter);
    }

    private static Object buildComposite(Object builder, boolean affectsCrumbling) {
        try {
            Method method = builder.getClass().getMethod("createCompositeState", boolean.class);
            return method.invoke(builder, affectsCrumbling);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to build composite state", e);
        }
    }

    private static RenderType create(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
            boolean affectsCrumbling, boolean sortOnUpload, Object compositeState) {
        try {
            return (RenderType) CREATE_RENDER_TYPE.invoke(null, name, format, mode, bufferSize, affectsCrumbling,
                    sortOnUpload, compositeState);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to create render type: " + name, e);
        }
    }

    private static final RenderType PAINTING_PIXELS = create("painting_pixels",
            DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, false,
            buildComposite(
                    setState(setState(setState(setState(builder(), "setShaderState", LEASH_SHADER),
                            "setTextureState", NO_TEXTURE), "setCullState", NO_CULL), "setLightmapState", LIGHTMAP),
                    false));

    public static RenderType paintingPixels() {
        return PAINTING_PIXELS;
    }

    public static RenderType paintingTexture(ResourceLocation location) {
        try {
            Object textureState = TEXTURE_STATE_CTOR.newInstance(location, /* blur */ false, /* mipmap */ true);
            Object compositeState = buildComposite(
                    setState(setState(setState(setState(setState(builder(), "setShaderState", ENTITY_SOLID_SHADER),
                            "setTextureState", textureState), "setTransparencyState", TRANSLUCENT_TRANSPARENCY),
                            "setLightmapState", LIGHTMAP), "setOverlayState", OVERLAY), true);
            return create("painting", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, /* buffer size */ 256,
                    /* no delegate */ false, /* need sorting data */ true, compositeState);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to create painting render type", e);
        }
    }
}
