package com.kaleblangley.ring_of_the_hundred_curses.mixin.inventory;

import com.kaleblangley.ring_of_the_hundred_curses.advancement.CurseAdvancementManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;
import static com.kaleblangley.ring_of_the_hundred_curses.init.ModPlayerEventKeys.*;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin {
    @Shadow @Final private Container enchantSlots;
    @Shadow @Final public int[] costs;

    @Shadow
    private List<net.minecraft.world.item.enchantment.EnchantmentInstance> getEnchantmentList(
            ItemStack stack, int slot, int cost
    ) {
        throw new AssertionError();
    }

    @Shadow @Final private net.minecraft.world.inventory.ContainerLevelAccess access;

    @Unique
    private final int[] ring_of_the_hundred_curses$baseCosts = new int[3];

    @Unique
    private int ring_of_the_hundred_curses$activeClickSlot = -1;

    @Inject(method = "slotsChanged", at = @At("RETURN"))
    private void ring_of_the_hundred_curses$applyGreedyTomeCost(Container container, CallbackInfo ci) {
        if (container != this.enchantSlots) return;
        System.arraycopy(this.costs, 0, this.ring_of_the_hundred_curses$baseCosts, 0,
                Math.min(this.costs.length, this.ring_of_the_hundred_curses$baseCosts.length));

        this.access.execute((level, pos) -> {
            Player player = level.getNearestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 8.0D, false);
            if (player == null || !RingUtil.configAndRing(player, getConfig().enableGreedyTome)) return;

            double multiplier = Math.max(0.0D, getConfig().greedyTomeCostMultiplier);
            boolean changed = false;
            for (int i = 0; i < this.costs.length && i < this.ring_of_the_hundred_curses$baseCosts.length; i++) {
                int baseCost = this.ring_of_the_hundred_curses$baseCosts[i];
                int modifiedCost = baseCost <= 0
                        ? baseCost
                        : (int) Math.min(Integer.MAX_VALUE, Math.ceil(baseCost * multiplier));
                changed |= modifiedCost != this.costs[i];
                this.costs[i] = modifiedCost;
            }
            if (changed) {
                CurseAdvancementManager.trigger(player, "greedy_tome");
            }
        });
        ((EnchantmentMenu) (Object) this).broadcastChanges();
    }

    @Inject(method = "clickMenuButton", at = @At("HEAD"))
    private void ring_of_the_hundred_curses$rememberClickSlot(
            Player player, int id, CallbackInfoReturnable<Boolean> cir
    ) {
        this.ring_of_the_hundred_curses$activeClickSlot = id >= 0 && id < this.costs.length ? id : -1;
    }

    @Inject(method = "clickMenuButton", at = @At("RETURN"))
    private void ring_of_the_hundred_curses$clearClickSlot(
            Player player, int id, CallbackInfoReturnable<Boolean> cir
    ) {
        this.ring_of_the_hundred_curses$activeClickSlot = -1;
    }

    @ModifyVariable(method = "getEnchantmentList", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private int ring_of_the_hundred_curses$useBaseCostForEnchantments(int cost) {
        int slot = this.ring_of_the_hundred_curses$activeClickSlot;
        if (slot >= 0 && slot < this.ring_of_the_hundred_curses$baseCosts.length
                && this.ring_of_the_hundred_curses$baseCosts[slot] > 0) {
            return this.ring_of_the_hundred_curses$baseCosts[slot];
        }
        return cost;
    }

    @Inject(method = "clickMenuButton", at = @At("RETURN"))
    private void ring_of_the_hundred_curses$trackRepeatedEnchanting(Player player, int id,
                                                                     CallbackInfoReturnable<Boolean> cir) {
        if (player.level().isClientSide || !cir.getReturnValue()
                || !RingUtil.configAndRing(player, getConfig().enableEndlessQuiz)) {
            return;
        }

        ItemStack item = enchantSlots.getItem(0);
        if (item.isEmpty() || !ring_of_the_hundred_curses$isWeaponOrTool(item)) return;

        String itemId = item.getItem().builtInRegistryHolder().key().location().toString();
        CompoundTag data = player.getPersistentData();
        long day = player.level().getDayTime() / 24000L;
        if (data.getLong(ENDLESS_QUIZ_DAY_KEY) != day
                || !itemId.equals(data.getString(ENDLESS_QUIZ_ITEM_KEY))) {
            data.putLong(ENDLESS_QUIZ_DAY_KEY, day);
            data.putString(ENDLESS_QUIZ_ITEM_KEY, itemId);
            data.putInt(ENDLESS_QUIZ_COUNT_KEY, 0);
        }

        int count = data.getInt(ENDLESS_QUIZ_COUNT_KEY) + 1;
        data.putInt(ENDLESS_QUIZ_COUNT_KEY, count);
        int limit = Math.max(0, getConfig().endlessQuizDailyLimit);
        int reduction = Math.max(0, getConfig().endlessQuizLevelReduction);
        if (count <= limit || reduction == 0) return;

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(item);
        if (enchantments.isEmpty()) return;
        Map<Enchantment, Integer> reduced = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            reduced.put(entry.getKey(), Math.max(1, entry.getValue() - reduction));
        }
        EnchantmentHelper.setEnchantments(reduced, item);
        enchantSlots.setItem(0, item);
        enchantSlots.setChanged();
        CurseAdvancementManager.trigger(player, "endless_quiz");
    }

    private static boolean ring_of_the_hundred_curses$isWeaponOrTool(ItemStack stack) {
        return stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof DiggerItem
                || stack.getItem() instanceof BowItem
                || stack.getItem() instanceof CrossbowItem
                || stack.getItem() instanceof TridentItem;
    }
}
