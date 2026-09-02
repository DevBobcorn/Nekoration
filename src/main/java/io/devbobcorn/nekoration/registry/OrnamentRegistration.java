package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.blocks.CandleHolderBlock;
import io.devbobcorn.nekoration.blocks.FlowerBasketBlock;
import io.devbobcorn.nekoration.blocks.WindowPlantBlock;
import io.devbobcorn.nekoration.blocks.AwningBlock;
import io.devbobcorn.nekoration.blocks.LampPostBlock;
import io.devbobcorn.nekoration.blocks.ShortAwningBlock;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers ornaments.
 */
public final class OrnamentRegistration {
    private static DeferredBlock<Block> WINDOW_PLANT_BLOCK;
    public static DeferredItem<DyeableBlockItem> WINDOW_PLANT_BLOCK_ITEM;
    public static final List<DeferredItem<DyeableBlockItem>> AWNING_BLOCK_ITEMS = new ArrayList<>();
    public static final List<DeferredItem<DyeableBlockItem>> CANDLE_HOLDER_BLOCK_ITEMS = new ArrayList<>();
    private static final List<DeferredItem<? extends BlockItem>> MISC_BLOCK_ITEMS = new ArrayList<>();
    private static final List<DeferredBlock<Block>> LAMP_POST_BLOCKS = new ArrayList<>();

    private static final String AWNING_CATEGORY_ICON_ITEM_ID = "short_awning_stripe";
    private static DeferredItem<DyeableBlockItem> awningCategoryIconItem;
    private static final String MISC_CATEGORY_ICON_ITEM_ID = "gold_candle_holder";
    private static DeferredItem<? extends BlockItem> miscCategoryIconItem;

    private OrnamentRegistration() {
    }

    public static void register(DeferredRegister.Blocks blocks, DeferredRegister.Items items) {
        registerAwning(blocks, items, "awning_pure", false);
        registerAwning(blocks, items, "awning_stripe", false);
        registerAwning(blocks, items, "short_awning_pure", true);
        registerAwning(blocks, items, "short_awning_stripe", true);
        registerWindowPlant(blocks, items, "window_plant");
        registerLampPost(blocks, items, "iron_lamp_post", Blocks.IRON_BLOCK);
        registerLampPost(blocks, items, "gold_lamp_post", Blocks.GOLD_BLOCK);
        registerLampPost(blocks, items, "quartz_lamp_post", Blocks.QUARTZ_BLOCK);
        registerCandleHolder(blocks, items, "iron_candle_holder", Blocks.IRON_BLOCK);
        registerCandleHolder(blocks, items, "gold_candle_holder", Blocks.GOLD_BLOCK);
        registerCandleHolder(blocks, items, "quartz_candle_holder", Blocks.QUARTZ_BLOCK);
        registerFlowerBasket(blocks, items, "iron_flower_basket", Blocks.IRON_BLOCK);
        registerFlowerBasket(blocks, items, "gold_flower_basket", Blocks.GOLD_BLOCK);
        registerFlowerBasket(blocks, items, "quartz_flower_basket", Blocks.QUARTZ_BLOCK);
    }

    private static void registerCandleHolder(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
            Block material) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new CandleHolderBlock(BlockBehaviour.Properties.ofFullCopy(material).strength(0.0F).noOcclusion()
                        .lightLevel(state -> state.getValue(CandleHolderBlock.FLAME).isLit() ? 15 : 0)));
        DeferredItem<DyeableBlockItem> item = registerDyeableBlockItem(items, id, block);
        CANDLE_HOLDER_BLOCK_ITEMS.add(item);
        MISC_BLOCK_ITEMS.add(item);
        if (MISC_CATEGORY_ICON_ITEM_ID.equals(id)) {
            miscCategoryIconItem = item;
        }
    }

    private static void registerFlowerBasket(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
            Block material) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new FlowerBasketBlock(BlockBehaviour.Properties.ofFullCopy(material).strength(0.0F).noOcclusion()));
        MISC_BLOCK_ITEMS.add(items.registerSimpleBlockItem(id, block));
    }

    private static void registerLampPost(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id,
            Block material) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new LampPostBlock(BlockBehaviour.Properties.ofFullCopy(material).strength(2, 6).noOcclusion()));
        LAMP_POST_BLOCKS.add(block);
        MISC_BLOCK_ITEMS.add(items.registerSimpleBlockItem(id, block));
    }

    private static void registerAwning(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id, boolean shortAwning) {
        DeferredBlock<Block> block = blocks.register(id, () -> shortAwning
                ? new ShortAwningBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).noOcclusion())
                : new AwningBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).noOcclusion()));
        DeferredItem<DyeableBlockItem> item = registerDyeableBlockItem(items, id, block);
        AWNING_BLOCK_ITEMS.add(item);
        if (AWNING_CATEGORY_ICON_ITEM_ID.equals(id)) {
            awningCategoryIconItem = item;
        }
    }

    public static List<DeferredItem<DyeableBlockItem>> awningBlockItemsView() {
        return Collections.unmodifiableList(AWNING_BLOCK_ITEMS);
    }

    public static List<DeferredBlock<Block>> lampPostBlocksView() {
        return Collections.unmodifiableList(LAMP_POST_BLOCKS);
    }

    public static List<DeferredItem<DyeableBlockItem>> candleHolderBlockItemsView() {
        return Collections.unmodifiableList(CANDLE_HOLDER_BLOCK_ITEMS);
    }

    private static void registerWindowPlant(DeferredRegister.Blocks blocks, DeferredRegister.Items items, String id) {
        DeferredBlock<Block> block = blocks.register(id,
                () -> new WindowPlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).noOcclusion()));
        DeferredItem<DyeableBlockItem> item = registerDyeableBlockItem(items, id, block);
        WINDOW_PLANT_BLOCK = block;
        WINDOW_PLANT_BLOCK_ITEM = item;
    }

    private static DeferredItem<DyeableBlockItem> registerDyeableBlockItem(DeferredRegister.Items items, String id,
            DeferredBlock<Block> block) {
        return items.registerItem(id, props -> new DyeableBlockItem(block.get(), props), new net.minecraft.world.item.Item.Properties());
    }

    public static DeferredBlock<Block> windowPlantBlock() {
        return WINDOW_PLANT_BLOCK;
    }

    public static DeferredItem<DyeableBlockItem> windowPlantBlockItem() {
        return WINDOW_PLANT_BLOCK_ITEM;
    }

    /** Icon for the Awning category of the Ornaments tab ({@value #AWNING_CATEGORY_ICON_ITEM_ID}). */
    public static DeferredItem<DyeableBlockItem> awningCategoryIconItem() {
        return awningCategoryIconItem;
    }

    /** Add awning and window plant stacks in every color (Awning category of the Ornaments tab). */
    public static void addAwningCategoryStacks(Consumer<ItemStack> out) {
        for (var holder : awningBlockItemsView()) {
            for (EnumNekoColor color : EnumNekoColor.values()) {
                out.accept(DyeableBlockItem.createCreativeTabStack(holder.get(), color));
            }
        }
        for (EnumNekoColor color : EnumNekoColor.values()) {
            out.accept(DyeableBlockItem.createCreativeTabStack(WINDOW_PLANT_BLOCK_ITEM.get(), color));
        }
    }

    /** Icon for the Misc category of the Ornaments tab ({@value #MISC_CATEGORY_ICON_ITEM_ID}). */
    public static DeferredItem<? extends BlockItem> miscCategoryIconItem() {
        return miscCategoryIconItem;
    }

    public static void addMiscCategoryStacks(Consumer<ItemStack> out) {
        for (var holder : MISC_BLOCK_ITEMS) {
            BlockItem item = holder.get();
            out.accept(item instanceof DyeableBlockItem dyeable
                    ? DyeableBlockItem.createCreativeTabStack(dyeable, EnumNekoColor.WHITE)
                    : new ItemStack(item));
        }
    }

    /** Add stone and cement pot and planter stacks (Pots and Planters category of the Ornaments tab). */
    public static void addPotsAndPlantersCategoryStacks(Consumer<ItemStack> out) {
        StoneBlockRegistration.addPotsAndPlantersStacks(out);
        CementBlockRegistration.addPotsAndPlantersStacks(out);
    }
}
