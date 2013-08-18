package airminer96.mods.transport.world;

import net.minecraft.logging.ILogAgent;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

public class TransportWorldServer extends WorldServerMulti implements TransportWorld {

	private final int id;

	public TransportWorldServer(int id, MinecraftServer par2MinecraftServer, ISaveHandler par3iSaveHandler, String par4Str, int par5, WorldSettings par6WorldSettings, WorldServer par7WorldServer, Profiler par8Profiler, ILogAgent par9iLogAgent) {
		super(par2MinecraftServer, par3iSaveHandler, par4Str, par5, par6WorldSettings, par7WorldServer, par8Profiler, par9iLogAgent);
		this.id = id;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public World getWorld() {
		return this;
	}

	public static void initDimension(int id, int dim) {
		WorldServer overworld = DimensionManager.getWorld(0);
		if (overworld == null) {
			throw new RuntimeException("Cannot Hotload Dim: Overworld is not Loaded!");
		}
		try {
			DimensionManager.getProviderType(dim);
		} catch (Exception e) {
			System.err.println("Cannot Hotload Dim: " + e.getMessage());
			return; // If a provider hasn't been registered then we can't
					// hotload the dim
		}
		MinecraftServer mcServer = overworld.getMinecraftServer();
		ISaveHandler savehandler = overworld.getSaveHandler();
		WorldSettings worldSettings = new WorldSettings(overworld.getWorldInfo());

		WorldServer world = (dim == 0 ? overworld : new TransportWorldServer(id, mcServer, savehandler, overworld.getWorldInfo().getWorldName(), dim, worldSettings, overworld, mcServer.theProfiler, overworld.getWorldLogAgent()));
		world.addWorldAccess(new WorldManager(mcServer, world));
		MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
		if (!mcServer.isSinglePlayer()) {
			world.getWorldInfo().setGameType(mcServer.getGameType());
		}

		mcServer.setDifficultyForAllWorlds(mcServer.getDifficulty());
	}

}
