package io.devbobcorn.nekoration.registry;

import java.util.List;

import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.DyeableVerticalConnectedBlock;
import io.devbobcorn.nekoration.blocks.VerticalConnectedBlock;
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

	blockItems = List.of(
		registerBlockItem(items, "cement", CEMENT),
		registerBlockItem(items, "trimmed_cement", TRIMMED_CEMENT),
		registerBlockItem(items, "paneled_cement", PANELED_CEMENT),
		registerBlockItem(items, "layered_cement", LAYERED_CEMENT));
    }

    private static DeferredItem<Item> registerBlockItem(DeferredRegister.Items items, String id,
	    DeferredBlock<? extends Block> block) {
	return items.registerItem(id, properties -> new DyeableBlockItem(block.get(), properties), new Item.Properties());
    }

    public static List<DeferredItem<Item>> blockItemsView() {
	return blockItems;
    }

    public static DeferredItem<Item> iconItem() {
	return blockItems.getFirst();
    }
}
