package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.blocks.containers.ContainerContents;
import com.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class C2SUpdateEaselMenuTexts {
	public ITextComponent[] texts = new ITextComponent[8];
    public DyeColor[] colors = new DyeColor[8];
    public BlockPos pos = BlockPos.ZERO;

	public C2SUpdateEaselMenuTexts(BlockPos pos, ITextComponent[] texts, DyeColor[] colors) {
        this.pos = pos;
		this.texts = texts;
        this.colors = colors;
	}

	public static void encode(final C2SUpdateEaselMenuTexts msg, final PacketBuffer packetBuffer) {
        packetBuffer.writeBlockPos(msg.pos);
        for (int i = 0;i < 8;i++)
		    packetBuffer.writeComponent(msg.texts[i]);
        for (int i = 0;i < 8;i++)
		    packetBuffer.writeEnum(msg.colors[i]);
	}

	public static C2SUpdateEaselMenuTexts decode(final PacketBuffer packetBuffer) {
        BlockPos pos = packetBuffer.readBlockPos();
        ITextComponent[] t = new ITextComponent[8];
        DyeColor[] c = new DyeColor[8];
        for (int i = 0;i < 8;i++)
		    t[i] = packetBuffer.readComponent();
        for (int i = 0;i < 8;i++)
            c[i] = packetBuffer.readEnum(DyeColor.class);
		return new C2SUpdateEaselMenuTexts(pos, t,c);
	}

	public static void handle(final C2SUpdateEaselMenuTexts msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on SERVER SIDE...
            ServerPlayerEntity player = contextSupplier.get().getSender();
            if (player != null) {
                ServerWorld world = player.getLevel();
                if (world.isLoaded(msg.pos)) {
                    TileEntity tileEntity = world.getBlockEntity(msg.pos);
                    if (tileEntity instanceof EaselMenuBlockEntity) {
                        for (int i = 0;i < 8;i++)
                            ((EaselMenuBlockEntity) tileEntity).setMessage(i, msg.texts[i]);
                        world.getChunkSource().blockChanged(msg.pos);
                        tileEntity.setChanged();
                        //System.out.println("TEXT UPDATE Packet Received FROM Client");
                        ItemStack[] its = new ItemStack[8];
                        ContainerContents cts = ((EaselMenuBlockEntity) tileEntity).contents;
                        for (int i = 0;i < 8;i++){
                            its[i] = cts.getItem(i);
                        }
                        // tHEN UPDATE ITEMSTACKS ON CLIENT SIDE, USED FOR RENDERING...
                        final S2CUpdateEaselMenuItems packet = new S2CUpdateEaselMenuItems(msg.pos, its, msg.texts, msg.colors);
                        ModPacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
                        //System.out.println("ITEM UPDATE Packet Sent from SERVER");
                    }
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
	}
}