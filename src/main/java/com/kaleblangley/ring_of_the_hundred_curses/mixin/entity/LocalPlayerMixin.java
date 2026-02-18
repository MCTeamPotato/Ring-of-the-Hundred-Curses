package com.kaleblangley.ring_of_the_hundred_curses.mixin.entity;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "canStartSprinting", at = @At("HEAD"), cancellable = true)
    private void ring_of_the_hundred_curses$starryPath(CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (!RingUtil.configAndRing(player, getConfig().enableStarryPath)) return;
        Level level = player.level();
        BlockPos eyePos = BlockPos.containing(player.getEyePosition());
        int skyDarken = ring_of_the_hundred_curses$computeSkyDarken(level);
        int brightness = level.getRawBrightness(eyePos, skyDarken);
        if (brightness < getConfig().starryPathMinimumLightLevel) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private static int ring_of_the_hundred_curses$computeSkyDarken(Level level) {
        if (level.dimensionType().hasFixedTime()) {
            return 0;
        }
        double rainFactor = 1.0D - (double) (level.getRainLevel(1.0F) * 5.0F) / 16.0D;
        double thunderFactor = 1.0D - (double) (level.getThunderLevel(1.0F) * 5.0F) / 16.0D;
        double timeFactor = 0.5D + 2.0D * Mth.clamp(Math.cos(level.getTimeOfDay(1.0F) * ((float) Math.PI * 2F)), -0.25D, 0.25D);
        return (int) ((1.0D - timeFactor * rainFactor * thunderFactor) * 11.0D);
    }
}
