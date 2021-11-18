package com.devbobcorn.nekoration.blocks;

import java.util.Map;

import com.devbobcorn.nekoration.NekoConfig;
import com.devbobcorn.nekoration.NekoConfig.HorConnectionDir;
import com.devbobcorn.nekoration.blocks.entities.ItemDisplayBlockEntity;
import com.devbobcorn.nekoration.blocks.states.HorizontalConnection;
import com.devbobcorn.nekoration.blocks.states.ModStateProperties;
import com.devbobcorn.nekoration.common.VanillaCompat;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallShelfBlock extends ItemDisplayBlock {
	private static final Map<Direction, VoxelShape> AABBs = getAABBs(6.0D);
	
	public static final EnumProperty<HorizontalConnection> CONNECTION  = ModStateProperties.HONRIZONTAL_CONNECTION;

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
		s.add(COLOR, FACING, CONNECTION, OPEN);
	}

	public WallShelfBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ItemDisplayBlockEntity(pos, state, true, false);
	}

	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return AABBs.get(state.getValue(FACING));
	}

	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);

		if (world.isClientSide) {
			return (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) ? InteractionResult.SUCCESS : super.use(state, world, pos, player, hand, hit);
		}
		
		if (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) {
			world.setBlock(pos, state.setValue(COLOR, VanillaCompat.COLOR_ITEMS.get(itemStack.getItem())), 3);
			return InteractionResult.CONSUME;
		}
		return super.use(state, world, pos, player, hand, hit);
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

			boolean connect = stateRef.getBlock() instanceof WallShelfBlock && stateRef.getBlock() == this;

			if (!connect && config == HorConnectionDir.BOTH){ // Block on the left refuses to connect, try the right one
				blockPosRef = getRightBlock(blockPos, ctx.getHorizontalDirection().getOpposite());
				stateRef = blockView.getBlockState(blockPosRef);
				connect = stateRef.getBlock() instanceof WallShelfBlock && stateRef.getBlock() == this;
				useLeft = false;
			}
			if (connect) {
				if (useLeft){
					switch (stateRef.getValue(CONNECTION)) {
						case S0:
						case D0:
						case T0:
							return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.D1);
						case D1:
						case T1:
						case T2:
						default:
							return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.T2);
						}
				} else {
					switch (stateRef.getValue(CONNECTION)) {
						case S0:
						case D1:
						case T2:
							return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.D0);
						case D0:
						case T1:
						case T0:
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
		if (connect && newState.getBlock() instanceof WallShelfBlock) {
			BlockState stateRef;
			if (flag1){ // Block on the right is ...
				stateRef = world.getBlockState(getLeftBlock(pos, state.getValue(FACING))); // Take the block on the left as an extra reference
				switch (newState.getValue(CONNECTION)) {
					case D1:
						return res.setValue(CONNECTION, HorizontalConnection.D0);
					case T1:
						return res.setValue(CONNECTION, (stateRef.getBlock() instanceof WallShelfBlock) ? HorizontalConnection.T1 : HorizontalConnection.T0);
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
						return res.setValue(CONNECTION, stateRef.getBlock() instanceof WallShelfBlock ? HorizontalConnection.T1 : HorizontalConnection.T2);
					case T0:
						return res.setValue(CONNECTION, HorizontalConnection.T1);
					default:
						break;
				}
			}
		}
		return res;
	}
}