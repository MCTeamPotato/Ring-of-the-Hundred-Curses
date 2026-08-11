package com.kaleblangley.ring_of_the_hundred_curses.mixin.inventory;

import com.kaleblangley.ring_of_the_hundred_curses.event.PlayerEvent;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantResultSlot.class)
public class MerchantResultSlotMixin {
    @Shadow @Final private Player player;
    @Shadow @Final private Merchant merchant;

    @Shadow
    public void onTake(Player player, ItemStack result) {
        throw new AssertionError();
    }

    @Inject(method = "remove", at = @At("RETURN"), cancellable = true)
    private void ring_of_the_hundred_curses$dodgyMerchant(
            int amount, CallbackInfoReturnable<ItemStack> cir
    ) {
        ItemStack result = cir.getReturnValue();
        if (result.isEmpty()) return;
        ItemStack swappedResult = PlayerEvent.maybeSwapMerchantResult(this.player, result);
        if (this.merchant instanceof AbstractVillager villager
                && PlayerEvent.shouldInterceptCustomsClearance(this.player, villager)) {
            this.onTake(this.player, swappedResult);
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }
        cir.setReturnValue(swappedResult);
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void ring_of_the_hundred_curses$customsClearance(
            Player player, ItemStack result, CallbackInfo ci
    ) {
        if (this.merchant instanceof AbstractVillager villager) {
            PlayerEvent.handleMerchantResultTaken(player, villager, result);
        }
    }
}
