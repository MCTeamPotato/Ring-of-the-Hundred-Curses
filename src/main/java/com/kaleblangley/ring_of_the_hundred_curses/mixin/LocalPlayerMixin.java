package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "canStartSprinting", at = @At("HEAD"), cancellable = true)
    private void preventSprintingInDarkness(CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (RingUtil.configAndRing(player, ModConfigManager.getConfig().enableStarryPath)) {
            int blockLight = player.level().getBrightness(LightLayer.BLOCK, player.blockPosition());
            if (blockLight < ModConfigManager.getConfig().starryPathMinimumLightLevel) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void stopSprintingInDarkness(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (player.isSprinting() && RingUtil.configAndRing(player, ModConfigManager.getConfig().enableStarryPath)) {
            int blockLight = player.level().getBrightness(LightLayer.BLOCK, player.blockPosition());
            if (blockLight < ModConfigManager.getConfig().starryPathMinimumLightLevel) {
                player.setSprinting(false);
            }
        }
    }
}