package io.devbobcorn.nekoration.network;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.CustomBlock;
import io.devbobcorn.nekoration.blocks.containers.CustomBlockMenu;
import io.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import io.devbobcorn.nekoration.xplat.PayloadContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** One validated edit from the Custom Block screen. */
public record CustomBlockEditPayload(BlockPos pos, int entry, int action, int value) implements CustomPacketPayload {
    public static final int MOVE_X = 0;
    public static final int MOVE_Y = 1;
    public static final int MOVE_Z = 2;
    public static final int ROTATE = 3;
    public static final int TINT = 4;
    public static final int CLEAR_TINT = 5;
    public static final int TOGGLE_AO = 6;
    public static final int CHANGE_LIGHT = 7;
    public static final int TOGGLE_TINT_ALL_FACES = 8;

    public static final Type<CustomBlockEditPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "custom_block_edit"));
    public static final StreamCodec<FriendlyByteBuf, CustomBlockEditPayload> STREAM_CODEC =
            StreamCodec.of((buffer, payload) -> payload.write(buffer), CustomBlockEditPayload::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CustomBlockEditPayload payload, PayloadContext context) {
        context.enqueue(() -> {
            if (!(context.player() instanceof ServerPlayer player)
                    || !(player.containerMenu instanceof CustomBlockMenu menu)
                    || !menu.stillValid(player)) {
                return;
            }
            CustomBlockEntity customBlock = menu.getCustomBlock();
            if (!customBlock.getBlockPos().equals(payload.pos())) {
                return;
            }
            BlockState state = customBlock.getBlockState();
            if (!(state.getBlock() instanceof CustomBlock)) {
                return;
            }
            if (payload.action() == TOGGLE_AO) {
                player.level().setBlock(payload.pos(), state.cycle(CustomBlock.CAST_AO), Block.UPDATE_ALL);
                return;
            }
            if (payload.action() == CHANGE_LIGHT) {
                if (payload.value() == -1 || payload.value() == 1) {
                    int light = Math.floorMod(state.getValue(CustomBlock.LIGHT) + payload.value(), 16);
                    player.level().setBlock(payload.pos(), state.setValue(CustomBlock.LIGHT, light), Block.UPDATE_ALL);
                }
                return;
            }
            if (payload.entry() != customBlock.activeIndex() || customBlock.activeEntry() == null) {
                return;
            }
            switch (payload.action()) {
                case MOVE_X, MOVE_Y, MOVE_Z -> {
                    if (payload.value() != -1 && payload.value() != 1) return;
                    customBlock.moveActive(payload.action(), payload.value());
                }
                case ROTATE -> {
                    if (payload.value() != -1 && payload.value() != 1) return;
                    customBlock.rotateActive(payload.value());
                }
                case TINT -> {
                    if ((payload.value() & 0xFF000000) != 0) return;
                    customBlock.tintActive(payload.value());
                }
                case CLEAR_TINT -> customBlock.clearActiveTint();
                case TOGGLE_TINT_ALL_FACES -> customBlock.toggleActiveTintAllFaces();
                default -> { return; }
            }
            customBlock.markUpdated();
        });
    }

    private static CustomBlockEditPayload read(FriendlyByteBuf buffer) {
        return new CustomBlockEditPayload(buffer.readBlockPos(), buffer.readByte(), buffer.readByte(), buffer.readInt());
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeByte(entry);
        buffer.writeByte(action);
        buffer.writeInt(value);
    }
}
