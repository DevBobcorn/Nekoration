package com.devbobcorn.nekoration.entities;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.items.PaletteItem;
import com.devbobcorn.nekoration.utils.TagTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
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
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fmllegacy.network.FMLPlayMessages;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import com.devbobcorn.nekoration.client.ClientHelper;

import java.awt.Color;

public class PaintingEntity extends HangingEntity implements IEntityAdditionalSpawnData {
	public PaintingData data;

	protected PaintingEntity(EntityType<PaintingEntity> type, Level world) {
		// Constructor 1: the default one, but not used to create instances in worlds
		super(ModEntityType.PAINTING_TYPE, world);
	}

	public PaintingEntity(Level world, BlockPos pos, Direction dir, short w, short h) {
		// Constructor 2: the one for server-side to create PaintingEntity Objects
		super(ModEntityType.$PAINTING_TYPE.get(), world, pos);
		this.setDirection(dir);
		data = new PaintingData(w, h, false, 20021222);
	}

	public PaintingEntity(FMLPlayMessages.SpawnEntity packet, Level world) {
		// Constructor 3: the one for client-side, creating instances with data packets
		// from the Server
		// Enable by adding 'setCustomClientFactory' when building the entity type
		super(ModEntityType.PAINTING_TYPE, world);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		// this.entityData.define(Dir, (byte)0);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		tag.putByte("Facing", (byte) direction.get2DDataValue());
		PaintingData.writeTo(data, tag);
		super.addAdditionalSaveData(tag);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		this.direction = Direction.from2DDataValue((int) (tag.getByte("Facing")));
		data = PaintingData.readFrom(tag);
		super.readAdditionalSaveData(tag);
		this.setDirection(this.direction);
	}

	@Override
	public int getWidth() {
		return (data == null) ? 1 : data.getWidth();
	}

	@Override
	public int getHeight() {
		return (data == null) ? 1 : data.getHeight();
	}

	@SuppressWarnings("deprecation")
	public InteractionResult interact(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		Level world = player.level;
		if (world.isClientSide) {
			if (stack.getItem() == ModItems.PALETTE.get()){
				//System.out.println("Open Painting using Palette Item.");
				// First get the existing data in this palette...
				CompoundTag nbt = stack.getTag();
				if (nbt != null && nbt.contains(PaletteItem.ACTIVE, TagTypes.BYTE_NBT_ID)){
					byte a = nbt.getByte(PaletteItem.ACTIVE);
					int[] c = nbt.getIntArray(PaletteItem.COLORS);
					Color[] col = new Color[6];
					for (int i = 0;i < 6;i++){
						col[i] = new Color(NekoColors.getRed(c[i]), NekoColors.getGreen(c[i]), NekoColors.getBlue(c[i]));
					}
					DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
						//Minecraft.getInstance().setScreen(new PaletteScreen(hand, a, col));
						ClientHelper.showPaintingScreen(this.getId(), a, col);
						//System.out.println("Open Painting GUI1.");
					});
				} else ClientHelper.showPaintingScreen(this.getId());
			} else {
				player.displayClientMessage(new TranslatableComponent("gui.nekoration.message.paint_with_palette"), true);
			}
		}
        return InteractionResult.SUCCESS;
	}

	@Override
	public ItemStack getPickedResult(HitResult target) {
		return new ItemStack(ModItems.PAINTING.get());
	}

	@Override
	public void dropItem(Entity entity) {
		if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
			/*
			if (entity instanceof Player) {
				Player playerentity = (Player) entity;
				if (playerentity.abilities.instabuild) {
					return;
				}
			}
			*/ // TODO
			this.spawnAtLocation(ModItems.PAINTING.get());
		}
	}

	@Override
	public void playPlacementSound() {
		this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		Packet<?> packet = NetworkHooks.getEntitySpawningPacket(this);
		return packet;
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		// Server sends...
		// We need the size direction on client-side to get the right Bounding box, so we pass 'em over
		buffer.writeShort(data.getWidth());
		buffer.writeShort(data.getHeight());
		buffer.writeVarIntArray(data.getPixels());
		// Also a more accurate position, maybe...
		buffer.writeDouble(this.position().x);
		buffer.writeDouble(this.position().y);
		buffer.writeDouble(this.position().z);
		buffer.writeBlockPos(this.blockPosition());
		buffer.writeByte(direction.get2DDataValue());
		buffer.writeDouble(this.getBoundingBox().minX);
		buffer.writeDouble(this.getBoundingBox().minY);
		buffer.writeDouble(this.getBoundingBox().minZ);
		buffer.writeDouble(this.getBoundingBox().maxX);
		buffer.writeDouble(this.getBoundingBox().maxY);
		buffer.writeDouble(this.getBoundingBox().maxZ);
		//System.out.println("SERVER: " + this.getBoundingBox() + " isClient: " + this.level.isClientSide);
		//System.out.println(this.position());
		//System.out.println(this.pos);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		// Client receives...
		this.data = new PaintingData(additionalData.readShort(), additionalData.readShort(), additionalData.readVarIntArray(), true, this.getUUID().hashCode());
		this.setPosRaw(additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble());
		this.pos = additionalData.readBlockPos();
		byte dir = additionalData.readByte();
		//this.setDirection(Direction.from2DDataValue(dir)); This will call recalcuateBoundingBox, which isn't what we want
		this.direction = Direction.from2DDataValue(dir);
		this.setYRot((float) (this.direction.get2DDataValue() * 90));
		this.yRotO = this.getYRot();
		//this.recalculateBoundingBox(); To use this we'll need the original position data, which we don't have on the client-side
		this.setBoundingBox(new AABB(additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble()));
	}

	@Override
	public void remove(Entity.RemovalReason reason){
		super.remove(reason);
		if (this.level.isClientSide){
			// Don't forget to Delete the cached Image of it...
			data.clearCache(data.getPaintingHash());
		}
	}
}
