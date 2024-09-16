package com.lnatit.retrorain.mixin.client;

import com.lnatit.retrorain.RetroRain;
import com.lnatit.retrorain.common.data.CellPos;
import com.lnatit.retrorain.common.data.Nepho;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Level.class, priority = 500)
public class RetroRainMixinLevel {
//    @ModifyReturnValue(method = "getRainLevel", at = @At("RETURN"))
//    private float retrorainInject$getRainLevel(float original) {
//        if (!((Level)(Object) this instanceof ClientLevel))
//            return original;
//        original = Math.max(original, RetroRain.RetroRainClient.rainManager.getRainLevel());
////        if (Minecraft.getInstance().cameraEntity != null) {
////            return Minecraft.getInstance().cameraEntity.getY() <= 196 ? original : 0.0f;
////        }
//        return original;
//    }

    @Shadow
    public float oRainLevel;

    @Shadow
    public float rainLevel;

    @Inject(method = {"getRainLevel"}, at = {@At("HEAD")}, cancellable = true)
    private void retrorainInject$getRainLevel(float delta, CallbackInfoReturnable<Float> cir) {
        if (((Level) (Object) this instanceof ClientLevel)) {
            if (Minecraft.getInstance().player != null) {
                if (
                        Nepho.getCell(
                                Minecraft.getInstance().level,
                                new CellPos(Minecraft.getInstance().player.getOnPos())
                        ) != Nepho.Type.CLEAR
                ) {
                    cir.setReturnValue(
                            Math.max(
                                    Mth.lerp(delta, this.oRainLevel, this.rainLevel),
                                    RetroRain.RetroRainClient.rainManager.getRainLevel()
                            )
                    );
                }
            }
        }
    }
}
