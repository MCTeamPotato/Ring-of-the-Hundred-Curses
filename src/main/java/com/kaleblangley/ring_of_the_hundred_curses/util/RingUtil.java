package com.kaleblangley.ring_of_the_hundred_curses.util;

import com.kaleblangley.ring_of_the_hundred_curses.init.ModItem;
import com.kaleblangley.ring_of_the_hundred_curses.item.CursedRing;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public class RingUtil {
    public static boolean configAndRing(LivingEntity livingEntity, boolean config){
        return isEquipRing(livingEntity) && config;
    }
    public static boolean configAndRing(Item item, boolean config){
        return isRing(item) && config;
    }
    public static boolean configAndRing(ItemStack itemStack, boolean config){
        return isRing(itemStack) && config;
    }
    public static boolean isEquipRing(LivingEntity livingEntity){
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(livingEntity).resolve();
        return curiosInventory.map(iCuriosItemHandler -> iCuriosItemHandler.isEquipped(ModItem.RING.get())).orElse(false);
    }

    public static boolean isRing(Item item){
        return item instanceof CursedRing;
    }
    public static boolean isRing(ItemStack itemStack){
        return isRing(itemStack.getItem());
    }
}
