package airminer96.mods.transport.world;

import java.util.Collection;
import java.util.HashMap;

import airminer96.mods.transport.entity.EntityTransportBlock;
import airminer96.mods.transport.util.Point3D;

public class TransportBlockContainer {

	private HashMap<Point3D, EntityTransportBlock> hashMap = new HashMap<Point3D, EntityTransportBlock>();

	public HashMap<Point3D, EntityTransportBlock> getHashMap() {
		return hashMap;
	}

	public Collection<EntityTransportBlock> getBlocks() {
		return ((HashMap<Point3D, EntityTransportBlock>) hashMap.clone()).values();
	}

	public EntityTransportBlock getBlock(int x, int y, int z) {
		return hashMap.get(new Point3D(x, y, z));
	}

	public void setBlock(int x, int y, int z, EntityTransportBlock block) {
		hashMap.put(new Point3D(x, y, z), block);
	}

	public void removeBlock(int x, int y, int z) {
		hashMap.remove(new Point3D(x, y, z));
	}

	public void remove(EntityTransportBlock block) {
		if (getBlock(block.blockX, block.blockY, block.blockZ) == block)
			removeBlock(block.blockX, block.blockY, block.blockZ);
	}

	public boolean isEmpty() {
		return hashMap.isEmpty();
	}

	public boolean contains(EntityTransportBlock block) {
		return hashMap.containsValue(block);
	}

	public void add(EntityTransportBlock block) {
		setBlock(block.blockX, block.blockY, block.blockZ, block);
	}

}
