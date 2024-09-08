package com.lnatit.retrorain.client.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.lnatit.retrorain.RetroRain.MOD_ID;

public class ParticleRegistry
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RAIN_DROP = PARTICLE_TYPES.register("rain_drop", () -> new SimpleParticleType(false));
}
