package com.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import com.devbobcorn.nekoration.blocks.states.LampPostType;
import com.devbobcorn.nekoration.blocks.states.ModStateProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.LanternBlock;

public class LampPostBlock extends CrossCollisionBlock {
    private final VoxelShape[] occlusionByIndex;

    public static final EnumProperty<LampPostType> TYPE = ModStateProperties.LAMP_POST_TYPE;

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

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
        s.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED, TYPE);
    }

    public boolean canConnect(BlockState state, boolean neighborIsFullSquare, Direction dir) {
        boolean bl2 = state.getBlock() instanceof LampPostBlock;
        return !isExceptionForConnection(state) && neighborIsFullSquare || bl2;
    }

    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.occlusionByIndex[this.getAABBIndex(state)];
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return this.getShape(state, world, pos, ctx);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        if ((Boolean) state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
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

    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }

    public LampPostType getType(Level blockView, BlockPos blockPos) {
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

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (world.isClientSide) {
           ItemStack itemstack = player.getItemInHand(hand);
           return itemstack.is(Items.LEAD) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        } else {
           return LeadItem.bindPlayerMobs(player, world, pos);
        }
    }

    @SuppressWarnings("null")
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level blockView = ctx.getLevel();
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
        return state.getBlock() instanceof LanternBlock || state.getBlock() instanceof BasketBlock;
    }

    protected boolean isValidDownBlock(BlockState state) {
        return state.getBlock() instanceof ChainBlock || state.getBlock() instanceof LanternBlock || state.getBlock() instanceof BasketBlock;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return Collections.singletonList(new ItemStack(this.asItem()));
    }
}
