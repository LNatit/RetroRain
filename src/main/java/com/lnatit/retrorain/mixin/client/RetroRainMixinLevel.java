package com.lnatit.retrorain.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.lnatit.retrorain.RetroRain;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Level.class)
public class RetroRainMixinLevel
{
    @ModifyReturnValue(method = "getRainLevel", at = @At("RETURN"))
    private float retrorainInject$getRainLevel(float original) {
        original = Math.max(original, RetroRain.RetroRainClient.rainManager.getRainLevel());
//        if (Minecraft.getInstance().cameraEntity != null) {
//            return Minecraft.getInstance().cameraEntity.getY() <= 196 ? original : 0.0f;
//        }
        return original;
    }
}
