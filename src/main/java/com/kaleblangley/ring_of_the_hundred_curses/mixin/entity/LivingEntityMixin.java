package com.kaleblangley.ring_of_the_hundred_curses.mixin.entity;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "getArmorValue", at = @At("RETURN"), cancellable = true)
    private void ring_of_the_hundred_curses$modifyArmorValueByDurability(CallbackInfoReturnable<Integer> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (!(livingEntity instanceof Player player)) {
            return;
        }
        if (!RingUtil.configAndRing(player, getConfig().enableDilapidatedWarrior)) {
            return;
        }
        double originalArmorValue = cir.getReturnValue();
        double fullArmorValue = 0.0d;
        double effectiveArmorValue = 0.0d;
        for (ItemStack armorStack : player.getArmorSlots()) {
            if (!(armorStack.getItem() instanceof ArmorItem armorItem)) {
                continue;
            }
            double defense = armorItem.getDefense();
            fullArmorValue += defense;
            if (armorStack.isDamageableItem() && armorStack.getMaxDamage() > 0) {
                int remainingDurability = armorStack.getMaxDamage() - armorStack.getDamageValue();
                double durabilityRatio = Math.max(0.0d, Math.min(1.0d, (double) remainingDurability / armorStack.getMaxDamage()));
                effectiveArmorValue += defense * durabilityRatio;
            } else {
                effectiveArmorValue += defense;
            }
        }
        if (fullArmorValue <= 0.0d) {
            return;
        }
        double nonArmorBonusValue = Math.max(0.0d, originalArmorValue - fullArmorValue);
        double adjustedArmorValue = nonArmorBonusValue + effectiveArmorValue;
        if (adjustedArmorValue >= originalArmorValue) {
            return;
        }
        cir.setReturnValue((int) Math.max(0, Math.round(adjustedArmorValue)));
    }
}
