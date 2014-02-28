package airminer96.mods.transport.command;

import java.util.HashMap;

import airminer96.mods.transport.entity.EntityTransportBlock;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
		if (var1 instanceof EntityPlayerMP) {
			String username = ((EntityPlayerMP) var1).username;
			if (!activePlayers.containsKey(username)) {
				activePlayers.put(username, new Integer[] { 0, 0, 0, 0, 0, 0, 0 });
				var1.sendChatToPlayer(ChatMessageComponent.createFromText("Mark two opposite vertices of the cuboid you want to transform"));
			} else {
				activePlayers.remove(username);
				var1.sendChatToPlayer(ChatMessageComponent.createFromText("Action cancalled"));
			}
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void playerInteractEvent(PlayerInteractEvent event) {
		World world = event.entityPlayer.worldObj;
		if (activePlayers.containsKey(event.entityPlayer.username) && !world.isRemote) {
			event.setCanceled(true);
			Integer[] array = activePlayers.get(event.entityPlayer.username);
			if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
				array[0] = (array[0] & 2) + 1;
				array[1] = event.x;
				array[2] = event.y;
				array[3] = event.z;
				event.entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Pos1 set"));
			} else if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
				array[0] = (array[0] & 1) + 2;
				array[4] = event.x;
				array[5] = event.y;
				array[6] = event.z;
				event.entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Pos2 set"));
			}
			if (array[0] == 3) {
				transformCuboid(world, array[1], array[2], array[3], array[4], array[5], array[6]);
				event.entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Action succeded."));
				activePlayers.remove(event.entityPlayer.username);
			}
		}
	}

	private void transformCuboid(World world, int xa, int ya, int za, int xb, int yb, int zb) {

		int id = EntityTransportBlock.getNextFreeID();
		World blockWorld = null;

		int x1, y1, z1, x2, y2, z2;

		if (xa <= xb) {
			x1 = xa;
			x2 = xb;
		} else {
			x1 = xb;
			x2 = xa;
		}

		if (ya <= yb) {
			y1 = ya;
			y2 = yb;
		} else {
			y1 = yb;
			y2 = ya;
		}

		if (za <= zb) {
			z1 = za;
			z2 = zb;
		} else {
			z1 = zb;
			z2 = za;
		}

		for (int x = x1; x <= x2; x++) {
			int blockX = x - (x1 + x2) / 2;
			for (int y = y1; y <= y2; y++) {
				int blockY = 128 + y - (y1 + y2) / 2;
				for (int z = z1; z <= z2; z++) {
					int blockZ = z - (z1 + z2) / 2;
					EntityTransportBlock entity = new EntityTransportBlock(world, id, blockX, blockY, blockZ);
					entity.setPosition(x + 0.5, y - y1 + y2 + 1, z + 0.5);
					if (blockWorld == null) {
						blockWorld = entity.getTransportWorld();
					}
					TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
					blockWorld.setBlock(blockX, blockY, blockZ, world.getBlockId(x, y, z));
					blockWorld.setBlockMetadataWithNotify(blockX, blockY, blockZ, world.getBlockMetadata(x, y, z), 3);
					if (tileEntity != null) {
						NBTTagCompound nbtTag = new NBTTagCompound();
						tileEntity.writeToNBT(nbtTag);
						blockWorld.setBlockTileEntity(blockX, blockY, blockZ, TileEntity.createAndLoadEntity(nbtTag));
					}
					world.spawnEntityInWorld(entity);
				}
			}
		}
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		// TODO Auto-generated method stub
		return "";
	}

}
