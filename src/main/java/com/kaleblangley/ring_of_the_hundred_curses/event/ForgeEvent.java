package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.api.event.EatEvent;
import com.kaleblangley.ring_of_the_hundred_curses.capability.CurseMaxSizeProvider;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModTag;
import com.kaleblangley.ring_of_the_hundred_curses.item.CursedRing;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.List;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;


@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvent {
    
    private static final List<MobEffect> HARMFUL_EFFECTS;
    
    static {
        List<MobEffect> effects = new java.util.ArrayList<>();
        for (MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
            if (effect.getCategory() == MobEffectCategory.HARMFUL && 
                effect != MobEffects.HARM && effect != MobEffects.HEAL) {
                effects.add(effect);
            }
        }
        HARMFUL_EFFECTS = List.copyOf(effects);
    }

    @SubscribeEvent
    public static void curiosChange(CurioChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (RingUtil.isRing(event.getTo())) {
                player.getInventory().items.forEach(itemStack -> {
                    RingUtil.setCurseMaxSizeCapability(itemStack, getConfig().maxStackSize);
                });
            } else if (RingUtil.isRing(event.getFrom())) {
                player.getInventory().items.forEach(itemStack -> {
                    RingUtil.setCurseMaxSizeCapability(itemStack, 0);
                });
            }
        }
    }

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<ItemStack> event) {
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
        ItemStack carryItem = event.getCarriedItem();
        ItemStack stackedOnItem = event.getStackedOnItem();
        RingUtil.backpackLimitSizeModify(event.getPlayer(), carryItem);

        if (stackedOnItem.is(Items.SHIELD) && event.getSlot().getContainerSlot() == 40 && RingUtil.configAndRing(event.getPlayer(), getConfig().enableShieldOnTheRight)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void pickUpItem(PlayerEvent.ItemPickupEvent event) {
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
        if (event.getEntity() instanceof Player player) {
            if (RingUtil.configAndRing(player, getConfig().enableLackOfOxygen)) {
                if (player.level().dimension() == Level.END && !getConfig().endCanBreath) {
                    event.setCanBreathe(false);
                    event.setConsumeAirAmount(2);
                } else if (player.level().dimension() == Level.NETHER && !getConfig().netherCanBreath) {
                    event.setCanBreathe(false);
                } else if ((player.yo < getConfig().minimumBreathY || player.yo > getConfig().maximumBreathY) && !player.hasEffect(MobEffects.WATER_BREATHING)) {
                    event.setCanRefillAir(false);
                    if (player.tickCount % 2 == 0) event.setCanBreathe(false);
                }
            }

            if (RingUtil.configAndRing(player, getConfig().enableOxygenDeprivation)) {
                if (player.isSprinting()) {
                    event.setCanBreathe(false);
                    event.setConsumeAirAmount(getConfig().sprintingAirConsumption);
                }
            }
        }
    }

    @SubscribeEvent
    public static void eatFood(EatEvent event) {
        LivingEntity entity = event.getEntity();
        FoodProperties foodProperties = event.getFoodProperties();
        ItemStack itemStack = event.getItemStack();

        if (RingUtil.configAndRing(entity, getConfig().enableGreedyEating)) {
            int newNutrition = (int) (foodProperties.getNutrition() * (getConfig().hungerReductionPercent));
            event.setNutrition(newNutrition);
        }

        if (itemStack.is(ModTag.RAW_FOOD) && RingUtil.configAndRing(entity, getConfig().enableWeakStomach)) {
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(getConfig().rawMeatDebuffId));
            MobEffectInstance effectInstance = new MobEffectInstance(effect, getConfig().rawMeatDebuffDuration, getConfig().rawMeatDebuffAmplifier);
            entity.addEffect(effectInstance);
        }
    }

    @SubscribeEvent
    public static void breakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        float originalSpeed = event.getOriginalSpeed();
        BlockState state = event.getState();
        ItemStack handItem = player.getMainHandItem();

        event.setNewSpeed(breakSpeedGet(player, originalSpeed, state, handItem));
    }

    private static float breakSpeedGet(Player player, float originalSpeed, BlockState state, ItemStack handItem) {
        if (!RingUtil.isEquipRing(player)) return originalSpeed;

        if (getConfig().enableSluggishHands) {
            originalSpeed *= getConfig().multiplyRawSpeed;
        }
        if (getConfig().enableWeaponless && state.is(ModTag.ALWAYS_DIG)) {
            return originalSpeed;
        }
        if (!getConfig().enableSinglePurposeTools) return originalSpeed;
        if (state.is(ModTag.AXE_DIG) && handItem.is(ItemTags.AXES)) return originalSpeed;
        if (state.is(ModTag.HOE_DIG) && handItem.is(ItemTags.HOES)) return originalSpeed;
        if (state.is(ModTag.PICKAXE_DIG) && handItem.is(ItemTags.PICKAXES)) return originalSpeed;
        if (state.is(ModTag.SHOVEL_DIG) && handItem.is(ItemTags.SHOVELS)) return originalSpeed;
        return 0;
    }

    @SubscribeEvent
    public static void weakwallEffect(LivingEntityUseItemEvent.Tick event) {
        if (event.getEntity() instanceof Player player && RingUtil.configAndRing(player, getConfig().enableWeakWall)) {
            ItemStack itemStack = event.getItem();
            if (itemStack.getItem() instanceof ShieldItem && !itemStack.isEmpty()) {
                if (player.tickCount % getConfig().shieldDurabilityDrainInterval == 0) {
                    itemStack.hurtAndBreak(getConfig().shieldDurabilityDrainAmount, player, (playerEntity) -> playerEntity.broadcastBreakEvent(player.getUsedItemHand()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void hordeMindEffect(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player && RingUtil.configAndRing(player, getConfig().enableHordeMind)) {
            LivingEntity deadEntity = event.getEntity();
            if (deadEntity instanceof Monster && deadEntity.level().random.nextDouble() < getConfig().hordeMindSpawnChance) {
                LivingEntity newEntity = (LivingEntity) deadEntity.getType().create(deadEntity.level());
                CompoundTag nbtData = new CompoundTag();
                deadEntity.saveWithoutId(nbtData);
                nbtData.remove("UUID");
                nbtData.putFloat("Health", newEntity.getMaxHealth());
                newEntity.load(nbtData);
                newEntity.setPos(deadEntity.getX(), deadEntity.getY(), deadEntity.getZ());
                deadEntity.level().addFreshEntity(newEntity);
            }
        }
    }

    @SubscribeEvent
    public static void timeRiftEffect(ProjectileImpactEvent event) {
        if (event.getProjectile() instanceof ThrownEnderpearl enderpearl) {
            if (enderpearl.getOwner() instanceof Player player) {
                if (RingUtil.configAndRing(player, getConfig().enableTimeRift)) {
                    Level level = enderpearl.level();
                    if (!level.isClientSide) {
                        Vec3 impactPos = enderpearl.position();
                        spawnEndermitesAtPosition(impactPos, level, player);
                    }
                }
            }
        }
    }

    private static void spawnEndermitesAtPosition(Vec3 position, Level level, Player player) {
        double spawnX = position.x;
        double spawnY = position.y;
        double spawnZ = position.z;
        for (int y = 0; y < 5; y++) {
            double testY = spawnY + y - 2;
            if (level.getBlockState(new BlockPos((int) spawnX, (int) testY - 1, (int) spawnZ)).isSolid() && !level.getBlockState(new BlockPos((int) spawnX, (int) testY, (int) spawnZ)).isSolid()) {
                spawnY = testY;
                break;
            }
        }
        Endermite endermite = new Endermite(EntityType.ENDERMITE, level);
        endermite.setPos(spawnX, spawnY, spawnZ);
        endermite.setTarget(player);
        level.addFreshEntity(endermite);
    }

    @SubscribeEvent
    public static void preventAnimalTaming(AnimalTameEvent event) {
        if (event.getTamer() instanceof Player) {
            Player player = (Player) event.getTamer();
            if (RingUtil.configAndRing(player, getConfig().enableLonelyMaster)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void neurologicalDegenerationEffect(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (RingUtil.configAndRing(player, getConfig().enableNeurologicalDegeneration)) {
                applyRandomHarmfulEffect(player);
            }
        }
    }

    private static void applyRandomHarmfulEffect(Player player) {
        if (HARMFUL_EFFECTS.isEmpty()) {
            return;
        }
        MobEffect randomEffect = HARMFUL_EFFECTS.get(player.level().random.nextInt(HARMFUL_EFFECTS.size()));
        int amplifier = player.level().random.nextInt(3);
        int duration = 1200 + player.level().random.nextInt(2401);
        MobEffectInstance effectInstance = new MobEffectInstance(randomEffect, duration, amplifier);
        player.addEffect(effectInstance);
    }
}