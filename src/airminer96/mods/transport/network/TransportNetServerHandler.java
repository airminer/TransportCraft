package airminer96.mods.transport.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.common.network.PacketDispatcher;
import airminer96.mods.transport.Transport;
import airminer96.mods.transport.entity.EntityTransportBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ReportedException;

public class TransportNetServerHandler extends NetServerHandler {

	public TransportNetServerHandler(MinecraftServer par1MinecraftServer, EntityPlayerMP par3EntityPlayerMP) {
		super(par1MinecraftServer, new DummyNetworkManager() , par3EntityPlayerMP);
	}
	
	@Override
	public void sendPacketToPlayer(Packet par1Packet)
    {
        if (par1Packet instanceof Packet3Chat)
        {
            Packet3Chat packet3chat = (Packet3Chat)par1Packet;
            int i = this.playerEntity.getChatVisibility();

            if (i == 2)
            {
                return;
            }

            if (i == 1 && !packet3chat.getIsServer())
            {
                return;
            }

        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataoutput = new DataOutputStream(outputStream);
        
        try {
        	dataoutput.writeInt(EntityTransportBlock.dimToId.get(playerEntity.worldObj.provider.dimensionId));
			par1Packet.writePacket(par1Packet, new DataOutputStream(outputStream));
			PacketDispatcher.sendPacketToAllPlayers(new Packet250CustomPayload(Transport.ID, outputStream.toByteArray()));
        } catch (IOException e) {
			e.printStackTrace();
		}
              
    }

}
