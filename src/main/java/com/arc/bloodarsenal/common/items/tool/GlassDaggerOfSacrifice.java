package com.arc.bloodarsenal.common.items.tool;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.arc.bloodarsenal.common.BloodArsenal;
import com.arc.bloodarsenal.common.BloodArsenalConfig;
import com.google.common.collect.Multimap;

import WayofTime.alchemicalWizardry.AlchemicalWizardry;
import WayofTime.alchemicalWizardry.api.tile.IBloodAltar;
import WayofTime.alchemicalWizardry.common.IDemon;
import WayofTime.alchemicalWizardry.common.demonVillage.demonHoard.demon.IHoardDemon;
import WayofTime.alchemicalWizardry.common.items.EnergyItems;
import WayofTime.alchemicalWizardry.common.spell.complex.effect.SpellHelper;

public class GlassDaggerOfSacrifice extends EnergyItems {

    private float weaponDamage;

    public GlassDaggerOfSacrifice() {
        super();
        setMaxStackSize(1);
        setEnergyUsed(100);
        setFull3D();
        setMaxDamage(100);
        weaponDamage = 3.0F;
    }

    @Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase,
            EntityLivingBase par3EntityLivingBase) {
        if (par3EntityLivingBase == null || par2EntityLivingBase == null
                || par3EntityLivingBase.worldObj.isRemote
                || (par3EntityLivingBase instanceof EntityPlayer && SpellHelper
                        .isFakePlayer(par3EntityLivingBase.worldObj, (EntityPlayer) par3EntityLivingBase))) {
            return false;
        }

        if (par2EntityLivingBase instanceof IHoardDemon) {
            return false;
        }

        if (par2EntityLivingBase.isChild() || par2EntityLivingBase instanceof EntityPlayer
                || par2EntityLivingBase instanceof IBossDisplayData) {
            return false;
        }

        World world = par2EntityLivingBase.worldObj;

        if (par2EntityLivingBase.isDead || par2EntityLivingBase.getHealth() < 0.5f) {
            return false;
        }

        if (par2EntityLivingBase instanceof IDemon) {
            ((IDemon) par2EntityLivingBase).setDropCrystal(false);
            this.findAndNotifyAltarOfDemon(world, par2EntityLivingBase);
        }

        int lifeEssence = AlchemicalWizardry.lpPerSactificeCustom.containsKey(par2EntityLivingBase.getClass())
                ? AlchemicalWizardry.lpPerSactificeCustom.get(par2EntityLivingBase.getClass())
                : AlchemicalWizardry.lpPerSacrificeBase;

        if (par2EntityLivingBase instanceof EntityVillager) {
            lifeEssence += 500;
        } else {
            lifeEssence += 100;
        }

        int criticalHit = world.rand.nextInt(9);
        if ((par2EntityLivingBase instanceof EntityVillager && criticalHit <= 2) || criticalHit <= 1) lifeEssence *= 5;

        if (findAndFillAltar(par2EntityLivingBase.worldObj, par2EntityLivingBase, lifeEssence)) {
            double posX = par2EntityLivingBase.posX;
            double posY = par2EntityLivingBase.posY;
            double posZ = par2EntityLivingBase.posZ;

            for (int i = 0; i < 8; i++) {
                SpellHelper.sendIndexedParticleToAllAround(
                        world,
                        posX,
                        posY,
                        posZ,
                        20,
                        world.provider.dimensionId,
                        1,
                        posX,
                        posY,
                        posZ);
            }

            par2EntityLivingBase.setHealth(-1);
            par2EntityLivingBase.onDeath(BloodArsenal.deathFromBlood);
        }

        if (!par2EntityLivingBase.isDead) {
            par2EntityLivingBase.addPotionEffect(new PotionEffect(BloodArsenalConfig.bleedingID, 20, 0));
        }

        return false;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        par3List.add(StatCollector.translateToLocal("tooltip.tool.glassDaggerOfSacrifice"));
    }

    @Override
    public float func_150893_a(ItemStack par1ItemStack, Block par2Block) {
        if (par2Block == Blocks.web) {
            return 15.0F;
        } else {
            Material material = par2Block.getMaterial();
            return material != Material.plants && material != Material.vine
                    && material != Material.coral
                    && material != Material.leaves
                    && material != Material.gourd ? 1.0F : 1.5F;
        }
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put(
                SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                new AttributeModifier(field_111210_e, "Tool modifier", 2.0d, 0));
        return multimap;
    }

    public boolean findAndNotifyAltarOfDemon(World world, EntityLivingBase sacrifice) {
        int posX = (int) Math.round(sacrifice.posX - 0.5f);
        int posY = (int) sacrifice.posY;
        int posZ = (int) Math.round(sacrifice.posZ - 0.5f);
        IBloodAltar altarEntity = this.getAltar(world, posX, posY, posZ);

        if (altarEntity == null) {
            return false;
        }

        altarEntity.addToDemonBloodDuration(50);

        return true;
    }

    public boolean findAndFillAltar(World world, EntityLivingBase sacrifice, int amount) {
        int posX = (int) Math.round(sacrifice.posX - 0.5f);
        int posY = (int) sacrifice.posY;
        int posZ = (int) Math.round(sacrifice.posZ - 0.5f);
        IBloodAltar altarEntity = this.getAltar(world, posX, posY, posZ);

        if (altarEntity == null) {
            return false;
        }

        altarEntity.sacrificialDaggerCall(amount, true);
        altarEntity.startCycle();
        return true;
    }

    public IBloodAltar getAltar(World world, int x, int y, int z) {
        TileEntity tileEntity;

        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                for (int k = -2; k <= 1; k++) {
                    tileEntity = world.getTileEntity(i + x, k + y, j + z);

                    if (tileEntity instanceof IBloodAltar) {
                        return (IBloodAltar) tileEntity;
                    }
                }
            }
        }

        return null;
    }
}
