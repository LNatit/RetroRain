package com.lnatit.retrorain;

import com.lnatit.retrorain.content.RainManager;
import com.lnatit.retrorain.data.DataRegistry;
import com.lnatit.retrorain.particle.ParticleRegistry;
import com.lnatit.retrorain.particle.RainDropParticle;
import com.lnatit.retrorain.particle.RainParticle;
import com.mojang.logging.LogUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.slf4j.Logger;

import static com.lnatit.retrorain.RetroRain.MOD_ID;

@Mod(MOD_ID)
public class RetroRain
{
    public static final String MOD_ID = "retrorain";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RetroRain(IEventBus modBus) {
        DataRegistry.DATA_TYPES.register(modBus);
    }

    @Mod(value = MOD_ID, dist = Dist.CLIENT)
    public static class RetroRainClient
    {
        public static final RainManager rainManager = new RainManager();

        public RetroRainClient(IEventBus modBus)
        {
            ParticleRegistry.PARTICLE_TYPES.register(modBus);
            modBus.addListener(this::onParticleProvidersRegister);
        }

        public void onParticleProvidersRegister(RegisterParticleProvidersEvent event)
        {
            event.registerSpriteSet(ParticleTypes.RAIN, RainParticle.Provider::new);
            event.registerSpriteSet(ParticleRegistry.RAIN_DROP.get(), RainDropParticle.Provider::new);
        }
    }
}
