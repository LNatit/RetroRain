package com.lnatit.retrorain.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.lnatit.retrorain.RetroRain;
import com.lnatit.retrorain.particle.ParticleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.lnatit.retrorain.RetroRain.MOD_ID;

@Mixin(LevelRenderer.class)
public class RetroRainMixinLevelRenderer
{
    private static final ResourceLocation RETRO_RAIN_LOCATION =
            ResourceLocation.fromNamespaceAndPath(MOD_ID,
                                                  "textures/environment/retro_rain.png"
            );

    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyArg(
            method = "renderSnowAndRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V",
                    ordinal = 0
            ),
            index = 1
    )
    private ResourceLocation retrorainRedirect$setUv0(ResourceLocation textureId) {
        return RetroRain.RetroRainClient.rainManager.isRetro() ? RETRO_RAIN_LOCATION : textureId;
    }

    @ModifyVariable(method = "renderSnowAndRain", at = @At("STORE"), ordinal = 3)
    private float retrorainModify$rainSpeed$rainAlpha(float originalSpeed, @Local(ordinal = 0, argsOnly = true) float partialTick, @Local(ordinal = 1) LocalFloatRef rainAlpha) {
        rainAlpha.set(Math.min(rainAlpha.get(), RetroRain.RetroRainClient.rainManager.getRainAlpha()));
        return originalSpeed * RetroRain.RetroRainClient.rainManager.getRainSpeedMag();
    }

    @Inject(
            method = "renderSnowAndRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setLight(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
                    ordinal = 1
            )
    )
    private void retrorainModify$groundRainOpacity(LightTexture lightTexture,
                                                   float partialTick,
                                                   double camX,
                                                   double camY,
                                                   double camZ,
                                                   CallbackInfo ci,
                                                   @Local(ordinal = 7) LocalFloatRef originalOpacity,
                                                   @Local(ordinal = 3) int renderRadius,
                                                   @Local(ordinal = 8) int localHeight,
                                                   @Local(ordinal = 9) int rainMinY
    ) {
        int dy = rainMinY - localHeight;
        int renderDiameter = renderRadius << 1;
        if (dy < renderDiameter) {
            float mag = RetroRain.RetroRainClient.rainManager.getGndAlpha();
            float opacity = originalOpacity.get();

            if (dy > 0) {
                float delta = (float) dy / renderDiameter;
                opacity *= Mth.lerp(delta, mag, 1.0f);
            }
            else {
                opacity *= mag;
            }
            originalOpacity.set(opacity);
        }
    }

    @ModifyVariable(method = "tickRain", at = @At("STORE"), ordinal = 0)
    private int retrorainModify$rainParticleNum(int originalNum,
                                                @Local RandomSource randomSource,
                                                @Local LevelReader levelReader,
                                                @Local(ordinal = 0) BlockPos cameraPos) {
        return RetroRain.RetroRainClient.rainManager.tickRainDrop(
                originalNum,
                randomSource,
                levelReader,
                cameraPos,
                this.minecraft
        );
    }
}
