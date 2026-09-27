package io.devbobcorn.nekoration.network;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.items.PaintingItem;
import io.devbobcorn.nekoration.registry.ModItems;
import io.devbobcorn.nekoration.xplat.PayloadContext;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * Client -> server sync packet for signing a painted painting item.
 * The author is always taken from the signing player, never from the client.
 */
public record PaintingSignUpdatePayload(InteractionHand hand, String title) implements CustomPacketPayload {

    private static final int MAX_TITLE_LENGTH = 32;

    public static final Type<PaintingSignUpdatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "painting_sign_update"));
    public static final StreamCodec<FriendlyByteBuf, PaintingSignUpdatePayload> STREAM_CODEC =
            StreamCodec.of((buffer, payload) -> payload.write(buffer), PaintingSignUpdatePayload::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PaintingSignUpdatePayload payload, PayloadContext context) {
        context.enqueue(() -> {
            // Handle this on SERVER SIDE...
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            ItemStack stack = player.getItemInHand(payload.hand());
            // Only unsigned painted paintings carrying painting data can be signed...
            if (stack.getItem() != ModItems.PAINTING.get())
                return;
            if (!PaintingItem.hasData(stack) || PaintingItem.isSigned(stack) || stack.getCount() != 1)
                return;
            String title = payload.title().trim();
            if (title.isEmpty())
                return;
            if (title.length() > MAX_TITLE_LENGTH)
                title = title.substring(0, MAX_TITLE_LENGTH);
            ItemStack updated = stack.copy();
            PaintingItem.setSignature(updated, title, player.getScoreboardName());
            player.setItemInHand(payload.hand(), updated);
        });
    }

    private static PaintingSignUpdatePayload read(FriendlyByteBuf buffer) {
        InteractionHand hand = buffer.readEnum(InteractionHand.class);
        String title = buffer.readUtf(MAX_TITLE_LENGTH);
        return new PaintingSignUpdatePayload(hand, title);
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeEnum(hand);
        buffer.writeUtf(title, MAX_TITLE_LENGTH);
    }
}
