package com.devbobcorn.nekoration.blocks.frames;

import java.util.Map;

import com.devbobcorn.nekoration.blocks.DyeableHorizontalBlock;
import com.devbobcorn.nekoration.blocks.DyeableHorizontalConnectBlock;
import com.devbobcorn.nekoration.blocks.WindowBlock;
import com.devbobcorn.nekoration.blocks.states.FramePart;
import com.devbobcorn.nekoration.blocks.states.HorizontalConnection;
import com.devbobcorn.nekoration.blocks.states.ModStateProperties;
import com.devbobcorn.nekoration.common.VanillaCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;

public class DyeableWindowFrameBlock extends DyeableHorizontalBlock {
    protected static Double thickness = 6.0D;

    private static final Map<Direction, VoxelShape> AABBs = getAABBs(4.0D);

    public static final IntegerProperty COLOR = BlockStateProperties.LEVEL;

    public static final EnumProperty<FramePart> PART = ModStateProperties.FRAME_PART;
    public static final BooleanProperty LEFT = ModStateProperties.LEFT;
    public static final BooleanProperty RIGHT = ModStateProperties.RIGHT;

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
        s.add(COLOR, FACING, PART, LEFT, RIGHT);
    }

    public final boolean connectOthers;

    public DyeableWindowFrameBlock(Properties settings) {
        super(settings);
        connectOthers = false;
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return AABBs.get(state.getValue(FACING));
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (world.isClientSide) {
            return (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        
        if (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) {
            world.setBlock(pos, state.setValue(COLOR, VanillaCompat.COLOR_ITEMS.get(itemStack.getItem())), 3);
            return InteractionResult.CONSUME;
        }

        if (itemStack.getItem() instanceof AxeItem){
            if (world.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            
            world.setBlock(pos, state.cycle(PART), 3);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    private boolean checkWindowBlock(BlockState state){
        return state.getMaterial() == Material.AIR || state.getBlock() instanceof WindowBlock || state.is(Tags.Blocks.GLASS) || state.is(Tags.Blocks.GLASS_PANES);
    }

    private boolean checkFrameBlock(BlockState state){
        return state.getBlock() instanceof DyeableWindowFrameBlock || state.getBlock() instanceof DyeableWindowSillBlock || state.getBlock() instanceof DyeableWindowTopBlock;
    }

    private boolean checkPart(BlockState state, FramePart part){
        if (part == FramePart.BOTTOM && state.getBlock() instanceof DyeableWindowSillBlock)
            return true;
        if (part == FramePart.TOP && state.getBlock() instanceof DyeableWindowTopBlock)
            return true;
        if (!(state.getBlock() instanceof DyeableWindowFrameBlock))
            return false;
        return state.getValue(PART) == part;
    }

    private FramePart getPart(BlockState state){
        if (state.getBlock() instanceof DyeableWindowSillBlock)
            return FramePart.BOTTOM;
        if (state.getBlock() instanceof DyeableWindowTopBlock)
            return FramePart.TOP;
        if (!(state.getBlock() instanceof DyeableWindowFrameBlock))
            return FramePart.MIDDLE; // ...
        return state.getValue(PART);
    }

    private boolean getLeft(BlockState state){
        if (state.getBlock() instanceof DyeableHorizontalConnectBlock) {
            HorizontalConnection its = state.getValue(DyeableHorizontalConnectBlock.CONNECTION);
            return (its == HorizontalConnection.S0 || its == HorizontalConnection.D0 || its == HorizontalConnection.T0);
        }
        if (state.getBlock() instanceof DyeableWindowFrameBlock) {
            return state.getValue(LEFT);
        }
        return false;
    }

    private boolean getRight(BlockState state){
        if (state.getBlock() instanceof DyeableHorizontalConnectBlock) {
            HorizontalConnection its = state.getValue(DyeableHorizontalConnectBlock.CONNECTION);
            return (its == HorizontalConnection.S0 || its == HorizontalConnection.D1 || its == HorizontalConnection.T2);
        }
        if (state.getBlock() instanceof DyeableWindowFrameBlock) {
            return state.getValue(RIGHT);
        }
        return false;
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction dir = ctx.getHorizontalDirection().getOpposite();
        Level world = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        BlockPos blockPosL = getLeftBlock(blockPos, dir);
        BlockPos blockPosR = getRightBlock(blockPos, dir);
        BlockPos blockPosU = getUpBlock(blockPos);
        BlockPos blockPosD = getDownBlock(blockPos);
        BlockState stateL = world.getBlockState(blockPosL);
        BlockState stateR = world.getBlockState(blockPosR);
        BlockState stateU = world.getBlockState(blockPosU);
        BlockState stateD = world.getBlockState(blockPosD);
        boolean l = checkFrameBlock(stateL);
        boolean r = checkFrameBlock(stateR);
        boolean u = checkFrameBlock(stateU);
        boolean d = checkFrameBlock(stateD);
        
        FramePart myPart;
        boolean resl = true;
        boolean resr = false;

        if (!u) { // Nothing Above
            if (!d) {// Nothing Below, Either
                if (l && !checkPart(stateL, FramePart.MIDDLE))
                    myPart = getPart(stateL);
                else if (r && !checkPart(stateR, FramePart.MIDDLE))
                    myPart = getPart(stateR);
                else {
                    BlockPos pos2 = getFurtherBlock(blockPos, dir);
                    if (checkWindowBlock(world.getBlockState(pos2.above()))) // The upper block behind it
                        myPart = FramePart.BOTTOM;
                    //else if (checkWindowBlock(world.getBlockState(pos2.below())))
                    //    myPart = FramePart.TOP;
                    else myPart = FramePart.TOP;
                }
            } else { // Something Below
                if (checkPart(stateD, FramePart.TOP)) // Down is top, we bottom
                    myPart = FramePart.BOTTOM;
                else myPart = FramePart.TOP;
            }
        } else { // Something Above
            if (checkPart(stateU, FramePart.BOTTOM)) // Up is bottom, we top
                myPart = FramePart.TOP;
            else if (!d) { // Nothing Below
                myPart = FramePart.BOTTOM;
            } else { // Somthing Below
                myPart = FramePart.MIDDLE;
            }
        }

        if (r && l) { // Connected on both sides
            if (checkPart(stateU, FramePart.MIDDLE) || checkPart(stateD, FramePart.MIDDLE))
                resl = (resr = true);
            else resl = (resr = false);
        } else if (l) { // right true, left false
            resr = true;
            resl = false;
        } else if (r) { // left true, right false
            resl = true;
            resr = false;
        } else { // Not connected horizontally, try asking vertical neighbors
            if (u) {
                resl = getLeft(stateU);
                resr = getRight(stateU);
            } else if (d) {
                resl = getLeft(stateD);
                resr = getRight(stateD);
            } // else : Really alone, use default values
        }

        return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(PART, myPart).setValue(LEFT, resl).setValue(RIGHT, resr);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        Direction dir = state.getValue(FACING);

        if (direction == dir || direction == dir.getOpposite()) // None of our business, ignore...
            return state;

        // ===
        BlockPos blockPosL = getLeftBlock(pos, dir);
        BlockPos blockPosR = getRightBlock(pos, dir);
        BlockPos blockPosU = getUpBlock(pos);
        BlockPos blockPosD = getDownBlock(pos);
        BlockState stateL = world.getBlockState(blockPosL);
        BlockState stateR = world.getBlockState(blockPosR);
        BlockState stateU = world.getBlockState(blockPosU);
        BlockState stateD = world.getBlockState(blockPosD);

        if (blockPosL.compareTo(posFrom) == 0) {
            stateL = newState;
        }
        if (blockPosR.compareTo(posFrom) == 0) {
            stateR = newState;
        }
        if (blockPosU.compareTo(posFrom) == 0) {
            stateU = newState;
        }
        if (blockPosD.compareTo(posFrom) == 0) {
            stateD = newState;
        }

        boolean l = checkFrameBlock(stateL);
        boolean r = checkFrameBlock(stateR);
        boolean u = checkFrameBlock(stateU);
        boolean d = checkFrameBlock(stateD);

        FramePart myPart;
        boolean resl = state.getValue(LEFT);
        boolean resr = state.getValue(RIGHT);

        if (!u) { // Nothing Above
            if (!d) {// Nothing Below, Either
                if (l && !checkPart(stateL, FramePart.MIDDLE))
                    myPart = getPart(stateL);
                else if (r && !checkPart(stateR, FramePart.MIDDLE))
                    myPart = getPart(stateR);
                else {
                    BlockPos pos2 = getFurtherBlock(pos, dir);
                    if (checkWindowBlock(world.getBlockState(pos2.above()))) // The upper block behind it
                        myPart = FramePart.BOTTOM;
                    //else if (checkWindowBlock(world.getBlockState(pos2.below())))
                    //    myPart = FramePart.TOP;
                    else myPart = FramePart.TOP;
                }
            } else { // Something Below
                if (checkPart(stateD, FramePart.TOP)) // Down is top, we bottom
                    myPart = FramePart.BOTTOM;
                else myPart = FramePart.TOP;
            }
        } else { // Something Above
            if (checkPart(stateU, FramePart.BOTTOM)) // Up is bottom, we top
                myPart = FramePart.TOP;
            else if (!d) { // Nothing Below
                myPart = FramePart.BOTTOM;
            } else { // Somthing Below
                myPart = FramePart.MIDDLE;
            }
        }

        if (r && l) { // Connected on both sides
            if (checkPart(stateU, FramePart.MIDDLE) || checkPart(stateD, FramePart.MIDDLE)) // Up or Down is middle, left and right both true
                resl = (resr = true);
            else resl = (resr = false);
        } else if (l) { // right true, left false
            resr = true;
            resl = false;
        } else if (r) { // left true, right false
            resl = true;
            resr = false;
        } else { // Not connected horizontally, try asking vertical neighbors
            if (u) {
                resl = getLeft(stateU);
                resr = getRight(stateU);
            } else if (d) {
                resl = getLeft(stateD);
                resr = getRight(stateD);
            } // else : Really alone, use default values
        }
        // ===

        return state.setValue(PART, myPart).setValue(LEFT, resl).setValue(RIGHT, resr);
    }

    public BlockPos getUpBlock(BlockPos pos) {
        return pos.above();
    }

    public BlockPos getDownBlock(BlockPos pos) {
        return pos.below();
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

    public BlockPos getFurtherBlock(BlockPos pos, Direction dir) {
        return pos.offset(dir.getOpposite().getNormal());
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