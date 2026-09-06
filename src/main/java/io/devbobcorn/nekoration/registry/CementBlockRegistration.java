package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.DyeableHorizontalConnectedBlock;
import io.devbobcorn.nekoration.blocks.DyeableVerticalConnectedBlock;
import io.devbobcorn.nekoration.blocks.HorizontalConnectedBlock;
import io.devbobcorn.nekoration.blocks.VerticalConnectedBlock;
import io.devbobcorn.nekoration.blocks.cement.DyeableFrameSideBlock;
import io.devbobcorn.nekoration.blocks.cement.DyeablePotBlock;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CementBlockRegistration {
    public static DeferredBlock<DyeableVerticalConnectedBlock> CEMENT;
    public static DeferredBlock<DyeableBlock> TRIMMED_CEMENT;
    public static DeferredBlock<DyeableVerticalConnectedBlock> PANELED_CEMENT;
    public static DeferredBlock<DyeableBlock> LAYERED_CEMENT;
    public static DeferredBlock<DyeableHorizontalConnectedBlock> CEMENT_FRAME_HEAD;
    public static DeferredBlock<DyeableHorizontalConnectedBlock> CEMENT_FRAME_PEAK;
    public static DeferredBlock<DyeableHorizontalConnectedBlock> CEMENT_FRAME_SILL;
    public static DeferredBlock<DyeableFrameSideBlock> CEMENT_FRAME_SIDE;
    public static DeferredBlock<DyeablePotBlock> CEMENT_POT;
    public static DeferredBlock<DyeablePotBlock> CEMENT_PLANTER;

    private static final String TAB_ICON_ITEM_ID = "paneled_cement";
    private static DeferredItem<Item> tabIconItem;

    private static List<DeferredItem<Item>> blockItems;

    private static List<DeferredBlock<? extends Block>> cementBlocks;

    private static final List<DeferredItem<Item>> potBlockItems = new ArrayList<>();

    private CementBlockRegistration() {
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
    CEMENT = blocks.register("cement", () -> new DyeableVerticalConnectedBlock(
        Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), VerticalConnectedBlock.ConnectionType.PILLAR, false));
    TRIMMED_CEMENT = blocks.register("trimmed_cement",
        () -> new DyeableBlock(Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE)));
    PANELED_CEMENT = blocks.register("paneled_cement", () -> new DyeableVerticalConnectedBlock(
        Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), VerticalConnectedBlock.ConnectionType.PILLAR, false));
    LAYERED_CEMENT = blocks.register("layered_cement",
        () -> new DyeableBlock(Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE)));
    CEMENT_FRAME_HEAD = blocks.register("cement_frame_head", () -> new DyeableHorizontalConnectedBlock(
        Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), HorizontalConnectedBlock.ConnectionType.BEAM, false, 2, 3, 0));
    CEMENT_FRAME_PEAK = blocks.register("cement_frame_peak", () -> new DyeableHorizontalConnectedBlock(
        Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), HorizontalConnectedBlock.ConnectionType.TRIPLE, false, 5, 12, 0));
    CEMENT_FRAME_SILL = blocks.register("cement_frame_sill", () -> new DyeableHorizontalConnectedBlock(
        Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), HorizontalConnectedBlock.ConnectionType.BEAM, false, 4, 4, 12));
    CEMENT_FRAME_SIDE = blocks.register("cement_frame_side",
        () -> new DyeableFrameSideBlock(Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE)));
    CEMENT_POT = blocks.register("cement_pot",
        () -> new DyeablePotBlock(Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), 6));
    CEMENT_PLANTER = blocks.register("cement_planter",
        () -> new DyeablePotBlock(Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), 8));

    blockItems = List.of(
        registerBlockItem(items, "cement", CEMENT),
        registerBlockItem(items, "trimmed_cement", TRIMMED_CEMENT),
        registerBlockItem(items, "paneled_cement", PANELED_CEMENT),
        registerBlockItem(items, "layered_cement", LAYERED_CEMENT),
        registerBlockItem(items, "cement_frame_head", CEMENT_FRAME_HEAD),
        registerBlockItem(items, "cement_frame_peak", CEMENT_FRAME_PEAK),
        registerBlockItem(items, "cement_frame_sill", CEMENT_FRAME_SILL),
        registerBlockItem(items, "cement_frame_side", CEMENT_FRAME_SIDE),
        registerPotItem(items, "cement_pot", CEMENT_POT),
        registerPotItem(items, "cement_planter", CEMENT_PLANTER));

    cementBlocks = List.of(
        CEMENT,
        TRIMMED_CEMENT,
        PANELED_CEMENT,
        LAYERED_CEMENT,
        CEMENT_FRAME_HEAD,
        CEMENT_FRAME_PEAK,
        CEMENT_FRAME_SILL,
        CEMENT_FRAME_SIDE,
        CEMENT_POT,
        CEMENT_PLANTER);
    }

    private static DeferredItem<Item> registerPotItem(DeferredRegister.Items items, String id,
            DeferredBlock<? extends Block> block) {
        DeferredItem<Item> blockItem = registerBlockItem(items, id, block);
        potBlockItems.add(blockItem);
        return blockItem;
    }

    private static DeferredItem<Item> registerBlockItem(DeferredRegister.Items items, String id,
        DeferredBlock<? extends Block> block) {
    DeferredItem<Item> blockItem = items.registerItem(id,
        properties -> new DyeableBlockItem(block.get(), properties), new Item.Properties());
    if (TAB_ICON_ITEM_ID.equals(id)) {
        tabIconItem = blockItem;
    }
    return blockItem;
    }

    public static List<DeferredItem<Item>> blockItemsView() {
    return blockItems;
    }

    public static List<DeferredBlock<? extends Block>> cementBlocksView() {
    return cementBlocks;
    }

    /** Creative tab icon ({@value #TAB_ICON_ITEM_ID}). */
    public static DeferredItem<Item> iconItem() {
    return tabIconItem;
    }

    /** Icon for the Pots and Planters category of the Ornaments tab ({@code cement_pot}, white). */
    public static DeferredItem<Item> potsCategoryIconItem() {
    return potBlockItems.getFirst();
    }

    /** Add cement pot and planter stacks in every color (Pots and Planters category of the Ornaments tab). */
    public static void addPotsAndPlantersStacks(Consumer<ItemStack> out) {
    for (var holder : potBlockItems) {
        for (EnumNekoColor color : EnumNekoColor.values()) {
        out.accept(DyeableBlockItem.createCreativeTabStack(holder.get(), color));
        }
    }
    }
}
