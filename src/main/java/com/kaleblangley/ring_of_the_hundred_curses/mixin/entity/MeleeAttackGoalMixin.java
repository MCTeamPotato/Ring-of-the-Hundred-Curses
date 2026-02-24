package com.kaleblangley.ring_of_the_hundred_curses.mixin.entity;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mixin(MeleeAttackGoal.class)
public class MeleeAttackGoalMixin {

    @Shadow
    @Final
    protected PathfinderMob mob;

    @Inject(method = "getAttackReachSqr", at = @At("RETURN"), cancellable = true)
    private void ring_of_the_hundred_curses$noShelterReach(LivingEntity target, CallbackInfoReturnable<Double> cir) {
        if (target instanceof Player player && RingUtil.configAndRing(player, getConfig().enableNoShelter)) {
            HitResult hitResult = this.mob.level().clip(new ClipContext(this.mob.getEyePosition(), target.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob));
            if (hitResult.getType() != HitResult.Type.BLOCK) {
                return;
            }
            double originalSqr = cir.getReturnValue();
            double reach = Math.sqrt(originalSqr) + getConfig().noShelterExtraReach;
            cir.setReturnValue(reach * reach);
        }
    }
}
