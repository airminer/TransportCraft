package airminer96.mods.transport.client.network;

import java.io.IOException;
import sun.reflect.ReflectionFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;

public class TransportNetClientHandler extends NetClientHandler {

	private TransportNetClientHandler(Minecraft par1Minecraft, String par2Str, int par3) throws IOException {
		super(par1Minecraft, par2Str, par3);
	}

	public static TransportNetClientHandler getNewInstance() {
		try {
			TransportNetClientHandler instance = (TransportNetClientHandler) ReflectionFactory.getReflectionFactory().newConstructorForSerialization(TransportNetClientHandler.class, Object.class.getConstructor()).newInstance();
			instance.mapStorage = new MapStorage((ISaveHandler) null);
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
