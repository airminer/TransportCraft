package airminer96.mods.transport.world;

import airminer96.mods.transport.entity.EntityTransportBlock;
import net.minecraft.world.WorldProvider;

public class TransportWorldProvider extends WorldProvider {

	@Override
	public String getDimensionName() {
		return "Transport World";
	}

	@Override
	public String getSaveFolder() {
		return "Transport" + EntityTransportBlock.dimToId.get(dimensionId);
	}

}
