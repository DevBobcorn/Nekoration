package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.blocks.states.HorizontalConnection;
import com.devbobcorn.nekoration.blocks.states.ModStateProperties;
import com.devbobcorn.nekoration.common.VanillaCompat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
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
import net.minecraft.world.IWorld;

public class DyeableHorizontalConnectBlock extends DyeableHorizontalBlock {
	protected static Double thickness = 6.0D;

	private static final Map<Direction, VoxelShape> AABBS = Maps
			.newEnumMap(ImmutableMap.of(
					Direction.NORTH, Block.box(0.0D, 0.0D, 16.0D - thickness, 16.0D, 16.0D, 16.0D),
					Direction.SOUTH, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, thickness), 
					Direction.EAST, Block.box(0.0D, 0.0D, 0.0D, thickness, 16.0D, 16.0D),
					Direction.WEST, Block.box(16.0D - thickness, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)));

	public static final IntegerProperty COLOR = BlockStateProperties.LEVEL;

	public enum ConnectionType {
		DOUBLE, TRIPLE, BEAM;
	}

	public static final EnumProperty<HorizontalConnection> CONNECTION  = ModStateProperties.HONRIZONTAL_CONNECTION;

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(COLOR, FACING, CONNECTION);
	  }

	public final ConnectionType type;
	public final boolean connectOthers;

	public DyeableHorizontalConnectBlock(Properties settings) {
		super(settings);
		type = ConnectionType.TRIPLE;
		connectOthers = false;
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
	}

	public DyeableHorizontalConnectBlock(Properties settings, ConnectionType tp, boolean co) {
		super(settings);
		type = ConnectionType.TRIPLE;
		connectOthers = false;
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
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

	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		World blockView = ctx.getLevel();
		BlockPos blockPos = ctx.getClickedPos();
		BlockPos blockPosL = getLeftBlock(blockPos, ctx.getHorizontalDirection().getOpposite());
		BlockState stateL = blockView.getBlockState(blockPosL);
		
		//System.out.println("BlockPlaced!");
		if (stateL.getBlock() instanceof DyeableHorizontalConnectBlock && (connectOthers || stateL.getBlock() == this)) {
			//System.out.println(stateL.getValue(CONNECTION));
			switch (stateL.getValue(CONNECTION)) {
			case S0:
				return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.D1);
			case D0:
				return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.D1);
			case T0:
				return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.D1);
			case D1:
				return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, type == ConnectionType.DOUBLE ? HorizontalConnection.S0 : HorizontalConnection.T2);
			case T1:
				return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.T2);
			case T2:
				return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, type == ConnectionType.BEAM ? HorizontalConnection.T2 : HorizontalConnection.S0);
			default:
				return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.T2);
			}
		}
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.S0);
	}

	public BlockState updateShape(BlockState state, Direction direction, BlockState newState,
			IWorld world, BlockPos pos, BlockPos posFrom) {
		BlockState res = state;
		
		if (direction == getRightDir(state.getValue(FACING)) && newState.getBlock() instanceof DyeableHorizontalConnectBlock && (connectOthers || newState.getBlock() == this)) {
			BlockState stateL = world.getBlockState(getLeftBlock(pos, state.getValue(FACING)));
			switch (newState.getValue(CONNECTION)) {
			case D1:
				return res.setValue(CONNECTION, HorizontalConnection.D0);
			case T1:
				return res.setValue(CONNECTION, (type == ConnectionType.BEAM && stateL.getBlock() instanceof DyeableHorizontalConnectBlock && (connectOthers || stateL.getBlock() == this)) ? HorizontalConnection.T1 : HorizontalConnection.T0);
			case T2:
				return res.setValue(CONNECTION, HorizontalConnection.T1);
			default:
				break;
			}
		}
		return res;
	}

	public BlockPos getLeftBlock(BlockPos pos, Direction dir) {
		switch (dir) {
		case NORTH:
			return pos.east();
		case EAST:
			return pos.south();
		case SOUTH:
			return pos.west();
		default:
			return pos.north();
		}
	}
	
	public BlockPos getRightBlock(BlockPos pos, Direction dir) {
		switch (dir) {
		case NORTH:
			return pos.west();
		case EAST:
			return pos.north();
		case SOUTH:
			return pos.east();
		default:
			return pos.south();
		}
	}

	public Direction getLeftDir(Direction selfDir) {
		switch (selfDir) {
		case NORTH:
			return Direction.EAST;
		case EAST:
			return Direction.SOUTH;
		case SOUTH:
			return Direction.WEST;
		default:
			return Direction.NORTH;
		}
	}
	
	public Direction getRightDir(Direction selfDir) {
		switch (selfDir) {
		case NORTH:
			return Direction.WEST;
		case EAST:
			return Direction.NORTH;
		case SOUTH:
			return Direction.EAST;
		default:
			return Direction.SOUTH;
		}
	}
}