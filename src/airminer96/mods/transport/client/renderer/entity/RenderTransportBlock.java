package airminer96.mods.transport.client.renderer.entity;

import airminer96.mods.transport.entity.EntityTransportBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderTransportBlock extends Render
{

	/**
	 * The actual render method that is used in doRender
	 */
	public void doRenderAircraftBlock(EntityTransportBlock par1EntityAircraftBlock, double par2, double par4, double par6, float par8, float par9)
	{
		
		this.renderBlocks = new RenderBlocks(par1EntityAircraftBlock.worldObj);
		Block block = Block.blocksList[par1EntityAircraftBlock.blockWorld.getBlockId(par1EntityAircraftBlock.blockX, par1EntityAircraftBlock.blockY, par1EntityAircraftBlock.blockZ)];
		
		//System.out.println(par1EntityAircraftBlock.blockWorld.getBlockId(par1EntityAircraftBlock.blockX, par1EntityAircraftBlock.blockY, par1EntityAircraftBlock.blockZ));
		
		if(block == null) return;	

		TileEntity tileEntity = par1EntityAircraftBlock.blockWorld.getBlockTileEntity(par1EntityAircraftBlock.blockX, par1EntityAircraftBlock.blockY, par1EntityAircraftBlock.blockZ);
		if(tileEntity != null) {
			TileEntityRenderer.instance.renderTileEntityAt(tileEntity, par2 - 0.5D, par4, par6 - 0.5D, par9);
		}
		func_110776_a(TextureMap.field_110575_b);

		Tessellator tessellator = Tessellator.instance;
		RenderHelper.disableStandardItemLighting();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);

		if (Minecraft.isAmbientOcclusionEnabled())
		{
			GL11.glShadeModel(GL11.GL_SMOOTH);
		}
		else
		{
			GL11.glShadeModel(GL11.GL_FLAT);
		}

		tessellator.startDrawingQuads();
		tessellator.setTranslation(((float)par2 - (float)par1EntityAircraftBlock.posX), ((float)par4 - (float)par1EntityAircraftBlock.posY + 1), ((float)par6 - (float)par1EntityAircraftBlock.posZ));
		tessellator.setColorOpaque(1, 1, 1);

		this.renderBlocks.renderBlockAllFaces(block, par1EntityAircraftBlock.blockX, par1EntityAircraftBlock.blockY, par1EntityAircraftBlock.blockZ);

		tessellator.setTranslation(0.0D, 0.0D, 0.0D);
		tessellator.draw();
		RenderHelper.enableStandardItemLighting();
		
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	@Override
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		this.doRenderAircraftBlock((EntityTransportBlock)par1Entity, par2, par4, par6, par8, par9);
	}

	@Override
	protected ResourceLocation func_110775_a(Entity entity) {
		// TODO Auto-generated method stub
		//return TextureMap.field_110575_b;
		return null;
	}
}