package airminer96.mods.transport.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandWorld extends CommandBase {

	@Override
	public String getCommandName() {
		return "world";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var1 instanceof EntityPlayerMP && var2.length >= 1) {
			MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) var1, Integer.parseInt(var2[0]), new TransportTeleporter(MinecraftServer.getServer().worldServerForDimension(Integer.parseInt(var2[0]))));
		}
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		// TODO Auto-generated method stub
		return "";
	}

}
