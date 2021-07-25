package com.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import com.devbobcorn.nekoration.blocks.states.LampPostType;
import com.devbobcorn.nekoration.blocks.states.ModStateProperties;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.FourWayBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LeadItem;
import net.minecraft.loot.LootContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class LampPostBlock extends FourWayBlock {
	private final VoxelShape[] occlusionByIndex;

	public static final net.minecraft.state.EnumProperty<LampPostType> TYPE = ModStateProperties.LAMP_POST_TYPE;

	public static final VoxelShape SHAPE = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 12.0d, 12.0D);

	public LampPostBlock(Properties settings) {
		super(2.0F, 2.0F, 16.0F, 16.0F, 16.0F, settings);
		this.registerDefaultState(
				this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false)
						.setValue(WEST, false).setValue(WATERLOGGED, false).setValue(TYPE, LampPostType.BASE));
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.valueOf(false))
				.setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false))
				.setValue(WEST, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
		this.occlusionByIndex = this.makeShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
	}

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED, TYPE);
	}

	public boolean canConnect(BlockState state, boolean neighborIsFullSquare, Direction dir) {
		Block block = state.getBlock();
		boolean bl2 = block instanceof LampPostBlock;
		return !isExceptionForConnection(block) && neighborIsFullSquare || bl2;
	}

	public VoxelShape getOcclusionShape(BlockState state, IBlockReader world, BlockPos pos) {
		return this.occlusionByIndex[this.getAABBIndex(state)];
	}

	public VoxelShape getVisualShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		return this.getShape(state, world, pos, ctx);
	}

	public BlockState updateShape(BlockState state, Direction direction, BlockState newState, IWorld world,
			BlockPos pos, BlockPos posFrom) {
		if ((Boolean) state.getValue(WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		return direction.getAxis().isHorizontal()
				? (BlockState) state.setValue(PROPERTY_BY_DIRECTION.get(direction),
						this.canConnect(newState, newState.isFaceSturdy(world, posFrom, direction.getOpposite()),
								direction.getOpposite()))
				: state.setValue(TYPE,
						(state.getValue(TYPE) == LampPostType.TOP && direction == Direction.UP
								&& newState.getBlock() instanceof LampPostBlock)
										? LampPostType.POLE
										: (direction == Direction.UP && isValidUpBlock(newState)) ? LampPostType.SIDE_UP
												: (direction == Direction.DOWN && isValidDownBlock(newState))
														? LampPostType.SIDE_DOWN
														: state.getValue(TYPE));
	}

	public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type) {
		return false;
	}

	public LampPostType getType(World blockView, BlockPos blockPos) {
		BlockPos blockPosD = blockPos.below();
		BlockPos blockPosU = blockPos.above();
		BlockState stateD = blockView.getBlockState(blockPosD);
		BlockState stateU = blockView.getBlockState(blockPosU);
		if (stateD.isFaceSturdy(blockView, blockPosD, Direction.DOWN))
			return LampPostType.BASE;
		if (stateD.getBlock() instanceof LampPostBlock && stateD.getValue(TYPE) != LampPostType.SIDE_UP
				&& stateD.getValue(TYPE) != LampPostType.SIDE_DOWN && !(stateU.getBlock() instanceof LampPostBlock))
			return LampPostType.TOP;
		BlockPos blockPos1 = blockPos.north();
		BlockPos blockPos2 = blockPos.east();
		BlockPos blockPos3 = blockPos.south();
		BlockPos blockPos4 = blockPos.west();
		BlockState[] states = { blockView.getBlockState(blockPos1), blockView.getBlockState(blockPos2),
				blockView.getBlockState(blockPos3), blockView.getBlockState(blockPos4) };

		for (int i = 0; i < 4; i++) {
			if ((states[i].getBlock() instanceof LampPostBlock && states[i].getValue(TYPE) == LampPostType.TOP)
					|| states[i].isSolidRender(blockView, blockPos)) {
				return LampPostType.SIDE_DOWN;
			}
		}
		return LampPostType.POLE;
	}

	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult hit) {
		if (world.isClientSide()) {
			ItemStack itemStack = player.getItemInHand(hand);
			return itemStack.getItem() == Items.LEAD ? ActionResultType.SUCCESS : ActionResultType.PASS;
		} else {
			return LeadItem.bindPlayerMobs(player, world, pos);
		}
	}

	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		World blockView = ctx.getLevel();
		BlockPos blockPos = ctx.getClickedPos();
		FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
		BlockPos blockPos1 = blockPos.north();
		BlockPos blockPos2 = blockPos.east();
		BlockPos blockPos3 = blockPos.south();
		BlockPos blockPos4 = blockPos.west();
		BlockState blockState1 = blockView.getBlockState(blockPos1);
		BlockState blockState2 = blockView.getBlockState(blockPos2);
		BlockState blockState3 = blockView.getBlockState(blockPos3);
		BlockState blockState4 = blockView.getBlockState(blockPos4);
		return super.getStateForPlacement(ctx)
				.setValue(NORTH,
						this.canConnect(blockState1, blockState1.isFaceSturdy(blockView, blockPos1, Direction.SOUTH),
								Direction.SOUTH))
				.setValue(EAST,
						this.canConnect(blockState2, blockState2.isFaceSturdy(blockView, blockPos2, Direction.WEST),
								Direction.WEST))
				.setValue(SOUTH,
						this.canConnect(blockState3, blockState3.isFaceSturdy(blockView, blockPos3, Direction.NORTH),
								Direction.NORTH))
				.setValue(WEST,
						this.canConnect(blockState4, blockState4.isFaceSturdy(blockView, blockPos4, Direction.EAST),
								Direction.EAST))
				.setValue(TYPE, this.getType(blockView, blockPos))
				.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	protected boolean isValidUpBlock(BlockState state) {
		return state.getBlock() instanceof LanternBlock;
	}

	protected boolean isValidDownBlock(BlockState state) {
		// return state.getBlock() instanceof ChainBlock || state.getBlock() instanceof
		// LanternBlock || state.getBlock() instanceof LampBlock;
		return state.getBlock() instanceof ChainBlock || state.getBlock() instanceof LanternBlock;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		return Collections.singletonList(new ItemStack(this.asItem()));
	}
}
