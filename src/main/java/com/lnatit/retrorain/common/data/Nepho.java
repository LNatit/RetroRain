package com.lnatit.retrorain.common.data;

import com.lnatit.retrorain.common.network.ChunkNephoPacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

public record Nepho(ArrayList<Type> map) {
    public static final Codec<Nepho> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Type.CODEC.listOf().xmap(ArrayList::new, Function.identity()).fieldOf("map").forGetter(Nepho::map)
    ).apply(ins, Nepho::new));
    public static final StreamCodec<ByteBuf, Nepho> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, Type.STREAM_CODEC),
            Nepho::map,
            Nepho::new
    );

    public static final Supplier<Nepho> DEFAULT = () -> new Nepho(
            new ArrayList<>(Arrays.asList(Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR,
                    Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR,
                    Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR,
                    Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR
            )));

    private Type getCell(int x, int y) {
        return map.get(x + 4 * y);
    }

    private void setCell(int x, int y, Type type) {
        map.set(x + 4 * y, type);
    }

    private void setAll(Type type) {
        Collections.fill(map, type);
    }

    public static Type getCell(Level level, CellPos pos) {
        if (level.getChunkSource().hasChunk(pos.getChunkX(), pos.getChunkZ())) {
            LevelChunk chunk = level.getChunk(pos.getChunkX(), pos.getChunkZ());
            Nepho data = chunk.getData(DataRegistry.NEPHO);
            return data.getCell(pos.getLocalX(), pos.getLocalZ());
        }
        return Type.CLEAR;
    }

    public static boolean setCell(Level level, CellPos pos, Type type) {
        if (level.getChunkSource().hasChunk(pos.getChunkX(), pos.getChunkZ())) {
            LevelChunk chunk = level.getChunk(pos.getChunkX(), pos.getChunkZ());
            Nepho data = chunk.getData(DataRegistry.NEPHO);
            data.setCell(pos.getLocalX(), pos.getLocalZ(), type);
            return sync(level, pos, data, chunk);
        }
        return false;
    }

    public static boolean setChunk(Level level, CellPos pos, Type type) {
        if (level.getChunkSource().hasChunk(pos.getChunkX(), pos.getChunkZ())) {
            LevelChunk chunk = level.getChunk(pos.getChunkX(), pos.getChunkZ());
            Nepho data = chunk.getData(DataRegistry.NEPHO);
            data.setAll(type);
            return sync(level, pos, data, chunk);
        }
        return false;
    }

    private static boolean sync(Level level, CellPos pos, Nepho data, LevelChunk chunk) {
        if (level instanceof ClientLevel) {
            PacketDistributor.sendToServer(new ChunkNephoPacket(data, pos.getChunkX(), pos.getChunkZ()));
        } else if (level instanceof ServerLevel serverLevel) {
            chunk.setUnsaved(true);
            for (ServerPlayer player : serverLevel.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false)) {
                player.connection.send(new ChunkNephoPacket(data, pos.getChunkX(), pos.getChunkZ()));
            }
        }
        return true;
    }

    public enum Type implements StringRepresentable {
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

        public Type next() {
            return switch (this) {
                case CLEAR -> RAIN;
                case RAIN -> RETRO;
                case RETRO -> CLEAR;
            };
        }

        public Type pre() {
            return this.next().next();
        }
    }
}
