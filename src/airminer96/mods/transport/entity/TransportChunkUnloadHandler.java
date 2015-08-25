package airminer96.mods.transport.entity;

import java.util.List;
import airminer96.mods.transport.Transport;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkEvent;

public class TransportChunkUnloadHandler {

	@ForgeSubscribe
	public void chunkUnloadEvent(ChunkEvent.Unload event) {
		if (!event.world.isRemote) {
			for (List entityList : event.getChunk().entityLists) {
				for (Object entity : entityList) {
					if (entity instanceof EntityTransportBlock) {
						EntityTransportBlock block = (EntityTransportBlock) entity;
						if (block.getTransportWorld().playerEntities.isEmpty()) {
							block.dissociateDim(false);
							Transport.logger.info(block + " removed");
							if (!EntityTransportBlock.idToEntServer.containsKey(block.id)) {
								DimensionManager.unloadWorld(block.dimID);
							}
						}
					}
				}
			}
		}
	}
}
