package com.arc.bloodarsenal.common.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.world.World;

public class EntityBloodTNT extends EntityTNTPrimed {

    private EntityLivingBase tntPlacedBy;

    @SuppressWarnings("unused")
    public EntityBloodTNT(World world) {
        super(world);
        fuse = 60;
    }

    public EntityBloodTNT(World world, double x, double y, double z, EntityLivingBase owner) {
        super(world, x, y, z, owner);
        fuse = 60;
        tntPlacedBy = owner;
    }

    @Override
    public void entityInit() {}

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        motionY -= 0.04D;
        moveEntity(motionX, motionY, motionZ);
        motionX *= 0.98D;
        motionY *= 0.98D;
        motionZ *= 0.98D;

        if (onGround) {
            motionX *= 0.7D;
            motionZ *= 0.7D;
            motionY *= -0.5D;
        }

        if (fuse-- <= 0) {
            setDead();

            if (!worldObj.isRemote) {
                explode();
            }
        } else {
            worldObj.spawnParticle("smoke", posX, posY + 0.5D, posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    private void explode() {
        float f = 6.0F;
        worldObj.createExplosion(this, posX, posY, posZ, f, true);
    }

    public EntityLivingBase getTntPlacedBy() {
        return tntPlacedBy;
    }
}
