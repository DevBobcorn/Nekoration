package io.devbobcorn.nekoration.registry;

import java.util.List;

import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.DyeableHorizontalConnectedBlock;
import io.devbobcorn.nekoration.blocks.DyeableVerticalConnectedBlock;
import io.devbobcorn.nekoration.blocks.HorizontalConnectedBlock;
import io.devbobcorn.nekoration.blocks.VerticalConnectedBlock;
import io.devbobcorn.nekoration.blocks.cement.DyeableFrameSideBlock;
import io.devbobcorn.nekoration.blocks.cement.DyeablePotBlock;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import net.minecraft.world.item.Item;
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
		() -> new DyeablePotBlock(Block.Properties.ofFullCopy(Blocks.WHITE_CONCRETE), 7));
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
		registerBlockItem(items, "cement_pot", CEMENT_POT),
		registerBlockItem(items, "cement_planter", CEMENT_PLANTER));
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

    /** Creative tab icon ({@value #TAB_ICON_ITEM_ID}). */
    public static DeferredItem<Item> iconItem() {
	return tabIconItem;
    }
}
