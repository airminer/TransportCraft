package airminer96.mods.transport;

import net.minecraft.world.World;

public class CommonProxy {

	// Client stuff
	public void registerRenderers() {
		// Nothing here as the server doesn't render graphics!
	}

	public World getTransportWorldClient(int dim) {
		return null;
	}
}
