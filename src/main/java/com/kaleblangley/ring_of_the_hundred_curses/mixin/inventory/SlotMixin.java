package com.kaleblangley.ring_of_the_hundred_curses.mixin.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Shadow @Final public Container container;

    @Inject(method = "getMaxStackSize()I", at = @At(value = "HEAD"), cancellable = true)
    public void newSize(CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(Math.min(this.container.getMaxStackSize(), 32));
    }
}
