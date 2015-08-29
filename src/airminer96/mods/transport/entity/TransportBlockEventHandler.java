package airminer96.mods.transport.entity;

import java.util.List;

import airminer96.mods.transport.Transport;
import airminer96.mods.transport.world.TransportWorldServer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class TransportBlockEventHandler {

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

	@ForgeSubscribe
	public void playerOpenContainerEvent(PlayerOpenContainerEvent event) {
		Container container = event.entityPlayer.openContainer;
		if (container instanceof ContainerChest) {
			ContainerChest chest = (ContainerChest) container;
			if (chest.getLowerChestInventory() instanceof TileEntity) {
				if (((TileEntity) chest.getLowerChestInventory()).worldObj instanceof TransportWorldServer) {
					event.setResult(Event.Result.ALLOW);
				}
			}
		}
	}
}
