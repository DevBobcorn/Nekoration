package io.devbobcorn.nekoration.blocks;

import io.devbobcorn.nekoration.blocks.states.CandleFlameType;
import io.devbobcorn.nekoration.common.VanillaCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Dyeable candle holder with a configurable flame, lit with flame items.
 */
public class CandleHolderBlock extends DyeableBlock {
    public static final EnumProperty<CandleFlameType> FLAME = EnumProperty.create("flame", CandleFlameType.class);
    private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public CandleHolderBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FLAME, CandleFlameType.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FLAME);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return VanillaCompat.FLAME_ITEMS.containsKey(stack.getItem()) ? ItemInteractionResult.SUCCESS
                    : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        CandleFlameType flame = VanillaCompat.FLAME_ITEMS.get(stack.getItem());
        if (flame != null) {
            level.setBlock(pos, state.setValue(FLAME, flame), Block.UPDATE_ALL);
            return ItemInteractionResult.CONSUME;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        CandleFlameType flame = state.getValue(FLAME);
        if (!flame.isLit()) {
            return;
        }
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.2D;
        double z = pos.getZ() + 0.5D;
        double h = pos.getY() + 1.0D;
        double r = 0.38D;

        SimpleParticleType type = switch (flame) {
            case FLAME -> ParticleTypes.FLAME;
            case SOUL_FLAME -> ParticleTypes.SOUL_FIRE_FLAME;
            default -> ParticleTypes.FIREWORK;
        };

        level.addParticle(type, x, y, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(type, x + r, h, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(type, x - r, h, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(type, x, h, z + r, 0.0D, 0.0D, 0.0D);
        level.addParticle(type, x, h, z - r, 0.0D, 0.0D, 0.0D);
    }
}
