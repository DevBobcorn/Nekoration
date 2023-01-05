package com.devbobcorn.nekoration.blocks;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.frames.DyeableWindowFrameBlock;
import com.devbobcorn.nekoration.blocks.frames.DyeableWindowSillBlock;
import com.devbobcorn.nekoration.blocks.frames.DyeableWindowTopBlock;
import com.devbobcorn.nekoration.items.DyeableBlockItem;
import com.devbobcorn.nekoration.items.DyeableWoodenBlockItem;
import com.devbobcorn.nekoration.items.HalfTimberBlockItem;
import com.devbobcorn.nekoration.items.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Nekoration.MODID);

    private static final Item.Properties stoneItemProps = new Item.Properties(); //.tab(ModItemTabs.STONE_GROUP);
    private static final Item.Properties woodenItemProps = new Item.Properties(); //.tab(ModItemTabs.WOODEN_GROUP);
    private static final Item.Properties winAndDoorItemProps = new Item.Properties(); //.tab(ModItemTabs.WINDOW_N_DOOR_GROUP);
    private static final Item.Properties decorItemProps = new Item.Properties(); //.tab(ModItemTabs.DECOR_GROUP);

    public static final RegistryObject<Block> STONE_LAYERED = registerDyeable("stone_layered", () -> new StoneBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F)), stoneItemProps);
    public static final RegistryObject<Block> STONE_BASE_BOTTOM = registerDyeable("stone_base_bottom", () -> new StoneBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F)), stoneItemProps);
    public static final RegistryObject<Block> STONE_FRAME_BOTTOM = registerDyeable("stone_frame_bottom", () -> new StoneBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F)), stoneItemProps);
    public static final RegistryObject<Block> STONE_PILLAR_BOTTOM = registerDyeable("stone_pillar_bottom", () -> new StoneBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F)), stoneItemProps);
    public static final RegistryObject<Block> STONE_BASE = registerDyeable("stone_base", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), stoneItemProps);
    public static final RegistryObject<Block> STONE_FRAME = registerDyeable("stone_frame", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), stoneItemProps);
    public static final RegistryObject<Block> STONE_PILLAR = registerDyeable("stone_pillar", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), stoneItemProps);
    public static final RegistryObject<Block> STONE_DORIC = registerDyeable("stone_doric", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), stoneItemProps);
    public static final RegistryObject<Block> STONE_IONIC = registerDyeable("stone_ionic", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), stoneItemProps);
    public static final RegistryObject<Block> STONE_CORINTHIAN = registerDyeable("stone_corinthian", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), stoneItemProps);

    public static final RegistryObject<Block> STONE_BOTTOM_THIN = registerDyeable("stone_bottom_thin", () -> new StoneBlock(Block.Properties.of(Material.STONE).noOcclusion().strength(1.5F, 6.0F)), stoneItemProps);
    public static final RegistryObject<Block> STONE_PILLAR_THIN = registerDyeable("stone_pillar_thin", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).noOcclusion().strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), stoneItemProps);
    public static final RegistryObject<Block> STONE_DORIC_THIN = registerDyeable("stone_doric_thin", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).noOcclusion().strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), stoneItemProps);
    public static final RegistryObject<Block> STONE_IONIC_THIN = registerDyeable("stone_ionic_thin", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).noOcclusion().strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), stoneItemProps);
    public static final RegistryObject<Block> STONE_CORINTHIAN_THIN = registerDyeable("stone_corinthian_thin", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).noOcclusion().strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), stoneItemProps);

    public static final RegistryObject<Block> WINDOW_TOP = registerDyeable("window_top", () -> new DyeableWindowTopBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F).noOcclusion(), DyeableHorizontalConnectBlock.ConnectionType.TRIPLE, false), winAndDoorItemProps);
    public static final RegistryObject<Block> WINDOW_SILL = registerDyeable("window_sill", () -> new DyeableWindowSillBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F).noOcclusion(), DyeableHorizontalConnectBlock.ConnectionType.BEAM, false), winAndDoorItemProps);
    public static final RegistryObject<Block> WINDOW_PLANT = registerDyeable("window_plant", () -> new DyeableHorizontalConnectBlock(Block.Properties.of(Material.LEAVES).strength(0.0F).noOcclusion(), DyeableHorizontalConnectBlock.ConnectionType.BEAM, false), winAndDoorItemProps);

    public static final RegistryObject<Block> WINDOW_FRAME = registerDyeable("window_frame", () -> new DyeableWindowFrameBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F).noOcclusion()), winAndDoorItemProps);

    public static final RegistryObject<Block> STONE_POT = registerDyeable("stone_pot", () -> new PotBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F).noOcclusion(), 6.0D), decorItemProps);
    public static final RegistryObject<Block> STONE_PLANTER = registerDyeable("stone_planter", () -> new PotBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F).noOcclusion(), 8.0D), decorItemProps);

    public static final RegistryObject<Block> HALF_TIMBER_P0 = registerHalfTimber("half_timber_p0", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P1 = registerHalfTimber("half_timber_p1", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P2 = registerHalfTimber("half_timber_p2", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P3 = registerHalfTimber("half_timber_p3", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P4 = registerHalfTimber("half_timber_p4", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P5 = registerHalfTimber("half_timber_p5", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P6 = registerHalfTimber("half_timber_p6", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P7 = registerHalfTimber("half_timber_p7", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P8 = registerHalfTimber("half_timber_p8", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P9 = registerHalfTimber("half_timber_p9", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));

    public static final RegistryObject<Block> HALF_TIMBER_PILLAR_P0 = registerHalfTimber("half_timber_pillar_p0", () -> new HalfTimberPillarBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F), HalfTimberPillarBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> HALF_TIMBER_PILLAR_P1 = registerHalfTimber("half_timber_pillar_p1", () -> new HalfTimberPillarBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F), HalfTimberPillarBlock.ConnectionType.TRIPLE, false));
    public static final RegistryObject<Block> HALF_TIMBER_PILLAR_P2 = registerHalfTimber("half_timber_pillar_p2", () -> new HalfTimberPillarBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F), HalfTimberPillarBlock.ConnectionType.TRIPLE, false));
    
    public static final RegistryObject<Block> WINDOW_SIMPLE = registerDyeableWooden("window_simple", () -> new WindowBlock(Block.Properties.of(Material.GLASS).noOcclusion().isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), winAndDoorItemProps);
    public static final RegistryObject<Block> WINDOW_ARCH = registerDyeableWooden("window_arch", () -> new WindowBlock(Block.Properties.of(Material.GLASS).noOcclusion().isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), winAndDoorItemProps);
    public static final RegistryObject<Block> WINDOW_CROSS = registerDyeableWooden("window_cross", () -> new WindowBlock(Block.Properties.of(Material.GLASS).noOcclusion().isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), winAndDoorItemProps);
    public static final RegistryObject<Block> WINDOW_SHADE = registerDyeableWooden("window_shade", () -> new WindowBlock(Block.Properties.of(Material.GLASS).noOcclusion().isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), winAndDoorItemProps);
    public static final RegistryObject<Block> WINDOW_LANCET = registerDyeableWooden("window_lancet", () -> new WindowBlock(Block.Properties.of(Material.GLASS).noOcclusion().isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false), winAndDoorItemProps);

    public static final RegistryObject<Block> DOOR_1 = register("door_1", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN), winAndDoorItemProps);
    public static final RegistryObject<Block> DOOR_2 = register("door_2", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN), winAndDoorItemProps);
    public static final RegistryObject<Block> DOOR_3 = register("door_3", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN), winAndDoorItemProps);

    public static final RegistryObject<Block> DOOR_TALL_1 = register("door_tall_1", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN, true), winAndDoorItemProps);
    public static final RegistryObject<Block> DOOR_TALL_2 = register("door_tall_2", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN, true), winAndDoorItemProps);
    public static final RegistryObject<Block> DOOR_TALL_3 = register("door_tall_3", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN, true), winAndDoorItemProps);

    public static final RegistryObject<Block> AWNING_PURE = registerDyeable("awning_pure", () -> new AwningBlock(Block.Properties.of(Material.WOOL).strength(0.8F)));
    public static final RegistryObject<Block> AWNING_STRIPE = registerDyeable("awning_stripe", () -> new AwningBlock(Block.Properties.of(Material.WOOL).strength(0.8F)));
    public static final RegistryObject<Block> AWNING_PURE_SHORT = registerDyeable("awning_pure_short", () -> new ShortAwningBlock(Block.Properties.of(Material.WOOL).strength(0.8F)));
    public static final RegistryObject<Block> AWNING_STRIPE_SHORT = registerDyeable("awning_stripe_short", () -> new ShortAwningBlock(Block.Properties.of(Material.WOOL).strength(0.8F)));

    public static final RegistryObject<Block> LAMP_POST_IRON = register("lamp_post_iron", () -> new LampPostBlock(Block.Properties.of(Material.METAL).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> LAMP_POST_GOLD = register("lamp_post_gold", () -> new LampPostBlock(Block.Properties.of(Material.METAL).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> LAMP_POST_QUARTZ = register("lamp_post_quartz", () -> new LampPostBlock(Block.Properties.of(Material.STONE).strength(2.0F, 6.0F)));

    public static final RegistryObject<Block> CANDLE_HOLDER_IRON = registerDyeable("candle_holder_iron", () -> new CandleHolderBlock(Block.Properties.of(Material.METAL).strength(0.0F).lightLevel(candleHolderEmission(15)).noOcclusion()), decorItemProps, false);
    public static final RegistryObject<Block> CANDLE_HOLDER_GOLD = registerDyeable("candle_holder_gold", () -> new CandleHolderBlock(Block.Properties.of(Material.METAL).strength(0.0F).lightLevel(candleHolderEmission(15)).noOcclusion()), decorItemProps, false);
    public static final RegistryObject<Block> CANDLE_HOLDER_QUARTZ = registerDyeable("candle_holder_quartz", () -> new CandleHolderBlock(Block.Properties.of(Material.STONE).strength(0.0F).lightLevel(candleHolderEmission(15)).noOcclusion()), decorItemProps, false);

    public static final RegistryObject<Block> FLOWER_BASKET_IRON = register("flower_basket_iron", () -> new BasketBlock(Block.Properties.of(Material.METAL).strength(0.0F).noOcclusion(), 6.0D));
    public static final RegistryObject<Block> FLOWER_BASKET_GOLD = register("flower_basket_gold", () -> new BasketBlock(Block.Properties.of(Material.METAL).strength(0.0F).noOcclusion(), 6.0D));
    public static final RegistryObject<Block> FLOWER_BASKET_QUARTZ = register("flower_basket_quartz", () -> new BasketBlock(Block.Properties.of(Material.STONE).strength(0.0F).noOcclusion(), 6.0D));

    public static final RegistryObject<Block> EASEL_MENU = registerDyeableWooden("easel_menu", () -> new EaselMenuBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), false));
    public static final RegistryObject<Block> EASEL_MENU_WHITE = registerDyeableWooden("easel_menu_white", () -> new EaselMenuBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), true));

    public static final RegistryObject<Block> PHONOGRAPH = register("phonograph", () -> new PhonographBlock(Block.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> CUSTOM = register("custom", () -> new CustomBlock(Block.Properties.of(Material.WOOD).strength(1.5F, 6.0F).lightLevel(customBlockEmission()).noOcclusion()));
    // Dream Was Taken only works as a default model provider for Custom Block, and has no other functions
    public static final RegistryObject<Block> DREAM_WAS_TAKEN = registerItemless("dream_was_taken", () -> new Block(Block.Properties.of(Material.WOOD).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> PRISMAP_TABLE = register("prismap_table", () -> new PrismapTableBlock(Block.Properties.of(Material.METAL).strength(1.5F, 6.0F).noOcclusion()));

    public static final RegistryObject<Block> PUMPKIN_TABLE = register("pumpkin_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> PUMPKIN_CHAIR = register("pumpkin_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 8, 24));
    
    public static final RegistryObject<Block> OAK_TABLE = register("oak_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> OAK_ROUND_TABLE = register("oak_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> OAK_CHAIR = register("oak_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 9, 24));
    public static final RegistryObject<Block> JUNGLE_TABLE = register("jungle_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> JUNGLE_ROUND_TABLE = register("jungle_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> JUNGLE_CHAIR = register("jungle_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 10, 26));
    public static final RegistryObject<Block> ACACIA_TABLE = register("acacia_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> ACACIA_ROUND_TABLE = register("acacia_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> ACACIA_CHAIR = register("acacia_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 10, 25));
    public static final RegistryObject<Block> BIRCH_TABLE = register("birch_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> BIRCH_ROUND_TABLE = register("birch_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> BIRCH_CHAIR = register("birch_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 10, 26));
    public static final RegistryObject<Block> DARK_OAK_TABLE = register("dark_oak_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> DARK_OAK_ROUND_TABLE = register("dark_oak_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> DARK_OAK_CHAIR = register("dark_oak_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 9, 24));
    public static final RegistryObject<Block> SPRUCE_TABLE = register("spruce_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> SPRUCE_ROUND_TABLE = register("spruce_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> SPRUCE_CHAIR = register("spruce_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 10, 26));
    public static final RegistryObject<Block> CRIMSON_TABLE = register("crimson_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> CRIMSON_ROUND_TABLE = register("crimson_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> CRIMSON_CHAIR = register("crimson_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 10, 26));
    public static final RegistryObject<Block> WARPED_TABLE = register("warped_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> WARPED_ROUND_TABLE = register("warped_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> WARPED_CHAIR = register("warped_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 10, 26));
    public static final RegistryObject<Block> MANGROVE_TABLE = register("mangrove_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> MANGROVE_ROUND_TABLE = register("mangrove_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> MANGROVE_CHAIR = register("mangrove_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 10, 26));

    public static final RegistryObject<Block> GLASS_TABLE = registerDyeableWooden("glass_table", () -> new DyeableWoodenBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> GLASS_ROUND_TABLE = registerDyeableWooden("glass_round_table", () -> new DyeableWoodenBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> ARM_CHAIR = registerDyeableWooden("arm_chair", () -> new DyeableChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 8, 24));
    public static final RegistryObject<Block> BENCH = registerDyeableWooden("bench", () -> new BenchBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));

    public static final RegistryObject<Block> DRAWER = registerDyeableWooden("drawer", () -> new CabinetBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), false));
    public static final RegistryObject<Block> CABINET = registerDyeableWooden("cabinet", () -> new CabinetBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), true));
    public static final RegistryObject<Block> DRAWER_CHEST = registerDyeableWooden("drawer_chest", () -> new CabinetBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), true));

    public static final RegistryObject<Block> CUPBOARD = registerDyeableWooden("cupboard", () -> new CupboardBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), true));
    public static final RegistryObject<Block> SHELF = registerDyeableWooden("shelf", () -> new CupboardBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), false));
    public static final RegistryObject<Block> WALL_SHELF = registerDyeableWooden("wall_shelf", () -> new WallShelfBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));

    private static RegistryObject<Block> register(String name, Supplier<Block> block) {
        // Use decor tab by default
        return register(name, block, decorItemProps);
    }

    private static RegistryObject<Block> register(String name, Supplier<Block> block, Item.Properties itemProps) {
        RegistryObject<Block> regObject = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(regObject.get(), itemProps));
        return regObject;
    }

    private static RegistryObject<Block> registerDyeable(String name, Supplier<Block> block) {
        // Use decor tab by default
        return registerDyeable(name, block, decorItemProps);
    }

    private static RegistryObject<Block> registerDyeable(String name, Supplier<Block> block, Item.Properties itemProps) {
        RegistryObject<Block> regObject = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new DyeableBlockItem(regObject.get(), itemProps));
        return regObject;
    }

    private static RegistryObject<Block> registerDyeable(String name, Supplier<Block> block, Item.Properties itemProps, boolean allVariants) {
        RegistryObject<Block> regObject = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new DyeableBlockItem(regObject.get(), itemProps, allVariants));
        return regObject;
    }

    private static RegistryObject<Block> registerHalfTimber(String name, Supplier<Block> block) {
        RegistryObject<Block> regObject = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new HalfTimberBlockItem(regObject.get(), woodenItemProps));
        return regObject;
    }

    private static RegistryObject<Block> registerDyeableWooden(String name, Supplier<Block> block) {
        // Use decor tab by default
        return registerDyeableWooden(name, block, decorItemProps);
    }

    private static RegistryObject<Block> registerDyeableWooden(String name, Supplier<Block> block, Item.Properties itemProps) {
        RegistryObject<Block> regObject = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new DyeableWoodenBlockItem(regObject.get(), itemProps));
        return regObject;
    }

    private static RegistryObject<Block> registerItemless(String name, Supplier<Block> block) {
        return BLOCKS.register(name, block);
    }

    public static ToIntFunction<BlockState> candleHolderEmission(int lightlevel) {
        return (state) -> {
            return state.getValue(BlockStateProperties.AGE_3) > 0 ? lightlevel : 0;
        };
    }

    public static ToIntFunction<BlockState> customBlockEmission() {
        return (state) -> {
            return state.getValue(BlockStateProperties.LEVEL);
        };
    }

    private static boolean never(BlockState state, BlockGetter world, BlockPos pos){
        return false;
    }
}
