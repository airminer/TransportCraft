package airminer96.mods.transport;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraftforge.common.DimensionManager;
import airminer96.mods.transport.entity.EntityTransportBlock;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public class TransportTickHandler implements IScheduledTickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		Transport.logger.info("TICK");
		for (Entry<Integer, ArrayList<EntityTransportBlock>> entry : ((HashMap<Integer, ArrayList<EntityTransportBlock>>) EntityTransportBlock.idToEntServer.clone()).entrySet()) {
			for (EntityTransportBlock entity : (ArrayList<EntityTransportBlock>) entry.getValue().clone()) {
				Transport.logger.info("Checking entity " + entity);
				if (entity.worldObj.getEntityByID(entity.entityId) != entity && entity.getTransportWorld().playerEntities.isEmpty()) {
					entity.dissociateDim(false);
					Transport.logger.info("Entity" + entity + " removed");
					if (!EntityTransportBlock.idToEntServer.containsKey(entity.id)) {
						DimensionManager.unloadWorld(entity.dimID);
					}
				}
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return "TransportTickHandler";
	}

	@Override
	public int nextTickSpacing() {
		return 1200;
	}

}