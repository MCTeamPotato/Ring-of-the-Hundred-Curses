package com.kaleblangley.ring_of_the_hundred_curses.mixin.inventory;

import com.kaleblangley.ring_of_the_hundred_curses.event.PlayerEvent;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantMenu.class)
public class MerchantMenuMixin {
    @Shadow @Final private Merchant trader;

    @Shadow
    private void playTradeSound() {
        throw new AssertionError();
    }

    @Inject(
            method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V",
            at = @At("RETURN")
    )
    private void ring_of_the_hundred_curses$applyTradePrices(
            int containerId, Inventory inventory, Merchant merchant, CallbackInfo ci
    ) {
        if (merchant instanceof AbstractVillager villager) {
            PlayerEvent.applyMerchantTradePrices(inventory.player, villager);
        }
    }

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void ring_of_the_hundred_curses$confiscateCustomsResult(
            Player player, int slotId, CallbackInfoReturnable<ItemStack> cir
    ) {
        if (slotId != 2 || player.level().isClientSide) return;

        Slot resultSlot = ((MerchantMenu) (Object) this).getSlot(slotId);
        ItemStack result = resultSlot.getItem();
        if (result.isEmpty()) return;

        ItemStack originalResult = result.copy();
        ItemStack swappedResult = PlayerEvent.maybeSwapMerchantResult(player, originalResult);
        boolean customsClearance = this.trader instanceof AbstractVillager
                && PlayerEvent.shouldInterceptCustomsClearance(player, (AbstractVillager) this.trader);

        if (!customsClearance && swappedResult == originalResult) return;
        if (!customsClearance) {
            resultSlot.setByPlayer(swappedResult);
            return;
        }

        ItemStack returnedResult = swappedResult.copy();
        resultSlot.setByPlayer(ItemStack.EMPTY);
        this.playTradeSound();
        resultSlot.onTake(player, swappedResult);
        ((MerchantMenu) (Object) this).broadcastChanges();
        cir.setReturnValue(returnedResult);
    }
}
