package com.devbobcorn.nekoration.blocks.entities;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.Nekoration;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//public class EaselMenuBlockEntity extends BlockEntity implements net.minecraft.world.Container, net.minecraft.world.MenuProvider {
public class EaselMenuBlockEntity extends BlockEntity {
	public static final int NUMBER_OF_SLOTS = 8;
	public final SimpleContainer contents;

	private final Component[] messages = new Component[] { TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY };
	private final ItemStack airStack = new ItemStack(Items.AIR);
	public ItemStack[] renderItems = { airStack, airStack, airStack, airStack, airStack, airStack, airStack, airStack };
	private boolean isEditable = true;
	private Player playerWhoMayEdit;
	private DyeColor[] textColors = { DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY };
	private boolean isGlowing;

	public final boolean white; // Not saved or synced between clients and server, just temporarily stores the variant type...

	public EaselMenuBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityType.EASEL_MENU_TYPE.get(), pos, state);
		white = false;
		isGlowing = false;
		contents = new SimpleContainer(NUMBER_OF_SLOTS);
	}

	public EaselMenuBlockEntity(boolean w, BlockPos pos, BlockState state) {
		super(ModBlockEntityType.EASEL_MENU_TYPE.get(), pos, state);
		white = w;
		isGlowing = false;
		contents = new SimpleContainer(NUMBER_OF_SLOTS);
	}
	/*

	// Return true if the given player is able to use this block. In this case it
	// checks that
	// 1) the world tileentity hasn't been replaced in the meantime, and
	// 2) the player isn't too far away from the centre of the block
	public boolean canPlayerAccessInventory(Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this)
			return false;
		final double X_CENTRE_OFFSET = 0.5;
		final double Y_CENTRE_OFFSET = 0.5;
		final double Z_CENTRE_OFFSET = 0.5;
		final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
		return player.distanceToSqr(worldPosition.getX() + X_CENTRE_OFFSET, worldPosition.getY() + Y_CENTRE_OFFSET,
				worldPosition.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
	}

	public CompoundTag save(CompoundTag tag) {
		super.save(tag);

		for (int i = 0; i < 8; ++i) {
			String s = Component.Serializer.toJson(this.messages[i]);
			tag.putString("Text" + (i + 1), s);
		}
		
		for (int i = 0; i < 8; ++i) {
			tag.putString("Color" + i, this.textColors[i].getName());
		}
		ListTag inventoryNBT = contents.createTag();
		tag.put("Contents", inventoryNBT);
		tag.putBoolean("Glowing", isGlowing);
		return tag;
	}

	public void load(CompoundTag tag) {
		this.isEditable = false;
		super.load(tag);
		//Items...
		ListTag inventoryNBT = tag.getList("Contents", NUMBER_OF_SLOTS);
		contents.fromTag(inventoryNBT);
		if (contents.getContainerSize() != NUMBER_OF_SLOTS)
			throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected.");
		// Texts...
		for (int i = 0;i < 8;++i)
			this.textColors[i] = DyeColor.byName(tag.getString("Color" + i), DyeColor.GRAY);

		for (int i = 0; i < 8; ++i) {
			String s = tag.getString("Text" + (i + 1));
			Component itextcomponent = Component.Serializer.fromJson(s.isEmpty() ? "\"\"" : s);
			if (this.level instanceof ServerLevel) {
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

	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.worldPosition, 2020, this.getUpdateTag());
	}

	public CompoundTag getUpdateTag() {
		return this.save(new CompoundTag());
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

	public void setAllowedPlayerEditor(Player player) {
		this.playerWhoMayEdit = player;
	}

	public Player getPlayerWhoMayEdit() {
		return this.playerWhoMayEdit;
	}

	public boolean executeClickCommands(Player player) {
		for (Component itextcomponent : this.messages) {
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

	public CommandSource createCommandSourceStack(@Nullable ServerPlayer player) {
		String s = player == null ? "EaselMenu" : player.getName().getString();
		Component itextcomponent = (Component) (player == null ? new TextComponent("EaselMenu")
				: player.getDisplayName());
		return new CommandSource(ICommandSource.NULL, Vector3d.atCenterOf(this.worldPosition), Vector2f.ZERO,
				(ServerLevel) this.level, 2, s, itextcomponent, this.level.getServer(), player);
	}

	// Container...
	public void dropAllContents(World world, BlockPos blockPos) {
		InventoryHelper.dropContents(world, blockPos, contents);
	}

	@Nullable
	@Override
	public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return EaselMenuMenu.createContainerServerSide(windowID, playerInventory, contents, this);
	}

	@Override
	public Component getDisplayName() {
		return new TranslatableComponent("container." + Nekoration.MODID + ".easel_menu");
	}
	*/

	public Component getMessage(int line) {
		return this.messages[line];
	}

	public void setMessage(int line, Component text) {
		this.messages[line] = text;
	}

	public Component[] getMessages(){
		return this.messages;
	}

	public boolean getGlowing(){
		return this.isGlowing;
	}

	public void setGlowing(boolean glow){
		this.isGlowing = glow;
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
}