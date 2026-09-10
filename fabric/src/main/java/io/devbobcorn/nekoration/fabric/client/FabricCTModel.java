package io.devbobcorn.nekoration.fabric.client;

import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
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
public final class FabricCTModel implements BakedModel, FabricBakedModel {
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
                // Normalize UVs against the original sprite...
                emitter.spriteBake(quad.getSprite(), net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView.BAKE_NORMALIZED);
                int index = data.get(quad.getDirection());
                NekoCTSpriteShiftEntry shift = index == -1 ? null
                        : core.behaviour().getShift(state, quad.getDirection(), quad.getSprite());
                if (shift != null && quad.getSprite() == shift.getOriginal()) {
                    for (int vertex = 0; vertex < 4; vertex++) {
                        float u = emitter.spriteU(vertex, 0);
                        float v = emitter.spriteV(vertex, 0);
                        // Sprite shift math uses [0..16] sprite-local coordinates.
                        emitter.sprite(vertex, 0,
                                shift.getTargetU(u * 16, index) / 16.0F,
                                shift.getTargetV(v * 16, index) / 16.0F);
                    }
                    // Bind the target sprite region.
                    emitter.spriteBake(shift.getTarget(), net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView.BAKE_NORMALIZED);
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
        context.fallbackConsumer().accept(core.originalModel());
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
