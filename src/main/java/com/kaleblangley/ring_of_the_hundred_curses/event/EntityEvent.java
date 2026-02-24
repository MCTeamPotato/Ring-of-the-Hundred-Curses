package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.api.event.EatEvent;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModTag;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
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
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;
import static com.kaleblangley.ring_of_the_hundred_curses.init.ModEventKeys.*;

@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvent {

    @SubscribeEvent
    public static void entitySpawn(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof PathfinderMob mob && !(mob instanceof RangedAttackMob) && getConfig().enableWorldAgainst) {
                mob.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(mob, Player.class, getConfig().entityAttackChange, true, false, entity -> entity instanceof Player player && RingUtil.isEquipRing(player)));
                mob.goalSelector.addGoal(1, new MeleeAttackGoal(mob, getConfig().entityAttackSpeed, true));
            }
            if (livingEntity instanceof Monster monster && getConfig().enableHorrificEntity) {
                applyHorrificEntityHealthBoost(monster);
            }
        }
    }

    private static void applyHorrificEntityHealthBoost(Monster monster) {
        CompoundTag data = monster.getPersistentData();
        if (data.getBoolean(HORRIFIC_ENTITY_ROLLED_TAG)) {
            return;
        }
        data.putBoolean(HORRIFIC_ENTITY_ROLLED_TAG, true);
        float chance = Mth.clamp(getConfig().horrificEntityChance, 0.0f, 1.0f);
        if (monster.getRandom().nextFloat() >= chance) {
            return;
        }

        AttributeInstance maxHealthAttr = monster.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr == null) {
            return;
        }
        if (maxHealthAttr.getModifier(HORRIFIC_ENTITY_HEALTH_UUID) != null) {
            return;
        }
        float minBonus = Math.max(0.0f, getConfig().horrificEntityMinHealthBonus);
        float maxBonus = Math.max(minBonus, getConfig().horrificEntityMaxHealthBonus);
        float healthBonus = minBonus + monster.getRandom().nextFloat() * (maxBonus - minBonus);
        if (healthBonus <= 0.0f) {
            return;
        }
        maxHealthAttr.addPermanentModifier(new AttributeModifier(HORRIFIC_ENTITY_HEALTH_UUID, "Horrific Entity Health", healthBonus, AttributeModifier.Operation.MULTIPLY_TOTAL));
        monster.setHealth(monster.getMaxHealth());
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
    public static void onRebornWrath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.level().isClientSide) {
            return;
        }
        if (!RingUtil.configAndRing(player, getConfig().enableRebornWrath)) {
            return;
        }
        LivingEntity killer = resolveRebornWrathBoss(player, event);
        if (killer == null) {
            return;
        }
        empowerRebornWrathBoss(killer);
    }

    private static LivingEntity resolveRebornWrathBoss(Player player, LivingDeathEvent event) {
        Entity sourceEntity = event.getSource().getEntity();
        if (sourceEntity instanceof LivingEntity sourceLiving && isRebornWrathBoss(sourceLiving)) {
            return sourceLiving;
        }
        Entity directEntity = event.getSource().getDirectEntity();
        if (directEntity instanceof LivingEntity directLiving && isRebornWrathBoss(directLiving)) {
            return directLiving;
        }
        if (directEntity instanceof Projectile projectile
                && projectile.getOwner() instanceof LivingEntity owner
                && isRebornWrathBoss(owner)) {
            return owner;
        }
        LivingEntity lastHurtByMob = player.getLastHurtByMob();
        if (lastHurtByMob != null && isRebornWrathBoss(lastHurtByMob)) {
            return lastHurtByMob;
        }
        return null;
    }

    private static boolean isRebornWrathBoss(LivingEntity livingEntity) {
        EntityType<?> entityType = livingEntity.getType();
        if (entityType.is(Tags.EntityTypes.BOSSES)) {
            return true;
        }
        ResourceLocation entityId = EntityType.getKey(entityType);
        for (String target : getConfig().rebornWrathBossTargets) {
            if (target.startsWith("#")) {
                try {
                    TagKey<EntityType<?>> tagKey = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(target.substring(1)));
                    if (entityType.is(tagKey)) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            } else if (target.equals(entityId.toString())) {
                return true;
            }
        }
        return false;
    }

    private static void empowerRebornWrathBoss(LivingEntity boss) {
        CompoundTag data = boss.getPersistentData();
        int maxStacks = Math.max(1, getConfig().rebornWrathMaxStacks);
        int stacks = Math.min(data.getInt(REBORN_WRATH_STACKS_KEY) + 1, maxStacks);
        data.putInt(REBORN_WRATH_STACKS_KEY, stacks);
        float healthBonusPerDeath = Math.max(0.0f, getConfig().rebornWrathHealthBonusPerDeath);
        float attackBonusPerDeath = Math.max(0.0f, getConfig().rebornWrathAttackBonusPerDeath);
        double totalHealthBonus = healthBonusPerDeath * stacks;
        double totalAttackBonus = attackBonusPerDeath * stacks;
        AttributeInstance maxHealth = boss.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.removeModifier(REBORN_WRATH_HEALTH_UUID);
            if (totalHealthBonus > 0.0d) {
                maxHealth.addPermanentModifier(new AttributeModifier(REBORN_WRATH_HEALTH_UUID, "Reborn Wrath Health", totalHealthBonus, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }
        AttributeInstance attackDamage = boss.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.removeModifier(REBORN_WRATH_ATTACK_UUID);
            if (totalAttackBonus > 0.0d) {
                attackDamage.addPermanentModifier(new AttributeModifier(REBORN_WRATH_ATTACK_UUID, "Reborn Wrath Attack", totalAttackBonus, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }

        boss.setHealth(boss.getMaxHealth());
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

            // 被刺高手：玩家潜行时，受到的伤害翻倍
            if (RingUtil.configAndRing(player, getConfig().enableExposedWeakness)) {
                if (player.isCrouching()) {
                    event.setAmount(event.getAmount() * getConfig().exposedWeaknessDamageMultiplier);
                }
            }
        }

        // 新鲜武器：武器耐久越低伤害越低
        if (event.getSource().getEntity() instanceof Player player) {
            if (RingUtil.configAndRing(player, getConfig().enableFreshWeapon)) {
                ItemStack weapon = player.getMainHandItem();
                if (weapon.isDamageableItem()) {
                    int maxDamage = weapon.getMaxDamage();
                    int currentDamage = weapon.getDamageValue();
                    float durabilityRatio = (float) (maxDamage - currentDamage) / maxDamage;
                    event.setAmount(event.getAmount() * durabilityRatio);
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
            if (level.getBlockState(groundPos).isCollisionShapeFullBlock(level, groundPos) && !level.getBlockState(spawnPos).isCollisionShapeFullBlock(level, spawnPos)) {
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

    // 龙阳之好：末影龙释放龙息时，玩家脚下一定会出现龙息
    @SubscribeEvent
    public static void onDraconicFavor(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof AreaEffectCloud cloud)) return;
        if (!(cloud.getOwner() instanceof EnderDragon)) return;
        if (cloud.getParticle() != ParticleTypes.DRAGON_BREATH) return;
        for (Player player : event.getLevel().players()) {
            if (!RingUtil.configAndRing(player, getConfig().enableDraconicFavor)) continue;
            if (player.distanceTo(cloud) > 128) continue;
            AreaEffectCloud playerCloud = new AreaEffectCloud(event.getLevel(), player.getX(), player.getY(), player.getZ());
            playerCloud.setOwner(player);
            playerCloud.setRadius(3.0F);
            playerCloud.setDuration(100);
            playerCloud.setParticle(ParticleTypes.DRAGON_BREATH);
            playerCloud.addEffect(new MobEffectInstance(MobEffects.HARM));
            event.getLevel().addFreshEntity(playerCloud);
        }
    }
}
