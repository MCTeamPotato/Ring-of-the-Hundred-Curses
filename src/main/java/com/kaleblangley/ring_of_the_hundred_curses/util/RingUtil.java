package com.kaleblangley.ring_of_the_hundred_curses.util;

import com.kaleblangley.ring_of_the_hundred_curses.init.ModItem;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public class RingUtil {
    public static boolean isEquipRing(LivingEntity livingEntity){
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(livingEntity).resolve();
        return curiosInventory.map(iCuriosItemHandler -> iCuriosItemHandler.isEquipped(ModItem.RING.get())).orElse(false);
    }
}
