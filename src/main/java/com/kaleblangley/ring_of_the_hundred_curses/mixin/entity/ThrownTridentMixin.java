package com.kaleblangley.ring_of_the_hundred_curses.mixin.entity;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin {

    @Shadow
    private boolean dealtDamage;

    @Shadow
    private ItemStack tridentItem;

    @Inject(method = "playerTouch", at = @At("HEAD"))
    private void ring_of_the_hundred_curses$friendOrFoe(Player player, CallbackInfo ci) {
        if (player.level().isClientSide) return;
        if (!this.dealtDamage) return;
        if (!RingUtil.configAndRing(player, ModConfigManager.getConfig().enableFriendOrFoe)) return;
        int loyaltyLevel = EnchantmentHelper.getLoyalty(this.tridentItem);
        if (loyaltyLevel <= 0) return;
        double chance = ModConfigManager.getConfig().friendOrFoeChance;
        if (player.getRandom().nextDouble() < chance) {
            float damage = ModConfigManager.getConfig().friendOrFoeDamage;
            ThrownTrident trident = (ThrownTrident) (Object) this;
            player.hurt(player.damageSources().trident(trident, null), damage);
        }
    }
}
