package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.entities.PaintingEntity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;


public class C2SUpdatePaintingData {
    public int paintingId;
    public byte partX;
    public byte partY;
    public byte partW;
    public byte partH;
    public int[] pixels;
    public int compositeHash;

    public C2SUpdatePaintingData(int id, byte x, byte y, byte w, byte h, int[] pixels, int hash) {
        this.paintingId = id;
        this.partX = x;
        this.partY = y;
        this.partW = w;
        this.partH = h;
        this.pixels = pixels;
        this.compositeHash = hash; // Used by other clients to check if the data's right...
    }

    public static void encode(final C2SUpdatePaintingData msg, final FriendlyByteBuf packetBuffer) {
        packetBuffer.writeInt(msg.paintingId);
        packetBuffer.writeByte(msg.partX);
        packetBuffer.writeByte(msg.partY);
        packetBuffer.writeByte(msg.partW);
        packetBuffer.writeByte(msg.partH);
        packetBuffer.writeVarIntArray(msg.pixels);
        packetBuffer.writeInt(msg.compositeHash);
    }

    public static C2SUpdatePaintingData decode(final FriendlyByteBuf packetBuffer) {
        int i = packetBuffer.readInt();
        byte x = packetBuffer.readByte();
        byte y = packetBuffer.readByte();
        byte w = packetBuffer.readByte();
        byte h = packetBuffer.readByte();
        int[] p = packetBuffer.readVarIntArray();
        int hash = packetBuffer.readInt();
        return new C2SUpdatePaintingData(i, x, y, w, h, p, hash);
    }

    public static void handle(final C2SUpdatePaintingData msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on SERVER SIDE...
            ServerPlayer player = contextSupplier.get().getSender();
            if (player != null) {
                // ...
                Entity entity = player.level.getEntity(msg.paintingId);
                if (entity instanceof PaintingEntity){
                    ((PaintingEntity)entity).data.setAreaPixels(msg.partX, msg.partY, msg.partW, msg.partH, msg.pixels);
                    final S2CUpdatePaintingData packet = new S2CUpdatePaintingData(msg.paintingId, msg.partX, msg.partY, msg.partW, msg.partH, msg.pixels, msg.compositeHash);
                    ModPacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}