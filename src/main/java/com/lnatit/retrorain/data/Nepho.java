package com.lnatit.retrorain.data;

import com.lnatit.retrorain.network.ChunkNephoPacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static com.lnatit.retrorain.RetroRain.LOGGER;

public record Nepho(List<Type> map)
{
    public static final Codec<Nepho> CODEC = ImmutableNepho.CODEC.xmap(ImmutableNepho::toMutable, Nepho::toImmutable);

    public static final Supplier<Nepho> DEFAULT = () -> new Nepho(
            Arrays.asList(Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR,
                          Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR,
                          Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR,
                          Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR
            ));
    public static final StreamCodec<ByteBuf, Nepho> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, Type.STREAM_CODEC),
            Nepho::map,
            Nepho::new
    );

    private ImmutableNepho toImmutable() {
        return new ImmutableNepho(map);
    }

    private Type getCell(int x, int y) {
        return map.get(x + 4 * y);
    }

    private void setCell(int x, int y, Type type) {
        map.set(x + 4 * y, type);
    }

    public static Type getCell(Level level, CellPos pos) {
        if (level.getChunkSource().hasChunk(pos.getChunkX(), pos.getChunkZ())) {
            LevelChunk chunk = level.getChunk(pos.getChunkX(), pos.getChunkZ());
            return chunk.getData(DataRegistry.NEPHO).getCell(pos.getLocalX(), pos.getLocalZ());
        }
        return Type.CLEAR;
    }

    public static void setCell(ServerLevel level, CellPos pos, Type type) {
        if (level.getChunkSource().hasChunk(pos.getChunkX(), pos.getChunkZ())) {
            if (level instanceof ServerLevel) {
                LOGGER.info("Server set!");
            }
            LevelChunk chunk = level.getChunk(pos.getChunkX(), pos.getChunkZ());
            Nepho data = chunk.getData(DataRegistry.NEPHO);
            data.setCell(pos.getLocalX(), pos.getLocalZ(), type);
            chunk.setUnsaved(true);

            for (ServerPlayer player : level.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false)) {
                player.connection.send(new ChunkNephoPacket(data, pos.getChunkX(), pos.getChunkZ()));
            }
        }
    }

    record ImmutableNepho(List<Type> map) {
        public static final Codec<ImmutableNepho> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                Type.CODEC.listOf().fieldOf("map").forGetter(ImmutableNepho::map)
        ).apply(ins, ImmutableNepho::new));

        private Nepho toMutable() {
            return new Nepho(new ArrayList<>(map));
        }
    }

    public enum Type implements StringRepresentable
    {
        CLEAR,
        RAIN,
        RETRO;
        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        public static final StreamCodec<ByteBuf, Type> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                Type::name,
                name -> Enum.valueOf(Type.class, name)
        );

        @Override
        @NotNull
        public String getSerializedName() {
            return name();
        }
    }
}
