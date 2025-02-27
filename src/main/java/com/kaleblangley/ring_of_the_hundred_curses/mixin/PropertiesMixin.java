package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.Properties.class)
public class PropertiesMixin {
    @Shadow int maxStackSize;

    @Inject(method = "stacksTo", at = @At(value = "RETURN"))
    public void modifyMaxStack(int pMaxStackSize, CallbackInfoReturnable<Item.Properties> cir){
        this.maxStackSize = 1;
    }
}
