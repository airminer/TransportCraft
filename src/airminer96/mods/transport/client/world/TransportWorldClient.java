package airminer96.mods.transport.client.world;

import airminer96.mods.transport.client.network.TransportNetClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;

public class TransportWorldClient extends WorldClient {

	public TransportWorldClient(int dim) {
		super(TransportNetClientHandler.getNewInstance(), getWorldSettings(), dim, Minecraft.getMinecraft().theWorld.difficultySetting, Minecraft.getMinecraft().mcProfiler, Minecraft.getMinecraft().getLogAgent());
	}

	private static WorldSettings getWorldSettings() {
		WorldInfo worldInfo = new WorldInfo(Minecraft.getMinecraft().theWorld.getWorldInfo());
		worldInfo.setTerrainType(WorldType.FLAT);
		worldInfo.setSpawnPosition(0, 0, 0);
		return new WorldSettings(worldInfo).func_82750_a("2;0;1;");
	}

	@Override
	public int getSkyBlockTypeBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4) {
		return super.getSkyBlockTypeBrightness(par1EnumSkyBlock, par2, par3, par4);
	}

}
