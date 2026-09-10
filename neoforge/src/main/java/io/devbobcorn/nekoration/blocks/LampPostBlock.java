package io.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import com.mojang.serialization.MapCodec;

import io.devbobcorn.nekoration.blocks.states.LampPostType;
import io.devbobcorn.nekoration.blocks.states.ModStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LampPostBlock extends CrossCollisionBlock {
    public static final MapCodec<LampPostBlock> CODEC = simpleCodec(LampPostBlock::new);
    public static final EnumProperty<LampPostType> TYPE = ModStateProperties.LAMP_POST_TYPE;
    private final VoxelShape[] occlusionByIndex;

    public LampPostBlock(Properties properties) {
        super(2, 2, 16, 16, 16, properties);
        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(WATERLOGGED, false)
                .setValue(TYPE, LampPostType.BASE));
        occlusionByIndex = makeShapes(2, 1, 16, 6, 15);
    }

    @Override
    protected MapCodec<LampPostBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, WATERLOGGED, TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluid = context.getLevel().getFluidState(pos);
        return defaultBlockState()
                .setValue(NORTH, connectsTo(level.getBlockState(pos.north()), level, pos.north(), Direction.SOUTH))
                .setValue(EAST, connectsTo(level.getBlockState(pos.east()), level, pos.east(), Direction.WEST))
                .setValue(SOUTH, connectsTo(level.getBlockState(pos.south()), level, pos.south(), Direction.NORTH))
                .setValue(WEST, connectsTo(level.getBlockState(pos.west()), level, pos.west(), Direction.EAST))
                .setValue(TYPE, getType(level, pos))
                .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (direction.getAxis().isHorizontal()) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(direction),
                    connectsTo(neighborState, level, neighborPos, direction.getOpposite()));
        }
        // The part type is fully re-derived from the current surroundings, so the
        // result never depends on the order in which the parts were placed.
        return state.setValue(TYPE, getType(level, pos));
    }

    private boolean connectsTo(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getBlock() instanceof LampPostBlock
                || !isExceptionForConnection(state) && state.isFaceSturdy(level, pos, direction);
    }

    private LampPostType getType(BlockGetter level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        BlockState above = level.getBlockState(pos.above());
        if (below.getBlock() instanceof LampPostBlock && below.getValue(TYPE).isPost()) {
            // Part of a vertical post: only the topmost part carries the cap, so that
            // the cap follows the post as blocks are added to or removed from the stack.
            return above.getBlock() instanceof LampPostBlock && above.getValue(TYPE).isPost()
                    ? LampPostType.POLE
                    : LampPostType.TOP;
        }
        if (below.isFaceSturdy(level, pos.below(), Direction.UP)) {
            return LampPostType.BASE;
        }
        if (isValidUpBlock(above)) {
            return LampPostType.SIDE_UP;
        }
        if (isValidDownBlock(below)) {
            return LampPostType.SIDE_DOWN;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighbor = level.getBlockState(neighborPos);
            if ((neighbor.getBlock() instanceof LampPostBlock && neighbor.getValue(TYPE) == LampPostType.TOP)
                    || neighbor.isFaceSturdy(level, neighborPos, direction.getOpposite())) {
                return LampPostType.SIDE_DOWN;
            }
        }
        return LampPostType.POLE;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathType) {
        return false;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return occlusionByIndex[getAABBIndex(state)];
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        return level.isClientSide ? InteractionResult.PASS : LeadItem.bindPlayerMobs(player, level, pos);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return stack.is(Items.LEAD) ? ItemInteractionResult.SUCCESS
                    : ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(asItem()));
    }

    protected boolean isValidUpBlock(BlockState state) {
        return state.getBlock() instanceof LanternBlock;
    }

    protected boolean isValidDownBlock(BlockState state) {
        return state.getBlock() instanceof ChainBlock || state.getBlock() instanceof LanternBlock;
    }
}
