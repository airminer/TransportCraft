package airminer96.mods.transport.client.world;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import airminer96.mods.transport.client.network.TransportNetClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;

public class TransportWorldClient extends WorldClient {

	public TransportNetClientHandler sendQueue;

	public TransportWorldClient(int dim) {
		super(TransportNetClientHandler.getNewInstance(), getWorldSettings(), dim, Minecraft.getMinecraft().theWorld.difficultySetting, Minecraft.getMinecraft().mcProfiler, Minecraft.getMinecraft().getLogAgent());
		sendQueue = ObfuscationReflectionHelper.getPrivateValue(WorldClient.class, this, "sendQueue");
		ObfuscationReflectionHelper.setPrivateValue(NetClientHandler.class, sendQueue, this, "worldClient");
	}

	private static WorldSettings getWorldSettings() {
		WorldInfo worldInfo = new WorldInfo(Minecraft.getMinecraft().theWorld.getWorldInfo());
		worldInfo.setTerrainType(WorldType.FLAT);
		worldInfo.setSpawnPosition(0, 0, 0);
		return new WorldSettings(worldInfo).func_82750_a("2;0;1;");
	}

}
