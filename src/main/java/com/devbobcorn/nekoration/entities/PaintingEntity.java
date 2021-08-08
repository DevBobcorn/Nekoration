package com.devbobcorn.nekoration.entities;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.items.PaletteItem;
import com.devbobcorn.nekoration.client.ClientHelper;
import com.devbobcorn.nekoration.exp.ExpNBTTypes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import java.awt.Color;

public class PaintingEntity extends HangingEntity implements IEntityAdditionalSpawnData {
	public PaintingData data;

	protected PaintingEntity(EntityType<PaintingEntity> type, World world) {
		// Constructor 1: the default one, but not used to create instances in worlds
		super(ModEntityType.PAINTING_TYPE, world);
	}

	public PaintingEntity(World world, BlockPos pos, Direction dir, short w, short h) {
		// Constructor 2: the one for server-side to create PaintingEntity Objects
		super(ModEntityType.$PAINTING_TYPE.get(), world, pos);
		this.setDirection(dir);
		data = new PaintingData(w, h, false, 20021222);
	}

	public PaintingEntity(FMLPlayMessages.SpawnEntity packet, World world) {
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
	public void addAdditionalSaveData(CompoundNBT tag) {
		tag.putByte("Facing", (byte) direction.get2DDataValue());
		PaintingData.writeTo(data, tag);
		super.addAdditionalSaveData(tag);
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT tag) {
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
	public ActionResultType interact(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		World world = player.level;
		if (world.isClientSide) {
			if (stack.getItem() == ModItems.PALETTE.get()){
				//System.out.println("Open Painting using Palette Item.");
				// First get the existing data in this palette...
				CompoundNBT nbt = stack.getTag();
				if (nbt != null && nbt.contains(PaletteItem.ACTIVE, ExpNBTTypes.BYTE_NBT_ID)){
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
				return ActionResultType.SUCCESS;
			} else {
				player.displayClientMessage(new TranslationTextComponent("gui.nekoration.message.paint_with_palette"), true);
				return ActionResultType.PASS;
			}
		}
        return (stack.getItem() == ModItems.PALETTE.get()) ? ActionResultType.SUCCESS : ActionResultType.PASS;
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack(ModItems.PAINTING.get());
	}

	@Override
	public void dropItem(Entity entity) {
		if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerentity = (PlayerEntity) entity;
				if (playerentity.abilities.instabuild) {
					return;
				}
			}

			this.spawnAtLocation(ModItems.PAINTING.get());
		}
	}

	@Override
	public void playPlacementSound() {
		this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		IPacket<?> packet = NetworkHooks.getEntitySpawningPacket(this);
		return packet;
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
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
	public void readSpawnData(PacketBuffer additionalData) {
		// Client receives...
		this.data = new PaintingData(additionalData.readShort(), additionalData.readShort(), additionalData.readVarIntArray(), true, this.getUUID().hashCode());
		this.setPosRaw(additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble());
		this.pos = additionalData.readBlockPos();
		byte dir = additionalData.readByte();
		//this.setDirection(Direction.from2DDataValue(dir)); This will call recalcuateBoundingBox, which isn't what we want
		this.direction = Direction.from2DDataValue(dir);
		this.yRot = (float) (this.direction.get2DDataValue() * 90);
		this.yRotO = this.yRot;
		//this.recalculateBoundingBox(); To use this we'll need the original position data, which we don't have on the client-side
		this.setBoundingBox(new AxisAlignedBB(additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble()));
	}

	@Override
	public void remove(){
		super.remove();
		if (this.level.isClientSide){
			// Don't forget to Delete the cached Image of it...
			data.clearCache(data.getPaintingHash());
		}
	}
}
