package com.lnatit.retrorain;

import com.lnatit.retrorain.client.content.RainManager;
import com.lnatit.retrorain.client.screen.NephogramScreen;
import com.lnatit.retrorain.common.data.DataRegistry;
import com.lnatit.retrorain.client.particle.ParticleRegistry;
import com.lnatit.retrorain.client.particle.RainDropParticle;
import com.lnatit.retrorain.client.particle.RainParticle;
import com.lnatit.retrorain.common.item.ItemRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

import static com.lnatit.retrorain.RetroRain.MOD_ID;

@Mod(MOD_ID)
public class RetroRain
{
    public static final String MOD_ID = "retrorain";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RetroRain(IEventBus modBus) {
        ItemRegistry.ITEMS.register(modBus);
        DataRegistry.DATA_TYPES.register(modBus);
        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(ItemRegistry.NEPHOGRAM);
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

        public static void openNephogram(Level level, Player player) {
            Minecraft.getInstance().setScreen(new NephogramScreen(level, player));
        }
    }
}
