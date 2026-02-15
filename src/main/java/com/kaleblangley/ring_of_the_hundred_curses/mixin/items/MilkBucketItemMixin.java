package com.kaleblangley.ring_of_the_hundred_curses.mixin.items;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MilkBucketItem.class)
public class MilkBucketItemMixin {

    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void ring_of_the_hundred_curses$onFinishUsingMilk(ItemStack stack, net.minecraft.world.level.Level level, LivingEntity entityLiving, CallbackInfoReturnable<ItemStack> cir) {
        if (!level.isClientSide && entityLiving instanceof Player player) {
            if (RingUtil.configAndRing(player, ModConfigManager.getConfig().enableLactoseIntolerance)) {
                if (entityLiving instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
                    serverPlayer.awardStat(Stats.ITEM_USED.get((MilkBucketItem) (Object) this));
                }
                if (entityLiving instanceof Player && !((Player) entityLiving).getAbilities().instabuild) {
                    stack.shrink(1);
                }
                cir.setReturnValue(stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack);
            }
        }
    }
}