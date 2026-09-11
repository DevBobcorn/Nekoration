package io.devbobcorn.nekoration.fabric.client;

import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import io.devbobcorn.nekoration.blocks.WindowPaneBlock;
import io.devbobcorn.nekoration.client.ct.NekoCTModel;
import io.devbobcorn.nekoration.client.ct.NekoCTSpriteShiftEntry;

/**
 * Fabric shell for the connected-textures core: per-position CT data is
 * computed from the render view at quad-emission time (the Fabric renderer
 * API provides the position, vanilla {@code getQuads} does not).
 */
public final class FabricCTModel implements BakedModel {
    private final NekoCTModel core;
    private RenderMaterial material;

    public FabricCTModel(BakedModel originalModel, io.devbobcorn.nekoration.client.ct.NekoConnectedTextureBehaviour behaviour) {
        this.core = new NekoCTModel(originalModel, behaviour);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter view, BlockState state, BlockPos pos,
            Supplier<RandomSource> randomSupplier, RenderContext context) {
        NekoCTModel.CTData data = core.createCTData(view, pos, state);
        var emitter = context.getEmitter();
        for (Direction side : appendNull(Direction.values())) {
            for (BakedQuad quad : core.originalQuads(state, side, randomSupplier.get())) {
                if (shouldCullPaneVerticalFace(state, quad.getDirection())
                        && data.isPaneVerticalFaceCulled(quad.getDirection())) {
                    continue;
                }
                emitter.fromVanilla(quad, material(), quad.getDirection());
                // Vanilla quads already carry final, normalized atlas UVs.
                int index = data.get(quad.getDirection());
                NekoCTSpriteShiftEntry shift = index == -1 ? null
                        : core.behaviour().getShift(state, quad.getDirection(), quad.getSprite());
                if (shift != null && quad.getSprite() == shift.getOriginal()) {
                    for (int vertex = 0; vertex < 4; vertex++) {
                        float u = emitter.u(vertex);
                        float v = emitter.v(vertex);
                        // Sprite shift math un-interpolates from the original sprite's
                        // atlas coordinates and returns target sprite atlas coordinates.
                        emitter.uv(vertex, shift.getTargetU(u, index), shift.getTargetV(v, index));
                    }
                }
                emitter.emit();
            }
        }
    }

    private RenderMaterial material() {
        if (material == null) {
            material = net.fabricmc.fabric.api.renderer.v1.RendererAccess.INSTANCE.getRenderer()
                    .materialFinder().find();
        }
        return material;
    }

    private static Direction[] appendNull(Direction[] values) {
        Direction[] result = new Direction[values.length + 1];
        result[0] = null;
        System.arraycopy(values, 0, result, 1, values.length);
        return result;
    }

    private static boolean shouldCullPaneVerticalFace(BlockState state, Direction face) {
        return state != null
                && state.getBlock() instanceof WindowPaneBlock
                && face != null
                && face.getAxis().isVertical();
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        // Route through the wrapped model's own emit logic (the non-deprecated
        // replacement for the removed fallbackConsumer()). Fabric API
        // interface-injects FabricBakedModel onto BakedModel at compile time.
        core.originalModel().emitItemQuads(stack, randomSupplier, context);
    }

    // BakedModel delegation

    @Override
    public java.util.List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
        return core.originalModel().getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return core.originalModel().useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return core.originalModel().isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return core.originalModel().usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return core.originalModel().isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return core.originalModel().getParticleIcon();
    }

    @Override
    public net.minecraft.client.renderer.block.model.ItemOverrides getOverrides() {
        return core.originalModel().getOverrides();
    }

    @Override
    public net.minecraft.client.renderer.block.model.ItemTransforms getTransforms() {
        return core.originalModel().getTransforms();
    }
}
