package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfig;
import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import net.minecraft.world.food.FoodProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {
    @Shadow @Final private int nutrition;

    @Inject(method = "getNutrition", at = @At(value = "RETURN"), cancellable = true)
    public void modifyNutrition(CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue((int) (this.nutrition * (ModConfigManager.getConfig().hungerReductionPercent)));
    }
}
