package airminer96.mods.transport.client.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import airminer96.mods.transport.Transport;
import airminer96.mods.transport.client.world.TransportWorldClient;
import airminer96.mods.transport.entity.EntityTransportBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class TransportClientPacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		DataInputStream dataInput = new DataInputStream(new ByteArrayInputStream(packet.data));
		try {
			TransportWorldClient world = (TransportWorldClient) EntityTransportBlock.worldClients.get(dataInput.readInt());
			if(world != null) {
				Packet innerPacket = Packet.getNewPacket(Minecraft.getMinecraft().getLogAgent(), dataInput.readUnsignedByte());
				innerPacket.readPacketData(dataInput);
				WorldClient theWorld = Minecraft.getMinecraft().theWorld;
				Minecraft.getMinecraft().theWorld = world;
				innerPacket.processPacket(world.sendQueue);
				Minecraft.getMinecraft().theWorld = theWorld;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
