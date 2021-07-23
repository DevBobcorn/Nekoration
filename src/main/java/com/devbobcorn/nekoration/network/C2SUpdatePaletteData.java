package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.items.PaletteItem;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

public class C2SUpdatePaletteData {
    public Hand hand = null;
	public byte active = 0;
    public int[] colors = new int[6];

	public C2SUpdatePaletteData(Hand hand, byte slot, int[] colors) {
        this.hand = hand;
		this.active = slot;
        this.colors = colors;
	}

	public static void encode(final C2SUpdatePaletteData msg, final PacketBuffer packetBuffer) {
        packetBuffer.writeEnum(msg.hand);
        packetBuffer.writeByte(msg.active);
        packetBuffer.writeVarIntArray(msg.colors);
	}

	public static C2SUpdatePaletteData decode(final PacketBuffer packetBuffer) {
        Hand h = packetBuffer.readEnum(Hand.class);
        byte a = packetBuffer.readByte();
        int[] c = packetBuffer.readVarIntArray();
		return new C2SUpdatePaletteData(h, a, c);
	}

	public static void handle(final C2SUpdatePaletteData msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on SERVER SIDE...
            ServerPlayerEntity player = contextSupplier.get().getSender();
            if (player != null) {
                // ...
                ItemStack updated = new ItemStack(ModItems.PALETTE.get());
                CompoundNBT nbt = updated.getOrCreateTag();
                nbt.putByte(PaletteItem.ACTIVE, msg.active);
                nbt.putIntArray(PaletteItem.COLORS, msg.colors);
                updated.setTag(nbt);
                player.setItemInHand(msg.hand, updated);
            }
        });
        contextSupplier.get().setPacketHandled(true);
	}
}