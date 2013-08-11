package airminer96.mods.transport.world;

import airminer96.mods.transport.entity.EntityTransportBlock;
import net.minecraft.logging.ILogAgent;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;

public class TransportWorldServer extends WorldServerMulti {

	public EntityTransportBlock entity;

	public TransportWorldServer(EntityTransportBlock par1EntityTransportBlock, MinecraftServer par2MinecraftServer, ISaveHandler par3iSaveHandler, String par4Str, int par5, WorldSettings par6WorldSettings, WorldServer par7WorldServer, Profiler par8Profiler, ILogAgent par9iLogAgent) {
		super(par2MinecraftServer, par3iSaveHandler, par4Str, par5, par6WorldSettings, par7WorldServer, par8Profiler, par9iLogAgent);
		entity = par1EntityTransportBlock;

	}

}
