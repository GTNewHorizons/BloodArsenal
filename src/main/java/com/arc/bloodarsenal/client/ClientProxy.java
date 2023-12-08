package com.arc.bloodarsenal.client;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.arc.bloodarsenal.client.renderer.block.RenderBloodTNTPrimed;
import com.arc.bloodarsenal.client.renderer.block.RenderTileLifeInfuser;
import com.arc.bloodarsenal.client.renderer.block.TileLifeInfuserRenderer;
import com.arc.bloodarsenal.client.renderer.block.TilePortableAltarRenderer;
import com.arc.bloodarsenal.client.renderer.block.item.TilePortableAltarItemRenderer;
import com.arc.bloodarsenal.client.renderer.item.RenderBow;
import com.arc.bloodarsenal.common.CommonProxy;
import com.arc.bloodarsenal.common.block.ModBlocks;
import com.arc.bloodarsenal.common.entity.EntityBloodTNT;
import com.arc.bloodarsenal.common.items.ModItems;
import com.arc.bloodarsenal.common.items.sigil.SigilUtils;
import com.arc.bloodarsenal.common.tileentity.TileLifeInfuser;
import com.arc.bloodarsenal.common.tileentity.TilePortableAltar;
import com.arc.bloodarsenal.common.tinkers.IClientCode;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        registerRenders();
        registerEvents();
        getClientWorld();
        initRendering();
    }

    @Override
    public void registerRenders() {
        MinecraftForgeClient.registerItemRenderer(ModItems.bound_bow, new RenderBow());

        RenderingRegistry.registerEntityRenderingHandler(EntityBloodTNT.class, new RenderBloodTNTPrimed());
        // RenderingRegistry.registerEntityRenderingHandler(EntityBloodHound.class, new
        // RenderEntityBloodHound(new ModelWolf(), new ModelWolf(), 0.5F));

        MinecraftForgeClient
                .registerItemRenderer(Item.getItemFromBlock(ModBlocks.life_infuser), new RenderTileLifeInfuser());

        ClientRegistry.bindTileEntitySpecialRenderer(TilePortableAltar.class, new TilePortableAltarRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileLifeInfuser.class, new TileLifeInfuserRenderer());
    }

    @Override
    public void registerEvents() {
        MinecraftForge.EVENT_BUS.register(new SigilUtils());
    }

    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }

    @Override
    public void initRendering() {
        MinecraftForgeClient.registerItemRenderer(
                ItemBlock.getItemFromBlock(ModBlocks.portable_altar),
                new TilePortableAltarItemRenderer());
    }

    @Override
    public void executeClientCode(IClientCode clientCode) {
        clientCode.executeClientCode();
    }
}
