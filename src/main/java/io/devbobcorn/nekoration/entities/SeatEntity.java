package io.devbobcorn.nekoration.entities;

import java.util.List;

import io.devbobcorn.nekoration.blocks.furniture.ChairBlock;
import io.devbobcorn.nekoration.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Invisible mount entity used to let players sit on chairs.
 */
public class SeatEntity extends Entity {
    private BlockPos sourcePos;

    public SeatEntity(EntityType<? extends SeatEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    private SeatEntity(EntityType<? extends SeatEntity> type, Level level, BlockPos sourcePos, double yOffset) {
        this(type, level);
        this.sourcePos = sourcePos.immutable();
        setPos(sourcePos.getX() + 0.5D, sourcePos.getY() + yOffset + 0.5D, sourcePos.getZ() + 0.5D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("SourceX") && tag.contains("SourceY") && tag.contains("SourceZ")) {
            this.sourcePos = new BlockPos(tag.getInt("SourceX"), tag.getInt("SourceY"), tag.getInt("SourceZ"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (sourcePos != null) {
            tag.putInt("SourceX", sourcePos.getX());
            tag.putInt("SourceY", sourcePos.getY());
            tag.putInt("SourceZ", sourcePos.getZ());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            return;
        }
        if (sourcePos == null) {
            sourcePos = blockPosition();
        }
        if (getPassengers().isEmpty() || !(level().getBlockState(sourcePos).getBlock() instanceof ChairBlock)) {
            discard();
        }
    }

    public static InteractionResult trySit(Level level, BlockPos pos, double yOffset, Player player) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        AABB seatBox = new AABB(pos);
        List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, seatBox);
        if (seats.isEmpty()) {
            SeatEntity seat = new SeatEntity(ModEntities.SEAT.get(), level, pos, yOffset);
            level.addFreshEntity(seat);
            player.startRiding(seat, false);
        } else if (!seats.get(0).hasPassenger(player)) {
            player.startRiding(seats.get(0), false);
        }
        return InteractionResult.CONSUME;
    }
}
