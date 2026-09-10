package io.devbobcorn.nekoration.entities;

import javax.annotation.Nullable;

import io.devbobcorn.nekoration.registry.ModEntities;
import io.devbobcorn.nekoration.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WallpaperEntity extends HangingEntity {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM =
            SynchedEntityData.defineId(WallpaperEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Byte> DATA_PART =
            SynchedEntityData.defineId(WallpaperEntity.class, EntityDataSerializers.BYTE);

    public enum Part {
        UPPER(0), LOWER(1), FULL(2);

        private final byte id;

        Part(int id) {
            this.id = (byte) id;
        }

        private static Part fromId(byte id) {
            return id >= 0 && id < values().length ? values()[id] : FULL;
        }
    }

    public WallpaperEntity(EntityType<? extends WallpaperEntity> type, Level level) {
        super(type, level);
    }

    public WallpaperEntity(Level level, BlockPos pos, Direction direction, ItemStack stack, Part part) {
        super(ModEntities.WALLPAPER.get(), level, pos);
        setItem(stack);
        setPart(part);
        setDirection(direction);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ITEM, new ItemStack(ModItems.WALLPAPER.get()));
        builder.define(DATA_PART, Part.FULL.id);
    }

    public ItemStack getItem() {
        return entityData.get(DATA_ITEM).copyWithCount(1);
    }

    private void setItem(ItemStack stack) {
        entityData.set(DATA_ITEM, stack.transmuteCopy(ModItems.WALLPAPER.get(), 1));
    }

    public Part getPart() {
        return Part.fromId(entityData.get(DATA_PART));
    }

    public void setPart(Part part) {
        entityData.set(DATA_PART, part.id);
        recalculateBoundingBox();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (DATA_PART.equals(accessor)) {
            recalculateBoundingBox();
        }
    }

    @Override
    protected AABB calculateBoundingBox(BlockPos pos, Direction direction) {
        Vec3 center = Vec3.atCenterOf(pos).relative(direction, -0.46875D);
        int height = getPart() == Part.FULL ? 2 : 1;
        if (height % 2 == 0) {
            center = center.relative(Direction.UP, 0.5D);
        }
        Direction.Axis axis = direction.getAxis();
        double xSize = axis == Direction.Axis.X ? 0.0625D : 0.875D;
        double zSize = axis == Direction.Axis.Z ? 0.0625D : 0.875D;
        return AABB.ofSize(center, xSize, height, zSize);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        Part part = getPart();
        if (part != Part.FULL && !level().isClientSide()) {
            setPart(part == Part.UPPER ? Part.LOWER : Part.UPPER);
        }
        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("facing", (byte) direction.get2DDataValue());
        tag.putByte("Part", getPart().id);
        tag.put("Item", getItem().save(registryAccess()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        direction = Direction.from2DDataValue(tag.contains("facing") ? tag.getByte("facing") : tag.getByte("Facing"));
        setPart(Part.fromId(tag.getByte("Part")));
        if (tag.contains("Item", CompoundTag.TAG_COMPOUND)) {
            setItem(ItemStack.parseOptional(registryAccess(), tag.getCompound("Item")));
        }
        setDirection(direction);
    }

    @Override
    public void dropItem(@Nullable Entity breaker) {
        if (!level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            return;
        }
        playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F);
        if (!(breaker instanceof Player player) || !player.hasInfiniteMaterials()) {
            spawnAtLocation(getItem());
        }
    }

    @Override
    public void playPlacementSound() {
        playSound(SoundEvents.BOOK_PUT, 1.0F, 1.0F);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, direction.get3DDataValue(), pos);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        setDirection(Direction.from3DDataValue(packet.getData()));
    }

    @Override
    public Vec3 trackingPosition() {
        return Vec3.atLowerCornerOf(pos);
    }

    @Override
    public ItemStack getPickResult() {
        return getItem();
    }
}
