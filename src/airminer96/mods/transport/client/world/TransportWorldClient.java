package airminer96.mods.transport.client.world;

import airminer96.mods.transport.client.network.TransportNetClientHandler;
import airminer96.mods.transport.world.TransportWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;

public class TransportWorldClient extends WorldClient implements TransportWorld {

	public TransportWorldClient(int dim) {
		super(TransportNetClientHandler.getNewInstance(), getWorldSettings(), dim, Minecraft.getMinecraft().theWorld.difficultySetting, Minecraft.getMinecraft().mcProfiler, Minecraft.getMinecraft().getLogAgent());
	}

	private static WorldSettings getWorldSettings() {
		WorldInfo worldInfo = new WorldInfo(Minecraft.getMinecraft().theWorld.getWorldInfo());
		worldInfo.setTerrainType(WorldType.FLAT);
		worldInfo.setSpawnPosition(0, 0, 0);
		return new WorldSettings(worldInfo).func_82750_a("2;0;1;");
	}

	/**
	 * Returns the block ID at coords x,y,z
	 */
	@Override
	public int getBlockId(int par1, int par2, int par3) {
		return 1;
	}

	@Override
	public int getSkyBlockTypeBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4) {
		return 15;
	}

	@Override
	public World getWorld() {
		return this;
	}

}
