package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.api.event.EatEvent;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModSound;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModTag;
import com.kaleblangley.ring_of_the_hundred_curses.goal.WorldAgainstMeleeAttackGoal;
import com.kaleblangley.ring_of_the_hundred_curses.mirage.MirageServerManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Iterator;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;
import static com.kaleblangley.ring_of_the_hundred_curses.init.ModEventKeys.*;
import static com.kaleblangley.ring_of_the_hundred_curses.init.ModPlayerEventKeys.*;

@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvent {

    @SubscribeEvent
    public static void onMiragePlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer serverPlayer)) return;
        MirageServerManager.tick(serverPlayer);
    }

    @SubscribeEvent
    public static void onMiragePlayerRespawn(PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            MirageServerManager.clear(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onMiragePlayerChangedDimension(PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            MirageServerManager.clear(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onMiragePlayerLoggedOut(PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            MirageServerManager.forget(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void entitySpawn(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof PathfinderMob mob && !(mob instanceof RangedAttackMob) && getConfig().enableWorldAgainst) {
                mob.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(mob, Player.class, getConfig().entityAttackChange, true, false, entity -> entity instanceof Player player && RingUtil.isEquipRing(player)));
                mob.goalSelector.addGoal(1, new WorldAgainstMeleeAttackGoal(mob, getConfig().entityAttackSpeed, true));
            }
        }
    }

    @SubscribeEvent
    public static void onPhantomGiftPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide || !level.isNight()) return;
        if (!RingUtil.configAndRing(player, getConfig().enablePhantomGift)) return;

        long night = level.getDayTime() / 24000L;
        CompoundTag data = player.getPersistentData();
        if (data.contains(PHANTOM_GIFT_LAST_NIGHT_KEY, Tag.TAG_LONG)
                && data.getLong(PHANTOM_GIFT_LAST_NIGHT_KEY) == night) {
            return;
        }
        if (spawnPhantomGiftPhantom(player)) {
            data.putLong(PHANTOM_GIFT_LAST_NIGHT_KEY, night);
        }
    }

    private static boolean spawnPhantomGiftPhantom(Player player) {
        Level level = player.level();
        Phantom phantom = EntityType.PHANTOM.create(level);
        if (phantom == null) return false;

        double x = player.getX() + (level.random.nextDouble() - 0.5D) * 16.0D;
        double y = Math.min(player.getY() + 20.0D, level.getMaxBuildHeight() - 2.0D);
        double z = player.getZ() + (level.random.nextDouble() - 0.5D) * 16.0D;
        phantom.moveTo(x, y, z, level.random.nextFloat() * 360.0F, 0.0F);
        phantom.setTarget(player);
        phantom.setPersistenceRequired();
        return level.addFreshEntity(phantom);
    }

    @SubscribeEvent
    public static void onDeafeningPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide || player.isSpectator()
                || !RingUtil.configAndRing(player, getConfig().enableDeafening)) {
            return;
        }

        int interval = Math.max(1, getConfig().deafeningCheckInterval);
        if (player.tickCount % interval != 0) return;
        if (getConfig().deafeningOnlyInDeepDark
                && !level.getBiome(player.blockPosition()).is(Biomes.DEEP_DARK)) {
            return;
        }
        if (player.getRandom().nextDouble()
                >= Mth.clamp(getConfig().deafeningSpawnChance, 0.0d, 1.0d)) {
            return;
        }

        double range = Math.max(4.0d, getConfig().deafeningSearchRange);
        if (!level.getEntitiesOfClass(Warden.class, player.getBoundingBox().inflate(range), LivingEntity::isAlive).isEmpty()) {
            return;
        }

        for (int attempt = 0; attempt < 8; attempt++) {
            int x = Mth.floor(player.getX()) + player.getRandom().nextInt(17) - 8;
            int z = Mth.floor(player.getZ()) + player.getRandom().nextInt(17) - 8;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos spawnPos = new BlockPos(x, y, z);
            if (!level.getBlockState(spawnPos).isAir()
                    || !level.getBlockState(spawnPos.above()).isAir()
                    || !level.getBlockState(spawnPos.below()).isCollisionShapeFullBlock(level, spawnPos.below())) {
                continue;
            }

            Warden warden = EntityType.WARDEN.create(level);
            if (warden == null) return;
            warden.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                    level.getRandom().nextFloat() * 360.0F, 0.0F);
            warden.setTarget(player);
            warden.setPersistenceRequired();
            level.addFreshEntity(warden);
            return;
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
                    int remainingFireTicks = Math.max(0, fireDuration);
                    player.setRemainingFireTicks(Math.max(player.getRemainingFireTicks(), remainingFireTicks));
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
        LivingEntity attacker = findDamageAttacker(event.getSource());
        if (attacker instanceof Player player
                && RingUtil.configAndRing(player, getConfig().enableFreshWeapon)) {
            ItemStack weapon = player.getMainHandItem();
            if (weapon.isDamageableItem()) {
                int maxDamage = weapon.getMaxDamage();
                int currentDamage = weapon.getDamageValue();
                float durabilityRatio = (float) (maxDamage - currentDamage) / maxDamage;
                event.setAmount(event.getAmount() * Mth.clamp(durabilityRatio, 0.0F, 1.0F));
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
    public static void onWeakenedStrikes(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player) return;

        Player attacker = null;
        if (event.getSource().getEntity() instanceof Player player) {
            attacker = player;
        } else if (event.getSource().getDirectEntity() instanceof Projectile projectile
                && projectile.getOwner() instanceof Player player) {
            attacker = player;
        }
        if (attacker == null || !RingUtil.configAndRing(attacker, getConfig().enableWeakenedStrikes)) return;

        float multiplier = Math.max(0.0f, Math.min(1.0f, getConfig().weakenedStrikesDamageMultiplier));
        event.setAmount(event.getAmount() * multiplier);
    }

    @SubscribeEvent
    public static void onPatternedAssault(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player || event.getEntity().level().isClientSide) return;
        LivingEntity target = event.getEntity();

        LivingEntity attacker = findDamageAttacker(event.getSource());
        if (!(attacker instanceof Player player)
                || !RingUtil.configAndRing(player, getConfig().enablePatternedAssault)
                || !isPatternedAssaultTarget(target)) {
            return;
        }

        String method = getPatternedAssaultMethod(event.getSource());
        CompoundTag allMethods = player.getPersistentData().contains(PATTERNED_ASSAULT_METHODS_KEY, Tag.TAG_COMPOUND)
                ? player.getPersistentData().getCompound(PATTERNED_ASSAULT_METHODS_KEY) : new CompoundTag();
        String targetKey = target.getUUID().toString();
        CompoundTag previous = allMethods.getCompound(targetKey);
        long gameTime = player.level().getGameTime();
        long resetAfter = getConfig().patternedAssaultResetAfterTicks;
        boolean sameRecentMethod = previous.contains("Method", Tag.TAG_STRING)
                && previous.getString("Method").equals(method)
                && (resetAfter <= 0 || gameTime - previous.getLong("GameTime") <= resetAfter);
        if (sameRecentMethod) {
            event.setCanceled(true);
            return;
        }

        previous.putString("Method", method);
        previous.putLong("GameTime", gameTime);
        allMethods.put(targetKey, previous);
        player.getPersistentData().put(PATTERNED_ASSAULT_METHODS_KEY, allMethods);
    }

    private static boolean isPatternedAssaultTarget(LivingEntity target) {
        if (target.getType().is(Tags.EntityTypes.BOSSES)) return true;
        ResourceLocation targetId = EntityType.getKey(target.getType());
        String[] targets = getConfig().patternedAssaultTargets;
        if (targets == null) return false;
        for (String configuredTarget : targets) {
            if (configuredTarget == null || configuredTarget.isBlank()) continue;
            if (configuredTarget.startsWith("#")) {
                try {
                    TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE,
                            new ResourceLocation(configuredTarget.substring(1)));
                    if (target.getType().is(tag)) return true;
                } catch (Exception ignored) {
                }
            } else if (configuredTarget.equals(targetId.toString())) {
                return true;
            }
        }
        return false;
    }

    private static String getPatternedAssaultMethod(DamageSource source) {
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return "projectile";
        String messageId = source.getMsgId();
        if (messageId.contains("magic") || messageId.equals("dragonBreath")
                || messageId.equals("sonic_boom")) return "magic";
        if (source.is(DamageTypeTags.IS_FIRE)) return "fire";
        if (source.is(DamageTypeTags.IS_EXPLOSION)) return "explosion";
        if (source.is(DamageTypeTags.IS_FALL)) return "fall";
        return "melee";
    }

    @SubscribeEvent
    public static void onUnbalancedWeapon(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player) return;

        Player attacker = null;
        if (event.getSource().getEntity() instanceof Player player) {
            attacker = player;
        } else if (event.getSource().getDirectEntity() instanceof Projectile projectile
                && projectile.getOwner() instanceof Player player) {
            attacker = player;
        }
        if (attacker == null || attacker.getMainHandItem().isEmpty()
                || !RingUtil.configAndRing(attacker, getConfig().enableUnbalancedWeapon)) {
            return;
        }

        double chance = Math.max(0.0D, Math.min(1.0D, getConfig().unbalancedWeaponChance));
        if (attacker.getRandom().nextDouble() >= chance) return;
        float multiplier = Math.max(0.0F, Math.min(1.0F, getConfig().unbalancedWeaponDamageMultiplier));
        event.setAmount(event.getAmount() * multiplier);
    }

    @SubscribeEvent
    public static void onBloodAndFlesh(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) return;
        if (!RingUtil.configAndRing(player, getConfig().enableBloodAndFlesh)) return;

        float extraDamagePercent = Math.max(0.0f, getConfig().bloodAndFleshExtraDamagePercent);
        event.setAmount(event.getAmount() + player.getMaxHealth() * extraDamagePercent);
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || player.level().isClientSide) {
            return;
        }

        LivingEntity attacker = findDamageAttacker(event.getSource());
        if (attacker == null || attacker == player) return;

        if (RingUtil.configAndRing(player, getConfig().enableJustifiedCombat)) {
            recordJustifiedAttacker(player, attacker);
        }
        if (attacker instanceof Phantom phantom
                && RingUtil.configAndRing(player, getConfig().enablePhantomGift)) {
            spawnPhantomGiftMobs(player, phantom);
        }
    }

    @SubscribeEvent
    public static void onCurseOfMisfortuneDrops(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide || event.getDrops().isEmpty()) return;

        LivingEntity attacker = findDamageAttacker(event.getSource());
        if (!(attacker instanceof Player player)
                || !RingUtil.configAndRing(player, getConfig().enableCurseOfMisfortune)) {
            return;
        }

        int rolls = Math.max(1, getConfig().curseOfMisfortuneRolls);
        Iterator<ItemEntity> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            ItemEntity drop = iterator.next();
            ItemStack stack = drop.getItem();
            int originalCount = stack.getCount();
            if (originalCount <= 0) {
                iterator.remove();
                continue;
            }

            int worstCount = originalCount;
            for (int roll = 0; roll < rolls; roll++) {
                worstCount = Math.min(worstCount, player.getRandom().nextInt(originalCount + 1));
            }
            if (worstCount <= 0) {
                iterator.remove();
            } else {
                stack.setCount(worstCount);
            }
        }
    }

    private static LivingEntity findDamageAttacker(DamageSource source) {
        Entity sourceEntity = source.getEntity();
        if (sourceEntity instanceof LivingEntity livingEntity) return livingEntity;
        if (sourceEntity instanceof Projectile projectile
                && projectile.getOwner() instanceof LivingEntity owner) {
            return owner;
        }

        Entity directEntity = source.getDirectEntity();
        if (directEntity instanceof LivingEntity livingEntity) return livingEntity;
        if (directEntity instanceof Projectile projectile
                && projectile.getOwner() instanceof LivingEntity owner) {
            return owner;
        }
        return null;
    }

    private static void recordJustifiedAttacker(Player player, LivingEntity attacker) {
        CompoundTag data = player.getPersistentData();
        ListTag attackers = data.getList(JUSTIFIED_COMBAT_ATTACKERS_KEY, Tag.TAG_STRING);
        String attackerId = attacker.getUUID().toString();
        for (int i = 0; i < attackers.size(); i++) {
            if (attackerId.equals(attackers.getString(i))) return;
        }
        attackers.add(StringTag.valueOf(attackerId));
        data.put(JUSTIFIED_COMBAT_ATTACKERS_KEY, attackers);
    }

    private static boolean hasJustifiedAttacker(Player player, LivingEntity target) {
        ListTag attackers = player.getPersistentData()
                .getList(JUSTIFIED_COMBAT_ATTACKERS_KEY, Tag.TAG_STRING);
        String targetId = target.getUUID().toString();
        for (int i = 0; i < attackers.size(); i++) {
            if (targetId.equals(attackers.getString(i))) return true;
        }
        return false;
    }

    private static void spawnPhantomGiftMobs(Player player, Phantom phantom) {
        String[] mobIds = getConfig().phantomGiftAirdropMobs;
        int count = Math.max(0, getConfig().phantomGiftAirdropCount);
        if (mobIds == null || mobIds.length == 0 || count == 0) return;

        long gameTime = player.level().getGameTime();
        CompoundTag data = phantom.getPersistentData();
        int interval = Math.max(1, getConfig().phantomGiftAirdropInterval);
        if (data.contains(PHANTOM_GIFT_LAST_AIRDROP_KEY, Tag.TAG_LONG)
                && gameTime - data.getLong(PHANTOM_GIFT_LAST_AIRDROP_KEY) < interval) {
            return;
        }
        data.putLong(PHANTOM_GIFT_LAST_AIRDROP_KEY, gameTime);

        int height = Math.max(1, getConfig().phantomGiftAirdropHeight);
        Level level = player.level();
        for (int i = 0; i < count; i++) {
            String mobId = mobIds[level.random.nextInt(mobIds.length)];
            try {
                EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(mobId));
                if (entityType == null) continue;
                Entity entity = entityType.create(level);
                if (!(entity instanceof Monster monster)) continue;

                double x = player.getX() + (level.random.nextDouble() - 0.5D) * 4.0D;
                double y = Math.min(player.getY() + height, level.getMaxBuildHeight() - 1.0D);
                double z = player.getZ() + (level.random.nextDouble() - 0.5D) * 4.0D;
                monster.moveTo(x, y, z, level.random.nextFloat() * 360.0F, 0.0F);
                monster.setTarget(player);
                monster.setDeltaMovement(0.0D, -0.15D, 0.0D);
                level.addFreshEntity(monster);
            } catch (Exception exception) {
                RingOfTheHundredCurses.LOGGER.warn("Invalid mob in phantom gift config: {}", mobId);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        if (PlayerEvent.isFoodComaActive(player)) {
            event.setCanceled(true);
            return;
        }
        if (RingUtil.configAndRing(player, getConfig().enableFocusDisturbance)) {
            double chance = Math.max(0.0D, Math.min(1.0D, getConfig().focusDisturbanceChance));
            if (chance > 0.0D && player.getRandom().nextDouble() < chance) {
                event.setCanceled(true);
                return;
            }
        }
        if (!RingUtil.configAndRing(player, getConfig().enableJustifiedCombat)) return;
        if (event.getTarget() instanceof LivingEntity target) {
            if (!hasJustifiedAttacker(player, target)) {
                event.setCanceled(true);
            }
        }
    }

    // 结束时刻：末影龙存在时循环播放音乐，并逐渐提高音调
    @SubscribeEvent
    public static void onEndingMomentPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !getConfig().enableEndingMoment) return;

        Player player = event.player;
        Level level = player.level();
        if (!RingUtil.configAndRing(player, getConfig().enableEndingMoment)) return;

        double range = Math.max(1.0D, getConfig().endingMomentRange);
        boolean dragonNearby = !level.getEntitiesOfClass(
                EnderDragon.class,
                player.getBoundingBox().inflate(range),
                dragon -> dragon.isAlive() && player.distanceToSqr(dragon) <= range * range
        ).isEmpty();
        CompoundTag data = player.getPersistentData();
        if (!dragonNearby) {
            data.remove(ENDING_MOMENT_COUNT_KEY);
            data.remove(ENDING_MOMENT_LAST_PLAY_KEY);
            return;
        }

        long gameTime = level.getGameTime();
        long interval = Math.max(1L, getConfig().endingMomentLoopInterval);
        long lastPlay = data.getLong(ENDING_MOMENT_LAST_PLAY_KEY);
        if (lastPlay != 0L && gameTime - lastPlay < interval) return;

        int count = Math.max(0, data.getInt(ENDING_MOMENT_COUNT_KEY));
        float pitch = Mth.clamp(
                getConfig().endingMomentBasePitch + count * getConfig().endingMomentPitchIncrease,
                0.5F,
                Math.max(0.5F, getConfig().endingMomentMaxPitch)
        );
        float volume = Math.max(0.0F, getConfig().endingMomentVolume);
        if (level.isClientSide) {
            level.playLocalSound(
                    player.blockPosition(), ModSound.BETTER_REMIX.get(), SoundSource.MASTER,
                    volume, pitch, false
            );
        } else {
            int darknessDuration = Math.max(0, getConfig().endingMomentDarknessDuration);
            if (darknessDuration > 0) {
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, darknessDuration, 0, false, false, true));
            }
        }
        data.putLong(ENDING_MOMENT_LAST_PLAY_KEY, gameTime);
        data.putInt(ENDING_MOMENT_COUNT_KEY, count + 1);
    }

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
