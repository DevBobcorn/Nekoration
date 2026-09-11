package io.devbobcorn.nekoration.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.registry.CementBlockRegistration;
import io.devbobcorn.nekoration.registry.OrnamentRegistration;
import io.devbobcorn.nekoration.registry.StoneBlockRegistration;
import io.devbobcorn.nekoration.registry.WoodenBlockRegistration;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** Block tag generator, e.g. makes lamp posts act as leash anchors. */
public final class NekorationBlockTagsProvider extends TagsProvider<Block> {
    public NekorationBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            ExistingFileHelper fileHelper) {
        super(output, Registries.BLOCK, lookupProvider, Nekoration.MODID, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.FENCES).addAll(keysOf(OrnamentRegistration.lampPostBlocksView()));
        tag(BlockTags.DOORS).addAll(keysOf(OrnamentRegistration.doorBlocksView()));

        tag(BlockTags.WALLS).addAll(keysOf(StoneBlockRegistration.stoneWallBlocksView()));

        tag(BlockTags.MINEABLE_WITH_AXE)
                .addAll(keysOf(WoodenBlockRegistration.woodenBlocksView()))
                .addAll(keysOf(OrnamentRegistration.pumpkinFurnitureBlocksView()));

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addAll(keysOf(StoneBlockRegistration.stoneBlocksView()))
                .addAll(keysOf(CementBlockRegistration.cementBlocksView()))
                .addAll(keysOf(OrnamentRegistration.doorBlocksView()))
                .addAll(keysOf(OrnamentRegistration.lampPostBlocksView()))
                .addAll(keysOf(OrnamentRegistration.candleHolderBlocksView()))
                .addAll(keysOf(OrnamentRegistration.flowerBasketBlocksView()));

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(OrnamentRegistration.windowPlantBlock().getKey());
    }

    /** Reinterprets wildcard supplier keys as concrete block registry keys (safe: keys carry no static type). */
    private static List<ResourceKey<Block>> keysOf(List<? extends RegistrySupplier<? extends Block>> suppliers) {
        return suppliers.stream().map(supplier -> supplier.<Block>castKey()).toList();
    }
}
