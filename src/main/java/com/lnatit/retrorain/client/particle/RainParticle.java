package com.lnatit.retrorain.client.particle;

import com.lnatit.retrorain.RetroRain;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

// TODO overwrite
public class RainParticle extends WaterDropParticle
{
    public RainParticle(ClientLevel level, double x, double y, double z)
    {
        super(level, x, y, z);
        this.gravity = RetroRain.RetroRainClient.rainManager.getInterpolatedRainGravity();
    }

    @Override
    public void tick() {
        this.gravity = RetroRain.RetroRainClient.rainManager.getInterpolatedRainGravity();
        super.tick();
    }

    public static class Provider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        public Particle createParticle(
                @NotNull SimpleParticleType type,
                @NotNull ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            RainParticle rainParticle = new RainParticle(level, x, y, z);
            rainParticle.pickSprite(this.sprite);
            return rainParticle;
        }
    }
}
