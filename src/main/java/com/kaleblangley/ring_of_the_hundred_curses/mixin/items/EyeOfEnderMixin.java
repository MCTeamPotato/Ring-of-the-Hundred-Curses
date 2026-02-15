package com.kaleblangley.ring_of_the_hundred_curses.mixin.items;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EyeOfEnder.class)
public class EyeOfEnderMixin {
    
    @Shadow
    private boolean surviveAfterDeath;

    @Inject(method = "signalTo", at = @At("TAIL"))
    private void ring_of_the_hundred_curses$forceShatter(BlockPos pos, CallbackInfo ci) {
        EyeOfEnder eyeOfEnder = (EyeOfEnder)(Object)this;
        Player nearestPlayer = eyeOfEnder.level().getNearestPlayer(
            eyeOfEnder.getX(), eyeOfEnder.getY(), eyeOfEnder.getZ(), 10.0, false);
        if (nearestPlayer != null && RingUtil.configAndRing(nearestPlayer, ModConfigManager.getConfig().enableShatteredEye)) {
            this.surviveAfterDeath = false;
        }
    }
} 