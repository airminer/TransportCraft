package airminer96.mods.transport.world;

import java.util.HashMap;

import net.minecraft.world.World;

public interface TransportWorld {

	public static final HashMap<Integer, Integer> worldIDs = new HashMap<Integer, Integer>();

	public World getWorld();

}
