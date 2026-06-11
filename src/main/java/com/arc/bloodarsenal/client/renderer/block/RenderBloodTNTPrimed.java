package com.arc.bloodarsenal.client.renderer.block;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.arc.bloodarsenal.common.block.ModBlocks;
import com.arc.bloodarsenal.common.entity.EntityBloodTNT;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBloodTNTPrimed extends Render {

    private final RenderBlocks blockRenderer = new RenderBlocks();

    public RenderBloodTNTPrimed() {
        this.shadowSize = 0.5F;
    }

    public void doRender(EntityBloodTNT entity, double x, double y, double z, float renderTicks) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);

        float timer = entity.fuse - renderTicks + 1.0F;
        if (timer < 10.0F) {
            float s = 1.0F - timer / 10.0F;

            if (s < 0.0F) {
                s = 0.0F;
            }

            if (s > 1.0F) {
                s = 1.0F;
            }

            float scale = 1.0F + s * s * s * 0.3F;
            GL11.glScalef(scale, scale, scale);
        }

        this.bindEntityTexture(entity);
        this.blockRenderer.renderBlockAsItem(ModBlocks.blood_tnt, 0, entity.getBrightness(renderTicks));

        if (entity.fuse / 5 % 2 == 0) {
            float flashAlpha = (1.0F - timer / 100.0F) * 0.8F;
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, flashAlpha);
            this.blockRenderer.renderBlockAsItem(ModBlocks.blood_tnt, 0, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        GL11.glPopMatrix();
    }

    protected ResourceLocation getEntityTexture() {
        return TextureMap.locationBlocksTexture;
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture();
    }

    public void doRender(Entity entity, double x, double y, double z, float ignored, float renderTicks) {
        this.doRender((EntityBloodTNT) entity, x, y, z, renderTicks);
    }
}
