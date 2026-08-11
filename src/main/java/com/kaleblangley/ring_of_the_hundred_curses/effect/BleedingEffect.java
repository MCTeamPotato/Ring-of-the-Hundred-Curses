package com.kaleblangley.ring_of_the_hundred_curses.effect;

import com.kaleblangley.ring_of_the_hundred_curses.init.ModDamageTypes;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModEffect;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

public class BleedingEffect extends MobEffect {
    public BleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        int interval = Math.max(1, getConfig().bleedingWoundInterval);
        return duration > 0 && duration % interval == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;

        if (entity instanceof Player player
                && !RingUtil.configAndRing(player, getConfig().enableBleedingWound)) {
            entity.removeEffect(ModEffect.BLEEDING.get());
            return;
        }

        float damage = Math.max(0.0F, getConfig().bleedingWoundDamage);
        if (damage <= 0.0F || !entity.isAlive()) return;

        int amplifierMultiplier = Math.max(1, amplifier + 1);
        entity.hurt(ModDamageTypes.bleeding(entity), damage * amplifierMultiplier);
    }
}
