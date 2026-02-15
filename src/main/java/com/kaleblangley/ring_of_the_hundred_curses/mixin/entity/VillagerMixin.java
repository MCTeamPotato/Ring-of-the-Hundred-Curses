package com.kaleblangley.ring_of_the_hundred_curses.mixin.entity;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mixin(Villager.class)
public class VillagerMixin {

    @Inject(method = "startTrading", at = @At("TAIL"))
    private void ring_of_the_hundred_curses$applySocialParadoxPrice(Player player, CallbackInfo ci) {
        if (!RingUtil.configAndRing(player, getConfig().enableSocialParadox)) {
            return;
        }

        for (MerchantOffer offer : ((Villager) (Object) this).getOffers()) {
            ItemStack baseCost = offer.getBaseCostA();
            if (baseCost.isEmpty()) {
                continue;
            }

            float ratio = Math.max(0.0f, getConfig().socialParadoxPriceIncreaseRatio);
            int extraCost = Math.max(0, (int) Math.ceil(baseCost.getCount() * ratio));
            if (extraCost > 0) {
                offer.addToSpecialPriceDiff(extraCost);
            }
        }
    }
}
