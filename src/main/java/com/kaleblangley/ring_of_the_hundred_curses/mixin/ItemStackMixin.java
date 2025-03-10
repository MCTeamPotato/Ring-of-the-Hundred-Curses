package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import com.kaleblangley.ring_of_the_hundred_curses.capability.CurseMaxSizeProvider;
import com.kaleblangley.ring_of_the_hundred_curses.capability.ICurseMaxSize;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin extends CapabilityProvider<ItemStack> {
    protected ItemStackMixin(Class<ItemStack> baseClass) {
        super(baseClass);
    }

    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    public void modifyStackSize(CallbackInfoReturnable<Integer> cir){
        int curseMaxSize = this.getCapability(CurseMaxSizeProvider.CURSE_MAX_SIZE).map(ICurseMaxSize::getCurseMaxSize).orElse(0);
        if (curseMaxSize != 0){
            cir.setReturnValue(curseMaxSize);
        }
    }
}
