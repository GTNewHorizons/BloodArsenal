package com.arc.bloodarsenal.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import com.arc.bloodarsenal.common.entity.EntityBloodTNT;
import com.arc.bloodarsenal.common.items.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBloodTNT extends BlockTNT {

    @SideOnly(Side.CLIENT)
    private IIcon top;

    @SideOnly(Side.CLIENT)
    private IIcon bottom;

    public BlockBloodTNT() {
        super();
        setHardness(0.0F);
        setStepSound(soundTypeGrass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side == 0 ? bottom : (side == 1 ? top : blockIcon);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);

        if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
            onBlockDestroyedByPlayer(world, x, y, z, 1);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
            onBlockDestroyedByPlayer(world, x, y, z, 1);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        if (!world.isRemote) {
            EntityBloodTNT entitytntprimed = new EntityBloodTNT(
                    world,
                    (float) x + 0.5F,
                    (float) y + 0.5F,
                    (float) z + 0.5F,
                    explosion.getExplosivePlacedBy());
            entitytntprimed.fuse = world.rand.nextInt(entitytntprimed.fuse / 4) + entitytntprimed.fuse / 8;
            world.spawnEntityInWorld(entitytntprimed);
        }
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
        spawnTNT(world, x, y, z, meta, null);
    }

    public void spawnTNT(World world, int x, int y, int z, int meta, EntityLivingBase entity) {
        if (!world.isRemote) {
            if ((meta & 1) == 1) {
                EntityBloodTNT entitytntprimed = new EntityBloodTNT(
                        world,
                        (float) x + 0.5F,
                        (float) y + 0.5F,
                        (float) z + 0.5F,
                        entity);
                world.spawnEntityInWorld(entitytntprimed);
                world.playSoundAtEntity(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
            float subY, float subZ) {
        if (player.getCurrentEquippedItem() != null
                && (player.getCurrentEquippedItem().getItem() == Items.flint_and_steel
                        || player.getCurrentEquippedItem().getItem() == ModItems.bound_igniter)) {
            spawnTNT(world, x, y, z, 1, player);
            world.setBlockToAir(x, y, z);
            player.getCurrentEquippedItem().damageItem(1, player);
            return true;
        } else {
            return super.onBlockActivated(world, x, y, z, player, side, subX, subY, subZ);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!(entity instanceof EntityArrow entityarrow) || world.isRemote || !entityarrow.isBurning()) {
            return;
        }
        spawnTNT(
                world,
                x,
                y,
                z,
                1,
                entityarrow.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) entityarrow.shootingEntity
                        : null);
        world.setBlockToAir(x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon("BloodArsenal:blood_tnt_side");
        top = iconRegister.registerIcon("BloodArsenal:blood_tnt_top");
        bottom = iconRegister.registerIcon("BloodArsenal:blood_tnt_bottom");
    }
}
