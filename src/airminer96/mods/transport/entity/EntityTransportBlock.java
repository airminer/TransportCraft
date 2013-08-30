package airminer96.mods.transport.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

import airminer96.mods.transport.Transport;
import airminer96.mods.transport.client.world.TransportWorldClient;
import airminer96.mods.transport.world.TransportWorldServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class EntityTransportBlock extends Entity implements IEntityAdditionalSpawnData {

	public static BitSet entityMap = new BitSet(Long.SIZE << 4);

	public static HashMap<Integer, Integer> idToDim = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> dimToId = new HashMap<Integer, Integer>();
	public static HashMap<Integer, ArrayList<EntityTransportBlock>> idToEnt = new HashMap<Integer, ArrayList<EntityTransportBlock>>();

	private int id;
	private int dimID;
	public int blockX;
	public int blockY;
	public int blockZ;
	private TransportWorldClient worldClient;

	public EntityTransportBlock(World par1World) {
		super(par1World);
		setSize(1F, 1F);
	}

	public EntityTransportBlock(World entityWorld, int id, int blockX, int blockY, int blockZ) {
		super(entityWorld);

		this.id = id;

		Transport.logger.info("EntityTransportBlock id " + id + " spawned");

		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;

		preventEntitySpawning = true;
		setSize(1F, 1F);
		// yOffset = height / 2.0F;
	}

	public static int getNextFreeID() {
		int next = 0;
		while (true) {
			next = entityMap.nextClearBit(next);
			if ((new File(DimensionManager.getCurrentSaveRootDirectory(), "Transport" + next)).exists()) {
				entityMap.set(next);
			} else {
				return next;
			}
		}
	}

	public void associateDim(int dim) {
		if (!idToEnt.containsKey(id)) {
			idToEnt.put(id, new ArrayList<EntityTransportBlock>());
			idToDim.put(id, dim);
			dimToId.put(dim, id);
		}
		if (!idToEnt.get(id).contains(this)) {
			idToEnt.get(id).add(this);
		}
	}

	public void dissociateDim() {
		idToEnt.get(id).remove(this);
		if (idToEnt.get(id).isEmpty()) {
			idToEnt.remove(id);
			dimToId.remove(idToDim.get(id));
			idToDim.remove(id);
		}
	}

	public World getTransportWorld() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			if (worldClient == null) {
				try {
					DimensionManager.registerDimension(dimID, Transport.providerID);
				} catch (IllegalArgumentException e) {
				}
				associateDim(dimID);
				worldClient = new TransportWorldClient(dimID);
			}
			return worldClient;
		} else {
			if (dimID == 0) {
				if (idToDim.containsKey(id)) {
					dimID = idToDim.get(id);
				} else {
					dimID = DimensionManager.getNextFreeDimId();
				}
			}
			try {
				DimensionManager.registerDimension(dimID, Transport.providerID);
			} catch (IllegalArgumentException e) {
			}
			if (DimensionManager.getWorld(dimID) == null) {
				associateDim(dimID);
				TransportWorldServer.initDimension(dimID);
			}
			return DimensionManager.getWorld(dimID);
		}
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they
	 * walk on. used for spiders and wolves to prevent them from trampling crops
	 */
	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void entityInit() {
	}

	/**
	 * Will get destroyed next tick.
	 */
	@Override
	public void setDead() {
		Transport.logger.info("DEAD!");
		if (!worldObj.isRemote) {
			dissociateDim();
			if (!idToEnt.containsKey(id)) {
				Transport.deleteQueue.add(dimID);
				DimensionManager.unloadWorld(dimID);
			}
		}
		isDead = true;
	}

	/**
	 * Called when the entity is attacked.
	 */
	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
		if (isEntityInvulnerable()) {
			return false;
		} else {
			if (!isDead && !worldObj.isRemote) {
				setDead();
				setBeenAttacked();
			}

			return true;
		}
	}

	/**
	 * Returns true if other Entities should be prevented from moving through
	 * this Entity.
	 */
	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}

	/**
	 * Returns a boundingBox used to collide the entity with other entities and
	 * blocks. This enables the entity to be pushable on contact, like boats or
	 * minecarts.
	 */
	@Override
	public AxisAlignedBB getCollisionBox(Entity par1Entity) {
		return par1Entity.boundingBox;
	}

	/**
	 * returns the bounding box for this entity
	 */
	@Override
	public AxisAlignedBB getBoundingBox() {
		Block block = Block.blocksList[getTransportWorld().getBlockId(blockX, blockY, blockZ)];
		if (block != null && block.getCollisionBoundingBoxFromPool(getTransportWorld(), blockX, blockY, blockZ) != null) {
			setSize((float) (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX()), (float) (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY()));
			return block.getCollisionBoundingBoxFromPool(getTransportWorld(), blockX, blockY, blockZ).getOffsetBoundingBox(posX - blockX - 0.5, posY - blockY, posZ - blockZ - 0.5);
		}
		return null;
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities
	 * when colliding.
	 */
	@Override
	public boolean canBePushed() {
		return true;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		// this.setDead();
		// this.setRotation(0, 0);
		// this.setRotation(rotationYaw + 1, 0);
		// this.setRotation(0, rotationPitch + 1);
	}

	/**
	 * Called when a player interacts with a mob. e.g. gets milk from a cow,
	 * gets into the saddle on a pig.
	 */
	@Override
	public boolean func_130002_c(EntityPlayer par1EntityPlayer) {
		Block block = Block.blocksList[getTransportWorld().getBlockId(blockX, blockY, blockZ)];
		// ItemStack item = par1EntityPlayer.getCurrentEquippedItem();
		// return
		// Minecraft.getMinecraft().playerController.onPlayerRightClick(par1EntityPlayer,
		// blockWorld, item, blockX, blockY, blockZ, 0,
		// Vec3.createVectorHelper(blockX, blockY, blockZ));
		if (block != null) {
			return block.onBlockActivated(getTransportWorld(), blockX, blockY, blockZ, par1EntityPlayer, 0, 0, 0, 0);
		}
		return false;
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		par1NBTTagCompound.setInteger("ID", id);
		par1NBTTagCompound.setInteger("BlockX", blockX);
		par1NBTTagCompound.setInteger("BlockY", blockY);
		par1NBTTagCompound.setInteger("BlockZ", blockZ);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		id = par1NBTTagCompound.getInteger("ID");
		blockX = par1NBTTagCompound.getInteger("BlockX");
		blockY = par1NBTTagCompound.getInteger("BlockY");
		blockZ = par1NBTTagCompound.getInteger("BlockZ");

	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeInt(id);
		getWorld();
		data.writeInt(dimID);
		data.writeInt(blockX);
		data.writeInt(blockY);
		data.writeInt(blockZ);

		data.writeInt(getTransportWorld().getBlockId(blockX, blockY, blockZ));
		data.writeInt(getTransportWorld().getBlockMetadata(blockX, blockY, blockZ));

		/*
		 * try {
		 * (new Packet51MapChunk(getTransportWorld().getWorld().getChunkFromChunkCoords(chunkCoordX, chunkCoordZ), true, 0)).writePacketData(data);
		 * } catch (IOException e) {
		 * e.printStackTrace();
		 * }
		 */
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		id = data.readInt();
		dimID = data.readInt();
		blockX = data.readInt();
		blockY = data.readInt();
		blockZ = data.readInt();

		((TransportWorldClient) getTransportWorld()).doPreChunk(blockX >> 4, blockZ >> 4, true);
		getTransportWorld().setBlock(blockX, blockY, blockZ, data.readInt(), data.readInt(), 0);

		/*
		 * WorldClient worldClient = ObfuscationReflectionHelper.getPrivateValue(NetClientHandler.class, Minecraft.getMinecraft().getNetHandler(), "worldClient");
		 * ObfuscationReflectionHelper.setPrivateValue(NetClientHandler.class, Minecraft.getMinecraft().getNetHandler(), getTransportWorld().getWorld(), "worldClient");
		 * Packet51MapChunk packet = new Packet51MapChunk();
		 * try {
		 * packet.readPacketData(data);
		 * Minecraft.getMinecraft().getNetHandler().handleMapChunk(packet);
		 * } catch (IOException e) {
		 * }
		 * ObfuscationReflectionHelper.setPrivateValue(NetClientHandler.class, Minecraft.getMinecraft().getNetHandler(), worldClient, "worldClient");
		 */
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getShadowSize() {
		return 0.0F;
	}

	@SideOnly(Side.CLIENT)
	public World getWorld() {
		return worldObj;
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Return whether this entity should be rendered as on fire.
	 */
	public boolean canRenderOnFire() {
		return false;
	}

}
