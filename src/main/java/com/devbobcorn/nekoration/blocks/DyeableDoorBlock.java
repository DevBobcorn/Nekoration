package com.devbobcorn.nekoration.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.List;

import com.devbobcorn.nekoration.common.VanillaCompat;

public class DyeableDoorBlock extends DoorBlock {
	protected static final VoxelShape TALL_SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 32.0D, 3.0D);
	protected static final VoxelShape TALL_NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 32.0D, 16.0D);
	protected static final VoxelShape TALL_WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 32.0D, 16.0D);
	protected static final VoxelShape TALL_EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 32.0D, 16.0D);

	public static final IntegerProperty COLOR = BlockStateProperties.LEVEL;
	public final boolean isTall;

	public DyeableDoorBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
		isTall = false;
	}

	public DyeableDoorBlock(Properties settings, boolean tall) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
		isTall = tall;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
		s.add(HALF, FACING, OPEN, HINGE, POWERED, COLOR);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		Direction direction = state.getValue(FACING);
		boolean flag = !state.getValue(OPEN);
		boolean flag1 = state.getValue(HINGE) == DoorHingeSide.RIGHT;
		if (isTall && state.getValue(HALF) == DoubleBlockHalf.UPPER)
			switch(direction) {
				case EAST:
				default:
					return flag ? TALL_EAST_AABB : (flag1 ? TALL_NORTH_AABB : TALL_SOUTH_AABB);
				case SOUTH:
					return flag ? TALL_SOUTH_AABB : (flag1 ? TALL_EAST_AABB : TALL_WEST_AABB);
				case WEST:
					return flag ? TALL_WEST_AABB : (flag1 ? TALL_SOUTH_AABB : TALL_NORTH_AABB);
				case NORTH:
					return flag ? TALL_NORTH_AABB : (flag1 ? TALL_WEST_AABB : TALL_EAST_AABB);
			}
		else 
			switch(direction) {
			case EAST:
			default:
				return flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
			case SOUTH:
				return flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
			case WEST:
				return flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
			case NORTH:
				return flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
		}
	 }

	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (world.isClientSide) {
			return (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) ? InteractionResult.SUCCESS
					: super.use(state, world, pos, player, hand, hit);
		}
		
		if (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) {
			world.setBlock(pos, state.setValue(COLOR, VanillaCompat.COLOR_ITEMS.get(itemStack.getItem())), 3);
			return InteractionResult.CONSUME;
		}
		return super.use(state, world, pos, player, hand, hit);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		// Drop only when the lower part's broken, or we'll get 2 dropped items...
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? Collections.singletonList(new ItemStack(this.asItem())) : Collections.emptyList();
	}
}