package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import com.kaleblangley.ring_of_the_hundred_curses.api.event.EatEvent;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @Shadow public abstract void eat(int pFoodLevelModifier, float pSaturationLevelModifier);

    @Inject(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"), cancellable = true)
    public void modifyNutrition(Item pItem, ItemStack pStack, LivingEntity entity, CallbackInfo ci, @Local FoodProperties foodproperties){
        EatEvent eatEvent = new EatEvent(entity, pItem, pStack, foodproperties, foodproperties.getNutrition(), foodproperties.getSaturationModifier());
        MinecraftForge.EVENT_BUS.post(eatEvent);
        if (!eatEvent.isCanceled()) {
            this.eat(eatEvent.getNutrition(), eatEvent.getSaturationModifier());
        }
        ci.cancel();
    }
}
