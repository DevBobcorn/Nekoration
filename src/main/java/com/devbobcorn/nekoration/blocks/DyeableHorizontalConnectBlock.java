package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.common.VanillaCompat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;

public class DyeableHorizontalConnectBlock extends HorizontalConnectBlock {
	protected static Double thickness = 6.0D;

	private static final Map<Direction, VoxelShape> AABBS = Maps
			.newEnumMap(ImmutableMap.of(
					Direction.NORTH, Block.box(0.0D, 0.0D, 16.0D - thickness, 16.0D, 16.0D, 16.0D),
					Direction.SOUTH, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, thickness), 
					Direction.EAST, Block.box(0.0D, 0.0D, 0.0D, thickness, 16.0D, 16.0D),
					Direction.WEST, Block.box(16.0D - thickness, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)));

	public static final IntegerProperty COLOR = BlockStateProperties.LEVEL;

	public DyeableHorizontalConnectBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
	}

	public DyeableHorizontalConnectBlock(Properties settings, ConnectionType tp, boolean co) {
		super(settings, tp, co);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
	}

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(CONNECTION, COLOR, FACING);
	}

	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		return AABBS.get(state.getValue(FACING));
	}

	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);

		if (world.isClientSide) {
			return (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) ? ActionResultType.SUCCESS
					: ActionResultType.PASS;
		}
		
		if (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) {
			world.setBlock(pos, state.setValue(COLOR, VanillaCompat.COLOR_ITEMS.get(itemStack.getItem())), 3);
			return ActionResultType.CONSUME;
		}
		return ActionResultType.PASS;
	}
}