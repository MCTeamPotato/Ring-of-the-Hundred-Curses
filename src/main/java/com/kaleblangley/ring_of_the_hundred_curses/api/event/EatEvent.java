package com.kaleblangley.ring_of_the_hundred_curses.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EatEvent extends LivingEvent {
    private final Item item;
    private final ItemStack itemStack;
    private final FoodProperties foodProperties;
    private int nutrition;
    private float saturationModifier;

    public EatEvent(LivingEntity entity, Item item, ItemStack itemStack, FoodProperties foodProperties, int nutrition, float saturationModifier) {
        super(entity);
        this.item = item;
        this.itemStack = itemStack;
        this.foodProperties = foodProperties;
        this.nutrition = nutrition;
        this.saturationModifier = saturationModifier;
    }

    public Item getItem() {
        return item;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public FoodProperties getFoodProperties() {
        return foodProperties;
    }

    public int getNutrition() {
        return nutrition;
    }

    public void setNutrition(int nutrition) {
        this.nutrition = nutrition;
    }

    public float getSaturationModifier() {
        return saturationModifier;
    }

    public void setSaturationModifier(float saturationModifier) {
        this.saturationModifier = saturationModifier;
    }
}
