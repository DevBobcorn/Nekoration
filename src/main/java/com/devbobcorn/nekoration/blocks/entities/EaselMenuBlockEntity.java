package com.devbobcorn.nekoration.blocks.entities;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.containers.ContainerContents;
import com.devbobcorn.nekoration.blocks.containers.EaselMenuContainer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EaselMenuBlockEntity extends TileEntity implements INamedContainerProvider {
	public static final int NUMBER_OF_SLOTS = 8;
	public final ContainerContents contents;

	private final ITextComponent[] messages = new ITextComponent[] { StringTextComponent.EMPTY, StringTextComponent.EMPTY, StringTextComponent.EMPTY, StringTextComponent.EMPTY, StringTextComponent.EMPTY, StringTextComponent.EMPTY, StringTextComponent.EMPTY, StringTextComponent.EMPTY };
	private final ItemStack airStack = new ItemStack(Items.AIR);
	public ItemStack[] renderItems = { airStack, airStack, airStack, airStack, airStack, airStack, airStack, airStack };
	private boolean isEditable = true;
	private PlayerEntity playerWhoMayEdit;
	private DyeColor[] textColors = { DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY };
	private boolean isGlowing;

	public final boolean white; // Not saved or synced between clients and server, just temporarily stores the variant type...

	public EaselMenuBlockEntity() {
		super(ModTileEntityType.EASEL_MENU_TYPE.get());
		white = false;
		isGlowing = false;
		contents = ContainerContents.createForTileEntity(NUMBER_OF_SLOTS, this::canPlayerAccessInventory, this::setChanged, this);
	}

	public EaselMenuBlockEntity(boolean w) {
		super(ModTileEntityType.EASEL_MENU_TYPE.get());
		white = w;
		isGlowing = false;
		contents = ContainerContents.createForTileEntity(NUMBER_OF_SLOTS, this::canPlayerAccessInventory, this::setChanged, this);
	}

	// Return true if the given player is able to use this block. In this case it
	// checks that
	// 1) the world tileentity hasn't been replaced in the meantime, and
	// 2) the player isn't too far away from the centre of the block
	public boolean canPlayerAccessInventory(PlayerEntity player) {
		if (this.level.getBlockEntity(this.worldPosition) != this)
			return false;
		final double X_CENTRE_OFFSET = 0.5;
		final double Y_CENTRE_OFFSET = 0.5;
		final double Z_CENTRE_OFFSET = 0.5;
		final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
		return player.distanceToSqr(worldPosition.getX() + X_CENTRE_OFFSET, worldPosition.getY() + Y_CENTRE_OFFSET,
				worldPosition.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
	}

	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		for (int i = 0; i < 8; ++i) {
			String s = ITextComponent.Serializer.toJson(this.messages[i]);
			tag.putString("Text" + (i + 1), s);
		}
		
		for (int i = 0; i < 8; ++i) {
			tag.putString("Color" + i, this.textColors[i].getName());
		}
		CompoundNBT inventoryNBT = contents.serializeNBT();
		tag.put("Contents", inventoryNBT);
		tag.putBoolean("Glowing", isGlowing);
		return tag;
	}

	public void load(BlockState state, CompoundNBT tag) {
		this.isEditable = false;
		super.load(state, tag);
		//Items...
		CompoundNBT inventoryNBT = tag.getCompound("Contents");
		contents.deserializeNBT(inventoryNBT);
		if (contents.getContainerSize() != NUMBER_OF_SLOTS)
			throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected.");
		// Texts...
		for (int i = 0;i < 8;++i)
			this.textColors[i] = DyeColor.byName(tag.getString("Color" + i), DyeColor.GRAY);

		for (int i = 0; i < 8; ++i) {
			String s = tag.getString("Text" + (i + 1));
			ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(s.isEmpty() ? "\"\"" : s);
			if (this.level instanceof ServerWorld) {
				try {
					this.messages[i] = TextComponentUtils.updateForEntity(
							this.createCommandSourceStack((ServerPlayerEntity) null), itextcomponent, (Entity) null, 0);
				} catch (CommandSyntaxException commandsyntaxexception) {
					this.messages[i] = itextcomponent;
				}
			} else {
				this.messages[i] = itextcomponent;
			}
		}
		for (int i = 0;i < 8;i++)
			renderItems[i] = this.contents.getItem(i);
		isGlowing = tag.getBoolean("Glowing");
	}

	public ITextComponent getMessage(int line) {
		return this.messages[line];
	}

	public void setMessage(int line, ITextComponent text) {
		this.messages[line] = text;
	}

	public ITextComponent[] getMessages(){
		return this.messages;
	}

	public boolean getGlowing(){
		return this.isGlowing;
	}

	public void setGlowing(boolean glow){
		this.isGlowing = glow;
	}

	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.worldPosition, 2020, this.getUpdateTag());
	}

	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}

	public boolean onlyOpCanSetNbt() {
		return true;
	}

	public boolean isEditable() {
		return this.isEditable;
	}

	@OnlyIn(Dist.CLIENT)
	public void setEditable(boolean editable) {
		this.isEditable = editable;
		if (!editable) {
			this.playerWhoMayEdit = null;
		}
	}

	public void setAllowedPlayerEditor(PlayerEntity player) {
		this.playerWhoMayEdit = player;
	}

	public PlayerEntity getPlayerWhoMayEdit() {
		return this.playerWhoMayEdit;
	}

	public boolean executeClickCommands(PlayerEntity player) {
		for (ITextComponent itextcomponent : this.messages) {
			Style style = itextcomponent == null ? null : itextcomponent.getStyle();
			if (style != null && style.getClickEvent() != null) {
				ClickEvent clickevent = style.getClickEvent();
				if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					player.getServer().getCommands().performCommand(
							this.createCommandSourceStack((ServerPlayerEntity) player), clickevent.getValue());
				}
			}
		}

		return true;
	}

	public CommandSource createCommandSourceStack(@Nullable ServerPlayerEntity player) {
		String s = player == null ? "EaselMenu" : player.getName().getString();
		ITextComponent itextcomponent = (ITextComponent) (player == null ? new StringTextComponent("EaselMenu")
				: player.getDisplayName());
		return new CommandSource(ICommandSource.NULL, Vector3d.atCenterOf(this.worldPosition), Vector2f.ZERO,
				(ServerWorld) this.level, 2, s, itextcomponent, this.level.getServer(), player);
	}

	public DyeColor[] getColor() {
		return this.textColors;
	}

	public boolean setColor(DyeColor[] color) {
		if (color != this.getColor()) {
			this.textColors = color;
			this.setChanged();
			this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
			return true;
		} else {
			return false;
		}
	}

	// Container...
	public void dropAllContents(World world, BlockPos blockPos) {
		InventoryHelper.dropContents(world, blockPos, contents);
	}

	@Nullable
	@Override
	public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return EaselMenuContainer.createContainerServerSide(windowID, playerInventory, contents, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("container." + Nekoration.MODID + ".easel_menu");
	}
}