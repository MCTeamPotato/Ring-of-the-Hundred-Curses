package com.kaleblangley.ring_of_the_hundred_curses.mixin.inventory;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public class ResultSlotMixin {

    @Inject(method = "onTake", at = @At("TAIL"))
    private void ring_of_the_hundred_curses$befuddledArtisan(Player player, ItemStack pStack, CallbackInfo ci) {
        if (player.level().isClientSide) return;
        if (!RingUtil.configAndRing(player, ModConfigManager.getConfig().enableBefuddledArtisan)) return;
        if (player.level().random.nextDouble() >= ModConfigManager.getConfig().befuddledArtisanChance) return;
        if (pStack.isDamageableItem()) {
            int maxDamage = pStack.getMaxDamage();
            int damageAmount = 1 + player.level().random.nextInt(Math.max(1, (int) (maxDamage * ModConfigManager.getConfig().befuddledArtisanDurabilityLossPercent)));
            pStack.setDamageValue(Math.min(damageAmount, maxDamage - 1));
        } else if (pStack.getCount() > 1) {
            int count = pStack.getCount();
            int reduction = 1 + player.level().random.nextInt(Math.max(1, (int) (count * ModConfigManager.getConfig().befuddledArtisanCountLossPercent)));
            pStack.setCount(Math.max(1, count - reduction));
        }
    }
}
