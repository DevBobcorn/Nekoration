package com.devbobcorn.nekoration.entities;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.items.PaintingItem;
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
import java.util.UUID;

public class PaintingEntity extends HangingEntity implements IEntityAdditionalSpawnData {
	public PaintingData data;

	protected PaintingEntity(EntityType<PaintingEntity> type, Level world) {
		// Constructor 1: the default one, but not used to create instances in worlds
		super(ModEntityType.PAINTING_TYPE, world);
	}

	public PaintingEntity(Level world, BlockPos pos, Direction dir, short w, short h) {
		// Constructor 2: the one for server-side to create brand-new PaintingEntity Objects
		super(ModEntityType.$PAINTING_TYPE.get(), world, pos);
		this.setDirection(dir);
		data = new PaintingData(w, h, false, this.uuid);
	}

	public PaintingEntity(Level world, BlockPos pos, Direction dir, short w, short h, UUID existingId) {
		// Constructor 3: the one for server-side to duplicate PaintingEntity Objects
		super(ModEntityType.$PAINTING_TYPE.get(), world, pos);
		this.setDirection(dir);
		data = new PaintingData(w, h, false, existingId);
	}

	public PaintingEntity(FMLPlayMessages.SpawnEntity packet, Level world) {
		// Constructor 3: the one for client-side, creating instances with data packets from the Server
		// Enable by adding 'setCustomClientFactory' when building the entity type
		super(ModEntityType.PAINTING_TYPE, world);
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
		data = PaintingData.readFrom(tag, this.uuid);
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
			} else if (stack.getItem() != ModItems.PAINTING.get()) {
				player.displayClientMessage(new TranslatableComponent("gui.nekoration.message.paint_with_palette"), true);
			}
		} else {
			if (stack.getItem() == ModItems.PAINTING.get()){
				if (PaintingItem.getType(stack) == PaintingItem.Type.BLANK.id) { // A blank painting...
					// Turn it into a link to itself...
					PaintingItem.setLink(stack, (short)(getWidth() / 16), (short)(getHeight() / 16), data.getUUID(), getId());
				}
			}
		}
        return InteractionResult.SUCCESS;
	}


	public ItemStack getPickItem(){
		ItemStack result = new ItemStack(ModItems.PAINTING.get());
		if (getWidth() <= 96 && getHeight() <= 96) // The painting with its content
			PaintingItem.setContent(result, (short)(getWidth() / 16), (short)(getHeight() / 16), data.getUUID(), data.getPixels());
		else {
			// Create a link to this Painting Entity...
			PaintingItem.setLink(result, (short)(getWidth() / 16), (short)(getHeight() / 16), data.getUUID(), getId());
		}
		return result;
	}

	@Override
	public ItemStack getPickedResult(HitResult target) {
		return getPickItem();
	}

	public ItemStack getDropItem(){
		ItemStack result = new ItemStack(ModItems.PAINTING.get());
		if (getWidth() <= 96 && getHeight() <= 96) // The painting with its content
			PaintingItem.setContent(result, (short)(getWidth() / 16), (short)(getHeight() / 16), data.getUUID(), data.getPixels());
		else {
			// Create a blank painting of the same size...
			PaintingItem.setSize(result, (short)(getWidth() / 16), (short)(getHeight() / 16));
		}
		return result;
	}

	@Override
	public void dropItem(Entity entity) {
		if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
			if (entity instanceof Player) {
				Player playerentity = (Player) entity;
				if (playerentity.getAbilities().instabuild) {
					return;
				}
			}
			this.spawnAtLocation(getDropItem());
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
		// We cannot get the same bounding box output with different position inputs,
		// because server makes adjustments to that. As a result, we need to pass the
		// right position and hitbox straight over to the clients...
		buffer.writeShort(data.getWidth());
		buffer.writeShort(data.getHeight());
		buffer.writeVarIntArray(data.getPixels());
		buffer.writeUUID(data.getUUID());
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
		this.data = new PaintingData(additionalData.readShort(), additionalData.readShort(), additionalData.readVarIntArray(), true, additionalData.readUUID());
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
	public void onRemovedFromWorld(){
		// In 1.17, remove(Entity.RemovalReason reason) is called on server-side only, and this method is instead called on clients...
		super.onRemovedFromWorld();
		// Don't forget to Delete the cached Image of it...
		data.clearCache(data.getPaintingHash());
	}
}
