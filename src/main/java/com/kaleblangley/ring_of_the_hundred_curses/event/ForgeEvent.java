package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.capability.CurseMaxSizeProvider;
import com.kaleblangley.ring_of_the_hundred_curses.item.CursedRing;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.List;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;


@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvent {
    @SubscribeEvent
    public static void curiosChange(CurioChangeEvent event){
        if (event.getEntity() instanceof Player player){
            if (RingUtil.isRing(event.getTo())){
                player.getInventory().items.forEach(itemStack -> {
                    RingUtil.setCurseMaxSizeCapability(itemStack, getConfig().maxStackSize);
                });
            } else if (RingUtil.isRing(event.getFrom())){
                player.getInventory().items.forEach(itemStack -> {
                    RingUtil.setCurseMaxSizeCapability(itemStack, 0);
                });
            }
        }
    }
    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<ItemStack> event){
        event.addCapability(new ResourceLocation(RingOfTheHundredCurses.MODID, "curse_max_size"), new CurseMaxSizeProvider());
    }

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
        RingUtil.backpackLimitSizeModify(event.getPlayer(), carryItem);

        if (stackedOnItem.is(Items.SHIELD) && event.getSlot().getContainerSlot() == 40 && getConfig().enableShieldOnTheRight) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void pickUpItem(PlayerEvent.ItemPickupEvent event){
        RingUtil.backpackLimitSizeModify(event.getEntity(), event.getStack());
    }

    @SubscribeEvent
    public static void itemSwap(LivingSwapItemsEvent.Hands event) {
        if (RingUtil.configAndRing(event.getEntity(), getConfig().enableShieldOnTheRight) && event.getItemSwappedToOffHand().is(Items.SHIELD)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void breathEvent(LivingBreatheEvent event) {
        if (event.getEntity() instanceof Player player && RingUtil.configAndRing(player, getConfig().enableLackOfOxygen)) {
            if (player.level().dimension() == Level.END && !getConfig().endCanBreath){
                event.setCanBreathe(false);
                event.setConsumeAirAmount(2);
            } else if (player.level().dimension() == Level.NETHER && !getConfig().netherCanBreath){
                event.setCanBreathe(false);
            } else if ((player.yo < getConfig().minimumBreathY || player.yo > getConfig().maximumBreathY) && !player.hasEffect(MobEffects.WATER_BREATHING)) {
                event.setCanRefillAir(false);
                if (player.tickCount % 2 == 0) event.setCanBreathe(false);
            }
        }
    }
}
