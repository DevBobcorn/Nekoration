package com.devbobcorn.nekoration.blocks.entities;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.containers.EaselMenuMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EaselMenuBlockEntity extends ContainerBlockEntity {
	public static final int NUMBER_OF_SLOTS = 8;

	private final Component[] messages = new Component[] { TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY };
	private final ItemStack airStack = ItemStack.EMPTY;
	public ItemStack[] renderItems = { airStack, airStack, airStack, airStack, airStack, airStack, airStack, airStack };
	private boolean isEditable = true;
	private Player playerWhoMayEdit;
	private DyeColor[] textColors = { DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY, DyeColor.GRAY };
	private boolean isGlowing;

	public final boolean white; // Not saved or synced between clients and server, just temporarily stores the variant type...

	public EaselMenuBlockEntity(BlockPos pos, BlockState state) {
		this(false, pos, state);
	}

	public EaselMenuBlockEntity(boolean w, BlockPos pos, BlockState state) {
		super(ModBlockEntityType.EASEL_MENU_TYPE.get(), pos, state);
		white = w;
		isGlowing = false;
	}

	public CompoundTag save(CompoundTag tag) {
		// Items...
		super.save(tag);
		// Texts...
		for (int i = 0; i < 8; ++i) {
			String s = Component.Serializer.toJson(this.messages[i]);
			tag.putString("Text" + (i + 1), s);
		}
		// Colors...
		for (int i = 0; i < 8; ++i) {
			tag.putString("Color" + i, this.textColors[i].getName());
		}
		// Glowing...
		tag.putBoolean("Glowing", isGlowing);
		return tag;
	}

	public void load(CompoundTag tag) {
		// Items...
		super.load(tag);
		for (int i = 0;i < NUMBER_OF_SLOTS;i++)
			renderItems[i] = getItem(i);
		// Texts...
		for (int i = 0; i < 8; ++i) {
			String s = tag.getString("Text" + (i + 1));
			Component itextcomponent = Component.Serializer.fromJson(s.isEmpty() ? "\"\"" : s);
			this.messages[i] = itextcomponent;
		}
		// Colors...
		for (int i = 0;i < 8;++i)
			this.textColors[i] = DyeColor.byName(tag.getString("Color" + i), DyeColor.GRAY);
		// Glowing...
		isGlowing = tag.getBoolean("Glowing");
	}

	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return new ClientboundBlockEntityDataPacket(this.worldPosition, 2020, this.getUpdateTag());
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

	@Override
	public int getContainerSize() {
		return NUMBER_OF_SLOTS;
	}

	@Override
	public Component getDisplayName() {
		return new TranslatableComponent("block." + Nekoration.MODID + ".easel_menu");
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent("block." + Nekoration.MODID + ".easel_menu");
	}

	@Override
	protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory) {
		return new EaselMenuMenu(windowId, playerInventory, this);
	}

	
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

	public boolean toggleGlowing(){
		return (isGlowing = !isGlowing);
	}

	public DyeColor[] getColors() {
		return this.textColors;
	}

	public void setColors(DyeColor[] color) {
		this.textColors = color;
	}

	public DyeColor getColor(int line) {
		return this.textColors[line];
	}

	public void setColor(int line, DyeColor color) {
		this.textColors[line] = color;
	}
}