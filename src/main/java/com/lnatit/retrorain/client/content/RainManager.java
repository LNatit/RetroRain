package com.lnatit.retrorain.client.content;

import com.lnatit.retrorain.common.data.CellPos;
import com.lnatit.retrorain.common.data.Nepho;
import com.lnatit.retrorain.client.particle.ParticleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

public class RainManager
{
    public static final float[] ALPHA_INTERPOLATE = new float[101];
    public static final float[] PARTICLE_PARTIALS = new float[101];

    static {
        for (int i = 0; i < 101; i++) {
            float partial = (i - 50f) / 50;
            float t = (1.0f + (float) Math.cos(Math.PI * partial)) / 2.0f;
            ALPHA_INTERPOLATE[i] = 1 - t * t * t * t * t;
            PARTICLE_PARTIALS[i] = (float) Math.sin((Mth.clamp(partial, 0.0f, 1.0f)) / 0.8f * Math.PI) * 0.3f;
        }
    }

    public boolean raining() {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            return Nepho.getCell(Minecraft.getInstance().level, new CellPos(Minecraft.getInstance().player.getOnPos())) != Nepho.Type.CLEAR;
        }
        return raining;
    }

    public boolean retro() {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
            return Nepho.getCell(Minecraft.getInstance().level, new CellPos(Minecraft.getInstance().player.getOnPos())) == Nepho.Type.RETRO;
        }
        return retro;
    }

    private boolean raining = false;
    private boolean retro = false;

    private int retroLevel = 100;

    private float rainLevel = 0.0f;

    public void setRaining(boolean raining, boolean retro) {
        if (raining) {
            this.retro = retro;
            if (!this.raining) {
                this.retroLevel = this.retro ? 0 : 100;
            }
        }
        this.raining = raining;
    }

    public void tick() {
        // I think it makes no sense to record the last rain level
        // and use it for interpolate partial ticks
        if (this.raining()) {
            this.rainLevel += 0.01f;
        }
        else {
            this.rainLevel -= 0.01f;
        }
        this.rainLevel = Mth.clamp(this.rainLevel, 0.0f, 1.0f);

        if (!this.retro()) {
            this.retroLevel += 1;
        }
        else {
            this.retroLevel -= 1;
        }
        this.retroLevel = Mth.clamp(this.retroLevel, 0, 100);
    }

    public float getRainLevel() {
        return this.rainLevel;
    }

    public float getRainAlpha() {
        return ALPHA_INTERPOLATE[retroLevel];
    }

    public boolean shouldRetro() {
        return this.retroLevel < 50;
    }

    public float getRainSpeedMag() {
        return shouldRetro() ? 0.8f : 1.0f;
    }

    public float getGndAlpha() {
        return shouldRetro() ? 0.0f : 1.0f;
    }

    public float getInterpolatedRainGravity() {
        return Mth.lerp(retroLevel / 100f, -0.04f, 0.06f);
    }

    private float getParticleNumMag() {
        return Mth.lerp(retroLevel / 100f, 0.6f, 1.0f);
    }

    private float getRetroParticlePartial() {
        return this.retro() ? PARTICLE_PARTIALS[retroLevel] : 0.0f;
    }

    public int tickRainDrop(int originalNum, RandomSource randomSource, LevelReader levelReader, BlockPos cameraPos, Minecraft minecraft) {
        float particleNum = (originalNum * this.getParticleNumMag());
        int retroParticles = (int) (particleNum * this.getRetroParticlePartial());

        for (int j = 0; j < retroParticles * 2; j++) {
            int k = randomSource.nextInt(21) - 10;
            int l = randomSource.nextInt(21) - 10;
            BlockPos mapPos = levelReader.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, cameraPos.offset(k, 0, l));

            if (mapPos.getY() > levelReader.getMinBuildHeight()) {
                Biome biome = levelReader.getBiome(mapPos).value();
                if (biome.getPrecipitationAt(mapPos) == Biome.Precipitation.RAIN) {
                    if (minecraft.options.particles().get() == ParticleStatus.MINIMAL) {
                        j++;
                    }

                    double xOffset = randomSource.nextDouble();
                    double zOffset = randomSource.nextDouble();
                    double y = Mth.randomBetween(randomSource, Math.max(cameraPos.getY() + 4, mapPos.getY() + 8),
                                                 cameraPos.getY() + 12
                    );


                    minecraft.level.addParticle(
                            ParticleRegistry.RAIN_DROP.get(),
                            (double) mapPos.getX() + xOffset,
                            y,
                            (double) mapPos.getZ() + zOffset,
                            0.0, 0.0, 0.0
                    );
                }
            }
        }

        return (int) particleNum - retroParticles;
    }
}
