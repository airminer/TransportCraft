package airminer96.mods.transport;

import java.util.ArrayList;
import java.util.logging.Logger;

import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.common.MinecraftForge;
import airminer96.mods.transport.entity.EntityTransportBlock;
import airminer96.mods.transport.server.command.CommandTransport;
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
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;

@Mod(modid=Transport.ID, name=Transport.ID, version=Transport.VERSION)
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels={Transport.ID})
public class Transport {

	public static final String ID = "TransportCraft";
	public static final String VERSION = "0.0.1";

	@Instance(Transport.ID)
	public static Transport instance;

	@SidedProxy(clientSide="airminer96.mods.transport.client.ClientProxy", serverSide="airminer96.mods.transport.CommonProxy")
	public static CommonProxy proxy;

	public static Logger logger;

	private static ArrayList<Integer> replaceQueue = new ArrayList<Integer>();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = Logger.getLogger(ID);
		logger.setParent(FMLLog.getLogger());
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		EntityRegistry.registerModEntity(EntityTransportBlock.class,"EntityAircraftBlock", 1, instance, 250,5, true);
		proxy.registerRenderers();
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

}
