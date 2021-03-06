package airminer96.mods.transport.client;

import net.minecraft.world.World;
import airminer96.mods.transport.CommonProxy;
import airminer96.mods.transport.client.renderer.entity.RenderTransportBlock;
import airminer96.mods.transport.client.world.TransportWorldClient;
import airminer96.mods.transport.entity.EntityTransportBlock;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityTransportBlock.class, new RenderTransportBlock());
	}

	@Override
	public World getTransportWorldClient(int dim) {
		return new TransportWorldClient(dim);
	}

}