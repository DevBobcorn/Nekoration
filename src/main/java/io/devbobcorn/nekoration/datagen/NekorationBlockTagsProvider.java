package io.devbobcorn.nekoration.datagen;

import java.util.concurrent.CompletableFuture;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.registry.CementBlockRegistration;
import io.devbobcorn.nekoration.registry.OrnamentRegistration;
import io.devbobcorn.nekoration.registry.StoneBlockRegistration;
import io.devbobcorn.nekoration.registry.WoodenBlockRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

/** Block tag generator, e.g. makes lamp posts act as leash anchors. */
public final class NekorationBlockTagsProvider extends TagsProvider<Block> {
    public NekorationBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            ExistingFileHelper fileHelper) {
        super(output, Registries.BLOCK, lookupProvider, Nekoration.MODID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.FENCES).addAll(OrnamentRegistration.lampPostBlocksView().stream()
                .map(DeferredBlock::getKey).toList());
        tag(BlockTags.DOORS).addAll(OrnamentRegistration.doorBlocksView().stream()
                .map(DeferredBlock::getKey).toList());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .addAll(WoodenBlockRegistration.woodenBlocksView().stream()
                        .map(DeferredBlock::getKey).toList())
                .addAll(OrnamentRegistration.pumpkinFurnitureBlocksView().stream()
                        .map(DeferredBlock::getKey).toList());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addAll(StoneBlockRegistration.stoneBlocksView().stream()
                        .map(DeferredBlock::getKey).toList())
                .addAll(CementBlockRegistration.cementBlocksView().stream()
                        .map(DeferredBlock::getKey).toList())
                .addAll(OrnamentRegistration.doorBlocksView().stream()
                        .map(DeferredBlock::getKey).toList())
                .addAll(OrnamentRegistration.lampPostBlocksView().stream()
                        .map(DeferredBlock::getKey).toList())
                .addAll(OrnamentRegistration.candleHolderBlocksView().stream()
                        .map(DeferredBlock::getKey).toList())
                .addAll(OrnamentRegistration.flowerBasketBlocksView().stream()
                        .map(DeferredBlock::getKey).toList());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(OrnamentRegistration.windowPlantBlock().getKey());
    }
}
