package com.kaleblangley.ring_of_the_hundred_curses.mixin.entity;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

/**
 * 无处藏身：增加怪物对佩戴戒指玩家的近战攻击判定距离，使攻击可以穿透方块
 * No Shelter: Increases melee attack reach against cursed players so attacks can penetrate blocks.
 */
@Mixin(MeleeAttackGoal.class)
public class MeleeAttackGoalMixin {

    @Inject(method = "getAttackReachSqr", at = @At("RETURN"), cancellable = true)
    private void ring_of_the_hundred_curses$noShelterReach(LivingEntity target, CallbackInfoReturnable<Double> cir) {
        if (target instanceof Player player && RingUtil.configAndRing(player, getConfig().enableNoShelter)) {
            double originalSqr = cir.getReturnValue();
            double reach = Math.sqrt(originalSqr) + getConfig().noShelterExtraReach;
            cir.setReturnValue(reach * reach);
        }
    }
}
