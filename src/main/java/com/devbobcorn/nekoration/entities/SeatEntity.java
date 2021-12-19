package com.devbobcorn.nekoration.entities;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

public class SeatEntity extends Entity {
    private BlockPos source;

    public SeatEntity(Level world) {
        super(ModEntityType.SEAT_TYPE, world);
        this.noPhysics = true;
    }

    private SeatEntity(Level world, BlockPos source, double yOffset) {
        this(world);
        this.source = source;
        this.setPos(source.getX() + 0.5, source.getY() + yOffset, source.getZ() + 0.5);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        if (source == null) {
            source = this.blockPosition();
        }
        if (!this.level.isClientSide) {
            if (this.getPassengers().isEmpty() || this.level.isEmptyBlock(source)) {
                this.remove(RemovalReason.KILLED);
                level.updateNeighbourForOutputSignal(blockPosition(), level.getBlockState(blockPosition()).getBlock());
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.0;
    }

    public BlockPos getSource() {
        return source;
    }

    @Override
    protected boolean canAddPassenger(Entity entity) {
        return true;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static InteractionResult create(Level world, BlockPos pos, double yOffset, Player player) {
        if (!world.isClientSide) {
            List<SeatEntity> seats = world.getEntitiesOfClass(SeatEntity.class, new AABB(pos.getX(),
                    pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0));
            if (seats.isEmpty()) {
                SeatEntity seat = new SeatEntity(world, pos, yOffset);
                world.addFreshEntity(seat);
                player.startRiding(seat, false);
            }
        }
        return InteractionResult.SUCCESS;
    }
}