package io.devbobcorn.nekoration.entities;

import java.awt.Color;
import java.util.UUID;

import javax.annotation.Nullable;

import io.devbobcorn.nekoration.NekoColors;
import io.devbobcorn.nekoration.client.ClientHelper;
import io.devbobcorn.nekoration.items.PaintingItem;
import io.devbobcorn.nekoration.items.PaletteItem;
import io.devbobcorn.nekoration.registry.ModEntities;
import io.devbobcorn.nekoration.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class PaintingEntity extends HangingEntity implements IEntityWithComplexSpawn {
    public PaintingData data;

    public PaintingEntity(EntityType<? extends PaintingEntity> type, Level level) {
        // Constructor 1: the default one, used to create instances on the client-side
        // (they'll get initialized by the spawn data right after)...
        super(ModEntities.PAINTING.get(), level);
    }
    public PaintingEntity(Level level, BlockPos pos, Direction dir, short w, short h) {
        // Constructor 2: the one for server-side to create brand-new PaintingEntity Objects
        // w and h are in pixels(1 block = 16 pixels)...
        super(ModEntities.PAINTING.get(), level, pos);
        this.setDirection(dir);
        this.data = new PaintingData(w, h, false, this.uuid);
    }

    public PaintingEntity(Level level, BlockPos pos, Direction dir, short w, short h, UUID existingId) {
        // Constructor 3: the one for server-side to duplicate PaintingEntity Objects
        super(ModEntities.PAINTING.get(), level, pos);
        this.setDirection(dir);
        this.data = new PaintingData(w, h, false, existingId);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("Facing", (byte) this.direction.get2DDataValue());
        PaintingData.writeTo(this.data, tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        this.direction = Direction.from2DDataValue(tag.getByte("Facing"));
        this.data = PaintingData.readFrom(tag, this.uuid);
        super.readAdditionalSaveData(tag);
        this.setDirection(this.direction);
    }

    /** The painting size in blocks(1 block = 16 pixels), used for the bounding box. */
    public int getWidth() {
        return (this.data == null) ? 1 : this.data.getWidth() / 16;
    }

    /** The painting size in blocks(1 block = 16 pixels), used for the bounding box. */
    public int getHeight() {
        return (this.data == null) ? 1 : this.data.getHeight() / 16;
    }

    @Override
    protected AABB calculateBoundingBox(BlockPos pos, Direction direction) {
        int width = getWidth();
        int height = getHeight();
        float f = 0.46875F;
        Vec3 vec3 = Vec3.atCenterOf(pos).relative(direction, -f);
        double d0 = offsetForPaintingSize(width);
        double d1 = offsetForPaintingSize(height);
        Direction counterClockwise = direction.getCounterClockWise();
        Vec3 vec31 = vec3.relative(counterClockwise, d0).relative(Direction.UP, d1);
        Direction.Axis axis = direction.getAxis();
        double d2 = axis == Direction.Axis.X ? 0.0625 : width;
        double d3 = height;
        double d4 = axis == Direction.Axis.Z ? 0.0625 : width;
        return AABB.ofSize(vec31, d2, d3, d4);
    }

    private double offsetForPaintingSize(int size) {
        return size % 2 == 0 ? 0.5 : 0.0;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Level level = player.level();
        if (level.isClientSide()) {
            if (stack.getItem() == ModItems.PALETTE.get()) {
                // First get the existing data in this palette...
                CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                if (tag.contains(PaletteItem.ACTIVE)) {
                    byte active = tag.getByte(PaletteItem.ACTIVE);
                    int[] colors = tag.getIntArray(PaletteItem.COLORS);
                    Color[] col = new Color[6];
                    for (int i = 0; i < 6; i++) {
                        col[i] = new Color(NekoColors.getRed(colors[i]), NekoColors.getGreen(colors[i]), NekoColors.getBlue(colors[i]));
                    }
                    ClientHelper.showPaintingScreen(this.getId(), active, col);
                } else {
                    ClientHelper.showPaintingScreen(this.getId());
                }
            } else if (stack.getItem() != ModItems.PAINTING.get()) {
                player.displayClientMessage(Component.translatable("gui.nekoration.message.paint_with_palette"), true);
            }
        } else {
            if (stack.getItem() == ModItems.PAINTING.get()
                    && PaintingItem.getType(stack) == PaintingItem.Type.BLANK.id) { // A blank painting...
                // Turn it into a link to itself...
                PaintingItem.setLink(stack, (short) (this.data.getWidth() / 16), (short) (this.data.getHeight() / 16), this.data.getUUID(), this.getId());
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private ItemStack getPickItem() {
        ItemStack result = new ItemStack(ModItems.PAINTING.get());
        if (this.data == null) {
            PaintingItem.setSize(result, (short) 1, (short) 1);
            return result;
        }
        if (this.data.getWidth() <= 96 && this.data.getHeight() <= 96) { // The painting with its content
            PaintingItem.setContent(result, (short) (this.data.getWidth() / 16), (short) (this.data.getHeight() / 16), this.data.getUUID(), this.data.getPixels());
        } else {
            // Create a link to this Painting Entity...
            PaintingItem.setLink(result, (short) (this.data.getWidth() / 16), (short) (this.data.getHeight() / 16), this.data.getUUID(), this.getId());
        }
        return result;
    }

    @Override
    public ItemStack getPickResult() {
        return getPickItem();
    }

    public ItemStack getDropItem() {
        ItemStack result = new ItemStack(ModItems.PAINTING.get());
        if (this.data == null) {
            PaintingItem.setSize(result, (short) 1, (short) 1);
            return result;
        }
        if (this.data.getWidth() <= 96 && this.data.getHeight() <= 96) { // The painting with its content
            PaintingItem.setContent(result, (short) (this.data.getWidth() / 16), (short) (this.data.getHeight() / 16), this.data.getUUID(), this.data.getPixels());
        } else {
            // Create a blank painting of the same size...
            PaintingItem.setSize(result, (short) (this.data.getWidth() / 16), (short) (this.data.getHeight() / 16));
        }
        return result;
    }

    @Override
    public void dropItem(@Nullable Entity entity) {
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (entity instanceof Player player && player.hasInfiniteMaterials()) {
                return;
            }
            this.spawnAtLocation(getDropItem());
        }
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
    }

    /**
     * Sets the location and rotation of the entity in the world.
     */
    @Override
    public void moveTo(double x, double y, double z, float yaw, float pitch) {
        this.setPos(x, y, z);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        this.setPos(x, y, z);
    }

    @Override
    public Vec3 trackingPosition() {
        return Vec3.atLowerCornerOf(this.pos);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.setDirection(Direction.from3DDataValue(packet.getData()));
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        // Sync the painting data to the clients...
        buffer.writeShort(this.data.getWidth());
        buffer.writeShort(this.data.getHeight());
        buffer.writeVarIntArray(this.data.getPixels());
        buffer.writeUUID(this.data.getUUID());
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        // Client receives...
        this.data = new PaintingData(additionalData.readShort(), additionalData.readShort(), additionalData.readVarIntArray(), true, additionalData.readUUID());
        // The bounding box got calculated with default width/height(data was null)...
        // Now that we've got the real size, recalculate it...
        this.recalculateBoundingBox();
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        if (this.level().isClientSide() && this.data != null) {
            // Don't forget to delete the cached image of it...
            this.data.clearCache(this.data.getPaintingHash());
        }
    }
}
