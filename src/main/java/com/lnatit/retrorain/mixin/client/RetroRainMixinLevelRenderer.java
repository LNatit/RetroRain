package com.lnatit.retrorain.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.lnatit.retrorain.RetroRain;
import com.lnatit.retrorain.common.data.CellPos;
import com.lnatit.retrorain.common.data.Nepho;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = LevelRenderer.class, priority = 1100)
public class RetroRainMixinLevelRenderer
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(
            method = {"tickRain", "renderSnowAndRain"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"
            )
    )
    private Biome.Precipitation retrorainWarp$getPrecipitationAt(Biome biome, BlockPos pos, Operation<Biome.Precipitation> original) {
        if (minecraft.player != null) {
            if (
                    Nepho.getCell(
                            minecraft.level,
                            new CellPos(Minecraft.getInstance().player.getOnPos())
                    ) != Nepho.Type.CLEAR
            ) {
                // compatibility with eclipticseasons
                return biome.getPrecipitationAt(pos);
            }
        }
        return original.call(biome, pos);
    }

    @ModifyVariable(method = "renderSnowAndRain", at = @At("STORE"), ordinal = 3)
    private float retrorainModify$rainSpeed$rainAlpha(
            float originalSpeed,
            @Local(ordinal = 0, argsOnly = true) float partialTick,
            @Local(ordinal = 1) LocalFloatRef rainAlpha
    ) {
        rainAlpha.set(Math.min(rainAlpha.get(), RetroRain.RetroRainClient.rainManager.getRainAlpha()));
        return originalSpeed * RetroRain.RetroRainClient.rainManager.getRainSpeedMag();
    }

    @ModifyArgs(
            method = "renderSnowAndRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setUv(FF)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
                    ordinal = 0
            )
    )
    private void retrorainRedirect$setUv0(
            Args args,
            @Local(ordinal = 9) int j2,
            @Local(ordinal = 10) int k2,
            @Share("yMax") LocalFloatRef yMax,
            @Share("yMin") LocalFloatRef yMin
    ) {
        if (!RetroRain.RetroRainClient.rainManager.shouldRetro()) {
            return;
        }
        yMax.set(args.get(1));
        yMin.set(yMax.get() - (j2 - k2) * 0.25f);
        args.set(0, 1.0f);
        args.set(1, yMin.get());
    }

    @ModifyArgs(
            method = "renderSnowAndRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setUv(FF)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
                    ordinal = 1
            )
    )
    private void retrorainRedirect$setUv1(Args args, @Share("yMin") LocalFloatRef yMin) {
        if (!RetroRain.RetroRainClient.rainManager.shouldRetro()) {
            return;
        }
        args.set(0, 0.0f);
        args.set(1, yMin.get());
    }

    @Inject(
            method = "renderSnowAndRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setLight(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
                    ordinal = 1
            )
    )
    private void retrorainModify$groundRainOpacity(
            LightTexture lightTexture,
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

    @ModifyArgs(
            method = "renderSnowAndRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setUv(FF)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
                    ordinal = 2
            )
    )
    private void retrorainRedirect$setUv2(Args args, @Share("yMax") LocalFloatRef yMax) {
        if (!RetroRain.RetroRainClient.rainManager.shouldRetro()) {
            return;
        }
        args.set(0, 0.0f);
        args.set(1, yMax.get());
    }

    @ModifyArgs(
            method = "renderSnowAndRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setUv(FF)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
                    ordinal = 3
            )
    )
    private void retrorainRedirect$setUv3(Args args, @Share("yMax") LocalFloatRef yMax) {
        if (!RetroRain.RetroRainClient.rainManager.shouldRetro()) {
            return;
        }
        args.set(0, 1.0f);
        args.set(1, yMax.get());
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
