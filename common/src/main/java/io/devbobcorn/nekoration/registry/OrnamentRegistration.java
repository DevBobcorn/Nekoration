package io.devbobcorn.nekoration.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.blocks.CandleHolderBlock;
import io.devbobcorn.nekoration.blocks.FlowerBasketBlock;
import io.devbobcorn.nekoration.blocks.NekoDoorBlock;
import io.devbobcorn.nekoration.blocks.TallDoorBlock;
import io.devbobcorn.nekoration.blocks.WindowPlantBlock;
import io.devbobcorn.nekoration.blocks.AwningBlock;
import io.devbobcorn.nekoration.blocks.LampPostBlock;
import io.devbobcorn.nekoration.blocks.ShortAwningBlock;
import io.devbobcorn.nekoration.blocks.furniture.ChairBlock;
import io.devbobcorn.nekoration.blocks.furniture.PumpkinTableBlock;
import io.devbobcorn.nekoration.items.AwningBlockItem;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.xplat.NekoRegistrar;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;



/**
 * Registers ornaments.
 */
public final class OrnamentRegistration {
    private static RegistrySupplier<Block> WINDOW_PLANT_BLOCK;
    public static RegistrySupplier<DyeableBlockItem> WINDOW_PLANT_BLOCK_ITEM;
    public static final List<RegistrySupplier<DyeableBlockItem>> AWNING_BLOCK_ITEMS = new ArrayList<>();
    public static final List<RegistrySupplier<DyeableBlockItem>> CANDLE_HOLDER_BLOCK_ITEMS = new ArrayList<>();
    public static final List<RegistrySupplier<BlockItem>> FURNITURE_BLOCK_ITEMS = new ArrayList<>();
    private static final List<RegistrySupplier<? extends BlockItem>> MISC_BLOCK_ITEMS = new ArrayList<>();
    private static final List<RegistrySupplier<Block>> LAMP_POST_BLOCKS = new ArrayList<>();
    private static final List<RegistrySupplier<Block>> DOOR_BLOCKS = new ArrayList<>();

    private static final String AWNING_CATEGORY_ICON_ITEM_ID = "short_awning_stripe";
    private static RegistrySupplier<DyeableBlockItem> awningCategoryIconItem;
    private static final String MISC_CATEGORY_ICON_ITEM_ID = "gold_candle_holder";
    private static RegistrySupplier<? extends BlockItem> miscCategoryIconItem;

    private static final List<RegistrySupplier<Block>> AWNING_BLOCKS = new ArrayList<>();
    private static final List<RegistrySupplier<Block>> CANDLE_HOLDER_BLOCKS = new ArrayList<>();
    private static final List<RegistrySupplier<Block>> FLOWER_BASKET_BLOCKS = new ArrayList<>();
    private static final List<RegistrySupplier<Block>> PUMPKIN_FURNITURE_BLOCKS = new ArrayList<>();

    private OrnamentRegistration() {
    }

    public static void register(NekoRegistrar registrar) {
        registerAwning(registrar, "awning_pure", false);
        registerAwning(registrar, "awning_stripe", false);
        registerAwning(registrar, "short_awning_pure", true);
        registerAwning(registrar, "short_awning_stripe", true);
        registerWindowPlant(registrar, "window_plant");
        registerLampPost(registrar, "iron_lamp_post", Blocks.IRON_BLOCK);
        registerLampPost(registrar, "gold_lamp_post", Blocks.GOLD_BLOCK);
        registerLampPost(registrar, "quartz_lamp_post", Blocks.QUARTZ_BLOCK);
        registerCandleHolder(registrar, "iron_candle_holder", Blocks.IRON_BLOCK);
        registerCandleHolder(registrar, "gold_candle_holder", Blocks.GOLD_BLOCK);
        registerCandleHolder(registrar, "quartz_candle_holder", Blocks.QUARTZ_BLOCK);
        registerFlowerBasket(registrar, "iron_flower_basket", Blocks.IRON_BLOCK);
        registerFlowerBasket(registrar, "gold_flower_basket", Blocks.GOLD_BLOCK);
        registerFlowerBasket(registrar, "quartz_flower_basket", Blocks.QUARTZ_BLOCK);
        registerDoors(registrar);
        registerPumpkinFurniture(registrar);
    }

    private static void registerDoors(NekoRegistrar registrar) {
        RegistrySupplier<Block> tallQuartz = registerTallDoor(registrar, "tall_quartz_door");
        RegistrySupplier<Block> tallChiseled = registerTallDoor(registrar, "tall_chiseled_quartz_door");
        RegistrySupplier<Block> tallBricks = registerTallDoor(registrar, "tall_quartz_bricks_door");

        registerDoor(registrar, "quartz_door", tallQuartz);
        registerDoor(registrar, "chiseled_quartz_door", tallChiseled);
        registerDoor(registrar, "quartz_bricks_door", tallBricks);

        MISC_BLOCK_ITEMS.add(registerDyeableBlockItem(registrar, "tall_quartz_door", tallQuartz));
        MISC_BLOCK_ITEMS.add(registerDyeableBlockItem(registrar, "tall_chiseled_quartz_door", tallChiseled));
        MISC_BLOCK_ITEMS.add(registerDyeableBlockItem(registrar, "tall_quartz_bricks_door", tallBricks));
    }

    private static RegistrySupplier<Block> registerTallDoor(NekoRegistrar registrar, String id) {
        RegistrySupplier<Block> block = registrar.block(id, () -> new TallDoorBlock(doorProperties()));
        DOOR_BLOCKS.add(block);
        return block;
    }

    private static void registerDoor(NekoRegistrar registrar, String id,
            RegistrySupplier<Block> tallVariant) {
        RegistrySupplier<Block> block = registrar.block(id,
                () -> new NekoDoorBlock(doorProperties(), () -> tallVariant.get()));
        MISC_BLOCK_ITEMS.add(registerDyeableBlockItem(registrar, id, block));
        DOOR_BLOCKS.add(block);
    }

    private static BlockBehaviour.Properties doorProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BLOCK)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }

    private static void registerPumpkinFurniture(NekoRegistrar registrar) {
        RegistrySupplier<Block> table = registrar.block("pumpkin_table", () -> new PumpkinTableBlock(pumpkinFurnitureProperties()));
        FURNITURE_BLOCK_ITEMS.add(registrar.item("pumpkin_table",
                () -> new BlockItem(table.get(), new Item.Properties())));
        PUMPKIN_FURNITURE_BLOCKS.add(table);

        RegistrySupplier<Block> chair = registrar.block("pumpkin_chair",
                () -> new ChairBlock(pumpkinFurnitureProperties(), 8, 24, 1, 4));
        FURNITURE_BLOCK_ITEMS.add(registrar.item("pumpkin_chair",
                () -> new BlockItem(chair.get(), new Item.Properties())));
        PUMPKIN_FURNITURE_BLOCKS.add(chair);
    }

    private static BlockBehaviour.Properties pumpkinFurnitureProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.PUMPKIN).noOcclusion();
    }

    private static void registerCandleHolder(NekoRegistrar registrar, String id,
            Block material) {
        RegistrySupplier<Block> block = registrar.block(id,
                () -> new CandleHolderBlock(BlockBehaviour.Properties.ofFullCopy(material).noOcclusion()
                        .lightLevel(state -> state.getValue(CandleHolderBlock.FLAME).isLit() ? 15 : 0)));
        CANDLE_HOLDER_BLOCKS.add(block);
        RegistrySupplier<DyeableBlockItem> item = registerDyeableBlockItem(registrar, id, block);
        CANDLE_HOLDER_BLOCK_ITEMS.add(item);
        MISC_BLOCK_ITEMS.add(item);
        if (MISC_CATEGORY_ICON_ITEM_ID.equals(id)) {
            miscCategoryIconItem = item;
        }
    }

    private static void registerFlowerBasket(NekoRegistrar registrar, String id,
            Block material) {
        RegistrySupplier<Block> block = registrar.block(id,
                () -> new FlowerBasketBlock(BlockBehaviour.Properties.ofFullCopy(material).noOcclusion()));
        FLOWER_BASKET_BLOCKS.add(block);
        MISC_BLOCK_ITEMS.add(registrar.item(id, () -> new BlockItem(block.get(), new Item.Properties())));
    }

    private static void registerLampPost(NekoRegistrar registrar, String id,
            Block material) {
        RegistrySupplier<Block> block = registrar.block(id,
                () -> new LampPostBlock(BlockBehaviour.Properties.ofFullCopy(material).noOcclusion()));
        LAMP_POST_BLOCKS.add(block);
        MISC_BLOCK_ITEMS.add(registrar.item(id, () -> new BlockItem(block.get(), new Item.Properties())));
    }

    private static void registerAwning(NekoRegistrar registrar, String id, boolean shortAwning) {
        RegistrySupplier<Block> block = registrar.block(id, () -> shortAwning
                ? new ShortAwningBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).noOcclusion())
                : new AwningBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).noOcclusion()));
        AWNING_BLOCKS.add(block);
        RegistrySupplier<DyeableBlockItem> item = shortAwning
                ? registerDyeableBlockItem(registrar, id, block)
                : registerAwningBlockItem(registrar, id, block);
        AWNING_BLOCK_ITEMS.add(item);
        if (AWNING_CATEGORY_ICON_ITEM_ID.equals(id)) {
            awningCategoryIconItem = item;
        }
    }

    public static List<RegistrySupplier<DyeableBlockItem>> awningBlockItemsView() {
        return Collections.unmodifiableList(AWNING_BLOCK_ITEMS);
    }

    public static List<RegistrySupplier<Block>> lampPostBlocksView() {
        return Collections.unmodifiableList(LAMP_POST_BLOCKS);
    }

    public static List<RegistrySupplier<Block>> doorBlocksView() {
        return Collections.unmodifiableList(DOOR_BLOCKS);
    }

    public static List<RegistrySupplier<DyeableBlockItem>> candleHolderBlockItemsView() {
        return Collections.unmodifiableList(CANDLE_HOLDER_BLOCK_ITEMS);
    }

    public static List<RegistrySupplier<Block>> awningBlocksView() {
        return Collections.unmodifiableList(AWNING_BLOCKS);
    }

    public static List<RegistrySupplier<Block>> candleHolderBlocksView() {
        return Collections.unmodifiableList(CANDLE_HOLDER_BLOCKS);
    }

    public static List<RegistrySupplier<Block>> flowerBasketBlocksView() {
        return Collections.unmodifiableList(FLOWER_BASKET_BLOCKS);
    }

    public static List<RegistrySupplier<Block>> pumpkinFurnitureBlocksView() {
        return Collections.unmodifiableList(PUMPKIN_FURNITURE_BLOCKS);
    }

    private static void registerWindowPlant(NekoRegistrar registrar, String id) {
        RegistrySupplier<Block> block = registrar.block(id,
                () -> new WindowPlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).noOcclusion()));
        RegistrySupplier<DyeableBlockItem> item = registerDyeableBlockItem(registrar, id, block);
        WINDOW_PLANT_BLOCK = block;
        WINDOW_PLANT_BLOCK_ITEM = item;
    }

    private static RegistrySupplier<DyeableBlockItem> registerDyeableBlockItem(NekoRegistrar registrar, String id,
            RegistrySupplier<Block> block) {
        return registrar.item(id, () -> new DyeableBlockItem(block.get(), new Item.Properties()));
    }

    private static RegistrySupplier<DyeableBlockItem> registerAwningBlockItem(NekoRegistrar registrar, String id,
            RegistrySupplier<Block> block) {
        return registrar.item(id, () -> new AwningBlockItem(block.get(), new Item.Properties()));
    }

    public static RegistrySupplier<Block> windowPlantBlock() {
        return WINDOW_PLANT_BLOCK;
    }

    public static RegistrySupplier<DyeableBlockItem> windowPlantBlockItem() {
        return WINDOW_PLANT_BLOCK_ITEM;
    }

    /** Icon for the Window Attachment category of the Ornaments tab ({@value #AWNING_CATEGORY_ICON_ITEM_ID}). */
    public static RegistrySupplier<DyeableBlockItem> awningCategoryIconItem() {
        return awningCategoryIconItem;
    }

    /** Add awning and window plant stacks in every color (Window Attachment category of the Ornaments tab). */
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
    public static RegistrySupplier<? extends BlockItem> miscCategoryIconItem() {
        return miscCategoryIconItem;
    }

    /** Add pumpkin furniture stacks (Furniture category of the Ornaments tab). */
    public static void addFurnitureCategoryStacks(Consumer<ItemStack> out) {
        for (var holder : FURNITURE_BLOCK_ITEMS) {
            out.accept(new ItemStack(holder.get()));
        }
    }

    public static void addMiscCategoryStacks(Consumer<ItemStack> out) {
        for (var holder : MISC_BLOCK_ITEMS) {
            BlockItem item = holder.get();
            out.accept(item instanceof DyeableBlockItem dyeable
                    ? DyeableBlockItem.createCreativeTabStack(dyeable, EnumNekoColor.WHITE)
                    : new ItemStack(item));
        }
        out.accept(new ItemStack(ModItems.WALLPAPER.get()));
    }

    /** Add stone and cement pot and planter stacks (Pots and Planters category of the Ornaments tab). */
    public static void addPotsAndPlantersCategoryStacks(Consumer<ItemStack> out) {
        StoneBlockRegistration.addPotsAndPlantersStacks(out);
        CementBlockRegistration.addPotsAndPlantersStacks(out);
    }
}
