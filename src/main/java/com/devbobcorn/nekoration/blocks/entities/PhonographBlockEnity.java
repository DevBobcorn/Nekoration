package com.devbobcorn.nekoration.blocks.entities;

import java.util.function.Function;

import javax.annotation.Nullable;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PhonographBlockEnity extends TileEntity {
	private final ITextComponent[] messages = new ITextComponent[] { StringTextComponent.EMPTY,
			StringTextComponent.EMPTY, StringTextComponent.EMPTY, StringTextComponent.EMPTY };

	public PhonographBlockEnity() {
		super(ModTileEntityType.PHONOGRAGH_TYPE);
	}

	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		for (int i = 0; i < 4; ++i) {
			String s = ITextComponent.Serializer.toJson(this.messages[i]);
			tag.putString("Text" + (i + 1), s);
		}
		return tag;
	}

	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

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
		}
	}

	@OnlyIn(Dist.CLIENT)
	public ITextComponent getMessage(int line) {
		return this.messages[line];
	}

	public void setMessage(int line, ITextComponent texts) {
		this.messages[line] = texts;
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		BlockState blockState = level.getBlockState(worldPosition);
		load(blockState, pkt.getTag()); // read from the nbt in the packet
	}

	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbtTagCompound = new CompoundNBT();
		save(nbtTagCompound);
		int tileEntityType = 42; // arbitrary number; only used for vanilla TileEntities. You can use it, or not,
									// as you want.
		return new SUpdateTileEntityPacket(this.worldPosition, tileEntityType, nbtTagCompound);
	}

	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}

	/*
	 * Populates this TileEntity with information from the tag, used by vanilla to
	 * transmit from server to client
	 */
	@Override
	public void handleUpdateTag(BlockState blockState, CompoundNBT tag) {
		this.load(blockState, tag);
	}

	public CommandSource createCommandSourceStack(@Nullable ServerPlayerEntity player) {
		ITextComponent text = new StringTextComponent("Phonograph");
		return new CommandSource(ICommandSource.NULL, Vector3d.atCenterOf(this.worldPosition), Vector2f.ZERO,
				(ServerWorld) this.level, 2, "Phonograph", text, this.level.getServer(), player);
	}
}