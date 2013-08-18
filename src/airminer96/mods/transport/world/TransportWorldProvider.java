package airminer96.mods.transport.world;

import net.minecraft.world.WorldProvider;

public class TransportWorldProvider extends WorldProvider {

	@Override
	public String getDimensionName() {
		return "Transport World";
	}

	@Override
	public String getSaveFolder() {
		return "Transport" + ((TransportWorld) worldObj).getID();
	}

}
