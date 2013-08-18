package airminer96.mods.transport.client.world;

import airminer96.mods.transport.client.network.TransportNetClientHandler;
import airminer96.mods.transport.world.TransportWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class TransportWorldClient extends WorldClient implements TransportWorld {

	private final int id;

	public TransportWorldClient(int id, int dim) {
		super(TransportNetClientHandler.getNewInstance(), new WorldSettings(Minecraft.getMinecraft().theWorld.getWorldInfo()), dim, Minecraft.getMinecraft().theWorld.difficultySetting, Minecraft.getMinecraft().mcProfiler, Minecraft.getMinecraft().getLogAgent());
		this.id = id;
	}

	/**
	 * Returns the block ID at coords x,y,z
	 */
	@Override
	public int getBlockId(int par1, int par2, int par3) {
		return 1;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public World getWorld() {
		return this;
	}

}
