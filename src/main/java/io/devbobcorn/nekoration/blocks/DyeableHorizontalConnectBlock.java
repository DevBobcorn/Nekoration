package io.devbobcorn.nekoration.blocks;

import java.util.Map;

import io.devbobcorn.nekoration.NekoConfig;
import io.devbobcorn.nekoration.NekoConfig.HorConnectionDir;
import io.devbobcorn.nekoration.blocks.states.HorizontalConnection;
import io.devbobcorn.nekoration.blocks.states.ModStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DyeableHorizontalConnectBlock extends DyeableHorizontalBlock {
    private static final Map<Direction, VoxelShape> SHAPES = getAABBs(4.0D, 16.0D);

    public enum ConnectionType {
        DOUBLE, TRIPLE, BEAM
    }

    public static final EnumProperty<HorizontalConnection> CONNECTION = ModStateProperties.HORIZONTAL_CONNECTION;

    public final ConnectionType type;
    public final boolean connectOtherVariant;

    public DyeableHorizontalConnectBlock(Properties settings) {
        this(settings, ConnectionType.TRIPLE, false);
    }

    public DyeableHorizontalConnectBlock(Properties settings, ConnectionType type, boolean connectOtherVariant) {
        super(settings);
        this.type = type;
        this.connectOtherVariant = connectOtherVariant;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(COLOR, io.devbobcorn.nekoration.NekoColors.EnumNekoColor.WHITE)
                .setValue(FACING, Direction.NORTH)
                .setValue(CONNECTION, HorizontalConnection.S0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTION);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState placed = super.getStateForPlacement(ctx);
        if (placed == null) {
            return null;
        }

        HorConnectionDir config = NekoConfig.HOR_CONNECTION_DIR.get();
        boolean useLeft = config == HorConnectionDir.LEFT2RIGHT || config == HorConnectionDir.BOTH;
        Direction facing = placed.getValue(FACING);
        BlockPos pos = ctx.getClickedPos();
        if (config != HorConnectionDir.NEITHER) {
            BlockPos refPos = useLeft ? getLeftBlock(pos, facing) : getRightBlock(pos, facing);
            BlockState refState = ctx.getLevel().getBlockState(refPos);
            boolean connect = canConnectTo(refState);

            if (!connect && config == HorConnectionDir.BOTH) {
                refPos = getRightBlock(pos, facing);
                refState = ctx.getLevel().getBlockState(refPos);
                connect = canConnectTo(refState);
                useLeft = false;
            }

            if (connect) {
                HorizontalConnection connection = useLeft
                        ? nextFromLeft(refState.getValue(CONNECTION))
                        : nextFromRight(refState.getValue(CONNECTION));
                return placed.setValue(CONNECTION, connection);
            }
        }
        return placed.setValue(CONNECTION, HorizontalConnection.S0);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
            BlockPos pos, BlockPos neighborPos) {
        BlockState res = state;
        HorConnectionDir config = NekoConfig.HOR_CONNECTION_DIR.get();
        Direction facing = state.getValue(FACING);

        boolean flag1 = direction == getRightDir(facing)
                && (config == HorConnectionDir.LEFT2RIGHT || config == HorConnectionDir.BOTH);
        boolean flag2 = direction == getLeftDir(facing)
                && (config == HorConnectionDir.RIGHT2LEFT || config == HorConnectionDir.BOTH);

        boolean connect = flag1 || flag2;
        if (connect && canConnectTo(neighborState)) {
            BlockState stateRef;
            if (flag1) {
                stateRef = level.getBlockState(getLeftBlock(pos, facing));
                return switch (neighborState.getValue(CONNECTION)) {
                    case D1 -> res.setValue(CONNECTION, HorizontalConnection.D0);
                    case T1 -> res.setValue(CONNECTION,
                            (type == ConnectionType.BEAM && canConnectTo(stateRef))
                                    ? HorizontalConnection.T1
                                    : HorizontalConnection.T0);
                    case T2 -> res.setValue(CONNECTION, HorizontalConnection.T1);
                    default -> res;
                };
            } else {
                stateRef = level.getBlockState(getRightBlock(pos, facing));
                return switch (neighborState.getValue(CONNECTION)) {
                    case D0 -> res.setValue(CONNECTION, HorizontalConnection.D1);
                    case T1 -> res.setValue(CONNECTION,
                            (type == ConnectionType.BEAM && canConnectTo(stateRef))
                                    ? HorizontalConnection.T1
                                    : HorizontalConnection.T2);
                    case T0 -> res.setValue(CONNECTION, HorizontalConnection.T1);
                    default -> res;
                };
            }
        }
        return res;
    }

    private boolean canConnectTo(BlockState state) {
        return state.getBlock() instanceof DyeableHorizontalConnectBlock
                && (connectOtherVariant || state.getBlock() == this);
    }

    private HorizontalConnection nextFromLeft(HorizontalConnection leftConnection) {
        return switch (leftConnection) {
            case S0, D0, T0 -> HorizontalConnection.D1;
            case D1 -> type == ConnectionType.DOUBLE ? HorizontalConnection.S0 : HorizontalConnection.T2;
            case T1 -> HorizontalConnection.T2;
            case T2 -> type == ConnectionType.BEAM ? HorizontalConnection.T2 : HorizontalConnection.S0;
        };
    }

    private HorizontalConnection nextFromRight(HorizontalConnection rightConnection) {
        return switch (rightConnection) {
            case S0, D1, T2 -> HorizontalConnection.D0;
            case D0 -> type == ConnectionType.DOUBLE ? HorizontalConnection.S0 : HorizontalConnection.T0;
            case T1 -> HorizontalConnection.T0;
            case T0 -> type == ConnectionType.BEAM ? HorizontalConnection.T0 : HorizontalConnection.S0;
        };
    }
}
