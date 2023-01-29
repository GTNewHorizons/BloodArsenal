package com.arc.bloodarsenal.common.block;

import java.util.Random;

import net.minecraft.block.BlockGlowstone;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import com.arc.bloodarsenal.common.items.ModItems;

public class BlockBloodInfusedGlowstone extends BlockGlowstone {

    public BlockBloodInfusedGlowstone() {
        super(Material.glass);
        setHardness(0.5F);
        setResistance(0.75F);
        setStepSound(soundTypeGlass);
        setLightLevel(1.0F);
    }

    public Item getItemDropped(int par1, Random random, int par3) {
        return ModItems.blood_infused_glowstone_dust;
    }
}
