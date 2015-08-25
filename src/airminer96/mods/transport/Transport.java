package airminer96.mods.transport;

import java.util.logging.Logger;

import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import airminer96.mods.transport.client.network.TransportClientPacketHandler;
import airminer96.mods.transport.command.CommandTransport;
import airminer96.mods.transport.command.CommandWorld;
import airminer96.mods.transport.entity.EntityTransportBlock;
import airminer96.mods.transport.entity.TransportChunkUnloadHandler;
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
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.registry.EntityRegistry;

@Mod(modid = Transport.ID, name = Transport.ID, version = Transport.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, clientPacketHandlerSpec = @SidedPacketHandler(channels = { Transport.ID }, packetHandler = TransportClientPacketHandler.class))
public class Transport {

	public static final String ID = "TransportCraft";
	public static final String VERSION = "0.0.1";

	@Instance(Transport.ID)
	public static Transport instance;

	@SidedProxy(clientSide = "airminer96.mods.transport.client.ClientProxy", serverSide = "airminer96.mods.transport.CommonProxy")
	public static CommonProxy proxy;

	public static Logger logger;

	public static int providerID;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = Logger.getLogger(ID);
		logger.setParent(FMLLog.getLogger());
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		EntityRegistry.registerModEntity(EntityTransportBlock.class, "EntityTransportBlock", 1, instance, 250, 5, true);
		proxy.registerRenderers();
		providerID = 2;
		while (!DimensionManager.registerProviderType(providerID, TransportWorldProvider.class, true))
			providerID++;
		logger.info("TransportWorldProvider successfully registered with ID " + providerID);
		MinecraftForge.EVENT_BUS.register(new TransportChunkUnloadHandler());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// Stub Method
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent event) {
		ServerCommandManager cm = (ServerCommandManager) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
		cm.registerCommand(new CommandTransport());
		cm.registerCommand(new CommandWorld());
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		EntityTransportBlock.idToDim.clear();
		EntityTransportBlock.dimToId.clear();
		EntityTransportBlock.idToEntServer.clear();
		EntityTransportBlock.idToEntClient.clear();
		EntityTransportBlock.worldClients.clear();
		logger.info("EntityTransportBlock lists cleared");
		for (int id : DimensionManager.getStaticDimensionIDs()) {
			if (DimensionManager.getProviderType(id) == providerID) {
				DimensionManager.unregisterDimension(id);
				DimensionManager.loadDimensionDataMap(null);
				logger.info("Dimension id " + id + " unregistered");
			}
		}
	}

}
