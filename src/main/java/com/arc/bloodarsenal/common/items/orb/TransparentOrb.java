package com.arc.bloodarsenal.common.items.orb;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import WayofTime.alchemicalWizardry.common.items.EnergyBattery;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TransparentOrb extends EnergyBattery {

    IIcon[] icons = new IIcon[45];

    public TransparentOrb(int damage) {
        super(damage);
        orbLevel = 6;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean something) {
        list.add(StatCollector.translateToLocal("tooltip.energybattery.desc"));
        list.add(StatCollector.translateToLocalFormatted("tooltip.energybattery.capacity", this.getMaxEssence()));

        if (itemStack.getTagCompound() != null) {
            list.add(
                    StatCollector.translateToLocal("tooltip.owner.currentowner") + " "
                            + itemStack.getTagCompound().getString("ownerName"));

            list.add(
                    StatCollector.translateToLocal("tooltip.energybattery.currentLP") + " "
                            + String.format("%,d", itemStack.getTagCompound().getInteger("essenceAmount")));

        }
    }

    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        NBTTagCompound itemTag = itemStack.getTagCompound();
        if (itemTag == null) {
            return;
        }
        if (world.isRemote) {
            return; // Changes of metadata and NBT will be synced to client, no need to proceed on client
        }

        int maxEssence = SoulNetworkHandler
                .getMaximumForOrbTier(SoulNetworkHandler.getCurrentMaxOrb(SoulNetworkHandler.getOwnerName(itemStack)));
        int section = maxEssence / 44;
        int currentEssence = SoulNetworkHandler.getCurrentEssence(SoulNetworkHandler.getOwnerName(itemStack));

        int fillLevel = 0;
        if (currentEssence > 0) {
            if (section > 0) fillLevel = Math.min((currentEssence / section), 44);
        }

        // For save conversion - old versions tracked using metadata instead of NBT
        if (!itemTag.hasKey("fillLevel") && itemStack.getItemDamage() > 0) {
            itemStack.setItemDamage(0);
        }

        if (itemTag.hasKey("ownerName")) {
            itemTag.setInteger("essenceAmount", currentEssence);
            itemTag.setInteger("fillLevel", fillLevel);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public IIcon getIcon(ItemStack itemStack, int pass) {
        NBTTagCompound itemTag = itemStack.getTagCompound();
        if (itemTag == null) {
            return itemIcon;
        }
        int i = itemStack.getTagCompound().getInteger("fillLevel");
        if (i < 0 || i >= icons.length) {
            return itemIcon;
        }
        return icons[i];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("BloodArsenal:orb/orb_0");

        for (int i = 0; i < icons.length; i++) {
            icons[i] = iconRegister.registerIcon("BloodArsenal:orb/orb_" + i);
        }
    }
}
