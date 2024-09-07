package com.lnatit.retrorain.content;

import com.lnatit.retrorain.RetroRain;
import com.lnatit.retrorain.screen.NephogramScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;

import static com.lnatit.retrorain.RetroRain.MOD_ID;

public class ContentRegistry
{
    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class Client
    {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Pre event) {
            RetroRain.RetroRainClient.rainManager.tick();
        }

        @SubscribeEvent
        public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
            // Restore to default state
            RetroRain.RetroRainClient.rainManager.setRaining(false, false);
        }
    }
}
