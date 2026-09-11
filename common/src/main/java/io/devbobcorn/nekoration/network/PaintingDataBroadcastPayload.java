package io.devbobcorn.nekoration.network;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.client.ClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import io.devbobcorn.nekoration.xplat.PayloadContext;

/**
 * Server -> client sync packet for a part of a painting's pixel data.
 * The client who edited the painting has already got the up-to-date data,
 * so it's skipped by checking the composite hash...
 */
public record PaintingDataBroadcastPayload(int paintingId, byte partX, byte partY, byte partW, byte partH,
        int[] pixels, int compositeHash) implements CustomPacketPayload {

    public static final Type<PaintingDataBroadcastPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "painting_data_broadcast"));
    public static final StreamCodec<FriendlyByteBuf, PaintingDataBroadcastPayload> STREAM_CODEC =
            StreamCodec.of((buffer, payload) -> payload.write(buffer), PaintingDataBroadcastPayload::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PaintingDataBroadcastPayload payload, PayloadContext context) {
        // Client-only logic lives in ClientHelper so this class stays server-safe.
        context.enqueue(() -> ClientHelper.handlePaintingBroadcast(payload));
    }

    private static PaintingDataBroadcastPayload read(FriendlyByteBuf buffer) {
        int id = buffer.readInt();
        byte x = buffer.readByte();
        byte y = buffer.readByte();
        byte w = buffer.readByte();
        byte h = buffer.readByte();
        int[] p = buffer.readVarIntArray();
        int hash = buffer.readInt();
        return new PaintingDataBroadcastPayload(id, x, y, w, h, p, hash);
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeInt(paintingId);
        buffer.writeByte(partX);
        buffer.writeByte(partY);
        buffer.writeByte(partW);
        buffer.writeByte(partH);
        buffer.writeVarIntArray(pixels);
        buffer.writeInt(compositeHash);
    }
}
