package com.lnatit.retrorain.network;

import com.lnatit.retrorain.RetroRain;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.lnatit.retrorain.RetroRain.MOD_ID;

public record PhaseUpdatePacket(boolean raining, boolean retro) implements CustomPacketPayload
{
    public static final Type<PhaseUpdatePacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "phase_update"));
    public static final StreamCodec<ByteBuf, PhaseUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            PhaseUpdatePacket::raining,
            ByteBufCodecs.BOOL,
            PhaseUpdatePacket::retro,
            PhaseUpdatePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PhaseUpdatePacket payload, IPayloadContext context) {
        RetroRain.RetroRainClient.rainManager.setRaining(payload.raining(), payload.retro());
    }
}
