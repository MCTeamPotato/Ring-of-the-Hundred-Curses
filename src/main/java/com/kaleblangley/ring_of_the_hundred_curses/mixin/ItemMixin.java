package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Shadow @Final private int maxStackSize;

    @Inject(method = "getMaxStackSize", at = @At(value = "HEAD"), cancellable = true)
    public void modifyMaxStack(CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(Math.min(this.maxStackSize, 32));
    }
}
