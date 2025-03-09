package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.item.CursedRing;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;


@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvent {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void ringTooltip(ItemTooltipEvent event) {
        List<Component> toolTips = event.getToolTip();
        MutableComponent slot = Component.translatable("curios.tooltip.slot").withStyle(ChatFormatting.GOLD);
        MutableComponent identifier = Component.translatable("curios.identifier.ring").withStyle(ChatFormatting.YELLOW);

        if (event.getItemStack().getItem() instanceof CursedRing) {
            toolTips.add(Component.translatable("message.ring_of_the_hundred_curses.description_1"));
            toolTips.add(slot.append(" ").append(identifier));
        }
    }

    @SubscribeEvent
    public static void entitySpawn(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof PathfinderMob mob && !(mob instanceof RangedAttackMob) && getConfig().enableWorldAgainst) {
                mob.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(mob, Player.class, getConfig().entityAttackChange, true, false, entity -> entity instanceof Player player && RingUtil.isEquipRing(player)));
                mob.goalSelector.addGoal(1, new MeleeAttackGoal(mob, getConfig().entityAttackSpeed, true));
            }
        }
    }

    @SubscribeEvent
    public static void sleepEvent(SleepingTimeCheckEvent event) {
        if (RingUtil.configAndRing(event.getEntity(), getConfig().enableSleeplessNights)) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void stackItem(ItemStackedOnOtherEvent event) {
        if (!RingUtil.isEquipRing(event.getPlayer())) return;
        ItemStack carryItem = event.getCarriedItem();
        ItemStack stackedOnItem = event.getStackedOnItem();
        int carryCount = carryItem.getCount();
        int stackedOnCount = stackedOnItem.getCount();
        int totalCount = carryCount + stackedOnCount;

        if (ItemStack.isSameItemSameTags(carryItem, stackedOnItem) && getConfig().enableBackpackLimit) {
            if (totalCount == getConfig().maxStackSize * 2) {
                event.setCanceled(true);
            } else if (totalCount > getConfig().maxStackSize) {
                if (carryCount < stackedOnCount) {
                    carryItem.setCount(stackedOnCount);
                    stackedOnItem.setCount(carryCount);
                }
                event.setCanceled(true);
            }
        }

        if (stackedOnItem.is(Items.SHIELD) && event.getSlot().getContainerSlot() == 40 && getConfig().enableShieldOnTheRight) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void livingSwapItem(LivingSwapItemsEvent.Hands event) {
        if (RingUtil.configAndRing(event.getEntity(), getConfig().enableShieldOnTheRight) && event.getItemSwappedToOffHand().is(Items.SHIELD)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void breathEvent(LivingBreatheEvent event) {
        if (event.getEntity() instanceof Player player && RingUtil.configAndRing(player, getConfig().enableLackOfOxygen)) {
            if ((player.yo < 30 || player.yo > 130) && !player.hasEffect(MobEffects.WATER_BREATHING)) {
                event.setCanRefillAir(false);
                if (player.tickCount % 2 == 0) event.setCanBreathe(false);
            }
        }
    }
}
