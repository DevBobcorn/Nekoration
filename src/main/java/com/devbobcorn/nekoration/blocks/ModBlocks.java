package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.Nekoration;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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

    public static final RegistryObject<Block> AWNING_PURE = BLOCKS.register("awning_pure", () -> new AwningBlock(Block.Properties.of(Material.WOOL)));
    public static final RegistryObject<Block> AWNING_STRIPE = BLOCKS.register("awning_stripe", () -> new AwningBlock(Block.Properties.of(Material.WOOL)));
    public static final RegistryObject<Block> AWNING_PURE_SHORT = BLOCKS.register("awning_pure_short", () -> new ShortAwningBlock(Block.Properties.of(Material.WOOL)));
    public static final RegistryObject<Block> AWNING_STRIPE_SHORT = BLOCKS.register("awning_stripe_short", () -> new ShortAwningBlock(Block.Properties.of(Material.WOOL)));

    public static final RegistryObject<Block> LAMP_POST_IRON = BLOCKS.register("lamp_post_iron", () -> new LampPostBlock(Block.Properties.of(Material.METAL)));
    public static final RegistryObject<Block> LAMP_POST_GOLD = BLOCKS.register("lamp_post_gold", () -> new LampPostBlock(Block.Properties.of(Material.METAL)));
    public static final RegistryObject<Block> LAMP_POST_QUARTZ = BLOCKS.register("lamp_post_quartz", () -> new LampPostBlock(Block.Properties.of(Material.STONE)));
}
