package com.devbobcorn.nekoration.blockentities;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EaselMenuBlockEnity extends TileEntity {
	private final ITextComponent[] messages = new ITextComponent[] { StringTextComponent.EMPTY,
			StringTextComponent.EMPTY, StringTextComponent.EMPTY, StringTextComponent.EMPTY };
	private boolean isEditable = true;
	private PlayerEntity playerWhoMayEdit;
	private final IReorderingProcessor[] renderMessages = new IReorderingProcessor[4];
	private DyeColor color = DyeColor.BLACK;

	public EaselMenuBlockEnity() {
		super(ModEntityType.EASEL_MENU_TYPE);
	}

	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		for (int i = 0; i < 4; ++i) {
			String s = ITextComponent.Serializer.toJson(this.messages[i]);
			tag.putString("Text" + (i + 1), s);
		}
		tag.putString("Color", this.color.getName());
		return tag;
	}

	public void load(BlockState state, CompoundNBT tag) {
		this.isEditable = false;
		super.load(state, tag);
		this.color = DyeColor.byName(tag.getString("Color"), DyeColor.BLACK);

		for (int i = 0; i < 4; ++i) {
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
			this.renderMessages[i] = null;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public ITextComponent getMessage(int line) {
		return this.messages[line];
	}

	public void setMessage(int line, ITextComponent texts) {
		this.messages[line] = texts;
		this.renderMessages[line] = null;
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public IReorderingProcessor getRenderMessage(int line, Function<ITextComponent, IReorderingProcessor> p_242686_2_) {
		if (this.renderMessages[line] == null && this.messages[line] != null) {
			this.renderMessages[line] = p_242686_2_.apply(this.messages[line]);
		}
		return this.renderMessages[line];
	}

	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.worldPosition, 9, this.getUpdateTag());
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

	public DyeColor getColor() {
		return this.color;
	}

	public boolean setColor(DyeColor color) {
		if (color != this.getColor()) {
			this.color = color;
			this.setChanged();
			this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
			return true;
		} else {
			return false;
		}
	}
}