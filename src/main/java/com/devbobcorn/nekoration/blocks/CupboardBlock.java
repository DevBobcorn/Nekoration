package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.NekoConfig;
import com.devbobcorn.nekoration.NekoConfig.VerConnectionDir;
import com.devbobcorn.nekoration.blocks.entities.ItemDisplayBlockEntity;
import com.devbobcorn.nekoration.common.VanillaCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class CupboardBlock extends ItemDisplayBlock {
    public static final BooleanProperty BOTTOM  = BlockStateProperties.BOTTOM;
    public final boolean playSound;

    public CupboardBlock(Properties settings, boolean p) {
        super(settings);
        this.playSound = p;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
        s.add(COLOR, FACING, OPEN, BOTTOM);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ItemDisplayBlockEntity(pos, state, false, playSound);
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
        VerConnectionDir config = NekoConfig.SERVER.verConnectionDir.get();
        
        if (config == VerConnectionDir.BOTH || config == VerConnectionDir.TOP2BOTTOM){
            BlockPos blockPosRef = blockPos.above();
            BlockState stateRef = blockView.getBlockState(blockPosRef);

            if (stateRef.getBlock() instanceof CupboardBlock){
                return super.getStateForPlacement(ctx).setValue(BOTTOM, true);
            } else return super.getStateForPlacement(ctx).setValue(BOTTOM, false);
        }
        return super.getStateForPlacement(ctx).setValue(BOTTOM, false);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        VerConnectionDir config = NekoConfig.SERVER.verConnectionDir.get();

        if (config == VerConnectionDir.BOTH || config == VerConnectionDir.BOTTOM2TOP)
            if (direction == Direction.UP)
                if (newState.getBlock() instanceof CupboardBlock)
                    return state.setValue(BOTTOM, true);
                else return state.setValue(BOTTOM, false);
        return state;
    }
}
