package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class C2SUpdateEaselMenuData {
	public Component[] texts = new Component[8];
    public DyeColor[] colors = new DyeColor[8];
    public BlockPos pos = BlockPos.ZERO;
    public boolean glow = false;

	public C2SUpdateEaselMenuData(BlockPos pos, Component[] texts, DyeColor[] colors, boolean glowing) {
        this.pos = pos;
		this.texts = texts;
        this.colors = colors;
        this.glow = glowing;
	}

	public static void encode(final C2SUpdateEaselMenuData msg, final FriendlyByteBuf packetBuffer) {
        packetBuffer.writeBlockPos(msg.pos);
        for (int i = 0;i < 8;i++)
		    packetBuffer.writeComponent(msg.texts[i]);
        for (int i = 0;i < 8;i++)
		    packetBuffer.writeEnum(msg.colors[i]);
        packetBuffer.writeBoolean(msg.glow);
	}

	public static C2SUpdateEaselMenuData decode(final FriendlyByteBuf packetBuffer) {
        BlockPos pos = packetBuffer.readBlockPos();
        Component[] t = new Component[8];
        DyeColor[] c = new DyeColor[8];
        for (int i = 0;i < 8;i++)
		    t[i] = packetBuffer.readComponent();
        for (int i = 0;i < 8;i++)
            c[i] = packetBuffer.readEnum(DyeColor.class);
        boolean g = packetBuffer.readBoolean();
		return new C2SUpdateEaselMenuData(pos, t, c, g);
	}

	public static void handle(final C2SUpdateEaselMenuData msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on SERVER SIDE...
            ServerPlayer player = contextSupplier.get().getSender();
            if (player != null) {
                ServerLevel world = player.getLevel();
                if (world.isLoaded(msg.pos)) {
                    BlockEntity tileEntity = world.getBlockEntity(msg.pos);
                    if (tileEntity instanceof EaselMenuBlockEntity) {
                        EaselMenuBlockEntity te = ((EaselMenuBlockEntity) tileEntity);
                        for (int i = 0;i < 8;i++)
                            te.setMessage(i, msg.texts[i]);
                        te.setColors(msg.colors);
                        te.setGlowing(msg.glow);
                        world.getChunkSource().blockChanged(msg.pos);
                        tileEntity.setChanged();
                        //System.out.println("TEXT UPDATE Packet Received From Client");
                        
                        ItemStack[] its = new ItemStack[8];
                        for (int i = 0;i < 8;i++){
                            its[i] = te.getItem(i);
                        }
                        // Then update items on Client Side, used for rendering...
                        final S2CUpdateEaselMenuData packet = new S2CUpdateEaselMenuData(msg.pos, its, msg.texts, msg.colors, msg.glow);
                        ModPacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
                        //System.out.println("Item Update Packet Sent From Server");
                    }
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
	}
}