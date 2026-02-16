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

    /**
     * 打磨损耗：砂轮会折损耐久值
     */
    @Inject(method = "createResult", at = @At("TAIL"))
    private void ring_of_the_hundred_curses$grindingWear(CallbackInfo ci) {
        GrindstoneMenu menu = (GrindstoneMenu) (Object) this;
        // result slot 是第3个 slot (index 2)
        Slot resultSlot = menu.getSlot(2);
        ItemStack result = resultSlot.getItem();
        if (result.isEmpty() || !result.isDamageableItem()) return;

        // 通过反射获取 access 字段太麻烦，用 getNearestPlayer 兜底
        // 遍历 menu 的 player inventory slots 来找到玩家
        // player inventory 从 slot index 3 开始 (0=input1, 1=input2, 2=output, 3-38=player inventory)
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
