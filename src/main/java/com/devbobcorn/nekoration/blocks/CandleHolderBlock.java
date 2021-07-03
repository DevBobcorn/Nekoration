package com.devbobcorn.nekoration.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.List;
import java.util.Random;

import com.devbobcorn.nekoration.particles.FlameParticleData;

public class CandleHolderBlock extends Block {
	// public static final IntegerProperty LEVEL =
	// BlockStateProperties.LEVEL_CAULDRON;

	public CandleHolderBlock(Properties settings) {
		super(settings);
	}

	// This method is called at random intervals - typically used by block which
	// produce occasional effects, like
	// smoke from a torch or stars from a portal.
	// In this case, we use it to spawn two different types of Particle- vanilla, or
	// custom.
	// Don't forget @OnlyIn(Dist.CLIENT) otherwise this will crash on a dedicated
	// server.
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		// Usually, it is desirable to spawn particles on the client only.
		// It is possible for the server to send a "spawn particle" command to the
		// client, but this requires a lot of bandwidth if
		// you are rendering even modest amounts of particles.
		// See ServerWorld.spawnParticle
		// Instead, send a custom message to the client and spawn lots of particles in
		// response to the single message
		// (see mbe60 MessageHandlerOnClient for an example).
		if (worldIn.isClientSide) { // is this on the client side? should always be true...
			// first example:
			// spawn a vanilla particle of LAVA type (smoke from lava)
			// The starting position is the [x,y,z] of the tip of the pole (i.e. at [0.5,
			// 1.0, 0.5] relative to the block position)
			// Set the initial velocity to zero.
			// When the particle is spawned, it will automatically add a random amount of
			// velocity - see EntityLavaFX constructor and
			// Particle constructor. This can be a nuisance if you don't want your Particle
			// to have a random starting velocity!

			// starting position = top of the pole
			double xpos = pos.getX() + 0.5;
			double ypos = pos.getY() + 1.0;
			double zpos = pos.getZ() + 0.5;
			double velocityX = 0; // increase in x position every tick
			double velocityY = 0; // increase in y position every tick;
			double velocityZ = 0; // increase in z position every tick

			final boolean IGNORE_RANGE_CHECK = false; // if true, always render particle regardless of how far away the
														// player is
			final double PERCENT_CHANCE_OF_LAVA_SPAWN = 10; // only spawn Lava particles occasionally (visually
															// distracting if too many)

			if (rand.nextDouble() < PERCENT_CHANCE_OF_LAVA_SPAWN / 100.0) {
				worldIn.addParticle(ParticleTypes.LAVA, IGNORE_RANGE_CHECK, xpos, ypos, zpos, velocityX, velocityY,
						velocityZ);
			}

			// second example:
			// spawn a custom Particle ("FlameParticle") with a texture we have added
			// ourselves.
			// FlameParticle also has custom movement and collision logic - it moves in a
			// straight line until it hits something.
			// To make it more interesting, the stream of fireballs will target the nearest
			// non-player entity within 16 blocks at
			// the height of the pole or above.
			xpos = pos.getX() + 0.5;
			ypos = pos.getY() + 1.0;
			zpos = pos.getZ() + 0.5;

			// add a small amount of wobble to stop particles rendering directly on top of
			// each other (z-fighting) which makes them look weird
			final double POSITION_WOBBLE_AMOUNT = 0.01;
			xpos += POSITION_WOBBLE_AMOUNT * (rand.nextDouble() - 0.5);
			zpos += POSITION_WOBBLE_AMOUNT * (rand.nextDouble() - 0.5);

			MonsterEntity mobTarget = getNearestTargetableMob(worldIn, xpos, ypos, zpos);
			Vector3d fireballDirection;
			if (mobTarget == null) { // no target: fire straight upwards
				fireballDirection = new Vector3d(0.0, 1.0, 0.0);
			} else { // otherwise: aim at the mob
				// the direction that the fireball needs to travel is calculated from the
				// starting point (the pole) and the
				// end point (the mob's eyes). A bit of googling on vector maths will show you
				// that you calculate this by
				// 1) subtracting the start point from the end point
				// 2) normalising the vector (if you don't do this, then the fireball will fire
				// faster if the mob is further away

				final float PARTIAL_TICKS = 1.0F;
				fireballDirection = mobTarget.getEyePosition(PARTIAL_TICKS).subtract(xpos, ypos, zpos); // NB this
																										// method only
																										// works on
																										// client side
				fireballDirection = fireballDirection.normalize();
			}

			// the velocity vector is now calculated as the fireball's speed multiplied by
			// the direction vector.

			final double SPEED_IN_BLOCKS_PER_SECOND = 2.0;
			final double TICKS_PER_SECOND = 20;
			final double SPEED_IN_BLOCKS_PER_TICK = SPEED_IN_BLOCKS_PER_SECOND / TICKS_PER_SECOND;

			velocityX = SPEED_IN_BLOCKS_PER_TICK * fireballDirection.x; // how much to increase the x position every
																		// tick
			velocityY = SPEED_IN_BLOCKS_PER_TICK * fireballDirection.y; // how much to increase the y position every
																		// tick
			velocityZ = SPEED_IN_BLOCKS_PER_TICK * fireballDirection.z; // how much to increase the z position every
																		// tick

			Color tint = getTint(pos);
			double diameter = getDiameter(pos);

			FlameParticleData flameParticleData = new FlameParticleData(tint, diameter);
			worldIn.addParticle(flameParticleData, IGNORE_RANGE_CHECK, xpos, ypos, zpos, velocityX, velocityY,
					velocityZ);
		}
	}

	// choose a semi-random colour based on the block's position
	// the texture has basically no blue in it so we don't bother varying that
	private Color getTint(BlockPos blockPos) {
		Color[] tints = { new Color(1.00f, 1.00f, 1.0f), // no tint (full white)
				new Color(1.00f, 0.75f, 1.0f), // redder
				new Color(1.00f, 0.50f, 1.0f), // much redder
				new Color(0.75f, 1.00f, 1.0f), // greener
				new Color(0.50f, 1.00f, 1.0f), // much greener
		};

		Random random = new Random(blockPos.hashCode());
		random.nextInt();
		random.nextInt(); // iterate a couple of times (the first nextInt() isn't very random)
		int idx = random.nextInt(tints.length);
		return tints[idx];
	}

	// choose a semi-random size based on the block's position
	private double getDiameter(BlockPos blockPos) {
		Random random = new Random(blockPos.hashCode());
		random.nextDouble();
		random.nextDouble(); // iterate a couple of times (the first nextDouble() isn't very random)

		final double MIN_DIAMETER = 0.05;
		final double MAX_DIAMETER = 0.35;
		return MIN_DIAMETER + (MAX_DIAMETER - MIN_DIAMETER) * random.nextDouble();
	}

	/**
	 * Returns the nearest targetable mob to the indicated [xpos, ypos, zpos].
	 * 
	 * @param world
	 * @param xpos
	 *            [x,y,z] position to s
	 * @param ypos
	 * @param zpos
	 * @return the nearest mob, or null if none within range.
	 */
	private MonsterEntity getNearestTargetableMob(World world, double xpos, double ypos, double zpos) {
		final double TARGETING_DISTANCE = 16;
		AxisAlignedBB targetRange = new AxisAlignedBB(xpos - TARGETING_DISTANCE, ypos, zpos - TARGETING_DISTANCE,
				xpos + TARGETING_DISTANCE, ypos + TARGETING_DISTANCE, zpos + TARGETING_DISTANCE);

		List<MonsterEntity> allNearbyMobs = world.getEntitiesOfClass(MonsterEntity.class, targetRange);
		MonsterEntity nearestMob = null;
		double closestDistance = Double.MAX_VALUE;
		for (MonsterEntity nextMob : allNearbyMobs) {
			double nextClosestDistance = nextMob.distanceToSqr(xpos, ypos, zpos);
			if (nextClosestDistance < closestDistance) {
				closestDistance = nextClosestDistance;
				nearestMob = nextMob;
			}
		}
		return nearestMob;
	}
}
