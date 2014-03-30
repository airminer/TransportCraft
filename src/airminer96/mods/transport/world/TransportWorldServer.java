package airminer96.mods.transport.world;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import airminer96.mods.transport.Transport;
import airminer96.mods.transport.network.TransportNetServerHandler;
import net.minecraft.logging.ILogAgent;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.common.FakePlayerFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

public class TransportWorldServer extends WorldServerMulti {

	public static final ArrayList<Integer> deleteQueue = new ArrayList<Integer>();

	public TransportWorldServer(MinecraftServer par1MinecraftServer, ISaveHandler par2iSaveHandler, String par3Str, int par4, WorldSettings par5WorldSettings, WorldServer par6WorldServer, Profiler par7Profiler, ILogAgent par8iLogAgent) {
		super(par1MinecraftServer, par2iSaveHandler, par3Str, par4, par5WorldSettings, par6WorldServer, par7Profiler, par8iLogAgent);
		FakePlayer fakePlayer = FakePlayerFactory.get(this, "["+Transport.ID+"]");
		new TransportNetServerHandler(par1MinecraftServer, fakePlayer);
		getPlayerManager().getOrCreateChunkWatcher(0, 0, true).addPlayer(fakePlayer);
		fakePlayer.loadedChunks.clear();
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
