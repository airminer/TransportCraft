package airminer96.mods.transport.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import airminer96.mods.transport.Transport;
import airminer96.mods.transport.client.world.TransportWorldClient;
import airminer96.mods.transport.world.TransportWorld;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class EntityTransportBlock extends Entity implements IEntityAdditionalSpawnData {
	private int id = 0;
	private int dimID = 0;
	public int blockX;
	public int blockY;
	public int blockZ;
	private TransportWorldClient worldClient;

	public EntityTransportBlock(World par1World) {
		super(par1World);
		setSize(1F, 1F);
	}

	public EntityTransportBlock(World par1World, double par2, double par4, double par6, int par8, int par9, int par10) {
		super(par1World);
		File lastID = new File(DimensionManager.getCurrentSaveRootDirectory(), "TransportID");
		if (lastID.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(lastID));
				id = Integer.parseInt(reader.readLine()) + 1;
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			id = 0;
		}
		FileWriter writer;
		try {
			writer = new FileWriter(lastID);
			writer.write(String.valueOf(id));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		blockX = par8;
		blockY = par9;
		blockZ = par10;
		preventEntitySpawning = true;
		setSize(1F, 1F);
		yOffset = height / 2.0F;
		setPosition(par2, par4, par6);
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
		prevPosX = par2;
		prevPosY = par4;
		prevPosZ = par6;
	}

	public TransportWorld getTransportWorld() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			if (worldClient == null) {
				worldClient = new TransportWorldClient(id, dimID);
			}
			return worldClient;
		} else {
			if (dimID == 0) {
				dimID = DimensionManager.getNextFreeDimId();
				DimensionManager.registerDimension(dimID, Transport.providerID);
			}
			if (DimensionManager.getWorld(dimID) == null) {
				TransportWorldServer.initDimension(id, dimID);
			}
			return (TransportWorld) DimensionManager.getWorld(dimID);
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
		if (!worldObj.isRemote) {
			DimensionManager.unloadWorld(dimID);
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
		Block block = Block.blocksList[getTransportWorld().getWorld().getBlockId(blockX, blockY, blockZ)];
		if (block != null && block.getCollisionBoundingBoxFromPool(getTransportWorld().getWorld(), blockX, blockY, blockZ) != null) {
			setSize((float) (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX()), (float) (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY()));
			return block.getCollisionBoundingBoxFromPool(getTransportWorld().getWorld(), blockX, blockY, blockZ).getOffsetBoundingBox(posX - blockX - 0.5, posY - blockY, posZ - blockZ - 0.5);
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
		Block block = Block.blocksList[getTransportWorld().getWorld().getBlockId(blockX, blockY, blockZ)];
		ItemStack item = par1EntityPlayer.getCurrentEquippedItem();
		// return
		// Minecraft.getMinecraft().playerController.onPlayerRightClick(par1EntityPlayer,
		// blockWorld, item, blockX, blockY, blockZ, 0,
		// Vec3.createVectorHelper(blockX, blockY, blockZ));
		return block.onBlockActivated(getTransportWorld().getWorld(), blockX, blockY, blockZ, par1EntityPlayer, 0, 0, 0, 0);
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
		if (dimID == 0) {
			getTransportWorld();
		}
		data.writeInt(dimID);
		data.writeInt(blockX);
		data.writeInt(blockY);
		data.writeInt(blockZ);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		id = data.readInt();
		dimID = data.readInt();
		blockX = data.readInt();
		blockY = data.readInt();
		blockZ = data.readInt();
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
