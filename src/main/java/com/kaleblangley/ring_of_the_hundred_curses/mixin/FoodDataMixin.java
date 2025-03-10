package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @Shadow public abstract void eat(int pFoodLevelModifier, float pSaturationLevelModifier);

    @Inject(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void modifyNutrition(Item pItem, ItemStack pStack, LivingEntity entity, CallbackInfo ci, FoodProperties foodproperties){
        if (RingUtil.configAndRing(entity, ModConfigManager.getConfig().enableGreedyEating)){
            int newNutrition = (int) (foodproperties.getNutrition() * (ModConfigManager.getConfig().hungerReductionPercent));
            this.eat(newNutrition, foodproperties.getSaturationModifier());
            ci.cancel();
        }
    }
}
