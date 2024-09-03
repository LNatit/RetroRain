package com.lnatit.retrorain.data;

import com.lnatit.retrorain.network.ChunkNephoPacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static com.lnatit.retrorain.RetroRain.MOD_ID;

@EventBusSubscriber(modid = MOD_ID, value = Dist.DEDICATED_SERVER)
public class DataRegistry
{
    public static final DeferredRegister<AttachmentType<?>> DATA_TYPES = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);

    public static final Supplier<AttachmentType<Nepho>> NEPHO =
            DATA_TYPES.register(
                    "nepho",
                    () -> AttachmentType.builder(Nepho.DEFAULT).serialize(Nepho.CODEC).build()
            );

    @SubscribeEvent
    public static void onChunkSent(ChunkWatchEvent.Sent event){
        PacketDistributor.sendToPlayer(
                event.getPlayer(),
                ChunkNephoPacket.from(event.getChunk(), event.getPos())
        );
    }
}
