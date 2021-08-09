package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.entities.PaintingEntity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;


public class C2SUpdatePaintingData {
    public int paintingId;
    public int[] pixels;
    public int compositeHash;

	public C2SUpdatePaintingData(int id, int[] pixels, int hash) {
        this.paintingId = id;
        this.pixels = pixels;
        this.compositeHash = hash; // Used by other clients to check if the data's right...
	}

	public static void encode(final C2SUpdatePaintingData msg, final FriendlyByteBuf packetBuffer) {
        packetBuffer.writeInt(msg.paintingId);
        packetBuffer.writeVarIntArray(msg.pixels);
        packetBuffer.writeInt(msg.compositeHash);
	}

	public static C2SUpdatePaintingData decode(final FriendlyByteBuf packetBuffer) {
        int i = packetBuffer.readInt();
        int[] p = packetBuffer.readVarIntArray();
        int hash = packetBuffer.readInt();
		return new C2SUpdatePaintingData(i, p, hash);
	}

	public static void handle(final C2SUpdatePaintingData msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on SERVER SIDE...
            ServerPlayer player = contextSupplier.get().getSender();
            if (player != null) {
                // ...
                Entity entity = player.level.getEntity(msg.paintingId);
                if (entity instanceof PaintingEntity){
                    ((PaintingEntity)entity).data.setPixels(msg.pixels);
                    System.out.println("Painting Updated!");
                    final S2CUpdatePaintingData packet = new S2CUpdatePaintingData(msg.paintingId, msg.pixels, msg.compositeHash);
                    ModPacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
	}
}