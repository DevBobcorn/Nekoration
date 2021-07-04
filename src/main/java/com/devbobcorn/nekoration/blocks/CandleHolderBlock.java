package com.devbobcorn.nekoration.blocks;

import java.util.Random;

import com.devbobcorn.nekoration.common.VanillaCompat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CandleHolderBlock extends DyeableBlock {
	public static final IntegerProperty FLAME = BlockStateProperties.AGE_3;

	public CandleHolderBlock(Properties settings) {
		super(settings);
	}

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(COLOR, FLAME);
	}

	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);

		if (world.isClientSide) {
			return (VanillaCompat.FLAME_ITEMS.containsKey(itemStack.getItem())) ? super.use(state, world, pos, player, hand, hit)
					: ActionResultType.PASS;
		}
		
		if (VanillaCompat.FLAME_ITEMS.containsKey(itemStack.getItem())) {
			world.setBlock(pos, state.setValue(FLAME, VanillaCompat.FLAME_ITEMS.get(itemStack.getItem())), 3);
			return ActionResultType.CONSUME;
		}
		return super.use(state, world, pos, player, hand, hit);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
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

			BasicParticleType type;

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
