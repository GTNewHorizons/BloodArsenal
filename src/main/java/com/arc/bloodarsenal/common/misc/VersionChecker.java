/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [May 31, 2014, 10:22:44 PM (GMT)]
 */
package com.arc.bloodarsenal.common.misc;

import com.arc.bloodarsenal.common.BloodArsenal;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

public class VersionChecker
{
    public static boolean doneChecking = false;
    public static String onlineVersion = "";
    public static boolean triedToWarnPlayer = false;

    public static boolean startedDownload = false;
    public static boolean downloadedFile = false;

    public void init()
    {
        new ThreadVersionChecker();
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event)
    {
        if (doneChecking && event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().thePlayer != null && !triedToWarnPlayer)
        {
            if (!onlineVersion.isEmpty())
            {
                EntityPlayer player = Minecraft.getMinecraft().thePlayer;
                int onlineBuild1 = Integer.parseInt(onlineVersion.substring(2, 3));
                int onlineBuild2 = Integer.parseInt(onlineVersion.substring(3));
                int clientBuild1 = BloodArsenal.VERSION.equals("@VERSION@") ? 0 : Integer.parseInt(BloodArsenal.VERSION.substring(2, 3));
                int clientBuild2 = BloodArsenal.VERSION.equals("@VERSION@") ? 0 : Integer.parseInt(BloodArsenal.VERSION.substring(3));

                if (onlineBuild1 > clientBuild1)
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("ba.versioning.outdated", BloodArsenal.VERSION, onlineVersion));

                    IChatComponent component = IChatComponent.Serializer.func_150699_a(StatCollector.translateToLocal("ba.versioning.updateMessage").replaceAll("%version%", onlineVersion));
                    player.addChatComponentMessage(component);
                }
                else if (onlineBuild1 == clientBuild1)
                {
                    if (onlineBuild2 > clientBuild2)
                    {
                        player.addChatComponentMessage(new ChatComponentTranslation("ba.versioning.outdated", BloodArsenal.VERSION, onlineVersion));

                        IChatComponent component = IChatComponent.Serializer.func_150699_a(StatCollector.translateToLocal("ba.versioning.updateMessage").replaceAll("%version%", onlineVersion));
                        player.addChatComponentMessage(component);
                    }
                }
            }

            triedToWarnPlayer = true;
        }
    }
}
