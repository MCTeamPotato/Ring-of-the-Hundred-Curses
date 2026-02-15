package com.kaleblangley.ring_of_the_hundred_curses.mixin.entity;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {

    @Inject(method = "stopHoldingOffHandItem", at = @At("HEAD"), cancellable = true)
    private static void ring_of_the_hundred_curses$unfairTrader(Piglin pPiglin, boolean pShouldBarter, CallbackInfo ci) {
        if (!pShouldBarter) return;
        if (!pPiglin.isAdult()) return;
        if (!pPiglin.getItemInHand(InteractionHand.OFF_HAND).isPiglinCurrency()) return;
        Optional<Player> nearestPlayer = pPiglin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
        if (nearestPlayer.isEmpty()) return;
        Player player = nearestPlayer.get();
        if (!RingUtil.configAndRing(player, getConfig().enableUnfairTrader)) return;
        if (pPiglin.level().random.nextDouble() < getConfig().unfairTraderChance) {
            pPiglin.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            ci.cancel();
        }
    }
}
