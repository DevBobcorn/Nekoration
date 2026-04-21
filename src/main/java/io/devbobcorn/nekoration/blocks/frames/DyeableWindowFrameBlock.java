package io.devbobcorn.nekoration.blocks.frames;

import java.util.Map;

import io.devbobcorn.nekoration.blocks.DyeableHorizontalConnectBlock;
import io.devbobcorn.nekoration.blocks.DyeableHorizontalBlock;
import io.devbobcorn.nekoration.blocks.WindowBlock;
import io.devbobcorn.nekoration.blocks.states.FramePart;
import io.devbobcorn.nekoration.blocks.states.HorizontalConnection;
import io.devbobcorn.nekoration.blocks.states.ModStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DyeableWindowFrameBlock extends DyeableHorizontalBlock {
    private static final Map<Direction, VoxelShape> SHAPES = getAABBs(4.0D, 16.0D);
    public static final EnumProperty<FramePart> PART = ModStateProperties.FRAME_PART;
    public static final BooleanProperty LEFT = ModStateProperties.LEFT;
    public static final BooleanProperty RIGHT = ModStateProperties.RIGHT;

    public DyeableWindowFrameBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(COLOR, io.devbobcorn.nekoration.NekoColors.EnumNekoColor.WHITE)
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, defaultPart())
                .setValue(LEFT, true)
                .setValue(RIGHT, false));
    }

    protected FramePart defaultPart() {
        return FramePart.MIDDLE;
    }

    protected FramePart normalizePart(FramePart computed) {
        return computed;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART, LEFT, RIGHT);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    private boolean checkWindowBlock(BlockState state) {
        return state.isAir()
                || state.getBlock() instanceof WindowBlock
                || state.getBlock() instanceof StainedGlassBlock
                || state.getBlock() instanceof StainedGlassPaneBlock
                || state.getBlock() instanceof IronBarsBlock;
    }

    private boolean checkFrameBlock(BlockState state) {
        return state.getBlock() instanceof DyeableWindowFrameBlock
                || state.getBlock() instanceof DyeableWindowSillBlock
                || state.getBlock() instanceof DyeableWindowTopBlock;
    }

    private boolean checkPart(BlockState state, FramePart part) {
        if (!checkFrameBlock(state)) {
            return false;
        }
        if (part == FramePart.BOTTOM && state.getBlock() instanceof DyeableWindowSillBlock) {
            return true;
        }
        if (part == FramePart.TOP && state.getBlock() instanceof DyeableWindowTopBlock) {
            return true;
        }
        return state.getBlock() instanceof DyeableWindowFrameBlock && state.getValue(PART) == part;
    }

    private FramePart getPart(BlockState state) {
        if (state.getBlock() instanceof DyeableWindowSillBlock) {
            return FramePart.BOTTOM;
        }
        if (state.getBlock() instanceof DyeableWindowTopBlock) {
            return FramePart.TOP;
        }
        if (!(state.getBlock() instanceof DyeableWindowFrameBlock)) {
            return FramePart.MIDDLE;
        }
        return state.getValue(PART);
    }

    private boolean getLeft(BlockState state) {
        if (state.getBlock() instanceof DyeableHorizontalConnectBlock) {
            HorizontalConnection connection = state.getValue(DyeableHorizontalConnectBlock.CONNECTION);
            return connection == HorizontalConnection.S0
                    || connection == HorizontalConnection.D0
                    || connection == HorizontalConnection.T0;
        }
        if (state.getBlock() instanceof DyeableWindowFrameBlock) {
            return state.getValue(LEFT);
        }
        return false;
    }

    private boolean getRight(BlockState state) {
        if (state.getBlock() instanceof DyeableHorizontalConnectBlock) {
            HorizontalConnection connection = state.getValue(DyeableHorizontalConnectBlock.CONNECTION);
            return connection == HorizontalConnection.S0
                    || connection == HorizontalConnection.D1
                    || connection == HorizontalConnection.T2;
        }
        if (state.getBlock() instanceof DyeableWindowFrameBlock) {
            return state.getValue(RIGHT);
        }
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction dir = ctx.getHorizontalDirection().getOpposite();
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState stateL = level.getBlockState(getLeftBlock(pos, dir));
        BlockState stateR = level.getBlockState(getRightBlock(pos, dir));
        BlockState stateU = level.getBlockState(getUpBlock(pos));
        BlockState stateD = level.getBlockState(getDownBlock(pos));
        boolean l = checkFrameBlock(stateL);
        boolean r = checkFrameBlock(stateR);
        boolean u = checkFrameBlock(stateU);
        boolean d = checkFrameBlock(stateD);

        FramePart part = computePart(level, pos, dir, stateL, stateR, stateU, stateD, l, r, u, d);
        boolean[] sideFlags = computeSideFlags(stateU, stateD, l, r, u, d);

        return super.getStateForPlacement(ctx)
                .setValue(FACING, dir)
                .setValue(PART, normalizePart(part))
                .setValue(LEFT, sideFlags[0])
                .setValue(RIGHT, sideFlags[1]);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor level,
            BlockPos pos, BlockPos neighborPos) {
        Direction dir = state.getValue(FACING);
        if (direction == dir || direction == dir.getOpposite()) {
            return state;
        }

        BlockPos blockPosL = getLeftBlock(pos, dir);
        BlockPos blockPosR = getRightBlock(pos, dir);
        BlockPos blockPosU = getUpBlock(pos);
        BlockPos blockPosD = getDownBlock(pos);
        BlockState stateL = blockPosL.equals(neighborPos) ? newState : level.getBlockState(blockPosL);
        BlockState stateR = blockPosR.equals(neighborPos) ? newState : level.getBlockState(blockPosR);
        BlockState stateU = blockPosU.equals(neighborPos) ? newState : level.getBlockState(blockPosU);
        BlockState stateD = blockPosD.equals(neighborPos) ? newState : level.getBlockState(blockPosD);

        boolean l = checkFrameBlock(stateL);
        boolean r = checkFrameBlock(stateR);
        boolean u = checkFrameBlock(stateU);
        boolean d = checkFrameBlock(stateD);

        FramePart part = computePart(level, pos, dir, stateL, stateR, stateU, stateD, l, r, u, d);
        boolean[] sideFlags = computeSideFlags(stateU, stateD, l, r, u, d);

        return state
                .setValue(PART, normalizePart(part))
                .setValue(LEFT, sideFlags[0])
                .setValue(RIGHT, sideFlags[1]);
    }

    private FramePart computePart(LevelAccessor level, BlockPos pos, Direction dir,
            BlockState stateL, BlockState stateR, BlockState stateU, BlockState stateD,
            boolean l, boolean r, boolean u, boolean d) {
        if (!u) {
            if (!d) {
                if (l && !checkPart(stateL, FramePart.MIDDLE)) {
                    return getPart(stateL);
                }
                if (r && !checkPart(stateR, FramePart.MIDDLE)) {
                    return getPart(stateR);
                }
                BlockPos pos2 = getFurtherBlock(pos, dir);
                if (checkWindowBlock(level.getBlockState(pos2.below()))) {
                    return FramePart.TOP;
                }
                if (checkWindowBlock(level.getBlockState(pos2.above()))) {
                    return FramePart.BOTTOM;
                }
                return FramePart.TOP;
            }
            if (checkPart(stateD, FramePart.TOP)) {
                return FramePart.BOTTOM;
            }
            return FramePart.TOP;
        }
        if (checkPart(stateU, FramePart.BOTTOM)) {
            return FramePart.TOP;
        }
        if (!d) {
            return FramePart.BOTTOM;
        }
        return FramePart.MIDDLE;
    }

    private boolean[] computeSideFlags(BlockState stateU, BlockState stateD, boolean l, boolean r, boolean u, boolean d) {
        boolean resl = true;
        boolean resr = false;
        if (r && l) {
            boolean middleNeighbor = checkPart(stateU, FramePart.MIDDLE) || checkPart(stateD, FramePart.MIDDLE);
            resl = middleNeighbor;
            resr = middleNeighbor;
        } else if (l) {
            resl = false;
            resr = true;
        } else if (r) {
            resl = true;
            resr = false;
        } else if (u) {
            resl = getLeft(stateU);
            resr = getRight(stateU);
        } else if (d) {
            resl = getLeft(stateD);
            resr = getRight(stateD);
        }
        return new boolean[] { resl, resr };
    }

    protected BlockPos getUpBlock(BlockPos pos) {
        return pos.above();
    }

    protected BlockPos getDownBlock(BlockPos pos) {
        return pos.below();
    }

    protected BlockPos getFurtherBlock(BlockPos pos, Direction dir) {
        return pos.relative(dir.getOpposite());
    }
}
