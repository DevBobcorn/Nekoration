package com.devbobcorn.nekoration.blocks;

import java.util.function.ToIntFunction;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.frames.DyeableWindowFrameBlock;
import com.devbobcorn.nekoration.blocks.frames.DyeableWindowSillBlock;
import com.devbobcorn.nekoration.blocks.frames.DyeableWindowTopBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Nekoration.MODID);

    public static final RegistryObject<Block> STONE_LAYERED = BLOCKS.register("stone_layered", () -> new StoneBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> STONE_BASE_BOTTOM = BLOCKS.register("stone_base_bottom", () -> new StoneBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> STONE_FRAME_BOTTOM = BLOCKS.register("stone_frame_bottom", () -> new StoneBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> STONE_PILLAR_BOTTOM = BLOCKS.register("stone_pillar_bottom", () -> new StoneBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> STONE_BASE = BLOCKS.register("stone_base", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> STONE_FRAME = BLOCKS.register("stone_frame", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> STONE_PILLAR = BLOCKS.register("stone_pillar", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> STONE_DORIC = BLOCKS.register("stone_doric", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> STONE_IONIC = BLOCKS.register("stone_ionic", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> STONE_CORINTHIAN = BLOCKS.register("stone_corinthian", () -> new StonePillarBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false));

    public static final RegistryObject<Block> WINDOW_TOP = BLOCKS.register("window_top", () -> new DyeableWindowTopBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F).noOcclusion(), DyeableHorizontalConnectBlock.ConnectionType.TRIPLE, false));
    public static final RegistryObject<Block> WINDOW_SILL = BLOCKS.register("window_sill", () -> new DyeableWindowSillBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F).noOcclusion(), DyeableHorizontalConnectBlock.ConnectionType.BEAM, false));
    public static final RegistryObject<Block> WINDOW_PLANT = BLOCKS.register("window_plant", () -> new DyeableHorizontalConnectBlock(Block.Properties.of(Material.LEAVES).strength(0.0F).noOcclusion(), DyeableHorizontalConnectBlock.ConnectionType.BEAM, false));

    public static final RegistryObject<Block> WINDOW_FRAME = BLOCKS.register("window_frame", () -> new DyeableWindowFrameBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F).noOcclusion()));

    public static final RegistryObject<Block> STONE_POT = BLOCKS.register("stone_pot", () -> new PotBlock(Block.Properties.of(Material.STONE).strength(1.5F, 6.0F).noOcclusion(), 6.0D));

    public static final RegistryObject<Block> HALF_TIMBER_P0 = BLOCKS.register("half_timber_p0", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P1 = BLOCKS.register("half_timber_p1", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P2 = BLOCKS.register("half_timber_p2", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P3 = BLOCKS.register("half_timber_p3", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P4 = BLOCKS.register("half_timber_p4", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P5 = BLOCKS.register("half_timber_p5", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P6 = BLOCKS.register("half_timber_p6", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P7 = BLOCKS.register("half_timber_p7", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P8 = BLOCKS.register("half_timber_p8", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<Block> HALF_TIMBER_P9 = BLOCKS.register("half_timber_p9", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));

	public static final RegistryObject<Block> HALF_TIMBER_PILLAR_P0 = BLOCKS.register("half_timber_pillar_p0", () -> new HalfTimberPillarBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F), HalfTimberPillarBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> HALF_TIMBER_PILLAR_P1 = BLOCKS.register("half_timber_pillar_p1", () -> new HalfTimberPillarBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F), HalfTimberPillarBlock.ConnectionType.TRIPLE, false));
    public static final RegistryObject<Block> HALF_TIMBER_PILLAR_P2 = BLOCKS.register("half_timber_pillar_p2", () -> new HalfTimberPillarBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F), HalfTimberPillarBlock.ConnectionType.TRIPLE, false));
    
    public static final RegistryObject<Block> WINDOW_SIMPLE = BLOCKS.register("window_simple", () -> new WindowBlock(Block.Properties.of(Material.GLASS).noOcclusion().isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> WINDOW_ARCH = BLOCKS.register("window_arch", () -> new WindowBlock(Block.Properties.of(Material.GLASS).noOcclusion().isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> WINDOW_CROSS = BLOCKS.register("window_cross", () -> new WindowBlock(Block.Properties.of(Material.GLASS).noOcclusion().isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> WINDOW_SHADE = BLOCKS.register("window_shade", () -> new WindowBlock(Block.Properties.of(Material.GLASS).noOcclusion().isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> WINDOW_LANCET = BLOCKS.register("window_lancet", () -> new WindowBlock(Block.Properties.of(Material.GLASS).noOcclusion().isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never), DyeableVerticalConnectBlock.ConnectionType.PILLAR, false));

    public static final RegistryObject<Block> DOOR_1 = BLOCKS.register("door_1", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> DOOR_2 = BLOCKS.register("door_2", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> DOOR_3 = BLOCKS.register("door_3", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));

    public static final RegistryObject<Block> DOOR_TALL_1 = BLOCKS.register("door_tall_1", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), true));
    public static final RegistryObject<Block> DOOR_TALL_2 = BLOCKS.register("door_tall_2", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), true));
    public static final RegistryObject<Block> DOOR_TALL_3 = BLOCKS.register("door_tall_3", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), true));

    public static final RegistryObject<Block> AWNING_PURE = BLOCKS.register("awning_pure", () -> new AwningBlock(Block.Properties.of(Material.WOOL).strength(0.8F)));
    public static final RegistryObject<Block> AWNING_STRIPE = BLOCKS.register("awning_stripe", () -> new AwningBlock(Block.Properties.of(Material.WOOL).strength(0.8F)));
    public static final RegistryObject<Block> AWNING_PURE_SHORT = BLOCKS.register("awning_pure_short", () -> new ShortAwningBlock(Block.Properties.of(Material.WOOL).strength(0.8F)));
    public static final RegistryObject<Block> AWNING_STRIPE_SHORT = BLOCKS.register("awning_stripe_short", () -> new ShortAwningBlock(Block.Properties.of(Material.WOOL).strength(0.8F)));

    public static final RegistryObject<Block> LAMP_POST_IRON = BLOCKS.register("lamp_post_iron", () -> new LampPostBlock(Block.Properties.of(Material.METAL).strength(5.0F, 6.0F)));
    public static final RegistryObject<Block> LAMP_POST_GOLD = BLOCKS.register("lamp_post_gold", () -> new LampPostBlock(Block.Properties.of(Material.METAL).strength(3.0F, 6.0F)));
    public static final RegistryObject<Block> LAMP_POST_QUARTZ = BLOCKS.register("lamp_post_quartz", () -> new LampPostBlock(Block.Properties.of(Material.STONE).strength(0.8F)));

    public static final RegistryObject<Block> CANDLE_HOLDER_IRON = BLOCKS.register("candle_holder_iron", () -> new CandleHolderBlock(Block.Properties.of(Material.METAL).strength(5.0F, 6.0F).lightLevel(candleHolderEmission(15)).noOcclusion()));
    public static final RegistryObject<Block> CANDLE_HOLDER_GOLD = BLOCKS.register("candle_holder_gold", () -> new CandleHolderBlock(Block.Properties.of(Material.METAL).strength(3.0F, 6.0F).lightLevel(candleHolderEmission(15)).noOcclusion()));
    public static final RegistryObject<Block> CANDLE_HOLDER_QUARTZ = BLOCKS.register("candle_holder_quartz", () -> new CandleHolderBlock(Block.Properties.of(Material.STONE).strength(0.8F).lightLevel(candleHolderEmission(15)).noOcclusion()));

    public static final RegistryObject<Block> FLOWER_BASKET_IRON = BLOCKS.register("flower_basket_iron", () -> new BasketBlock(Block.Properties.of(Material.METAL).strength(0.0F).noOcclusion(), 6.0D));
    public static final RegistryObject<Block> FLOWER_BASKET_GOLD = BLOCKS.register("flower_basket_gold", () -> new BasketBlock(Block.Properties.of(Material.METAL).strength(0.0F).noOcclusion(), 6.0D));
    public static final RegistryObject<Block> FLOWER_BASKET_QUARTZ = BLOCKS.register("flower_basket_quartz", () -> new BasketBlock(Block.Properties.of(Material.STONE).strength(0.0F).noOcclusion(), 6.0D));

    public static final RegistryObject<Block> EASEL_MENU = BLOCKS.register("easel_menu", () -> new EaselMenuBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), false));
    public static final RegistryObject<Block> EASEL_MENU_WHITE = BLOCKS.register("easel_menu_white", () -> new EaselMenuBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), true));

    public static final RegistryObject<Block> PHONOGRAPH = BLOCKS.register("phonograph", () -> new PhonographBlock(Block.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> CUSTOM = BLOCKS.register("custom", () -> new CustomBlock(Block.Properties.of(Material.WOOD).strength(1.5F, 6.0F).noOcclusion()));
    public static final RegistryObject<Block> PRISMAP_TABLE = BLOCKS.register("prismap_table", () -> new PrismapTableBlock(Block.Properties.of(Material.METAL).strength(1.5F, 6.0F).noOcclusion()));

    public static final RegistryObject<Block> PUMPKIN_TABLE = BLOCKS.register("pumpkin_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> PUMPKIN_CHAIR = BLOCKS.register("pumpkin_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 8, 24));
    
    public static final RegistryObject<Block> OAK_TABLE = BLOCKS.register("oak_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> OAK_ROUND_TABLE = BLOCKS.register("oak_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> OAK_CHAIR = BLOCKS.register("oak_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 9, 24));
    public static final RegistryObject<Block> JUNGLE_TABLE = BLOCKS.register("jungle_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> JUNGLE_ROUND_TABLE = BLOCKS.register("jungle_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> JUNGLE_CHAIR = BLOCKS.register("jungle_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 10, 26));
    public static final RegistryObject<Block> ACACIA_TABLE = BLOCKS.register("acacia_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> ACACIA_ROUND_TABLE = BLOCKS.register("acacia_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> ACACIA_CHAIR = BLOCKS.register("acacia_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 10, 25));
    public static final RegistryObject<Block> BIRCH_TABLE = BLOCKS.register("birch_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> BIRCH_ROUND_TABLE = BLOCKS.register("birch_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> BIRCH_CHAIR = BLOCKS.register("birch_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 10, 26));
    public static final RegistryObject<Block> DARK_OAK_TABLE = BLOCKS.register("dark_oak_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> DARK_OAK_ROUND_TABLE = BLOCKS.register("dark_oak_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> DARK_OAK_CHAIR = BLOCKS.register("dark_oak_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 9, 24));
    public static final RegistryObject<Block> SPRUCE_TABLE = BLOCKS.register("spruce_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> SPRUCE_ROUND_TABLE = BLOCKS.register("spruce_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> SPRUCE_CHAIR = BLOCKS.register("spruce_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 10, 26));
    public static final RegistryObject<Block> CRIMSON_TABLE = BLOCKS.register("crimson_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> CRIMSON_ROUND_TABLE = BLOCKS.register("crimson_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> CRIMSON_CHAIR = BLOCKS.register("crimson_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 10, 26));
    public static final RegistryObject<Block> WARPED_TABLE = BLOCKS.register("warped_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> WARPED_ROUND_TABLE = BLOCKS.register("warped_round_table", () -> new TableBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> WARPED_CHAIR = BLOCKS.register("warped_chair", () -> new ChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 10, 26));

    public static final RegistryObject<Block> GLASS_TABLE = BLOCKS.register("glass_table", () -> new DyeableWoodenBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> GLASS_ROUND_TABLE = BLOCKS.register("glass_round_table", () -> new DyeableWoodenBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
    public static final RegistryObject<Block> ARM_CHAIR = BLOCKS.register("arm_chair", () -> new DyeableChairBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), 8, 24));

    public static final RegistryObject<Block> DRAWER = BLOCKS.register("drawer", () -> new CabinetBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), false));
    public static final RegistryObject<Block> CABINET = BLOCKS.register("cabinet", () -> new CabinetBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), true));
    public static final RegistryObject<Block> DRAWER_CHEST = BLOCKS.register("drawer_chest", () -> new CabinetBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion(), true));

	public static ToIntFunction<BlockState> candleHolderEmission(int lightlevel) {
		return (state) -> {
			return state.getValue(BlockStateProperties.AGE_3) > 0 ? lightlevel : 0;
		};
	}

    private static boolean never(BlockState state, BlockGetter world, BlockPos pos){
        return false;
    }
}
