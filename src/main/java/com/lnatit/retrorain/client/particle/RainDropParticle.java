package com.lnatit.retrorain.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class RainDropParticle extends WaterDropParticle
{
    public static final float RETRO_GRAVITY = -0.04f;
    public static final float AVG_SPEED = -7.0f / 16.0f;
    public static final float FALL_DISTANCE = 8.0f + AVG_SPEED * AVG_SPEED / RETRO_GRAVITY / 2;

    protected RainDropParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.yd = -(3.0f + Math.random()) / 8.0f;
        this.gravity = RETRO_GRAVITY;
        this.lifetime = (int) (-FALL_DISTANCE / yd);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- < -32) {
            this.remove();
        }
        else {
            if (this.lifetime > -24 && this.lifetime <= 0) {
                this.yd = this.yd - (double) this.gravity;
            }
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.98F;
            this.zd *= 0.98F;
            if (this.onGround) {
                if (Math.random() < 0.5) {
                    this.remove();
                }
                this.xd *= 0.7F;
                this.zd *= 0.7F;
            }
        }
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
            RainDropParticle rainDropParticle = new RainDropParticle(level, x, y, z);
            rainDropParticle.pickSprite(this.sprite);
            return rainDropParticle;
        }
    }
}
