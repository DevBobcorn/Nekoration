package com.devbobcorn.nekoration.blocks;

import java.util.function.ToIntFunction;

import com.devbobcorn.nekoration.Nekoration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Nekoration.MODID);

    public static final RegistryObject<Block> STONE_BASE_BOTTOM = BLOCKS.register("stone_base_bottom", () -> new DyeableBlock(Block.Properties.of(Material.STONE)));
    public static final RegistryObject<Block> STONE_FRAME_BOTTOM = BLOCKS.register("stone_frame_bottom", () -> new DyeableBlock(Block.Properties.of(Material.STONE)));
    public static final RegistryObject<Block> STONE_PILLAR_BOTTOM = BLOCKS.register("stone_pillar_bottom", () -> new DyeableBlock(Block.Properties.of(Material.STONE)));
    public static final RegistryObject<Block> STONE_BASE = BLOCKS.register("stone_base", () -> new DyeableVerticalConnectBlock(Block.Properties.of(Material.STONE), VerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> STONE_FRAME = BLOCKS.register("stone_frame", () -> new DyeableVerticalConnectBlock(Block.Properties.of(Material.STONE), VerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> STONE_PILLAR = BLOCKS.register("stone_pillar", () -> new DyeableVerticalConnectBlock(Block.Properties.of(Material.STONE), VerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> STONE_DORIC = BLOCKS.register("stone_doric", () -> new DyeableVerticalConnectBlock(Block.Properties.of(Material.STONE), VerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> STONE_IONIC = BLOCKS.register("stone_ionic", () -> new DyeableVerticalConnectBlock(Block.Properties.of(Material.STONE), VerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> STONE_CORINTHIAN = BLOCKS.register("stone_corinthian", () -> new DyeableVerticalConnectBlock(Block.Properties.of(Material.STONE), VerticalConnectBlock.ConnectionType.PILLAR, false));

    public static final RegistryObject<Block> WINDOW_TOP = BLOCKS.register("window_top", () -> new DyeableHorizontalConnectBlock(Block.Properties.of(Material.STONE).noOcclusion(), HorizontalConnectBlock.ConnectionType.TRIPLE, false));
    public static final RegistryObject<Block> WINDOW_SILL = BLOCKS.register("window_sill", () -> new DyeableHorizontalConnectBlock(Block.Properties.of(Material.STONE).noOcclusion(), HorizontalConnectBlock.ConnectionType.BEAM, false));

    public static final RegistryObject<Block> STONE_POT = BLOCKS.register("stone_pot", () -> new PotBlock(Block.Properties.of(Material.STONE).noOcclusion()));

    public static final RegistryObject<Block> HALF_TIMBER_P0 = BLOCKS.register("half_timber_p0", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD)));
    public static final RegistryObject<Block> HALF_TIMBER_P1 = BLOCKS.register("half_timber_p1", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD)));
    public static final RegistryObject<Block> HALF_TIMBER_P2 = BLOCKS.register("half_timber_p2", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD)));
    public static final RegistryObject<Block> HALF_TIMBER_P3 = BLOCKS.register("half_timber_p3", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD)));
    public static final RegistryObject<Block> HALF_TIMBER_P4 = BLOCKS.register("half_timber_p4", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD)));
    public static final RegistryObject<Block> HALF_TIMBER_P5 = BLOCKS.register("half_timber_p5", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD)));
    public static final RegistryObject<Block> HALF_TIMBER_P6 = BLOCKS.register("half_timber_p6", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD)));
    public static final RegistryObject<Block> HALF_TIMBER_P7 = BLOCKS.register("half_timber_p7", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD)));
    public static final RegistryObject<Block> HALF_TIMBER_P8 = BLOCKS.register("half_timber_p8", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD)));
    public static final RegistryObject<Block> HALF_TIMBER_P9 = BLOCKS.register("half_timber_p9", () -> new HalfTimberBlock(Block.Properties.of(Material.WOOD)));

	public static final RegistryObject<Block> HALF_TIMBER_PILLAR_P0 = BLOCKS.register("half_timber_pillar_p0", () -> new HalfTimberPillarBlock(Block.Properties.of(Material.WOOD), VerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> HALF_TIMBER_PILLAR_P1 = BLOCKS.register("half_timber_pillar_p1", () -> new HalfTimberPillarBlock(Block.Properties.of(Material.WOOD), VerticalConnectBlock.ConnectionType.TRIPLE, false));
    public static final RegistryObject<Block> HALF_TIMBER_PILLAR_P2 = BLOCKS.register("half_timber_pillar_p2", () -> new HalfTimberPillarBlock(Block.Properties.of(Material.WOOD), VerticalConnectBlock.ConnectionType.TRIPLE, false));

    public static final RegistryObject<Block> WINDOW_ARCH = BLOCKS.register("window_arch", () -> new WindowBlock(Block.Properties.of(Material.GLASS).noOcclusion(), VerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> WINDOW_CROSS = BLOCKS.register("window_cross", () -> new WindowBlock(Block.Properties.of(Material.GLASS).noOcclusion(), VerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> WINDOW_SHADE = BLOCKS.register("window_shade", () -> new WindowBlock(Block.Properties.of(Material.GLASS).noOcclusion(), VerticalConnectBlock.ConnectionType.PILLAR, false));
    public static final RegistryObject<Block> WINDOW_LANCET = BLOCKS.register("window_lancet", () -> new WindowBlock(Block.Properties.of(Material.GLASS).noOcclusion(), VerticalConnectBlock.ConnectionType.PILLAR, false));

    public static final RegistryObject<Block> DOOR_1 = BLOCKS.register("door_1", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).noOcclusion()));
    public static final RegistryObject<Block> DOOR_2 = BLOCKS.register("door_2", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).noOcclusion()));
    public static final RegistryObject<Block> DOOR_3 = BLOCKS.register("door_3", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).noOcclusion()));

    public static final RegistryObject<Block> DOOR_TALL_1 = BLOCKS.register("door_tall_1", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).noOcclusion()));
    public static final RegistryObject<Block> DOOR_TALL_2 = BLOCKS.register("door_tall_2", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).noOcclusion()));
    public static final RegistryObject<Block> DOOR_TALL_3 = BLOCKS.register("door_tall_3", () -> new DyeableDoorBlock(Block.Properties.of(Material.WOOD).noOcclusion()));

    public static final RegistryObject<Block> AWNING_PURE = BLOCKS.register("awning_pure", () -> new AwningBlock(Block.Properties.of(Material.WOOL)));
    public static final RegistryObject<Block> AWNING_STRIPE = BLOCKS.register("awning_stripe", () -> new AwningBlock(Block.Properties.of(Material.WOOL)));
    public static final RegistryObject<Block> AWNING_PURE_SHORT = BLOCKS.register("awning_pure_short", () -> new ShortAwningBlock(Block.Properties.of(Material.WOOL)));
    public static final RegistryObject<Block> AWNING_STRIPE_SHORT = BLOCKS.register("awning_stripe_short", () -> new ShortAwningBlock(Block.Properties.of(Material.WOOL)));

    public static final RegistryObject<Block> LAMP_POST_IRON = BLOCKS.register("lamp_post_iron", () -> new LampPostBlock(Block.Properties.of(Material.METAL)));
    public static final RegistryObject<Block> LAMP_POST_GOLD = BLOCKS.register("lamp_post_gold", () -> new LampPostBlock(Block.Properties.of(Material.METAL)));
    public static final RegistryObject<Block> LAMP_POST_QUARTZ = BLOCKS.register("lamp_post_quartz", () -> new LampPostBlock(Block.Properties.of(Material.STONE)));

    public static final RegistryObject<Block> CANDLE_HOLDER_IRON = BLOCKS.register("candle_holder_iron", () -> new CandleHolderBlock(Block.Properties.of(Material.METAL).lightLevel(candleHolderEmission(15)).noOcclusion()));
    public static final RegistryObject<Block> CANDLE_HOLDER_GOLD = BLOCKS.register("candle_holder_gold", () -> new CandleHolderBlock(Block.Properties.of(Material.METAL).lightLevel(candleHolderEmission(15)).noOcclusion()));
    public static final RegistryObject<Block> CANDLE_HOLDER_QUARTZ = BLOCKS.register("candle_holder_quartz", () -> new CandleHolderBlock(Block.Properties.of(Material.STONE).lightLevel(candleHolderEmission(15)).noOcclusion()));

    public static final RegistryObject<Block> EASEL_MENU = BLOCKS.register("easel_menu", () -> new EaselMenuBlock(Block.Properties.of(Material.WOOD).noOcclusion()));

    public static final RegistryObject<Block> PHONOGRAPH = BLOCKS.register("phonograph", () -> new PhonographBlock(Block.Properties.of(Material.METAL).noOcclusion()));

	public static ToIntFunction<BlockState> candleHolderEmission(int lightlevel) {
		return (state) -> {
			return state.getValue(BlockStateProperties.AGE_3) > 0 ? lightlevel : 0;
		};
	}
}
