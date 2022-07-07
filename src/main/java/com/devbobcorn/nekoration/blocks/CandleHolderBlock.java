package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.common.VanillaCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CandleHolderBlock extends DyeableBlock {
    public static final IntegerProperty FLAME = BlockStateProperties.AGE_3;

    public CandleHolderBlock(Properties settings) {
        super(settings);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
        s.add(COLOR, FLAME);
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (world.isClientSide) {
            return (VanillaCompat.FLAME_ITEMS.containsKey(itemStack.getItem())) ? InteractionResult.SUCCESS : super.use(state, world, pos, player, hand, hit);
        }
        
        if (VanillaCompat.FLAME_ITEMS.containsKey(itemStack.getItem())) {
            world.setBlock(pos, state.setValue(FLAME, VanillaCompat.FLAME_ITEMS.get(itemStack.getItem())), 3);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        if (stateIn.getValue(FLAME) > 0) {
            double x = (double) pos.getX() + 0.5D;
            double y = (double) pos.getY() + 1.2D;
            double z = (double) pos.getZ() + 0.5D;

            double h = (double) pos.getY() + 1.0D;

            double r = 0.38D;

            double x1 = x + r;
            double x2 = x - r;
            double z1 = z + r;
            double z2 = z - r;

            SimpleParticleType type;

            switch (stateIn.getValue(FLAME)) {
            case 1:
                type = ParticleTypes.FLAME;
                break;
            case 2:
                type = ParticleTypes.SOUL_FIRE_FLAME;
                break;
            case 3:
            default:
                type = ParticleTypes.FIREWORK;
                break;
            }

            worldIn.addParticle(type, x, y, z, 0.0D, 0.0D, 0.0D);

            worldIn.addParticle(type, x1, h, z, 0.0D, 0.0D, 0.0D);
            worldIn.addParticle(type, x2, h, z, 0.0D, 0.0D, 0.0D);
            worldIn.addParticle(type, x, h, z1, 0.0D, 0.0D, 0.0D);
            worldIn.addParticle(type, x, h, z2, 0.0D, 0.0D, 0.0D);
        }
    }
}
