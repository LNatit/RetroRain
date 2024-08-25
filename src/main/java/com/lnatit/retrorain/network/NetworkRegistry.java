package com.lnatit.retrorain.network;

import com.lnatit.retrorain.RetroRain;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.lnatit.retrorain.RetroRain.MOD_ID;

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkRegistry
{
    public static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void onNetworkRegistry(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(PhaseUpdatePacket.TYPE, PhaseUpdatePacket.STREAM_CODEC, new MainThreadPayloadHandler<>(
                (payload, context) -> RetroRain.RetroRainClient.rainManager.setRaining(payload.raining(),
                                                                                       payload.retro()
                )));
    }
}