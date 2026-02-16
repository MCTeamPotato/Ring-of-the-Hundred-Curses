package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.api.event.EatEvent;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModTag;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvent {

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

    @SubscribeEvent
    public static void preventAnimalTaming(AnimalTameEvent event) {
        if (event.getTamer() instanceof Player) {
            Player player = event.getTamer();
            if (RingUtil.configAndRing(player, getConfig().enableLonelyMaster)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onShieldBlock(ShieldBlockEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (RingUtil.configAndRing(player, getConfig().enableMuscleWeakness)) {
                if (player.getRandom().nextInt(100) < getConfig().muscleWeaknessChance) {
                    player.getCooldowns().addCooldown(player.getUseItem().getItem(), 100);
                    player.stopUsingItem();
                    player.level().broadcastEntityEvent(player, (byte) 30);
                }
            }
        }
    }

    @SubscribeEvent
    public static void lavaSacrificeEffect(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (RingUtil.configAndRing(player, getConfig().enableLavaSacrifice)) {
                if (player.level().dimension() == Level.NETHER) {
                    int fireDuration = getConfig().lavaSacrificeFireDuration;
                    player.setSecondsOnFire(fireDuration / 20);
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
            BlockPos groundPos = new BlockPos((int) spawnX, (int) testY - 1, (int) spawnZ);
            BlockPos spawnPos = new BlockPos((int) spawnX, (int) testY, (int) spawnZ);
            if (level.getBlockState(groundPos).isCollisionShapeFullBlock(level, groundPos) && 
                !level.getBlockState(spawnPos).isCollisionShapeFullBlock(level, spawnPos)) {
                spawnY = testY;
                break;
            }
        }
        Endermite endermite = new Endermite(EntityType.ENDERMITE, level);
        endermite.setPos(spawnX, spawnY, spawnZ);
        endermite.setTarget(player);
        level.addFreshEntity(endermite);
    }

    private static final String JUSTIFIED_COMBAT_TAG = "ring_of_the_hundred_curses.attacked_player";

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                attacker.addTag(JUSTIFIED_COMBAT_TAG);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (!RingUtil.configAndRing(player, getConfig().enableJustifiedCombat)) return;
        if (player.level().isClientSide) return;
        if (event.getTarget() instanceof LivingEntity target) {
            if (!target.getTags().contains(JUSTIFIED_COMBAT_TAG)) {
                event.setCanceled(true);
            }
        }
    }
} 