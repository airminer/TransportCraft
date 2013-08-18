package airminer96.mods.transport.command;

import java.util.HashMap;
import airminer96.mods.transport.entity.EntityTransportBlock;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class CommandTransport extends CommandBase {

	public CommandTransport() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	private HashMap<String, Integer[]> activePlayers = new HashMap<String, Integer[]>();

	@Override
	public String getCommandName() {
		return "tc";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		/*
		 * World world = ((EntityPlayerMP) var1).worldObj; EntityAircraftBlock
		 * entity = new EntityAircraftBlock(world, 56, 56, 56, 1, 0);
		 * world.spawnEntityInWorld(entity);
		 */
		if (var1 instanceof EntityPlayerMP) {
			String username = ((EntityPlayerMP) var1).username;
			if (!activePlayers.containsKey(username)) {
				if (var2.length >= 3) {
					Integer[] array = { new Integer(var2[0]), new Integer(var2[1]), new Integer(var2[2]) };
					activePlayers.put(username, array);
				} else {
					activePlayers.put(username, new Integer[0]);
				}
				var1.sendChatToPlayer(ChatMessageComponent.func_111066_d("Rigchtclick a block to make it an entity"));
			} else {
				activePlayers.remove(username);
				var1.sendChatToPlayer(ChatMessageComponent.func_111066_d("Action cancalled"));
			}
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void playerInteractEvent(PlayerInteractEvent event) {
		// System.out.println("event!");
		if (activePlayers.containsKey(event.entityPlayer.username)) {
			/*
			 * if
			 * (event.action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK
			 * )) { info.setPoint1(point);
			 * player.addChatMessage(FEChatFormatCodes.PURPLE + "Pos1 set to " +
			 * event.x + ", " + event.y + ", " + event.z);
			 * event.setCanceled(true); }
			 */
			// right Click
			World world = event.entityPlayer.worldObj;
			if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) && !world.isRemote) {
				int x;
				int y;
				int z;
				Integer[] array = activePlayers.get(event.entityPlayer.username);
				if (array.length == 3) {
					x = array[0];
					y = array[1];
					z = array[2];
				} else {
					x = event.x;
					y = event.y;
					z = event.z;
				}
				event.setCanceled(true);
				// world.setBlock(event.x, event.y, event.z, 0);
				EntityTransportBlock entity = new EntityTransportBlock(world, (event.x + 0.5F), (event.y + 1F), (event.z + 0.5F), x, y, z);
				world.spawnEntityInWorld(entity);
				event.entityPlayer.sendChatToPlayer(ChatMessageComponent.func_111066_d(x + " " + y + " " + z));
				event.entityPlayer.sendChatToPlayer(ChatMessageComponent.func_111066_d("Action succeded."));
				activePlayers.remove(event.entityPlayer.username);
			}

		}

	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		// TODO Auto-generated method stub
		return "";
	}

}
