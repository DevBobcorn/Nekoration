package io.devbobcorn.nekoration.blocks;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Dyeable awning with an axe-toggleable end cap. */
public class AwningBlock extends DyeableHorizontalBlock {
    public static final BooleanProperty IS_END = BlockStateProperties.BOTTOM;
    public static final MapCodec<AwningBlock> CODEC = simpleCodec(AwningBlock::new);
    private static final VoxelShape SHAPE = Block.box(0.1D, 0.1D, 0.1D, 15.9D, 15.9D, 15.9D);

    public AwningBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(IS_END, false));
    }

    @Override
    protected MapCodec<AwningBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IS_END);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof AxeItem) {
            if (level.isClientSide()) {
                return ItemInteractionResult.SUCCESS;
            }
            level.setBlock(pos, state.cycle(IS_END), Block.UPDATE_ALL);
            return ItemInteractionResult.CONSUME;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
