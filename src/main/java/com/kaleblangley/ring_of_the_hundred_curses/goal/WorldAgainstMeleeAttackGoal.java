package com.kaleblangley.ring_of_the_hundred_curses.goal;

import com.kaleblangley.ring_of_the_hundred_curses.advancement.CurseAdvancementManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

/**
 * Melee goal used by 世界为敌. The vanilla reach formula scales with the
 * mob's width twice, which makes wide passive mobs such as sheep attack while
 * there is still a visible gap between their hitboxes.
 */
public class WorldAgainstMeleeAttackGoal extends MeleeAttackGoal {
    private static final double CONTACT_BUFFER = 0.15D;
    private boolean noShelterReachApplied;

    public WorldAgainstMeleeAttackGoal(
            PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen
    ) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
    }

    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        double contactReach = (this.mob.getBbWidth() + target.getBbWidth()) * 0.5D + CONTACT_BUFFER;
        noShelterReachApplied = false;

        if (target instanceof Player player
                && RingUtil.configAndRing(player, getConfig().enableNoShelter)) {
            HitResult hitResult = this.mob.level().clip(new ClipContext(
                    this.mob.getEyePosition(),
                    target.getEyePosition(),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    this.mob
            ));
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                contactReach += Math.max(0.0D, getConfig().noShelterExtraReach);
                noShelterReachApplied = getConfig().noShelterExtraReach > 0.0D;
            }
        }

        return contactReach * contactReach;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double distanceToTargetSqr) {
        int previousHurtTime = target.hurtTime;
        super.checkAndPerformAttack(target, distanceToTargetSqr);
        if (target instanceof Player player && target.hurtTime > previousHurtTime) {
            CurseAdvancementManager.trigger(player, "world_against");
            if (noShelterReachApplied) {
                CurseAdvancementManager.trigger(player, "no_shelter");
            }
        }
    }
}
