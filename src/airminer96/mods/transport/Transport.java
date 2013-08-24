package airminer96.mods.transport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import airminer96.mods.transport.command.CommandTransport;
import airminer96.mods.transport.entity.EntityTransportBlock;
import airminer96.mods.transport.world.TransportWorld;
import airminer96.mods.transport.world.TransportWorldProvider;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;

@Mod(modid = Transport.ID, name = Transport.ID, version = Transport.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { Transport.ID })
public class Transport {

	public static final String ID = "TransportCraft";
	public static final String VERSION = "0.0.1";

	@Instance(Transport.ID)
	public static Transport instance;

	@SidedProxy(clientSide = "airminer96.mods.transport.client.ClientProxy", serverSide = "airminer96.mods.transport.CommonProxy")
	public static CommonProxy proxy;

	public static Logger logger;

	public static int providerID;

	public static final ArrayList<Integer> deleteQueue = new ArrayList<Integer>();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = Logger.getLogger(ID);
		logger.setParent(FMLLog.getLogger());
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		EntityRegistry.registerModEntity(EntityTransportBlock.class, "EntityAircraftBlock", 1, instance, 250, 5, true);
		proxy.registerRenderers();
		providerID = 2;
		while (!DimensionManager.registerProviderType(providerID, TransportWorldProvider.class, true))
			providerID++;
		logger.info("TransportWorldProvider successfully registered with ID " + providerID);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// Stub Method
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent event) {
		ServerCommandManager cm = (ServerCommandManager) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
		cm.registerCommand(new CommandTransport());
	}

	@ForgeSubscribe
	public void worldUnload(WorldEvent.Unload event) {
		if (event.world.provider instanceof TransportWorldProvider) {
			try {
				DimensionManager.unregisterDimension(event.world.provider.dimensionId);
				DimensionManager.loadDimensionDataMap(null);
				logger.info("Dimension " + event.world.provider.dimensionId + " unregistered");
			} catch (IllegalArgumentException e) {
			}
			if (deleteQueue.contains(event.world.provider.dimensionId)) {
				recursiveDelete(new File(DimensionManager.getCurrentSaveRootDirectory(), event.world.provider.getSaveFolder()));
				deleteQueue.remove((Object) event.world.provider.dimensionId);
			}
			EntityTransportBlock.entityMap.clear(TransportWorld.worldIDs.get(event.world.provider.dimensionId));
			TransportWorld.worldIDs.remove(event.world.provider.dimensionId);
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
				logger.info("File " + file.getCanonicalPath() + " deleted");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		for (int id : DimensionManager.getStaticDimensionIDs()) {
			if (DimensionManager.getProviderType(id) == providerID) {
				DimensionManager.unregisterDimension(id);
				DimensionManager.loadDimensionDataMap(null);
				logger.info("Dimension id " + id + " unregistered");
			}
		}
	}

}
