package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.items.PaletteItem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class C2SUpdatePaletteData {
    public InteractionHand hand = null;
	public byte active = 0;
    public int[] colors = new int[6];

	public C2SUpdatePaletteData(InteractionHand hand, byte slot, int[] colors) {
        this.hand = hand;
		this.active = slot;
        this.colors = colors;
	}

	public static void encode(final C2SUpdatePaletteData msg, final FriendlyByteBuf packetBuffer) {
        packetBuffer.writeEnum(msg.hand);
        packetBuffer.writeByte(msg.active);
        packetBuffer.writeVarIntArray(msg.colors);
	}

	public static C2SUpdatePaletteData decode(final FriendlyByteBuf packetBuffer) {
        InteractionHand h = packetBuffer.readEnum(InteractionHand.class);
        byte a = packetBuffer.readByte();
        int[] c = packetBuffer.readVarIntArray();
		return new C2SUpdatePaletteData(h, a, c);
	}

	public static void handle(final C2SUpdatePaletteData msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on SERVER SIDE...
            ServerPlayer player = contextSupplier.get().getSender();
            if (player != null) {
                // ...
                ItemStack updated = new ItemStack(ModItems.PALETTE.get());
                CompoundTag nbt = updated.getOrCreateTag();
                nbt.putByte(PaletteItem.ACTIVE, msg.active);
                nbt.putIntArray(PaletteItem.COLORS, msg.colors);
                updated.setTag(nbt);
                player.setItemInHand(msg.hand, updated);
            }
        });
        contextSupplier.get().setPacketHandled(true);
	}
}