package com.kaleblangley.ring_of_the_hundred_curses.mixin.inventory;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mixin(GrindstoneMenu.class)
public class GrindstoneMenuMixin {

    @Inject(method = "createResult", at = @At("TAIL"))
    private void ring_of_the_hundred_curses$grindingWear(CallbackInfo ci) {
        GrindstoneMenu menu = (GrindstoneMenu) (Object) this;
        Slot resultSlot = menu.getSlot(2);
        ItemStack result = resultSlot.getItem();
        if (result.isEmpty() || !result.isDamageableItem()) return;
        Slot playerSlot = menu.getSlot(3);
        if (!(playerSlot.container instanceof Inventory playerInv)) return;
        Player player = playerInv.player;
        if (!RingUtil.configAndRing(player, getConfig().enableGrindingWear)) return;
        double lossPercent = getConfig().grindingWearDurabilityLossPercent;
        int durabilityLoss = (int) Math.ceil(result.getMaxDamage() * lossPercent);
        int newDamage = result.getDamageValue() + durabilityLoss;
        result.setDamageValue(Math.min(newDamage, result.getMaxDamage() - 1));
    }
}
