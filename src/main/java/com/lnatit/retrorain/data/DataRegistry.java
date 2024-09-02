package com.lnatit.retrorain.data;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static com.lnatit.retrorain.RetroRain.MOD_ID;

public class DataRegistry
{
    public static final DeferredRegister<AttachmentType<?>> DATA_TYPES = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);

    public static final Supplier<AttachmentType<Nepho>> NEPHO =
            DATA_TYPES.register(
                    "nepho",
                    () -> AttachmentType.builder(Nepho.DEFAULT).serialize(Nepho.CODEC).build()
            );
}
