package com.lnatit.retrorain.network;

import com.lnatit.retrorain.data.DataRegistry;
import com.lnatit.retrorain.data.Nepho;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.lnatit.retrorain.RetroRain.LOGGER;
import static com.lnatit.retrorain.RetroRain.MOD_ID;

public record ChunkNephoPacket(Nepho data, int chunkX, int chunkZ) implements CustomPacketPayload
{
    public static final Type<ChunkNephoPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "nepho_data"));
    public static final StreamCodec<ByteBuf, ChunkNephoPacket> STREAM_CODEC = StreamCodec.composite(
            Nepho.STREAM_CODEC,
            ChunkNephoPacket::data,
            ByteBufCodecs.VAR_INT,
            ChunkNephoPacket::chunkX,
            ByteBufCodecs.VAR_INT,
            ChunkNephoPacket::chunkZ,
            ChunkNephoPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static ChunkNephoPacket from(LevelChunk chunk, ChunkPos pos) {
        return new ChunkNephoPacket(chunk.getData(DataRegistry.NEPHO), pos.x, pos.z);
    }

    public static void handle(ChunkNephoPacket payload, IPayloadContext context) {
        if (Minecraft.getInstance().level != null) {
            LOGGER.info("Client Synced!");
            Minecraft.getInstance().level.getChunk(payload.chunkX(),
                                                   payload.chunkZ()
            ).setData(DataRegistry.NEPHO, payload.data());
        }
    }
}
