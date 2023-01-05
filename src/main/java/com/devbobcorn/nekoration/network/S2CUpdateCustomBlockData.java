package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

public class S2CUpdateCustomBlockData {
    public BlockPos pos = BlockPos.ZERO;
    public byte dir = 0;
    public int offsetX, offsetY, offsetZ;
    public boolean retint, showHint;
    public int red, green, blue;
    public BlockState displayState;

    public S2CUpdateCustomBlockData(BlockPos pos, byte dir, int[] offset, boolean retint, boolean showHint, int[] color, BlockState state) {
        this.pos = pos;
        this.dir = dir;
        offsetX = offset[0];
        offsetY = offset[1];
        offsetZ = offset[2];
        this.retint = retint;
        this.showHint = showHint;
        red = color[0];
        green = color[1];
        blue = color[2];
        displayState = state;
    }

    public static void encode(final S2CUpdateCustomBlockData msg, final FriendlyByteBuf packetBuffer) {
        packetBuffer.writeBlockPos(msg.pos);
        packetBuffer.writeByte(msg.dir);
        packetBuffer.writeInt(msg.offsetX);
        packetBuffer.writeInt(msg.offsetY);
        packetBuffer.writeInt(msg.offsetZ);
        packetBuffer.writeBoolean(msg.retint);
        packetBuffer.writeBoolean(msg.showHint);
        packetBuffer.writeInt(msg.red);
        packetBuffer.writeInt(msg.green);
        packetBuffer.writeInt(msg.blue);
        packetBuffer.writeNbt(NbtUtils.writeBlockState(msg.displayState));
    }

    @SuppressWarnings({ "resource", "null" })
    public static S2CUpdateCustomBlockData decode(final FriendlyByteBuf packetBuffer) {
        BlockPos pos = packetBuffer.readBlockPos();
        byte dir = packetBuffer.readByte();
        int[] offset = new int[3];
        for (int i = 0;i < 3;i++)
            offset[i] = packetBuffer.readInt();
        boolean retint = packetBuffer.readBoolean();
        boolean showHint = packetBuffer.readBoolean();
        int[] color = new int[3];
        for (int i = 0;i < 3;i++)
            color[i] = packetBuffer.readInt();
        BlockState state = NbtUtils.readBlockState(Minecraft.getInstance().level.holderLookup(Registries.BLOCK), packetBuffer.readNbt());
        return new S2CUpdateCustomBlockData(pos, dir, offset, retint, showHint, color, state);
    }

    @SuppressWarnings({ "resource", "null" })
    public static void handle(final S2CUpdateCustomBlockData msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on CLIENT SIDE...
            ClientLevel world = Minecraft.getInstance().level;
            if (world.isLoaded(msg.pos)) {
                BlockEntity tileEntity = world.getBlockEntity(msg.pos);
                if (tileEntity instanceof CustomBlockEntity) {
                    CustomBlockEntity te = (CustomBlockEntity) tileEntity;

                    te.dir = msg.dir;
                    te.offset[0] = msg.offsetX;
                    te.offset[1] = msg.offsetY;
                    te.offset[2] = msg.offsetZ;
                    te.retint = msg.retint;
                    te.showHint = msg.showHint;
                    te.color[0] = msg.red;
                    te.color[1] = msg.green;
                    te.color[2] = msg.blue;
                    te.displayState = msg.displayState;
                    tileEntity.setChanged();
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
