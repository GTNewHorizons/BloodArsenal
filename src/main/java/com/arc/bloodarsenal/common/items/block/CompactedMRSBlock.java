package com.arc.bloodarsenal.common.items.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import WayofTime.alchemicalWizardry.api.rituals.Rituals;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CompactedMRSBlock extends ItemBlock {

    public CompactedMRSBlock(Block block) {
        super(block);
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        if (GuiScreen.isShiftKeyDown()) {
            addHiddenTooltip(par1ItemStack, par3List);
        } else {
            addStringToTooltip(StatCollector.translateToLocal("tooltip.shiftinfo"), par3List);
        }
    }

    public void addHiddenTooltip(ItemStack par1ItemStack, List par3List) {
        if (par1ItemStack.hasTagCompound()) {
            NBTTagCompound tag = par1ItemStack.getTagCompound();

            if (tag.hasKey("ritualName")) {
                String ritualName = Rituals.getNameOfRitual(tag.getString("ritualName"));

                addStringToTooltip(
                        StatCollector.translateToLocal("tooltip.ritualName") + " &c" + ritualName + "&7",
                        par3List);
            }
        }
    }

    public void addStringToTooltip(String s, List<String> tooltip) {
        tooltip.add(s.replaceAll("&", "\u00a7"));
    }
}
