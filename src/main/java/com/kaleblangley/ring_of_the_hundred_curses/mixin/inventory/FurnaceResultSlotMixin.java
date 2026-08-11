package com.kaleblangley.ring_of_the_hundred_curses.mixin.inventory;

import com.kaleblangley.ring_of_the_hundred_curses.event.PlayerEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FurnaceResultSlot.class)
public class FurnaceResultSlotMixin {
    @Shadow @Final private Player player;

    @Inject(method = "remove", at = @At("RETURN"), cancellable = true)
    private void ring_of_the_hundred_curses$terribleCook(int amount,
                                                          CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        if (result.isEmpty()) return;
        cir.setReturnValue(PlayerEvent.maybeConvertTerribleCook(this.player, result));
    }
}
