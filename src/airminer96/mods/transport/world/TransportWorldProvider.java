package airminer96.mods.transport.world;

import airminer96.mods.transport.entity.EntityTransportBlock;
import net.minecraft.world.WorldProvider;

public class TransportWorldProvider extends WorldProvider {

	int id;
	boolean idSet = false;

	@Override
	public String getDimensionName() {
		return "Transport World";
	}

	@Override
	public String getSaveFolder() {
		if (!idSet) {
			id = EntityTransportBlock.dimToId.get(dimensionId);
			idSet = true;
		}
		return "Transport" + id;
	}

}
