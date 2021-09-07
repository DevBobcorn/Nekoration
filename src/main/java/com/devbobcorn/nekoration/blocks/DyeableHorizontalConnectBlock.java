package com.devbobcorn.nekoration.blocks;

import java.util.Map;

import com.devbobcorn.nekoration.NekoConfig;
import com.devbobcorn.nekoration.NekoConfig.HorConnectionDir;
import com.devbobcorn.nekoration.blocks.states.HorizontalConnection;
import com.devbobcorn.nekoration.blocks.states.ModStateProperties;
import com.devbobcorn.nekoration.common.VanillaCompat;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

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

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
		s.add(COLOR, FACING, CONNECTION);
	}

	public final ConnectionType type;
	public final boolean connectOtherVariant;

	public DyeableHorizontalConnectBlock(Properties settings) {
		super(settings);
		type = ConnectionType.TRIPLE;
		connectOtherVariant = false;
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
	}

	public DyeableHorizontalConnectBlock(Properties settings, ConnectionType tp, boolean co) {
		super(settings);
		type = tp;
		connectOtherVariant = co;
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
	}

	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return AABBS.get(state.getValue(FACING));
	}

	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);

		if (world.isClientSide) {
			return (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) ? InteractionResult.SUCCESS
					: InteractionResult.PASS;
		}
		
		if (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) {
			world.setBlock(pos, state.setValue(COLOR, VanillaCompat.COLOR_ITEMS.get(itemStack.getItem())), 3);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}

	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Level blockView = ctx.getLevel();
		BlockPos blockPos = ctx.getClickedPos();
		HorConnectionDir config = NekoConfig.SERVER.horConnectionDir.get();
		boolean useLeft = config == HorConnectionDir.LEFT2RIGHT || config == HorConnectionDir.BOTH;
		
		if (config != HorConnectionDir.NEITHER){
			BlockPos blockPosRef = (useLeft) ?
				getLeftBlock(blockPos, ctx.getHorizontalDirection().getOpposite()) :
				getRightBlock(blockPos, ctx.getHorizontalDirection().getOpposite());
			BlockState stateRef = blockView.getBlockState(blockPosRef);

			boolean connect = stateRef.getBlock() instanceof DyeableHorizontalConnectBlock && (connectOtherVariant || stateRef.getBlock() == this);

			if (!connect && config == HorConnectionDir.BOTH){ // Block on the left refuses to connect, try the right one
				blockPosRef = getRightBlock(blockPos, ctx.getHorizontalDirection().getOpposite());
				stateRef = blockView.getBlockState(blockPosRef);
				connect = stateRef.getBlock() instanceof DyeableHorizontalConnectBlock && (connectOtherVariant || stateRef.getBlock() == this);
				useLeft = false;
			}
			if (connect) {
				if (useLeft){
					switch (stateRef.getValue(CONNECTION)) {
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
				} else {
					switch (stateRef.getValue(CONNECTION)) {
						case S0:
							return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.D0);
						case D1:
							return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.D0);
						case T2:
							return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.D0);
						case D0:
							return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, type == ConnectionType.DOUBLE ? HorizontalConnection.S0 : HorizontalConnection.T0);
						case T1:
							return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.T0);
						case T0:
							return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, type == ConnectionType.BEAM ? HorizontalConnection.T0 : HorizontalConnection.S0);
						default:
							return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.T0);
					}
				}
			}
		}
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.S0);
	}

	public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
		BlockState res = state;
		HorConnectionDir config = NekoConfig.SERVER.horConnectionDir.get();
		boolean flag1 = direction == getRightDir(state.getValue(FACING)) && (config == HorConnectionDir.LEFT2RIGHT ||  config == HorConnectionDir.BOTH);
		boolean flag2 = direction == getLeftDir(state.getValue(FACING)) && (config == HorConnectionDir.RIGHT2LEFT ||  config == HorConnectionDir.BOTH);

		boolean connect = flag1 || flag2;
		if (connect && newState.getBlock() instanceof DyeableHorizontalConnectBlock && (connectOtherVariant || newState.getBlock() == this)) {
			BlockState stateRef;
			if (flag1){ // Block on the right is ...
				stateRef = world.getBlockState(getLeftBlock(pos, state.getValue(FACING))); // Take the block on the left as an extra reference
				switch (newState.getValue(CONNECTION)) {
					case D1:
						return res.setValue(CONNECTION, HorizontalConnection.D0);
					case T1:
						return res.setValue(CONNECTION, (type == ConnectionType.BEAM && stateRef.getBlock() instanceof DyeableHorizontalConnectBlock && (connectOtherVariant || stateRef.getBlock() == this)) ? HorizontalConnection.T1 : HorizontalConnection.T0);
					case T2:
						return res.setValue(CONNECTION, HorizontalConnection.T1);
					default:
						break;
				}
			} else { // Block on the left is ...
				stateRef = world.getBlockState(getRightBlock(pos, state.getValue(FACING))); // Take the block on the right as an extra reference
				switch (newState.getValue(CONNECTION)) {
					case D0:
						return res.setValue(CONNECTION, HorizontalConnection.D1);
					case T1:
						return res.setValue(CONNECTION, (type == ConnectionType.BEAM && stateRef.getBlock() instanceof DyeableHorizontalConnectBlock && (connectOtherVariant || stateRef.getBlock() == this)) ? HorizontalConnection.T1 : HorizontalConnection.T2);
					case T0:
						return res.setValue(CONNECTION, HorizontalConnection.T1);
					default:
						break;
				}
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