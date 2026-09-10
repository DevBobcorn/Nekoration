package io.devbobcorn.nekoration.network;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.items.PaintingItem;
import io.devbobcorn.nekoration.registry.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client -> server sync packet for a blank painting item's size.
 */
public record PaintingSizeUpdatePayload(InteractionHand hand, short width, short height, int count)
        implements CustomPacketPayload {

    public static final Type<PaintingSizeUpdatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "painting_size_update"));
    public static final StreamCodec<FriendlyByteBuf, PaintingSizeUpdatePayload> STREAM_CODEC =
            StreamCodec.of((buffer, payload) -> payload.write(buffer), PaintingSizeUpdatePayload::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PaintingSizeUpdatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            // Handle this on SERVER SIDE...
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            ItemStack updated = new ItemStack(ModItems.PAINTING.get(), payload.count());
            PaintingItem.setSize(updated, payload.width(), payload.height());
            player.setItemInHand(payload.hand(), updated);
        });
    }

    private static PaintingSizeUpdatePayload read(FriendlyByteBuf buffer) {
        InteractionHand hand = buffer.readEnum(InteractionHand.class);
        short w = buffer.readShort();
        short h = buffer.readShort();
        int c = buffer.readInt();
        return new PaintingSizeUpdatePayload(hand, w, h, c);
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeEnum(hand);
        buffer.writeShort(width);
        buffer.writeShort(height);
        buffer.writeInt(count);
    }
}
