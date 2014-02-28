package airminer96.mods.transport.world;

import airminer96.mods.transport.entity.EntityTransportBlock;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;

public class TransportWorldProvider extends WorldProvider {

	int id;
	boolean idSet = false;

	@Override
	public IChunkProvider createChunkGenerator() {
		return terrainType.getChunkGenerator(worldObj, "2;0;1;");
	}

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
