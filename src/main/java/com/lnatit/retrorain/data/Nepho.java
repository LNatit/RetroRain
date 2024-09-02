package com.lnatit.retrorain.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public record Nepho(List<Type> map)
{
    public static final Codec<Nepho> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Type.CODEC.listOf().fieldOf("map").forGetter(o -> o.map)
    ).apply(ins, Nepho::new));

    public static final Supplier<Nepho> DEFAULT = () -> new Nepho(
            Arrays.asList(Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR,
                          Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR, Type.CLEAR,
                          Type.CLEAR, Type.CLEAR
            ));

    enum Type implements StringRepresentable
    {
        CLEAR,
        RAIN,
        RETRO;
        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        @Override
        @NotNull
        public String getSerializedName() {
            return name();
        }
    }
}
