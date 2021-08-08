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

public class C2SUpdateEaselMenuData {
	public ITextComponent[] texts = new ITextComponent[8];
    public DyeColor[] colors = new DyeColor[8];
    public BlockPos pos = BlockPos.ZERO;
    public boolean glow = false;

	public C2SUpdateEaselMenuData(BlockPos pos, ITextComponent[] texts, DyeColor[] colors, boolean glowing) {
        this.pos = pos;
		this.texts = texts;
        this.colors = colors;
        this.glow = glowing;
	}

	public static void encode(final C2SUpdateEaselMenuData msg, final PacketBuffer packetBuffer) {
        packetBuffer.writeBlockPos(msg.pos);
        for (int i = 0;i < 8;i++)
		    packetBuffer.writeComponent(msg.texts[i]);
        for (int i = 0;i < 8;i++)
		    packetBuffer.writeEnum(msg.colors[i]);
        packetBuffer.writeBoolean(msg.glow);
	}

	public static C2SUpdateEaselMenuData decode(final PacketBuffer packetBuffer) {
        BlockPos pos = packetBuffer.readBlockPos();
        ITextComponent[] t = new ITextComponent[8];
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
            ServerPlayerEntity player = contextSupplier.get().getSender();
            if (player != null) {
                ServerWorld world = player.getLevel();
                if (world.isLoaded(msg.pos)) {
                    TileEntity tileEntity = world.getBlockEntity(msg.pos);
                    if (tileEntity instanceof EaselMenuBlockEntity) {
                        EaselMenuBlockEntity te = ((EaselMenuBlockEntity) tileEntity);
                        for (int i = 0;i < 8;i++)
                            te.setMessage(i, msg.texts[i]);
                        te.setColor(msg.colors);
                        te.setGlowing(msg.glow);
                        world.getChunkSource().blockChanged(msg.pos);
                        tileEntity.setChanged();
                        //System.out.println("TEXT UPDATE Packet Received From Client");
                        
                        ItemStack[] its = new ItemStack[8];
                        ContainerContents cts = te.contents;
                        for (int i = 0;i < 8;i++){
                            its[i] = cts.getItem(i);
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