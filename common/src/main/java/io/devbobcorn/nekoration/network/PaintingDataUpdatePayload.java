package io.devbobcorn.nekoration.network;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.entities.PaintingEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import io.devbobcorn.nekoration.xplat.NekoPlatform;
import io.devbobcorn.nekoration.xplat.PayloadContext;

/**
 * Client -> server sync packet for a part of a painting's pixel data.
 * The server will relay it to all clients as a {@link PaintingDataBroadcastPayload}.
 */
public record PaintingDataUpdatePayload(int paintingId, byte partX, byte partY, byte partW, byte partH,
        int[] pixels, int compositeHash) implements CustomPacketPayload {

    public static final Type<PaintingDataUpdatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "painting_data_update"));
    public static final StreamCodec<FriendlyByteBuf, PaintingDataUpdatePayload> STREAM_CODEC =
            StreamCodec.of((buffer, payload) -> payload.write(buffer), PaintingDataUpdatePayload::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PaintingDataUpdatePayload payload, PayloadContext context) {
        context.enqueue(() -> {
            // Handle this on SERVER SIDE...
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            Entity entity = player.level().getEntity(payload.paintingId());
            if (entity instanceof PaintingEntity painting) {
                painting.data.setAreaPixels(payload.partX(), payload.partY(), payload.partW(), payload.partH(), payload.pixels());
                final var broadcast = new PaintingDataBroadcastPayload(payload.paintingId(), payload.partX(), payload.partY(),
                        payload.partW(), payload.partH(), payload.pixels(), payload.compositeHash());
                NekoPlatform.sendToAllPlayers(broadcast);
            }
        });
    }

    private static PaintingDataUpdatePayload read(FriendlyByteBuf buffer) {
        int id = buffer.readInt();
        byte x = buffer.readByte();
        byte y = buffer.readByte();
        byte w = buffer.readByte();
        byte h = buffer.readByte();
        int[] p = buffer.readVarIntArray();
        int hash = buffer.readInt();
        return new PaintingDataUpdatePayload(id, x, y, w, h, p, hash);
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeInt(paintingId);
        buffer.writeByte(partX);
        buffer.writeByte(partY);
        buffer.writeByte(partW);
        buffer.writeByte(partH);
        buffer.writeVarIntArray(pixels);
        buffer.writeInt(compositeHash); // Used by other clients to check if the data's right...
    }
}
