package airminer96.mods.transport.world;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import airminer96.mods.transport.Transport;
import net.minecraft.logging.ILogAgent;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

public class TransportWorldServer extends WorldServerMulti {

	public static final ArrayList<Integer> deleteQueue = new ArrayList<Integer>();

	public TransportWorldServer(MinecraftServer par2MinecraftServer, ISaveHandler par3iSaveHandler, String par4Str, int par5, WorldSettings par6WorldSettings, WorldServer par7WorldServer, Profiler par8Profiler, ILogAgent par9iLogAgent) {
		super(par2MinecraftServer, par3iSaveHandler, par4Str, par5, par6WorldSettings, par7WorldServer, par8Profiler, par9iLogAgent);
	}

	@Override
	public void flush() {
		super.flush();
		try {
			DimensionManager.unregisterDimension(provider.dimensionId);
			DimensionManager.loadDimensionDataMap(null);
			Transport.logger.info("Dimension " + provider.dimensionId + " unregistered");
		} catch (IllegalArgumentException e) {
		}
		if (deleteQueue.contains(provider.dimensionId)) {
			recursiveDelete(new File(DimensionManager.getCurrentSaveRootDirectory(), provider.getSaveFolder()));
			deleteQueue.remove((Object) provider.dimensionId);
		}
	}

	private void recursiveDelete(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					recursiveDelete(f);
				}
			}
			file.delete();
			try {
				Transport.logger.info("File " + file.getCanonicalPath() + " deleted");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void initDimension(int dim) {
		WorldServer overworld = DimensionManager.getWorld(0);
		if (overworld == null) {
			throw new RuntimeException("Cannot Hotload Dim: Overworld is not Loaded!");
		}
		try {
			DimensionManager.getProviderType(dim);
		} catch (Exception e) {
			System.err.println("Cannot Hotload Dim: " + e.getMessage());
			return; // If a provider hasn't been registered then we can't hotload the dim
		}
		MinecraftServer mcServer = overworld.getMinecraftServer();
		ISaveHandler savehandler = overworld.getSaveHandler();
		WorldSettings worldSettings = new WorldSettings(overworld.getWorldInfo());

		WorldServer world = (dim == 0 ? overworld : new TransportWorldServer(mcServer, savehandler, overworld.getWorldInfo().getWorldName(), dim, worldSettings, overworld, mcServer.theProfiler, overworld.getWorldLogAgent()));
		world.addWorldAccess(new WorldManager(mcServer, world));
		MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
		if (!mcServer.isSinglePlayer()) {
			world.getWorldInfo().setGameType(mcServer.getGameType());
		}

		mcServer.setDifficultyForAllWorlds(mcServer.getDifficulty());
	}

}
