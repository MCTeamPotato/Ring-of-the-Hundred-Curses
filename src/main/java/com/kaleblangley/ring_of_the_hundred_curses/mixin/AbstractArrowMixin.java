package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin {

    @ModifyArg(method = "shoot(DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;shoot(DDDFF)V"), index = 4)
    public float increaseInaccuracy(float inaccuracy) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        if (arrow.getOwner() instanceof Player player) {
            if (RingUtil.configAndRing(player, ModConfigManager.getConfig().enableOutlineMaster)) {
                return inaccuracy + ModConfigManager.getConfig().outlineMasterInaccuracyIncrease;
            }
        }
        return inaccuracy;
    }
} 