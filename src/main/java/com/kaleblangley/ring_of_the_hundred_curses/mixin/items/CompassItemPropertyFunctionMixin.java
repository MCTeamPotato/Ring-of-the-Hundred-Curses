package com.kaleblangley.ring_of_the_hundred_curses.mixin.items;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompassItemPropertyFunction.class)
public class CompassItemPropertyFunctionMixin {

    @Inject(method = "unclampedCall", at = @At("HEAD"), cancellable = true)
    private void disableCompass(ItemStack pStack, ClientLevel pLevel, LivingEntity pEntity, int pSeed, CallbackInfoReturnable<Float> cir) {
        Entity entity = pEntity != null ? pEntity : pStack.getEntityRepresentation();
        if (entity instanceof LivingEntity livingEntity && RingUtil.configAndRing(livingEntity, ModConfigManager.getConfig().enableLostDirection)) {
            // Return a random-ish rotation based on game time to make the compass spin
            long time = pLevel != null ? pLevel.getGameTime() : 0;
            double rotation = Math.sin(time * 0.1) * 0.5 + Math.random() * 0.1;
            cir.setReturnValue((float) rotation);
        }
    }
}
