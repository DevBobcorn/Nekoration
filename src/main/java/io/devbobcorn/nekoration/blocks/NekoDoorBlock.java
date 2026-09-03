package io.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;

import io.devbobcorn.nekoration.blocks.states.DoorSegment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Quartz door spanning two blocks. Using bone meal grows it into its tall (three blocks) variant.
 */
public class NekoDoorBlock extends DoorBlock {
    public static final MapCodec<NekoDoorBlock> CODEC = simpleCodec(NekoDoorBlock::new);

    /** Tall variant this door grows into with bone meal, if any. */
    private final Supplier<? extends Block> tallVariant;

    public NekoDoorBlock(Properties properties) {
        this(properties, null);
    }

    public NekoDoorBlock(Properties properties, @Nullable Supplier<? extends Block> tallVariant) {
        super(BlockSetType.OAK, properties);
        this.tallVariant = tallVariant;
    }

    @Override
    public MapCodec<? extends DoorBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context)) {
            boolean flag = level.hasNeighborSignal(blockpos) || level.hasNeighborSignal(blockpos.above());
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection())
                    .setValue(HINGE, this.getHinge(context))
                    .setValue(POWERED, Boolean.valueOf(flag))
                    .setValue(OPEN, Boolean.valueOf(flag))
                    .setValue(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    /**
     * Vanilla hinge logic, but with a door-neighbor check that is safe for all door types
     * placed next to the position (e.g. tall doors keyed by their segment property).
     */
    protected DoorHingeSide getHinge(BlockPlaceContext context) {
        BlockGetter blockgetter = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getHorizontalDirection();
        BlockPos blockpos1 = blockpos.above();
        Direction direction1 = direction.getCounterClockWise();
        BlockPos blockpos2 = blockpos.relative(direction1);
        BlockState blockstate = blockgetter.getBlockState(blockpos2);
        BlockPos blockpos3 = blockpos1.relative(direction1);
        BlockState blockstate1 = blockgetter.getBlockState(blockpos3);
        Direction direction2 = direction.getClockWise();
        BlockPos blockpos4 = blockpos.relative(direction2);
        BlockState blockstate2 = blockgetter.getBlockState(blockpos4);
        BlockPos blockpos5 = blockpos1.relative(direction2);
        BlockState blockstate3 = blockgetter.getBlockState(blockpos5);
        int i = (blockstate.isCollisionShapeFullBlock(blockgetter, blockpos2) ? -1 : 0)
                + (blockstate1.isCollisionShapeFullBlock(blockgetter, blockpos3) ? -1 : 0)
                + (blockstate2.isCollisionShapeFullBlock(blockgetter, blockpos4) ? 1 : 0)
                + (blockstate3.isCollisionShapeFullBlock(blockgetter, blockpos5) ? 1 : 0);
        boolean flag = isDoorAtLowerLevel(blockstate);
        boolean flag1 = isDoorAtLowerLevel(blockstate2);
        if ((!flag || flag1) && i <= 0) {
            if ((!flag1 || flag) && i >= 0) {
                int j = direction.getStepX();
                int k = direction.getStepZ();
                Vec3 vec3 = context.getClickLocation();
                double d0 = vec3.x - (double) blockpos.getX();
                double d1 = vec3.z - (double) blockpos.getZ();
                return (j >= 0 || !(d1 < 0.5)) && (j <= 0 || !(d1 > 0.5)) && (k >= 0 || !(d0 > 0.5)) && (k <= 0 || !(d0 < 0.5))
                        ? DoorHingeSide.LEFT
                        : DoorHingeSide.RIGHT;
            }
            return DoorHingeSide.LEFT;
        }
        return DoorHingeSide.RIGHT;
    }

    /** Whether the state is the bottom part of any door placed at ground level. */
    protected static boolean isDoorAtLowerLevel(BlockState state) {
        if (state.getBlock() instanceof TallDoorBlock) {
            return state.getValue(TallDoorBlock.SEGMENT) == DoorSegment.LOWER;
        }
        return state.getBlock() instanceof DoorBlock && state.hasProperty(HALF)
                && state.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level,
            BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
            // Only match other halves of the same door; other door types (e.g. tall doors) must not be copied.
            return facingState.is(this) && facingState.getValue(HALF) != doubleblockhalf
                    ? facingState.setValue(HALF, doubleblockhalf)
                    : Blocks.AIR.defaultBlockState();
        }
        return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.BONE_MEAL) && tallVariant != null && tryGrow(level, pos, state, tallVariant.get())) {
            if (!level.isClientSide && !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    /** Replaces this 2-block door with the 3-block tall variant, keeping its orientation. */
    private boolean tryGrow(Level level, BlockPos pos, BlockState state, Block tallBlock) {
        BlockPos blockpos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        BlockPos toppos = blockpos.above(2);
        if (level.isOutsideBuildHeight(toppos) || !level.getBlockState(toppos).canBeReplaced()) {
            return false;
        }
        if (!level.isClientSide) {
            BlockState blockstate = TallDoorBlock.withSegment(tallBlock.defaultBlockState()
                    .setValue(FACING, state.getValue(FACING))
                    .setValue(HINGE, state.getValue(HINGE))
                    .setValue(OPEN, state.getValue(OPEN))
                    .setValue(POWERED, state.getValue(POWERED)), DoorSegment.LOWER);
            // Clear the old upper half without shape updates so the lower half does not pop off first.
            level.setBlock(blockpos.above(), Blocks.AIR.defaultBlockState(), 3, 0);
            level.setBlock(blockpos, blockstate, 3);
            level.setBlock(blockpos.above(), TallDoorBlock.withSegment(blockstate, DoorSegment.MIDDLE), 3);
            level.setBlock(toppos, TallDoorBlock.withSegment(blockstate, DoorSegment.UPPER), 3);
        }
        level.addParticle(ParticleTypes.EXPLOSION_EMITTER,
                (double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D, (double) blockpos.getZ() + 0.5D,
                0.0D, 0.0D, 0.0D);
        return true;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        // Only the lower half drops, otherwise breaking the door could yield two items.
        return state.getValue(HALF) == DoubleBlockHalf.LOWER
                ? Collections.singletonList(new ItemStack(this.asItem()))
                : Collections.emptyList();
    }
}
