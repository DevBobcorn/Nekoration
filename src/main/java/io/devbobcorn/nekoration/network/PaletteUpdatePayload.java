package io.devbobcorn.nekoration.network;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.items.PaletteItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client -> server sync packet for the palette item's color data.
 */
public record PaletteUpdatePayload(InteractionHand hand, byte active, int[] colors) implements CustomPacketPayload {

    public static final Type<PaletteUpdatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "palette_update"));
    public static final StreamCodec<FriendlyByteBuf, PaletteUpdatePayload> STREAM_CODEC =
            StreamCodec.of((buffer, payload) -> payload.write(buffer), PaletteUpdatePayload::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PaletteUpdatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            // Handle this on SERVER SIDE...
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            ItemStack held = player.getItemInHand(payload.hand());
            if (!held.is(io.devbobcorn.nekoration.registry.ModItems.PALETTE.get())) {
                return;
            }
            PaletteItem.setColorData(held, payload.active(), payload.colors());
        });
    }

    private static PaletteUpdatePayload read(FriendlyByteBuf buffer) {
        InteractionHand hand = buffer.readEnum(InteractionHand.class);
        byte a = buffer.readByte();
        int[] c = buffer.readVarIntArray();
        return new PaletteUpdatePayload(hand, a, c);
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeEnum(hand);
        buffer.writeByte(active);
        buffer.writeVarIntArray(colors);
    }
}
