package com.lnatit.retrorain.common.network;

import com.lnatit.retrorain.common.data.DataRegistry;
import com.lnatit.retrorain.common.data.Nepho;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

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

    public static ChunkNephoPacket from(LevelChunk chunk, ChunkPos pos) {
        return new ChunkNephoPacket(chunk.getData(DataRegistry.NEPHO), pos.x, pos.z);
    }

    public static void clientHandle(ChunkNephoPacket payload, IPayloadContext context) {
        if (Minecraft.getInstance().level != null) {
//            LOGGER.info("Client Synced!");
            Minecraft.getInstance().level.getChunk(payload.chunkX(),
                    payload.chunkZ()
            ).setData(DataRegistry.NEPHO, payload.data());
        }
    }

    public static void serverHandle(ChunkNephoPacket payload, IPayloadContext context) {
        // tmd
//        boolean flag = payload.chunkX < 5 && payload.chunkZ < 5;
        if (context instanceof ServerPayloadContext serverContext) {
            ServerPlayer player = serverContext.player();
            if (player.hasPermissions(2)) {
                ChunkPos pos = player.chunkPosition();
                ServerLevel level = player.serverLevel();
                // 我诗人啊？
                if (pos.distanceSquared(payload.getChunkPos()) < 9)
                {
                    level.getChunk(payload.chunkX, payload.chunkZ).setData(DataRegistry.NEPHO, payload.data());
                    PacketDistributor.sendToPlayersTrackingChunk(level, payload.getChunkPos(), payload);
                }
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public ChunkPos getChunkPos() {
        return new ChunkPos(chunkX, chunkZ);
    }
}
