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
import io.devbobcorn.nekoration.xplat.NekoRegistrar;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;



public final class CementBlockRegistration {
    private static final List<String> FULL_CUBE_BLOCK_IDS = List.of(
        "cement", "cement_base", "paneled_cement", "paneled_cement_base",
        "cement_pillar_simple", "cement_pillar_doric", "cement_pillar_ionic",
        "cement_pillar_corinthian", "cement_pillar_base", "layered_cement");

    public static RegistrySupplier<DyeableVerticalConnectedBlock> CEMENT;
    public static RegistrySupplier<DyeableBlock> CEMENT_BASE;
    public static RegistrySupplier<DyeableVerticalConnectedBlock> PANELED_CEMENT;
    public static RegistrySupplier<DyeableBlock> PANELED_CEMENT_BASE;
    public static RegistrySupplier<DyeableVerticalConnectedBlock> CEMENT_PILLAR_SIMPLE;
    public static RegistrySupplier<DyeableVerticalConnectedBlock> CEMENT_PILLAR_DORIC;
    public static RegistrySupplier<DyeableVerticalConnectedBlock> CEMENT_PILLAR_IONIC;
    public static RegistrySupplier<DyeableVerticalConnectedBlock> CEMENT_PILLAR_CORINTHIAN;
    public static RegistrySupplier<DyeableBlock> CEMENT_PILLAR_BASE;
    public static RegistrySupplier<DyeableBlock> LAYERED_CEMENT;
    public static RegistrySupplier<DyeableHorizontalConnectedBlock> CEMENT_FRAME_HEAD;
    public static RegistrySupplier<DyeableHorizontalConnectedBlock> CEMENT_FRAME_PEAK;
    public static RegistrySupplier<DyeableHorizontalConnectedBlock> CEMENT_FRAME_SILL;
    public static RegistrySupplier<DyeableFrameSideBlock> CEMENT_FRAME_SIDE;
    public static RegistrySupplier<DyeablePotBlock> CEMENT_POT;
    public static RegistrySupplier<DyeablePotBlock> CEMENT_PLANTER;

    private static final String TAB_ICON_ITEM_ID = "paneled_cement_base";
    private static RegistrySupplier<Item> tabIconItem;

    private static List<RegistrySupplier<Item>> blockItems;

    private static List<RegistrySupplier<? extends Block>> cementBlocks;

    private static final List<RegistrySupplier<Item>> potBlockItems = new ArrayList<>();

    private CementBlockRegistration() {
    }

    public static void register(NekoRegistrar registrar) {
    CEMENT = registrar.block("cement", () -> new DyeableVerticalConnectedBlock(
        Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), VerticalConnectedBlock.ConnectionType.PILLAR, false));
    CEMENT_BASE = registrar.block("cement_base",
        () -> new DyeableBlock(Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE)));
    PANELED_CEMENT = registrar.block("paneled_cement", () -> new DyeableVerticalConnectedBlock(
        Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), VerticalConnectedBlock.ConnectionType.PILLAR, false));
    PANELED_CEMENT_BASE = registrar.block("paneled_cement_base",
        () -> new DyeableBlock(Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE)));
    CEMENT_PILLAR_SIMPLE = registrar.block("cement_pillar_simple", () -> new DyeableVerticalConnectedBlock(
        Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), VerticalConnectedBlock.ConnectionType.PILLAR, false));
    CEMENT_PILLAR_DORIC = registrar.block("cement_pillar_doric", () -> new DyeableVerticalConnectedBlock(
        Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), VerticalConnectedBlock.ConnectionType.PILLAR, false));
    CEMENT_PILLAR_IONIC = registrar.block("cement_pillar_ionic", () -> new DyeableVerticalConnectedBlock(
        Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), VerticalConnectedBlock.ConnectionType.PILLAR, false));
    CEMENT_PILLAR_CORINTHIAN = registrar.block("cement_pillar_corinthian", () -> new DyeableVerticalConnectedBlock(
        Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), VerticalConnectedBlock.ConnectionType.PILLAR, false));
    CEMENT_PILLAR_BASE = registrar.block("cement_pillar_base",
        () -> new DyeableBlock(Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE)));
    LAYERED_CEMENT = registrar.block("layered_cement",
        () -> new DyeableBlock(Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE)));
    CEMENT_FRAME_HEAD = registrar.block("cement_frame_head", () -> new DyeableHorizontalConnectedBlock(
        Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), HorizontalConnectedBlock.ConnectionType.BEAM, false, 2, 3, 0));
    CEMENT_FRAME_PEAK = registrar.block("cement_frame_peak", () -> new DyeableHorizontalConnectedBlock(
        Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), HorizontalConnectedBlock.ConnectionType.TRIPLE, false, 5, 12, 0));
    CEMENT_FRAME_SILL = registrar.block("cement_frame_sill", () -> new DyeableHorizontalConnectedBlock(
        Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), HorizontalConnectedBlock.ConnectionType.BEAM, false, 4, 4, 12));
    CEMENT_FRAME_SIDE = registrar.block("cement_frame_side",
        () -> new DyeableFrameSideBlock(Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE)));
    CEMENT_POT = registrar.block("cement_pot",
        () -> new DyeablePotBlock(Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), 6));
    CEMENT_PLANTER = registrar.block("cement_planter",
        () -> new DyeablePotBlock(Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), 8));

    blockItems = List.of(
        registerBlockItem(registrar, "cement", CEMENT),
        registerBlockItem(registrar, "cement_base", CEMENT_BASE),
        registerBlockItem(registrar, "paneled_cement", PANELED_CEMENT),
        registerBlockItem(registrar, "paneled_cement_base", PANELED_CEMENT_BASE),
        registerBlockItem(registrar, "cement_pillar_simple", CEMENT_PILLAR_SIMPLE),
        registerBlockItem(registrar, "cement_pillar_doric", CEMENT_PILLAR_DORIC),
        registerBlockItem(registrar, "cement_pillar_ionic", CEMENT_PILLAR_IONIC),
        registerBlockItem(registrar, "cement_pillar_corinthian", CEMENT_PILLAR_CORINTHIAN),
        registerBlockItem(registrar, "cement_pillar_base", CEMENT_PILLAR_BASE),
        registerBlockItem(registrar, "layered_cement", LAYERED_CEMENT),
        registerBlockItem(registrar, "cement_frame_head", CEMENT_FRAME_HEAD),
        registerBlockItem(registrar, "cement_frame_peak", CEMENT_FRAME_PEAK),
        registerBlockItem(registrar, "cement_frame_sill", CEMENT_FRAME_SILL),
        registerBlockItem(registrar, "cement_frame_side", CEMENT_FRAME_SIDE),
        registerPotItem(registrar, "cement_pot", CEMENT_POT),
        registerPotItem(registrar, "cement_planter", CEMENT_PLANTER));

    cementBlocks = List.of(
        CEMENT,
        CEMENT_BASE,
        PANELED_CEMENT,
        PANELED_CEMENT_BASE,
        CEMENT_PILLAR_SIMPLE,
        CEMENT_PILLAR_DORIC,
        CEMENT_PILLAR_IONIC,
        CEMENT_PILLAR_CORINTHIAN,
        CEMENT_PILLAR_BASE,
        LAYERED_CEMENT,
        CEMENT_FRAME_HEAD,
        CEMENT_FRAME_PEAK,
        CEMENT_FRAME_SILL,
        CEMENT_FRAME_SIDE,
        CEMENT_POT,
        CEMENT_PLANTER);
    }

    private static RegistrySupplier<Item> registerPotItem(NekoRegistrar registrar, String id,
            RegistrySupplier<? extends Block> block) {
        RegistrySupplier<Item> blockItem = registerBlockItem(registrar, id, block);
        potBlockItems.add(blockItem);
        return blockItem;
    }

    private static RegistrySupplier<Item> registerBlockItem(NekoRegistrar registrar, String id,
        RegistrySupplier<? extends Block> block) {
    RegistrySupplier<Item> blockItem = registrar.item(id,
        () -> new DyeableBlockItem(block.get(), new Item.Properties()));
    if (TAB_ICON_ITEM_ID.equals(id)) {
        tabIconItem = blockItem;
    }
    return blockItem;
    }

    public static List<RegistrySupplier<Item>> blockItemsView() {
    return blockItems;
    }

    public static List<RegistrySupplier<? extends Block>> cementBlocksView() {
    return cementBlocks;
    }

    public static List<String> fullCubeBlockIds() {
    return FULL_CUBE_BLOCK_IDS;
    }

    public static boolean isFullCube(Block block) {
    return block == CEMENT.get()
        || block == CEMENT_BASE.get()
        || block == PANELED_CEMENT.get()
        || block == PANELED_CEMENT_BASE.get()
        || block == CEMENT_PILLAR_SIMPLE.get()
        || block == CEMENT_PILLAR_DORIC.get()
        || block == CEMENT_PILLAR_IONIC.get()
        || block == CEMENT_PILLAR_CORINTHIAN.get()
        || block == CEMENT_PILLAR_BASE.get()
        || block == LAYERED_CEMENT.get();
    }

    /** Creative tab icon ({@value #TAB_ICON_ITEM_ID}). */
    public static RegistrySupplier<Item> iconItem() {
    return tabIconItem;
    }

    /** Icon for the Pots and Planters category of the Ornaments tab ({@code cement_pot}, white). */
    public static RegistrySupplier<Item> potsCategoryIconItem() {
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
