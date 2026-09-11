package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.HalfTimberCreativeTabOrdering;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.xplat.NekoRegistrar;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;

/**
 * Creative mode tabs. Uses the vanilla builder overload (row + column) so the
 * same code compiles on every loader. (Mojang marks that overload deprecated
 * in the 1.21.1 mappings, but it remains the only cross-loader-compatible
 * builder entry point, so deprecation is intentionally suppressed.)
 */
public final class ModCreativeTabs {
    public static RegistrySupplier<CreativeModeTab> NEKORATION_CEMENT_BLOCKS_TAB;
    public static RegistrySupplier<CreativeModeTab> NEKORATION_STONE_BLOCKS_TAB;
    public static RegistrySupplier<CreativeModeTab> NEKORATION_WOODEN_BLOCKS_TAB;
    public static RegistrySupplier<CreativeModeTab> NEKORATION_ORNAMENTS_TAB;

    private ModCreativeTabs() {
    }

    public static void register(NekoRegistrar registrar) {
        NEKORATION_CEMENT_BLOCKS_TAB = registrar.register(Registries.CREATIVE_MODE_TAB, "nekoration_cement_blocks",
                () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                        .title(Component.translatable("itemGroup.nekoration_cement_blocks"))
                        .icon(() -> DyeableBlockItem.createCreativeTabStack(CementBlockRegistration.iconItem().get(),
                                EnumNekoColor.WHITE))
                        .displayItems((parameters, output) -> CementBlockRegistration.blockItemsView()
                                .forEach(holder -> {
                                    for (EnumNekoColor color : EnumNekoColor.values()) {
                                        output.accept(DyeableBlockItem.createCreativeTabStack(holder.get(), color));
                                    }
                                }))
                        .build());

        NEKORATION_STONE_BLOCKS_TAB = registrar.register(Registries.CREATIVE_MODE_TAB, "nekoration_stone_blocks",
                () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                        .title(Component.translatable("itemGroup.nekoration_stone_blocks"))
                        .icon(() -> DyeableBlockItem.createCreativeTabStack(StoneBlockRegistration.iconItem().get(),
                                EnumNekoColor.WHITE))
                        .displayItems((parameters, output) -> {
                            ArrayList<ItemStack> stacks = new ArrayList<>();
                            StoneBlockRegistration.blockItemsView().forEach(holder -> {
                                stacks.add(new ItemStack(holder.get()));
                            });
                            stacks.forEach(output::accept);
                        })
                        .build());

        NEKORATION_WOODEN_BLOCKS_TAB = registrar.register(Registries.CREATIVE_MODE_TAB, "nekoration_wooden_blocks",
                () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                        .title(Component.translatable("itemGroup.nekoration_wooden_blocks"))
                        .icon(() -> DyeableBlockItem.createCreativeTabStack(WoodenBlockRegistration.iconItem().get(),
                                EnumNekoColor.WHITE))
                        .displayItems((parameters, output) -> {
                            ArrayList<ItemStack> halfTimberStacks = new ArrayList<>();
                            WoodenBlockRegistration.halfTimberBlockItemsView().forEach(holder -> {
                                halfTimberStacks.add(DyeableBlockItem.createCreativeTabStack(holder.get(), EnumNekoColor.WHITE));
                            });
                            halfTimberStacks.sort(HalfTimberCreativeTabOrdering.stackComparator());
                            halfTimberStacks.forEach(output::accept);
                            WoodenBlockRegistration.windowBlockItemsView().forEach(holder -> output.accept(new ItemStack(holder.get())));
                            WoodenBlockRegistration.addFurnitureCategoryStacks(output::accept);
                            WoodenBlockRegistration.addContainerCategoryStacks(output::accept);
                        })
                        .build());

        NEKORATION_ORNAMENTS_TAB = registrar.register(Registries.CREATIVE_MODE_TAB, "nekoration_ornaments",
                () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                        .title(Component.translatable("itemGroup.nekoration_ornaments"))
                        .icon(() -> DyeableBlockItem.createCreativeTabStack(
                                WoodenBlockRegistration.ornamentsTabIconItem().get(), EnumNekoColor.BLACK))
                        .displayItems((parameters, output) -> {
                            OrnamentRegistration.addAwningCategoryStacks(output::accept);
                            WoodenBlockRegistration.addFurnitureCategoryStacks(output::accept);
                            OrnamentRegistration.addFurnitureCategoryStacks(output::accept);
                            WoodenBlockRegistration.addContainerCategoryStacks(output::accept);
                            OrnamentRegistration.addPotsAndPlantersCategoryStacks(output::accept);
                            OrnamentRegistration.addMiscCategoryStacks(output::accept);
                            // Paintings & Palettes...
                            output.accept(new ItemStack(ModItems.PAINTING.get()));
                            output.accept(new ItemStack(ModItems.PALETTE.get()));
                        })
                        .build());
    }
}
