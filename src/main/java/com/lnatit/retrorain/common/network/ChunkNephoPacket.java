package com.lnatit.retrorain.common.network;

import com.lnatit.retrorain.common.data.DataRegistry;
import com.lnatit.retrorain.common.data.Nepho;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

import static com.lnatit.retrorain.RetroRain.LOGGER;
import static com.lnatit.retrorain.RetroRain.MOD_ID;

public record ChunkNephoPacket(Nepho data, int chunkX, int chunkZ) implements CustomPacketPayload {
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
    public static final DirectionalPayloadHandler<ChunkNephoPacket> HANDLER =
            new DirectionalPayloadHandler<>(ChunkNephoPacket::clientHandle, ChunkNephoPacket::serverHandle);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static ChunkNephoPacket from(LevelChunk chunk, ChunkPos pos) {
        return new ChunkNephoPacket(chunk.getData(DataRegistry.NEPHO), pos.x, pos.z);
    }

    public static void clientHandle(ChunkNephoPacket payload, IPayloadContext context) {
        if (Minecraft.getInstance().level != null) {
            LOGGER.info("Client Synced!");
            Minecraft.getInstance().level.getChunk(payload.chunkX(),
                    payload.chunkZ()
            ).setData(DataRegistry.NEPHO, payload.data());
        }
    }

    public static void serverHandle(ChunkNephoPacket payload, IPayloadContext context) {
        if (context instanceof ServerPayloadContext serverContext && payload.chunkX < 5 && payload.chunkZ < 5) {
            ServerPlayer player = serverContext.player();
            ChunkPos pos = player.chunkPosition();
            player.level().getChunk(pos.x + payload.chunkX - 2, pos.z + payload.chunkZ - 2).setData(DataRegistry.NEPHO, payload.data());
        }
    }
}
