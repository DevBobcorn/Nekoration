package io.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;

import io.devbobcorn.nekoration.blocks.states.DoorSegment;
import io.devbobcorn.nekoration.blocks.states.ModStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;

/**
 * Quartz door spanning three blocks. The vanilla {@code half} property is kept in sync
 * ({@code lower} for the bottom segment, {@code upper} for the other two) for compatibility
 * with code that assumes two-block doors; the authoritative property is {@link #SEGMENT}.
 */
public class TallDoorBlock extends NekoDoorBlock {
    public static final MapCodec<TallDoorBlock> CODEC = simpleCodec(TallDoorBlock::new);
    public static final EnumProperty<DoorSegment> SEGMENT = ModStateProperties.DOOR_SEGMENT;

    public TallDoorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, Boolean.valueOf(false))
                .setValue(HINGE, DoorHingeSide.LEFT)
                .setValue(POWERED, Boolean.valueOf(false))
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(SEGMENT, DoorSegment.LOWER));
    }

    @Override
    public MapCodec<? extends DoorBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // Note: HALF must stay in the state definition, the vanilla DoorBlock constructor
        // sets it when registering the default state.
        builder.add(HALF, FACING, OPEN, HINGE, POWERED, SEGMENT);
    }

    /** Sets the segment while keeping the redundant vanilla {@code half} property consistent. */
    public static BlockState withSegment(BlockState state, DoorSegment segment) {
        return state.setValue(SEGMENT, segment)
                .setValue(HALF, segment == DoorSegment.LOWER ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        if (blockpos.getY() < level.getMaxBuildHeight() - 2
                && level.getBlockState(blockpos.above()).canBeReplaced(context)
                && level.getBlockState(blockpos.above(2)).canBeReplaced(context)) {
            boolean flag = level.hasNeighborSignal(blockpos) || level.hasNeighborSignal(blockpos.above());
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection())
                    .setValue(HINGE, this.getHinge(context))
                    .setValue(POWERED, Boolean.valueOf(flag))
                    .setValue(OPEN, Boolean.valueOf(flag))
                    .setValue(SEGMENT, DoorSegment.LOWER)
                    .setValue(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), withSegment(state, DoorSegment.MIDDLE), 3);
        level.setBlock(pos.above(2), withSegment(state, DoorSegment.UPPER), 3);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level,
            BlockPos currentPos, BlockPos facingPos) {
        DoorSegment doorsegment = state.getValue(SEGMENT);
        if (facing.getAxis() == Direction.Axis.Y) {
            DoorSegment expected = expectedNeighborSegment(doorsegment, facing);
            if (expected != null) {
                // Copy the changed neighbor so opening / redstone keeps all segments in sync, or pop off.
                return facingState.is(this) && facingState.getValue(SEGMENT) == expected
                        ? withSegment(facingState, doorsegment)
                        : Blocks.AIR.defaultBlockState();
            }
        }
        return doorsegment == DoorSegment.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    /**
     * The segment the vertical neighbor in the given direction should have, or {@code null}
     * when the direction does not point at another segment of this door.
     */
    private static DoorSegment expectedNeighborSegment(DoorSegment segment, Direction facing) {
        if (facing == Direction.UP) {
            return segment == DoorSegment.LOWER ? DoorSegment.MIDDLE
                    : segment == DoorSegment.MIDDLE ? DoorSegment.UPPER : null;
        }
        if (facing == Direction.DOWN) {
            return segment == DoorSegment.UPPER ? DoorSegment.MIDDLE
                    : segment == DoorSegment.MIDDLE ? DoorSegment.LOWER : null;
        }
        return null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(SEGMENT) == DoorSegment.LOWER) {
            BlockPos blockpos = pos.below();
            return level.getBlockState(blockpos).isFaceSturdy(level, blockpos, Direction.UP);
        }
        return level.getBlockState(pos.below()).is(this);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
            boolean isMoving) {
        boolean flag = this.hasSignalAtAnySegment(level, pos);
        if (!this.defaultBlockState().is(block) && flag != state.getValue(POWERED)) {
            if (flag != state.getValue(OPEN)) {
                level.playSound(null, pos,
                        flag ? this.type().doorOpen() : this.type().doorClose(),
                        SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
                level.gameEvent(null, flag ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            }
            level.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(flag)).setValue(OPEN, Boolean.valueOf(flag)), 2);
        }
    }

    /** Redstone power applied to any of the three segments opens the whole door. */
    private boolean hasSignalAtAnySegment(Level level, BlockPos pos) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        if (level.hasNeighborSignal(pos)) {
            return true;
        }
        blockpos$mutableblockpos.set(pos).move(Direction.DOWN);
        while (level.getBlockState(blockpos$mutableblockpos).is(this)) {
            if (level.hasNeighborSignal(blockpos$mutableblockpos)) {
                return true;
            }
            blockpos$mutableblockpos.move(Direction.DOWN);
        }
        blockpos$mutableblockpos.set(pos).move(Direction.UP);
        while (level.getBlockState(blockpos$mutableblockpos).is(this)) {
            if (level.hasNeighborSignal(blockpos$mutableblockpos)) {
                return true;
            }
            blockpos$mutableblockpos.move(Direction.UP);
        }
        return false;
    }

    @Override
    protected void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion,
            BiConsumer<ItemStack, BlockPos> dropConsumer) {
        if (explosion.canTriggerBlocks() && state.getValue(SEGMENT) == DoorSegment.LOWER
                && this.type().canOpenByWindCharge() && !state.getValue(POWERED)) {
            this.setOpen(null, level, state, pos, !this.isOpen(state));
        }
        super.onExplosionHit(state, level, pos, explosion, dropConsumer);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && (player.isCreative() || !player.hasCorrectToolForDrops(state, level, pos))) {
            // Remove the segments below the broken one first so they do not drop their item.
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            blockpos$mutableblockpos.set(pos).move(Direction.DOWN);
            while (level.getBlockState(blockpos$mutableblockpos).is(this)) {
                BlockState blockstate = level.getBlockState(blockpos$mutableblockpos);
                level.setBlock(blockpos$mutableblockpos, Blocks.AIR.defaultBlockState(), 35,
                        level.isClientSide ? 0 : 2);
                level.levelEvent(player, 2001, blockpos$mutableblockpos, Block.getId(blockstate));
                blockpos$mutableblockpos.move(Direction.DOWN);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected long getSeed(BlockState state, BlockPos pos) {
        int offset = switch (state.getValue(SEGMENT)) {
            case LOWER -> 0;
            case MIDDLE -> 1;
            case UPPER -> 2;
        };
        return Mth.getSeed(pos.getX(), pos.below(offset).getY(), pos.getZ());
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        // Only the lower segment drops, otherwise breaking the door could yield multiple items.
        return state.getValue(SEGMENT) == DoorSegment.LOWER
                ? Collections.singletonList(new ItemStack(this.asItem()))
                : Collections.emptyList();
    }
}
