package airminer96.mods.transport.client.network;

import java.io.IOException;

import sun.reflect.ReflectionFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
//import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;

public class TransportNetClientHandler extends NetHandlerPlayClient {

	public TransportNetClientHandler(Minecraft p_i45061_1_, GuiScreen p_i45061_2_, NetworkManager p_i45061_3_) {
		super(p_i45061_1_, p_i45061_2_, p_i45061_3_);
	}

	public static NetHandlerPlayClient getNewInstance() {
		try {
			TransportNetClientHandler instance = (TransportNetClientHandler) ReflectionFactory.getReflectionFactory().newConstructorForSerialization(TransportNetClientHandler.class, Object.class.getConstructor()).newInstance();
			instance.mapStorageOrigin = new MapStorage((ISaveHandler) null);
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
