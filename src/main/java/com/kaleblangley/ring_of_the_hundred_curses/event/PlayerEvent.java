package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.advancement.CurseAdvancementManager;
import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.api.event.ChunkThunderEvent;
import com.kaleblangley.ring_of_the_hundred_curses.api.event.EatEvent;
import com.kaleblangley.ring_of_the_hundred_curses.api.event.StepOnBlockEvent;
import com.kaleblangley.ring_of_the_hundred_curses.capability.CustomsClearanceProvider;
import com.kaleblangley.ring_of_the_hundred_curses.capability.ICustomsClearance;
import com.kaleblangley.ring_of_the_hundred_curses.item.CursedRing;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModDamageTypes;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModEffect;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.Containers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModBlock;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;
import static com.kaleblangley.ring_of_the_hundred_curses.init.ModEventKeys.*;
import static com.kaleblangley.ring_of_the_hundred_curses.init.ModPlayerEventKeys.*;

@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvent {

    private static final List<MobEffect> HARMFUL_EFFECTS;
    private static final List<UUID> POTION_CONFLICT_GUARD = new ArrayList<>();

    static {
        List<MobEffect> effects = new java.util.ArrayList<>();
        for (MobEffect effect : ForgeRegistries.MOB_EFFECTS) {
            if (effect.getCategory() == MobEffectCategory.HARMFUL && effect != MobEffects.HARM && effect != MobEffects.HEAL && effect != MobEffects.LEVITATION) {
                effects.add(effect);
            }
        }
        HARMFUL_EFFECTS = List.copyOf(effects);
    }

    // 均衡饮食：同一食物吃太多减少回复
    @SubscribeEvent
    public static void onEatFood(EatEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (RingUtil.configAndRing(player, getConfig().enableBalancedDiet)) {
                CompoundTag data = player.getPersistentData();
                CompoundTag dietCounts = data.contains(BALANCED_DIET_KEY, Tag.TAG_COMPOUND)
                        ? data.getCompound(BALANCED_DIET_KEY) : new CompoundTag();
                String foodId = ForgeRegistries.ITEMS.getKey(event.getItem()).toString();
                int eatCount = dietCounts.getInt(foodId);
                int threshold = getConfig().balancedDietThreshold;
                if (eatCount >= threshold) {
                    int extra = eatCount - threshold;
                    float reduction = Math.min(extra * getConfig().balancedDietReductionPerExtra, getConfig().balancedDietMaxReduction);
                    float ratio = 1.0f - reduction;
                    int originalNutrition = event.getNutrition();
                    float originalSaturation = event.getSaturationModifier();
                    int reducedNutrition = Math.max((int) (originalNutrition * ratio), 0);
                    float reducedSaturation = originalSaturation * ratio;
                    if (reducedNutrition != originalNutrition || reducedSaturation != originalSaturation) {
                        event.setNutrition(reducedNutrition);
                        event.setSaturationModifier(reducedSaturation);
                        CurseAdvancementManager.trigger(player, "balanced_diet");
                    }
                }
                dietCounts.putInt(foodId, eatCount + 1);
                data.put(BALANCED_DIET_KEY, dietCounts);
            }
            if (RingUtil.configAndRing(player, getConfig().enableHollowStomach)) {
                int maxHunger = getConfig().hollowStomachMaxHunger;
                FoodData foodData = player.getFoodData();
                int currentHunger = foodData.getFoodLevel();
                int nutritionToAdd = event.getNutrition();
                if (currentHunger >= maxHunger) {
                    if (nutritionToAdd != 0) {
                        event.setNutrition(0);
                        CurseAdvancementManager.trigger(player, "hollow_stomach");
                    }
                } else if (currentHunger + nutritionToAdd > maxHunger) {
                    int limitedNutrition = maxHunger - currentHunger;
                    if (limitedNutrition != nutritionToAdd) {
                        event.setNutrition(limitedNutrition);
                        CurseAdvancementManager.trigger(player, "hollow_stomach");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void curiosChange(CurioChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (RingUtil.isRing(event.getTo())) {
                player.getInventory().items.forEach(itemStack -> RingUtil.setCurseMaxSizeCapability(itemStack, getConfig().maxStackSize));
            } else if (RingUtil.isRing(event.getFrom())) {
                player.getInventory().items.forEach(itemStack -> RingUtil.setCurseMaxSizeCapability(itemStack, 0));
            }
        }
    }

    @SubscribeEvent
    public static void sleepEvent(SleepingTimeCheckEvent event) {
        if (RingUtil.configAndRing(event.getEntity(), getConfig().enableSleeplessNights)) {
            event.setResult(Event.Result.DENY);
            CurseAdvancementManager.trigger(event.getEntity(), "sleepless_nights");
        }
    }

    @SubscribeEvent
    public static void stackItem(ItemStackedOnOtherEvent event) {
        ItemStack carryItem = event.getCarriedItem();
        ItemStack stackedOnItem = event.getStackedOnItem();
        RingUtil.backpackLimitSizeModify(event.getPlayer(), carryItem);
        if (stackedOnItem.is(Items.SHIELD) && event.getSlot().getContainerSlot() == 40 && RingUtil.configAndRing(event.getPlayer(), getConfig().enableShieldOnTheRight)) {
            event.setCanceled(true);
            CurseAdvancementManager.trigger(event.getPlayer(), "shield_on_the_right");
        }
    }

    @SubscribeEvent
    public static void pickUpItem(ItemPickupEvent event) {
        RingUtil.backpackLimitSizeModify(event.getEntity(), event.getStack());
    }

    @SubscribeEvent
    public static void breakSpeed(BreakSpeed event) {
        Player player = event.getEntity();
        float originalSpeed = event.getOriginalSpeed();
        BlockState state = event.getState();
        ItemStack handItem = player.getMainHandItem();
        event.setNewSpeed(breakSpeedGet(player, originalSpeed, state, handItem));
    }

    @SubscribeEvent
    public static void neurologicalDegenerationEffect(PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        if (RingUtil.configAndRing(player, getConfig().enableNeurologicalDegeneration)) {
            if (applyRandomHarmfulEffect(player)) {
                CurseAdvancementManager.trigger(player, "neurological_degeneration");
            }
        }
        if (RingUtil.configAndRing(player, getConfig().enableFleshCollapse)) {
            scheduleFleshCollapseApply(player);
        }
    }

    @SubscribeEvent
    public static void playerBreakEvent(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player != null && !player.level().isClientSide && isFoodComaActive(player)) {
            event.setCanceled(true);
            return;
        }
        if (player != null && !player.level().isClientSide && focusDisturbanceFails(player)) {
            event.setCanceled(true);
            return;
        }
        if (player != null && RingUtil.configAndRing(player, getConfig().enableWormHoard)) {
            BlockState state = event.getState();
            Level level = player.level();
            if (!level.isClientSide && isWormHoardTarget(state.getBlock())) {
                if (level.random.nextDouble() < getConfig().wormHoardSpawnChance) {
                    if (spawnSilverfishAtPosition(event.getPos(), level, player)) {
                        CurseAdvancementManager.trigger(player, "worm_hoard");
                    }
                }
            }
        }
        if (player != null && RingUtil.configAndRing(player, getConfig().enableBarrenHarvest)) {
            BlockState state = event.getState();
            Level level = player.level();
            if (!level.isClientSide && state.getBlock() instanceof CropBlock cropBlock) {
                if (cropBlock.isMaxAge(state)) {
                    if (level.random.nextDouble() < getConfig().barrenHarvestChance) {
                        event.setCanceled(true);
                        level.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), 3);
                        ItemStack deadBush = new ItemStack(Items.DEAD_BUSH, 1);
                        Containers.dropItemStack(level, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), deadBush);
                        CurseAdvancementManager.trigger(player, "barren_harvest");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDepthCharge(ItemFishedEvent event) {
        Player player = event.getEntity();
        if (!RingUtil.configAndRing(player, getConfig().enableDepthCharge)) {
            return;
        }
        Level level = player.level();
        if (level.isClientSide) {
            return;
        }
        FishingHook hook = event.getHookEntity();
        Vec3 hookPos = hook.position();
        if (level.random.nextDouble() < getConfig().depthChargeTntChance) {
            if (spawnTntAtPosition(hookPos, level, player)) {
                CurseAdvancementManager.trigger(player, "depth_charge");
            }
            return;
        }
        if (level.random.nextDouble() < getConfig().depthChargeMobChance) {
            if (spawnHostileMobAtPosition(hookPos, level, player)) {
                CurseAdvancementManager.trigger(player, "depth_charge");
            }
        }
    }

    public static ItemStack maybeSwapMerchantResult(Player player, ItemStack originalResult) {
        if (player.level().isClientSide
                || originalResult.isEmpty()
                || !RingUtil.configAndRing(player, getConfig().enableDodgyMerchant)
                || !canBeSwappedItem(originalResult)) {
            return originalResult;
        }

        double swapChance = Mth.clamp(getConfig().dodgyMerchantSwapChance, 0.0D, 1.0D);
        if (player.getRandom().nextDouble() >= swapChance) return originalResult;

        ItemStack randomItem = getDodgyMerchantRandomItem(player);
        if (randomItem.isEmpty()) return originalResult;
        randomItem.setCount(Math.min(originalResult.getCount(), randomItem.getMaxStackSize()));
        if (!ItemStack.isSameItemSameTags(originalResult, randomItem) || originalResult.getCount() != randomItem.getCount()) {
            CurseAdvancementManager.trigger(player, "dodgy_merchant");
        }
        return randomItem;
    }

    public static void applyMerchantTradePrices(Player player, AbstractVillager merchant) {
        if (player == null || player.level().isClientSide || merchant == null) return;

        CompoundTag appliedPrices = merchant.getPersistentData().getCompound("RingCursesAppliedTradePrices");
        int offerIndex = 0;
        for (MerchantOffer offer : merchant.getOffers()) {
            String offerKey = Integer.toString(offerIndex++);
            int previousCursePrice = appliedPrices.getInt(offerKey);
            if (previousCursePrice != 0) {
                offer.setSpecialPriceDiff(offer.getSpecialPriceDiff() - previousCursePrice);
            }

            int extraCost = 0;
            int socialCost = 0;
            int bargainingCost = 0;
            ItemStack baseCost = offer.getBaseCostA();
            if (!baseCost.isEmpty() && RingUtil.configAndRing(player, getConfig().enableSocialParadox)) {
                float ratio = Math.max(0.0f, getConfig().socialParadoxPriceIncreaseRatio);
                socialCost = Math.max(0, (int) Math.ceil(baseCost.getCount() * ratio));
                extraCost += socialCost;
            }
            if (!baseCost.isEmpty() && RingUtil.configAndRing(player, getConfig().enableBargainingPower)) {
                double healthRatio = merchant.getMaxHealth() <= 0.0f
                        ? 0.0d : Mth.clamp(merchant.getHealth() / merchant.getMaxHealth(), 0.0f, 1.0f);
                double multiplier = Math.max(0.0d, getConfig().bargainingPowerPriceMultiplier);
                int maxExtraCost = Math.max(0, getConfig().bargainingPowerMaxExtraCost);
                int healthCost = (int) Math.ceil(baseCost.getCount() * healthRatio * multiplier);
                bargainingCost = Math.min(healthCost, maxExtraCost);
                extraCost += bargainingCost;
            }
            if (extraCost > 0) {
                offer.addToSpecialPriceDiff(extraCost);
                if (socialCost > 0) {
                    CurseAdvancementManager.trigger(player, "social_paradox");
                }
                if (bargainingCost > 0) {
                    CurseAdvancementManager.trigger(player, "bargaining_power");
                }
            }
            appliedPrices.putInt(offerKey, extraCost);
        }
        merchant.getPersistentData().put("RingCursesAppliedTradePrices", appliedPrices);
    }

    public static boolean shouldInterceptCustomsClearance(Player player, AbstractVillager merchant) {
        return player != null
                && !player.level().isClientSide
                && merchant instanceof Villager villager
                && RingUtil.configAndRing(player, getConfig().enableCustomsClearance)
                && villager.getVillagerData().getLevel() >= getConfig().customsClearanceMinLevel
                && player.getCapability(CustomsClearanceProvider.CUSTOMS_CLEARANCE).isPresent();
    }

    public static void handleMerchantResultTaken(Player player, AbstractVillager merchant, ItemStack result) {
        if (player.level().isClientSide || result.isEmpty()
                || !shouldInterceptCustomsClearance(player, merchant)) {
            return;
        }

        ICustomsClearance customs = player.getCapability(CustomsClearanceProvider.CUSTOMS_CLEARANCE).orElse(null);
        if (customs == null) return;

        long deliveryTime = player.level().getDayTime()
                + (long) Math.max(0, getConfig().customsClearanceWaitDays) * 24000L;
        customs.addPendingItem(result, deliveryTime);
        if (player.containerMenu.getCarried() == result) {
            player.containerMenu.setCarried(ItemStack.EMPTY);
        }
        result.setCount(0);
        player.containerMenu.broadcastChanges();
        int waitDays = Math.max(0, getConfig().customsClearanceWaitDays);
        player.displayClientMessage(
                Component.translatable("message.ring_of_the_hundred_curses.customs_clearance.held", waitDays)
                        .withStyle(ChatFormatting.YELLOW),
                true
        );
        CurseAdvancementManager.trigger(player, "customs_clearance");
    }

    private static float breakSpeedGet(Player player, float originalSpeed, BlockState state, ItemStack handItem) {
        if (!RingUtil.isEquipRing(player)) return originalSpeed;

        float modifiedSpeed = originalSpeed;
        if (getConfig().enableSluggishHands) {
            modifiedSpeed = originalSpeed * getConfig().multiplyRawSpeed;
            if (modifiedSpeed != originalSpeed) {
                CurseAdvancementManager.trigger(player, "sluggish_hands");
            }
        }

        boolean alwaysDig = state.is(ModTag.ALWAYS_DIG);
        boolean axeDig = state.is(ModTag.AXE_DIG);
        boolean hoeDig = state.is(ModTag.HOE_DIG);
        boolean pickaxeDig = state.is(ModTag.PICKAXE_DIG);
        boolean shovelDig = state.is(ModTag.SHOVEL_DIG);
        boolean requiresTool = axeDig || hoeDig || pickaxeDig || shovelDig;

        // ALWAYS_DIG contains soft blocks such as dirt, sand and logs. They must remain
        // breakable by hand, even when the tool-specific tags also contain the block.
        if (alwaysDig && handItem.isEmpty()) {
            return modifiedSpeed;
        }

        if (getConfig().enableWeaponless && requiresTool && handItem.isEmpty()) {
            if (modifiedSpeed != 0.0F) {
                CurseAdvancementManager.trigger(player, "weaponless");
            }
            return 0.0F;
        }
        if (!getConfig().enableSinglePurposeTools || !requiresTool) return modifiedSpeed;

        boolean matchingTool = (axeDig && handItem.is(ItemTags.AXES))
                || (hoeDig && handItem.is(ItemTags.HOES))
                || (pickaxeDig && handItem.is(ItemTags.PICKAXES))
                || (shovelDig && handItem.is(ItemTags.SHOVELS));
        if (matchingTool) return modifiedSpeed;

        if (modifiedSpeed != 0.0F) {
            CurseAdvancementManager.trigger(player, "single_purpose_tools");
        }
        return 0.0F;
    }

    private static boolean applyRandomHarmfulEffect(Player player) {
        if (HARMFUL_EFFECTS.isEmpty()) {
            return false;
        }
        MobEffect randomEffect = HARMFUL_EFFECTS.get(player.level().random.nextInt(HARMFUL_EFFECTS.size()));
        int minAmplifier = getConfig().neurologicalDegenerationMinAmplifier;
        int maxAmplifier = getConfig().neurologicalDegenerationMaxAmplifier;
        int minDuration = getConfig().neurologicalDegenerationMinDuration;
        int maxDuration = getConfig().neurologicalDegenerationMaxDuration;
        if (minAmplifier > maxAmplifier) {
            minAmplifier = maxAmplifier;
        }
        if (minDuration > maxDuration) {
            minDuration = maxDuration;
        }
        int amplifier = minAmplifier + player.level().random.nextInt(Math.max(1, maxAmplifier - minAmplifier + 1));
        int duration = minDuration + player.level().random.nextInt(Math.max(1, maxDuration - minDuration + 1));
        MobEffectInstance effectInstance = new MobEffectInstance(randomEffect, duration, amplifier);
        return player.addEffect(effectInstance);
    }

    private static boolean applyFleshCollapse(Player player) {
        float maxHealth = getFleshCollapseReferenceMaxHealth(player);
        float healthPercent = Mth.clamp(getConfig().fleshCollapseHealthPercent, 0.0f, 1.0f);
        float targetHealth = Mth.clamp(maxHealth * healthPercent, 1.0f, maxHealth);
        boolean changed = player.getHealth() != targetHealth;
        player.setHealth(targetHealth);
        int maxHunger = RingUtil.configAndRing(player, getConfig().enableHollowStomach)
                ? Mth.clamp(getConfig().hollowStomachMaxHunger, 1, 20)
                : 20;
        float hungerPercent = Mth.clamp(getConfig().fleshCollapseHungerPercent, 0.0f, 1.0f);
        int targetHunger = Mth.clamp(Math.round(maxHunger * hungerPercent), 0, maxHunger);
        FoodData foodData = player.getFoodData();
        changed |= foodData.getFoodLevel() != targetHunger;
        foodData.setFoodLevel(targetHunger);
        float targetSaturation = Math.min(foodData.getSaturationLevel(), targetHunger);
        changed |= foodData.getSaturationLevel() != targetSaturation;
        foodData.setSaturation(targetSaturation);
        return changed;
    }

    private static float getFleshCollapseReferenceMaxHealth(Player player) {
        float currentMaxHealth = player.getMaxHealth();
        if (!RingUtil.configAndRing(player, getConfig().enableFragileLife)) {
            return currentMaxHealth;
        }
        float fragileLifeMax = Math.max(1.0f, 20.0f - getConfig().reducesHealth);
        return Math.min(currentMaxHealth, fragileLifeMax);
    }

    private static void scheduleFleshCollapseApply(Player player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        int runTick = server.getTickCount() + 10;
        server.tell(new TickTask(runTick, () -> {
            if (player.isRemoved() || player.level().isClientSide) {
                return;
            }
            if (!RingUtil.configAndRing(player, getConfig().enableFleshCollapse)) {
                return;
            }
            if (applyFleshCollapse(player)) {
                CurseAdvancementManager.trigger(player, "flesh_collapse");
            }
        }));
    }

    private static boolean isWormHoardTarget(Block block) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
        String blockIdString = blockId.toString();
        for (String target : getConfig().wormHoardTargetBlocks) {
            if (target.startsWith("#")) {
                String tagName = target.substring(1);
                try {
                    ResourceLocation tagLocation = new ResourceLocation(tagName);
                    TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, tagLocation);
                    if (block.defaultBlockState().is(tagKey)) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            } else if (target.equals(blockIdString)) {
                return true;
            }
        }
        return false;
    }

    private static boolean spawnSilverfishAtPosition(BlockPos blockPos, Level level, Player player) {
        int maxSilverfish = getConfig().wormHoardMaxSilverfish;
        if (maxSilverfish <= 0) return false;
        int numToSpawn = 1 + level.random.nextInt(maxSilverfish);
        boolean spawned = false;
        for (int i = 0; i < numToSpawn; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 2.0;
            double offsetZ = (level.random.nextDouble() - 0.5) * 2.0;
            double spawnX = blockPos.getX() + 0.5 + offsetX;
            double spawnY = blockPos.getY();
            double spawnZ = blockPos.getZ() + 0.5 + offsetZ;
            for (int y = 0; y < 3; y++) {
                double testY = spawnY + y;
                BlockPos testPos = new BlockPos((int) spawnX, (int) testY, (int) spawnZ);
                if (!level.getBlockState(testPos).isCollisionShapeFullBlock(level, testPos) && !level.getBlockState(testPos.above()).isCollisionShapeFullBlock(level, testPos.above())) {
                    spawnY = testY;
                    break;
                }
            }
            Silverfish silverfish = new Silverfish(EntityType.SILVERFISH, level);
            silverfish.setPos(spawnX, spawnY, spawnZ);
            silverfish.setTarget(player);
            spawned |= level.addFreshEntity(silverfish);
        }
        return spawned;
    }

    private static boolean canBeSwappedItem(ItemStack stack) {
        return !(stack.getItem() instanceof CursedRing) && !stack.is(Items.NETHERITE_INGOT) && !stack.is(Items.DIAMOND) && !stack.is(Items.EMERALD);
    }

    private static ItemStack getDodgyMerchantRandomItem(Player player) {
        Level level = player.level();
        String[] junkItemStrings = getConfig().dodgyMerchantJunkItems;
        List<Item> junkItems = new ArrayList<>();
        for (String itemString : junkItemStrings) {
            try {
                ResourceLocation itemLocation = new ResourceLocation(itemString);
                Item item = ForgeRegistries.ITEMS.getValue(itemLocation);
                if (item != null && item != Items.AIR) {
                    junkItems.add(item);
                }
            } catch (Exception e) {
                RingOfTheHundredCurses.LOGGER.warn("Invalid item in dodgy merchant config: {}", itemString);
            }
        }
        if (junkItems.isEmpty()) return ItemStack.EMPTY;
        Item randomItem = junkItems.get(level.random.nextInt(junkItems.size()));
        int count = 1 + level.random.nextInt(3);
        return new ItemStack(randomItem, count);
    }

    private static boolean spawnTntAtPosition(Vec3 position, Level level, Player player) {
        PrimedTnt tnt = new PrimedTnt(level, position.x, position.y, position.z, player);
        tnt.setFuse(40);
        boolean spawned = level.addFreshEntity(tnt);
        Vec3 playerPos = player.position().add(0, 1, 0);
        Vec3 direction = playerPos.subtract(position).normalize();
        double speed = 0.8;
        tnt.setDeltaMovement(direction.x * speed, Math.max(0.3, direction.y * speed + 0.2), direction.z * speed);
        return spawned;
    }

    private static boolean spawnHostileMobAtPosition(Vec3 position, Level level, Player player) {
        String[] mobStrings = getConfig().depthChargeHostileMobs;
        if (mobStrings.length == 0) return false;
        String randomMobId = mobStrings[level.random.nextInt(mobStrings.length)];
        try {
            ResourceLocation mobLocation = new ResourceLocation(randomMobId);
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(mobLocation);
            if (entityType != null) {
                LivingEntity mob = (LivingEntity) entityType.create(level);
                if (mob instanceof Monster monster) {
                    monster.setPos(position.x, position.y, position.z);
                    monster.setTarget(player);
                    boolean spawned = level.addFreshEntity(monster);
                    Vec3 playerPos = player.position().add(0, 1, 0);
                    Vec3 direction = playerPos.subtract(position).normalize();
                    double speed = 0.6;
                    monster.setDeltaMovement(direction.x * speed, Math.max(0.2, direction.y * speed + 0.15), direction.z * speed);
                    monster.invulnerableTime = 20;
                    return spawned;
                }
            }
        } catch (Exception e) {
            RingOfTheHundredCurses.LOGGER.warn("Invalid mob in depth charge config: {}", randomMobId);
        }
        return false;
    }

    public static ItemStack maybeConvertTerribleCook(Player player, ItemStack cookedMeal) {
        if (player.level().isClientSide
                || !RingUtil.configAndRing(player, getConfig().enableTerribleCook)
                || cookedMeal.isEmpty()
                || !cookedMeal.isEdible()
                || cookedMeal.is(ModTag.RAW_FOOD)
                || cookedMeal.is(Items.SUSPICIOUS_STEW)) {
            return cookedMeal;
        }
        double chance = Mth.clamp(getConfig().terribleCookChance, 0.0d, 1.0d);
        if (player.getRandom().nextDouble() >= chance) return cookedMeal;

        MobEffect debuff = chooseTerribleCookDebuff(player);
        int duration = Math.max(1, getConfig().terribleCookDebuffDuration);
        ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);
        SuspiciousStewItem.saveMobEffect(stew, debuff, duration);
        CurseAdvancementManager.trigger(player, "terrible_cook");
        return stew;
    }

    public static ItemStack maybeConvertTerribleCook(Level level, BlockPos pos, ItemStack cookedMeal) {
        if (level == null || level.isClientSide || pos == null || cookedMeal.isEmpty()) return cookedMeal;

        double range = Math.max(0.0D, getConfig().terribleCookPlayerRange);
        if (range <= 0.0D) return cookedMeal;

        Player player = level.getNearestPlayer(
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                range,
                entity -> entity instanceof Player candidate
                        && RingUtil.configAndRing(candidate, getConfig().enableTerribleCook)
        );
        return player == null ? cookedMeal : maybeConvertTerribleCook(player, cookedMeal);
    }

    private static MobEffect chooseTerribleCookDebuff(Player player) {
        List<MobEffect> candidates = new ArrayList<>();
        String[] configuredIds = getConfig().terribleCookDebuffIds;
        if (configuredIds != null) {
            for (String configuredId : configuredIds) {
                MobEffect effect = resolveMobEffect(configuredId);
                if (effect != null && !candidates.contains(effect)) {
                    candidates.add(effect);
                }
            }
        }
        if (candidates.isEmpty()) return MobEffects.CONFUSION;
        return candidates.get(player.getRandom().nextInt(candidates.size()));
    }

    private static MobEffect resolveMobEffect(String effectId) {
        if (effectId == null || effectId.isBlank()) return null;
        try {
            return ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effectId));
        } catch (Exception ignored) {
            return null;
        }
    }

    @SubscribeEvent
    public static void onRegenerationBanHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || player.level().isClientSide
                || !RingUtil.configAndRing(player, getConfig().enableRegenerationBan)) {
            return;
        }

        float minimumAmount = Math.max(0.0F, getConfig().regenerationBanMinimumAmount);
        if (event.getAmount() < minimumAmount || player.getHealth() >= player.getMaxHealth()) return;

        CompoundTag data = player.getPersistentData();
        float pending = Math.max(0.0F, data.getFloat(REGENERATION_BAN_PENDING_KEY));
        data.putFloat(REGENERATION_BAN_PENDING_KEY, pending + Math.max(0.0F, event.getAmount()));
        long delay = Math.max(0L, getConfig().regenerationBanDelay);
        long due = Math.max(data.getLong(REGENERATION_BAN_DUE_KEY), player.level().getGameTime() + delay);
        data.putLong(REGENERATION_BAN_DUE_KEY, due);
        event.setCanceled(true);
        CurseAdvancementManager.trigger(player, "regeneration_ban");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onOverhealingHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) return;
        CompoundTag data = player.getPersistentData();
        if (!RingUtil.configAndRing(player, getConfig().enableOverhealing)) {
            data.remove(OVERHEALING_END_KEY);
            return;
        }

        long gameTime = player.level().getGameTime();
        long cooldownEnd = data.getLong(OVERHEALING_END_KEY);
        if (cooldownEnd > gameTime) {
            event.setCanceled(true);
            return;
        }
        if (event.getAmount() < Math.max(0.0F, getConfig().overhealingMinimumAmount)) return;

        double chance = Mth.clamp(getConfig().overhealingChance, 0.0D, 1.0D);
        if (chance > 0.0D && player.getRandom().nextDouble() < chance) {
            data.putLong(OVERHEALING_END_KEY, gameTime + Math.max(0L, getConfig().overhealingCooldown));
            CurseAdvancementManager.trigger(player, "overhealing");
        }
    }

    @SubscribeEvent
    public static void onPotionConflicts(MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof Player player)
                || player.level().isClientSide
                || !RingUtil.configAndRing(player, getConfig().enablePotionConflicts)) {
            return;
        }

        MobEffectInstance addedEffect = event.getEffectInstance();
        if (addedEffect.getEffect().getCategory() != MobEffectCategory.BENEFICIAL) return;

        float beneficialMultiplier = Math.max(0.0F, getConfig().potionConflictsBeneficialDurationMultiplier);
        float harmfulMultiplier = Math.max(0.0F, getConfig().potionConflictsHarmfulDurationMultiplier);
        if (POTION_CONFLICT_GUARD.contains(player.getUUID())) return;

        List<MobEffectInstance> replacements = new ArrayList<>();
        for (MobEffectInstance existingEffect : player.getActiveEffects()) {
            if (existingEffect == addedEffect
                    || existingEffect.getEffect() == addedEffect.getEffect()
                    || existingEffect.getDuration() < 0) continue;
            boolean beneficial = existingEffect.getEffect().getCategory() == MobEffectCategory.BENEFICIAL;
            float multiplier = beneficial ? beneficialMultiplier : harmfulMultiplier;
            int duration = Math.max(1, Math.round(existingEffect.getDuration() * multiplier));
            replacements.add(new MobEffectInstance(
                    existingEffect.getEffect(),
                    duration,
                    existingEffect.getAmplifier(),
                    existingEffect.isAmbient(),
                    existingEffect.isVisible(),
                    existingEffect.showIcon()
            ));
        }

        if (replacements.isEmpty()) return;
        POTION_CONFLICT_GUARD.add(player.getUUID());
        try {
            for (MobEffectInstance replacement : replacements) {
                player.removeEffect(replacement.getEffect());
                player.addEffect(replacement);
            }
            CurseAdvancementManager.trigger(player, "potion_conflicts");
        } finally {
            POTION_CONFLICT_GUARD.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onDistantDeflectionHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || player.level().isClientSide
                || !RingUtil.configAndRing(player, getConfig().enableDistantDeflection)) {
            return;
        }

        float amount = Math.max(0.0F, event.getAmount());
        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)) {
            float rangedMultiplier = Mth.clamp(getConfig().distantDeflectionRangedDamageMultiplier, 0.0F, 1.0F);
            amount *= rangedMultiplier;
        }
        float maximumDamage = Math.max(0.0F, getConfig().distantDeflectionMaxDamage);
        float modifiedAmount = Math.min(amount, maximumDamage);
        if (modifiedAmount != event.getAmount()) {
            event.setAmount(modifiedAmount);
            CurseAdvancementManager.trigger(player, "distant_deflection");
        }
    }

    @SubscribeEvent
    public static void onFoodComaFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)
                || player.level().isClientSide
                || !event.getItem().isEdible()
                || !RingUtil.configAndRing(player, getConfig().enableFoodComa)) {
            return;
        }

        long duration = Math.max(0L, getConfig().foodComaDuration);
        if (duration == 0L) return;
        CompoundTag data = player.getPersistentData();
        long end = Math.max(data.getLong(FOOD_COMA_END_KEY), player.level().getGameTime() + duration);
        data.putLong(FOOD_COMA_END_KEY, end);
        CurseAdvancementManager.trigger(player, "food_coma");
    }

    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        Player player = event.getEntity();
        if (RingUtil.configAndRing(player, getConfig().enableSoulSuppression)) {
            int reduced = (int) (event.getAmount() * (1.0f - getConfig().xpReductionPercent));
            int modifiedAmount = Math.max(reduced, 0);
            if (modifiedAmount != event.getAmount()) {
                event.setAmount(modifiedAmount);
                CurseAdvancementManager.trigger(player, "soul_suppression");
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide) return;
        long gameTime = level.getGameTime();
        updateSimpleEquipmentCurses(player);
        enforceWeakMagicConstitution(player);
        updateFeastOrFamine(player);
        tickRegenerationBan(player, gameTime);
        tickFoodComa(player, gameTime);
        if (RingUtil.configAndRing(player, getConfig().enableHypocrisyBody)) {
            int max = getConfig().fragileBodyMaxInvulnerableTime;
            if (player.invulnerableTime > max) {
                player.invulnerableTime = max;
                CurseAdvancementManager.trigger(player, "hypocrisy_body");
            }
        }
        if (RingUtil.configAndRing(player, getConfig().enableRottingHunger)) {
            int expireTime = getConfig().rottingHungerExpireTime;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.isEmpty() || !stack.isEdible() || stack.is(Items.ROTTEN_FLESH)) continue;
                CompoundTag tag = stack.getOrCreateTag();
                if (!tag.contains("RotHungerTick")) {
                    tag.putLong("RotHungerTick", gameTime);
                    continue;
                }
                long startTime = tag.getLong("RotHungerTick");
                if (gameTime - startTime >= expireTime) {
                    int count = stack.getCount();
                    player.getInventory().setItem(i, new ItemStack(Items.ROTTEN_FLESH, count));
                    CurseAdvancementManager.trigger(player, "rotting_hunger");
                }
            }
        }
        if (gameTime % 20 == 0) {
            checkCustomsClearanceDelivery(player, level.getDayTime());
        }
    }

    private static void tickRegenerationBan(Player player, long gameTime) {
        CompoundTag data = player.getPersistentData();
        if (!RingUtil.configAndRing(player, getConfig().enableRegenerationBan)) {
            data.remove(REGENERATION_BAN_PENDING_KEY);
            data.remove(REGENERATION_BAN_DUE_KEY);
            return;
        }

        float pending = data.getFloat(REGENERATION_BAN_PENDING_KEY);
        if (pending <= 0.0F) {
            data.remove(REGENERATION_BAN_PENDING_KEY);
            data.remove(REGENERATION_BAN_DUE_KEY);
            return;
        }
        if (gameTime < data.getLong(REGENERATION_BAN_DUE_KEY)) return;

        if (player.isAlive()) {
            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + pending));
        }
        data.remove(REGENERATION_BAN_PENDING_KEY);
        data.remove(REGENERATION_BAN_DUE_KEY);
    }

    private static void tickFoodComa(Player player, long gameTime) {
        CompoundTag data = player.getPersistentData();
        if (!RingUtil.configAndRing(player, getConfig().enableFoodComa)) {
            data.remove(FOOD_COMA_END_KEY);
            return;
        }

        long end = data.getLong(FOOD_COMA_END_KEY);
        if (end <= gameTime) {
            data.remove(FOOD_COMA_END_KEY);
            return;
        }

        player.setSprinting(false);
        Vec3 motion = player.getDeltaMovement();
        if (motion.x != 0.0D || motion.z != 0.0D) {
            player.setDeltaMovement(0.0D, motion.y, 0.0D);
        }
    }

    public static boolean isFoodComaActive(Player player) {
        return RingUtil.configAndRing(player, getConfig().enableFoodComa)
                && player.getPersistentData().getLong(FOOD_COMA_END_KEY) > player.level().getGameTime();
    }

    private static void enforceWeakMagicConstitution(Player player) {
        if (!RingUtil.configAndRing(player, getConfig().enableWeakMagicConstitution)) return;

        int maximumLevel = Mth.clamp(getConfig().weakMagicConstitutionMaxLevel, 0, 255);
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.isEmpty()) continue;

            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            if (enchantments.isEmpty()) continue;

            Map<Enchantment, Integer> adjustedEnchantments = new HashMap<>(enchantments);
            boolean changed = false;
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                int level = entry.getValue();
                if (level <= maximumLevel) continue;

                changed = true;
                if (maximumLevel == 0) {
                    adjustedEnchantments.remove(entry.getKey());
                } else {
                    adjustedEnchantments.put(entry.getKey(), maximumLevel);
                }
            }

            if (changed) {
                EnchantmentHelper.setEnchantments(adjustedEnchantments, stack);
                CurseAdvancementManager.trigger(player, "weak_magic_constitution");
            }
        }
    }

    private static void updateSimpleEquipmentCurses(Player player) {
        boolean hasEquipment = false;
        for (ItemStack stack : player.getArmorSlots()) {
            if (!stack.isEmpty()) {
                hasEquipment = true;
                break;
            }
        }
        if (!hasEquipment) {
            for (ItemStack stack : player.getHandSlots()) {
                if (!stack.isEmpty()) {
                    hasEquipment = true;
                    break;
                }
            }
        }

        boolean heavyShackles = hasEquipment
                && RingUtil.configAndRing(player, getConfig().enableHeavyShackles);
        double movementReduction = Math.min(0.95D, Math.max(0.0D, getConfig().heavyShacklesMovementReduction));
        if (updateTransientModifier(
                player.getAttribute(Attributes.MOVEMENT_SPEED),
                HEAVY_SHACKLES_SPEED_UUID,
                "Heavy Shackles Speed",
                heavyShackles,
                -movementReduction
        )) {
            CurseAdvancementManager.trigger(player, "heavy_shackles");
        }

        boolean fragileArmor = hasEquipment
                && RingUtil.configAndRing(player, getConfig().enableFragileArmor);
        double armorMultiplier = Math.min(1.0D, Math.max(0.0D, getConfig().fragileArmorMultiplier));
        double armorModifier = armorMultiplier - 1.0D;
        boolean fragileArmorChanged = updateTransientModifier(
                player.getAttribute(Attributes.ARMOR),
                FRAGILE_ARMOR_UUID,
                "Fragile Armor",
                fragileArmor,
                armorModifier
        );
        fragileArmorChanged |= updateTransientModifier(
                player.getAttribute(Attributes.ARMOR_TOUGHNESS),
                FRAGILE_ARMOR_TOUGHNESS_UUID,
                "Fragile Armor Toughness",
                fragileArmor,
                armorModifier
        );
        if (fragileArmorChanged) {
            CurseAdvancementManager.trigger(player, "fragile_armor");
        }

        boolean weakSwimmer = player.isInWater()
                && RingUtil.configAndRing(player, getConfig().enableWeakSwimmer);
        double swimSpeedMultiplier = Mth.clamp(getConfig().weakSwimmerSwimSpeedMultiplier, 0.0D, 1.0D);
        if (updateTransientModifier(
                player.getAttribute(ForgeMod.SWIM_SPEED.get()),
                WEAK_SWIMMER_SPEED_UUID,
                "Weak Swimmer Speed",
                weakSwimmer,
                swimSpeedMultiplier - 1.0D
        )) {
            CurseAdvancementManager.trigger(player, "weak_swimmer");
        }

        boolean overburdened = RingUtil.configAndRing(player, getConfig().enableOverburdened)
                && countInventoryItems(player) > Math.max(0, getConfig().overburdenedItemThreshold);
        double overburdenedMovementReduction = Mth.clamp(getConfig().overburdenedMovementReduction, 0.0D, 0.95D);
        if (updateTransientModifier(
                player.getAttribute(Attributes.MOVEMENT_SPEED),
                OVERBURDENED_SPEED_UUID,
                "Overburdened Speed",
                overburdened,
                -overburdenedMovementReduction
        )) {
            CurseAdvancementManager.trigger(player, "overburdened");
        }
    }

    private static int countInventoryItems(Player player) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            count += player.getInventory().getItem(i).getCount();
        }
        return count;
    }

    private static boolean isOverburdened(Player player) {
        return RingUtil.configAndRing(player, getConfig().enableOverburdened)
                && countInventoryItems(player) > Math.max(0, getConfig().overburdenedItemThreshold);
    }

    private static boolean updateTransientModifier(
            AttributeInstance attribute, UUID uuid, String name, boolean shouldApply, double amount
    ) {
        if (attribute == null) return false;
        AttributeModifier existing = attribute.getModifier(uuid);
        if (!shouldApply) {
            if (existing != null) {
                attribute.removeModifier(uuid);
            }
            return false;
        }
        if (existing == null) {
            attribute.addTransientModifier(new AttributeModifier(uuid, name, amount, AttributeModifier.Operation.MULTIPLY_TOTAL));
            return true;
        } else if (Double.compare(existing.getAmount(), amount) != 0) {
            attribute.removeModifier(uuid);
            attribute.addTransientModifier(new AttributeModifier(uuid, name, amount, AttributeModifier.Operation.MULTIPLY_TOTAL));
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onWeakSwimmerDrowning(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || player.level().isClientSide
                || !event.getSource().is(DamageTypeTags.IS_DROWNING)
                || !RingUtil.configAndRing(player, getConfig().enableWeakSwimmer)) {
            return;
        }

        double depth = Math.max(0.0D, player.level().getSeaLevel() - player.getY());
        double perBlock = Math.max(0.0D, getConfig().weakSwimmerDrowningDamagePerBlock);
        double maximumMultiplier = Math.max(1.0D, getConfig().weakSwimmerMaxDrowningMultiplier);
        float multiplier = (float) Math.min(maximumMultiplier, 1.0D + depth * perBlock);
        float originalAmount = event.getAmount();
        float modifiedAmount = originalAmount * multiplier;
        if (modifiedAmount != originalAmount) {
            event.setAmount(modifiedAmount);
            CurseAdvancementManager.trigger(player, "weak_swimmer");
        }
    }

    @SubscribeEvent
    public static void onOverburdenedFall(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || player.level().isClientSide
                || !event.getSource().is(DamageTypeTags.IS_FALL)
                || !isOverburdened(player)) {
            return;
        }

        float multiplier = Math.max(1.0F, getConfig().overburdenedFallDamageMultiplier);
        float originalAmount = event.getAmount();
        float modifiedAmount = originalAmount * multiplier;
        if (modifiedAmount != originalAmount) {
            event.setAmount(modifiedAmount);
            CurseAdvancementManager.trigger(player, "overburdened");
        }
    }

    @SubscribeEvent
    public static void onBleedingWoundHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || player.level().isClientSide
                || event.isCanceled()
                || event.getAmount() <= 0.0F
                || event.getSource().is(ModDamageTypes.BLEEDING)
                || !RingUtil.configAndRing(player, getConfig().enableBleedingWound)) {
            return;
        }

        double chance = Mth.clamp(getConfig().bleedingWoundChance, 0.0D, 1.0D);
        if (player.getRandom().nextDouble() >= chance) return;

        int duration = Math.max(1, getConfig().bleedingWoundDuration);
        MobEffectInstance existing = player.getEffect(ModEffect.BLEEDING.get());
        if (existing != null) {
            duration = Math.max(duration, existing.getDuration());
        }
        if (player.addEffect(new MobEffectInstance(ModEffect.BLEEDING.get(), duration, 0, false, true, true))) {
            CurseAdvancementManager.trigger(player, "bleeding_wound");
        }
    }

    private static void updateFeastOrFamine(Player player) {
        if (!RingUtil.configAndRing(player, getConfig().enableFeastOrFamine)) return;

        FoodData foodData = player.getFoodData();
        int foodLevel = foodData.getFoodLevel();
        float saturation = foodData.getSaturationLevel();
        boolean famine = foodLevel <= getConfig().feastOrFamineLowFoodLevel
                || saturation <= getConfig().feastOrFamineLowSaturation;
        boolean feast = foodLevel >= getConfig().feastOrFamineHighFoodLevel
                || saturation >= getConfig().feastOrFamineHighSaturation;
        int duration = Math.max(1, getConfig().feastOrFamineDebuffDuration);
        int amplifier = Math.max(0, getConfig().feastOrFamineDebuffAmplifier);

        if (famine && !feast) {
            if (player.addEffect(new MobEffectInstance(MobEffects.HUNGER, duration, amplifier, false, true))) {
                CurseAdvancementManager.trigger(player, "feast_or_famine");
            }
        } else if (feast && !famine) {
            if (player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, amplifier, false, true))) {
                CurseAdvancementManager.trigger(player, "feast_or_famine");
            }
        }
    }

    @SubscribeEvent
    public static void onWaterShacklesTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) return;
        AttributeModifier existing = speedAttr.getModifier(WATER_SHACKLES_UUID);
        boolean shouldApply = player.isInWater() && RingUtil.configAndRing(player, getConfig().enableWaterShackles);
        if (shouldApply && existing == null) {
            double slowdown = -getConfig().waterShacklesSlowdown;
            speedAttr.addTransientModifier(new AttributeModifier(WATER_SHACKLES_UUID, "Water Shackles", slowdown, AttributeModifier.Operation.MULTIPLY_TOTAL));
            CurseAdvancementManager.trigger(player, "water_shackles");
        } else if (!shouldApply && existing != null) {
            speedAttr.removeModifier(WATER_SHACKLES_UUID);
        }
    }

    // 气压失序：过高或过低位置减速+增加重力（降低跳跃）
    @SubscribeEvent
    public static void onPressureDisorderTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;

        boolean shouldApply = RingUtil.configAndRing(player, getConfig().enablePressureDisorder);
        double intensity = 0.0;

        if (shouldApply) {
            double y = player.getY();
            double lowY = getConfig().pressureDisorderLowY;
            double highY = getConfig().pressureDisorderHighY;
            double range = getConfig().pressureDisorderRange;

            if (y < lowY) {
                intensity = Math.min((lowY - y) / range, 1.0);
            } else if (y > highY) {
                intensity = Math.min((y - highY) / range, 1.0);
            }
        }

        // 移速降低
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            AttributeModifier existingSpeed = speedAttr.getModifier(PRESSURE_DISORDER_SPEED_UUID);
            if (intensity > 0) {
                double slowdown = -getConfig().pressureDisorderMaxSpeedReduction * intensity;
                boolean changed = existingSpeed == null || Double.compare(existingSpeed.getAmount(), slowdown) != 0;
                if (changed) {
                    if (existingSpeed != null) speedAttr.removeModifier(PRESSURE_DISORDER_SPEED_UUID);
                    speedAttr.addTransientModifier(new AttributeModifier(PRESSURE_DISORDER_SPEED_UUID, "Pressure Disorder Speed", slowdown, AttributeModifier.Operation.MULTIPLY_TOTAL));
                    CurseAdvancementManager.trigger(player, "pressure_disorder");
                }
            } else if (existingSpeed != null) {
                speedAttr.removeModifier(PRESSURE_DISORDER_SPEED_UUID);
            }
        }

        // 重力增加（降低跳跃高度）
        AttributeInstance gravityAttr = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
        if (gravityAttr != null) {
            AttributeModifier existingGravity = gravityAttr.getModifier(PRESSURE_DISORDER_GRAVITY_UUID);
            if (intensity > 0) {
                double gravityIncrease = getConfig().pressureDisorderMaxGravityIncrease * intensity;
                boolean changed = existingGravity == null || Double.compare(existingGravity.getAmount(), gravityIncrease) != 0;
                if (changed) {
                    if (existingGravity != null) gravityAttr.removeModifier(PRESSURE_DISORDER_GRAVITY_UUID);
                    gravityAttr.addTransientModifier(new AttributeModifier(PRESSURE_DISORDER_GRAVITY_UUID, "Pressure Disorder Gravity", gravityIncrease, AttributeModifier.Operation.MULTIPLY_TOTAL));
                    CurseAdvancementManager.trigger(player, "pressure_disorder");
                }
            } else if (existingGravity != null) {
                gravityAttr.removeModifier(PRESSURE_DISORDER_GRAVITY_UUID);
            }
        }
    }

    @SubscribeEvent
    public static void onLossOfSynchronicityHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        if (!RingUtil.configAndRing(player, getConfig().enableLossOfSynchronicity)) {
            return;
        }
        float maxHealth = player.getMaxHealth();
        if (maxHealth <= 0.0f) {
            return;
        }
        float healthRatio = Mth.clamp(player.getHealth() / maxHealth, 0.0f, 1.0f);
        float originalAmount = event.getAmount();
        float modifiedAmount = originalAmount * healthRatio;
        if (modifiedAmount != originalAmount) {
            event.setAmount(modifiedAmount);
            CurseAdvancementManager.trigger(player, "loss_of_synchronicity");
        }
    }

    @SubscribeEvent
    public static void onUnitedAdversariesHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.level().isClientSide) {
            return;
        }
        if (!RingUtil.configAndRing(player, getConfig().enableUnitedAdversaries)) {
            return;
        }
        double range = Math.max(1.0d, getConfig().unitedAdversariesRange);
        int hostileCount = player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(range),
                mob -> mob instanceof Enemy && mob.isAlive() && mob.getTarget() == player).size();
        if (hostileCount <= 0) {
            return;
        }
        float perMobBonus = Math.max(0.0f, getConfig().unitedAdversariesPerMobBonus);
        float maxBonus = Math.max(0.0f, getConfig().unitedAdversariesMaxBonus);
        float bonus = Math.min(hostileCount * perMobBonus, maxBonus);
        float originalAmount = event.getAmount();
        float modifiedAmount = originalAmount * (1.0f + bonus);
        if (modifiedAmount != originalAmount) {
            event.setAmount(modifiedAmount);
            CurseAdvancementManager.trigger(player, "united_adversaries");
        }
    }

    @SubscribeEvent
    public static void onStepOnBlock(StepOnBlockEvent event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getState().getBlock() instanceof FarmBlock)) return;
        if (!RingUtil.configAndRing(player, getConfig().enableClumsyFarmer)) return;
        if (event.getLevel().random.nextDouble() < getConfig().clumsyFarmerChance) {
            FarmBlock.turnToDirt(player, event.getState(), event.getLevel(), event.getPos());
            CurseAdvancementManager.trigger(player, "clumsy_farmer");
        }
    }

    @SubscribeEvent
    public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
        Level level = event.getLevel();
        Player nearestPlayer = level.getNearestPlayer(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), 8.0, false);
        if (nearestPlayer == null) return;
        int originalLevel = event.getEnchantLevel();
        int newLevel = originalLevel;
        if (RingUtil.configAndRing(nearestPlayer, getConfig().enableWeakMagicConstitution)) {
            int maximumLevel = Mth.clamp(getConfig().weakMagicConstitutionMaxLevel, 0, 30);
            newLevel = Math.min(newLevel, maximumLevel);
        }
        if (newLevel != originalLevel) {
            event.setEnchantLevel(newLevel);
            CurseAdvancementManager.trigger(nearestPlayer, "weak_magic_constitution");
        }
    }


    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        if (event.getLevel().isClientSide()) return;
        Player player = event.getEntity();
        if (!RingUtil.configAndRing(player, getConfig().enableOverzealousGrowth)) return;
        BlockPos pos = event.getPos();
        BlockState state = event.getLevel().getBlockState(pos);
        if (!(state.getBlock() instanceof CropBlock cropBlock)) return;
        int currentAge = cropBlock.getAge(state);
        if (currentAge <= 0) return;
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag growthCounts = persistentData.contains(OVERZEALOUS_GROWTH_KEY) ? persistentData.getCompound(OVERZEALOUS_GROWTH_KEY) : new CompoundTag();
        String posKey = pos.getX() + "," + pos.getY() + "," + pos.getZ();
        int fertCount = growthCounts.getInt(posKey);
        double baseChance = getConfig().overzealousGrowthBaseChance;
        double increment = getConfig().overzealousGrowthChanceIncrement;
        double maxChance = getConfig().overzealousGrowthMaxChance;
        double chance = Math.min(baseChance + fertCount * increment, maxChance);
        fertCount++;
        growthCounts.putInt(posKey, fertCount);
        persistentData.put(OVERZEALOUS_GROWTH_KEY, growthCounts);
        if (player.level().random.nextDouble() < chance) {
            int newAge = Math.max(0, currentAge - 1);
            event.getLevel().setBlock(pos, cropBlock.getStateForAge(newAge), 2);
            event.setResult(Event.Result.ALLOW);
            CurseAdvancementManager.trigger(player, "overzealous_growth");
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (isFoodComaActive(player)) {
            event.setCanceled(true);
            return;
        }
        if (focusDisturbanceFails(player)) {
            event.setCanceled(true);
            CurseAdvancementManager.trigger(player, "focus_disturbance");
            return;
        }
        if (!RingUtil.configAndRing(player, getConfig().enableUnlitObjects)) return;
        BlockState placedState = event.getPlacedBlock();
        Block placedBlock = placedState.getBlock();
        BlockPos pos = event.getPos();
        Level level = (Level) event.getLevel();
        boolean extinguished = false;
        if (placedBlock == Blocks.TORCH) {
            level.setBlock(pos, ModBlock.EXTINGUISHED_TORCH.get().defaultBlockState(), 3);
            extinguished = true;
        } else if (placedBlock == Blocks.WALL_TORCH) {
            BlockState extinguishedState = ModBlock.EXTINGUISHED_WALL_TORCH.get().defaultBlockState().setValue(WallTorchBlock.FACING, placedState.getValue(WallTorchBlock.FACING));
            level.setBlock(pos, extinguishedState, 3);
            extinguished = true;
        } else if (placedBlock instanceof CampfireBlock) {
            BlockState replacedState = event.getBlockSnapshot().getReplacedBlock();
            boolean isNewPlacement = !(replacedState.getBlock() instanceof CampfireBlock);
            if (isNewPlacement && placedState.getValue(CampfireBlock.LIT)) {
                level.setBlock(pos, placedState.setValue(CampfireBlock.LIT, false), 3);
                extinguished = true;
            }
        }
        if (extinguished) {
            CurseAdvancementManager.trigger(player, "unlit_objects");
        }
    }


    @SubscribeEvent
    public static void onDeepSeaEntanglementTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        Level level = player.level();
        if (!RingUtil.configAndRing(player, getConfig().enableDeepSeaEntanglement)) return;
        CompoundTag data = player.getPersistentData();
        int swimTicks = data.getInt(SWIM_TIME_KEY);
        int maxSwimTicks = Math.max(1, getConfig().deepSeaEntanglementSwimTime * 20);
        int recoverTicks = getConfig().deepSeaEntanglementRecoverTime * 20;
        if (RingUtil.isInWaterOrAtSurface(player)) {
            swimTicks++;
            data.putInt(SWIM_TIME_KEY, swimTicks);
            if (swimTicks >= maxSwimTicks) {
                Vec3 motion = player.getDeltaMovement();
                double sinkingSpeed = Math.max(0.01, getConfig().deepSeaEntanglementSinkingSpeed);
                double ySpeed = Math.min(motion.y, -sinkingSpeed);
                player.setDeltaMovement(motion.x * 0.7, ySpeed, motion.z * 0.7);
                player.setSwimming(false);
                player.setJumping(false);
                if (motion.y != ySpeed || motion.x != motion.x * 0.7 || motion.z != motion.z * 0.7) {
                    CurseAdvancementManager.trigger(player, "deep_sea_entanglement");
                }
                if (!level.isClientSide && swimTicks % 20 == 0) {
                    player.displayClientMessage(Component.translatable("message.ring_of_the_hundred_curses.deep_sea_entanglement.sinking").withStyle(ChatFormatting.RED), true);
                }
            } else {
                int remainSeconds = (maxSwimTicks - swimTicks) / 20;
                if (!level.isClientSide && swimTicks % 20 == 0) {
                    ChatFormatting color = remainSeconds <= 5 ? ChatFormatting.RED : remainSeconds <= 10 ? ChatFormatting.YELLOW : ChatFormatting.AQUA;
                    player.displayClientMessage(Component.translatable("message.ring_of_the_hundred_curses.deep_sea_entanglement.countdown", remainSeconds).withStyle(color), true);
                }
            }
        } else {
            if (swimTicks > 0) {
                int recoverRate = recoverTicks > 0 ? Math.max(1, maxSwimTicks / recoverTicks) : maxSwimTicks;
                swimTicks = Math.max(0, swimTicks - recoverRate);
                data.putInt(SWIM_TIME_KEY, swimTicks);
            }
        }
    }

    // 破裂之门：传送门是一次性的，使用了之后两端都会炸裂
    @SubscribeEvent
    public static void onTravelToDimension(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (!RingUtil.configAndRing(player, getConfig().enableShatteredPortal)) return;
        if (event.getDimension() == player.level().dimension()) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        if (destroyPortalAt(serverLevel, player.blockPosition())) {
            CurseAdvancementManager.trigger(player, "shattered_portal");
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        if (!RingUtil.configAndRing(player, getConfig().enableShatteredPortal)) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        if (destroyPortalAt(serverLevel, player.blockPosition())) {
            CurseAdvancementManager.trigger(player, "shattered_portal");
        }
        player.displayClientMessage(Component.translatable("message.ring_of_the_hundred_curses.shattered_portal").withStyle(ChatFormatting.RED), true);
    }

    private static boolean destroyPortalAt(ServerLevel level, BlockPos center) {
        int searchRadius = 5;
        List<BlockPos> portalBlocks = new ArrayList<>();
        List<BlockPos> frameBlocks = new ArrayList<>();
        boolean hasEndPortal = false;
        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (state.getBlock() instanceof NetherPortalBlock) {
                        portalBlocks.add(pos);
                    } else if (state.getBlock() instanceof EndPortalBlock) {
                        portalBlocks.add(pos);
                        hasEndPortal = true;
                    }
                }
            }
        }
        if (portalBlocks.isEmpty()) return false;
        for (BlockPos portalPos : portalBlocks) {
            for (BlockPos neighbor : BlockPos.betweenClosed(portalPos.offset(-1, -1, -1), portalPos.offset(1, 1, 1))) {
                BlockState neighborState = level.getBlockState(neighbor);
                Block block = neighborState.getBlock();
                if (hasEndPortal) {
                    if (block == Blocks.END_PORTAL_FRAME) {
                        frameBlocks.add(neighbor.immutable());
                    }
                } else {
                    if (block == Blocks.OBSIDIAN) {
                        frameBlocks.add(neighbor.immutable());
                    }
                }
            }
        }
        double centerX = 0, centerY = 0, centerZ = 0;
        for (BlockPos pos : portalBlocks) {
            centerX += pos.getX();
            centerY += pos.getY();
            centerZ += pos.getZ();
        }
        centerX = centerX / portalBlocks.size() + 0.5;
        centerY = centerY / portalBlocks.size() + 0.5;
        centerZ = centerZ / portalBlocks.size() + 0.5;
        for (BlockPos pos : portalBlocks) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        for (BlockPos pos : frameBlocks) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        level.explode(null, centerX, centerY, centerZ, 2.0f, Level.ExplosionInteraction.NONE);
        return true;
    }

    // 海关过境：高级村民交易需要等待几天才能拿到物品

    @SubscribeEvent
    public static void onItemCrafted(ItemCraftedEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || !focusDisturbanceFails(player)) return;
        event.getCrafting().setCount(0);
        CurseAdvancementManager.trigger(player, "focus_disturbance");
    }

    private static boolean focusDisturbanceFails(Player player) {
        if (!RingUtil.configAndRing(player, getConfig().enableFocusDisturbance)) return false;
        double chance = Mth.clamp(getConfig().focusDisturbanceChance, 0.0D, 1.0D);
        return chance > 0.0D && player.getRandom().nextDouble() < chance;
    }

    private static void checkCustomsClearanceDelivery(Player player, long gameTime) {
        CompoundTag persistentData = player.getPersistentData();
        ICustomsClearance customs = player.getCapability(CustomsClearanceProvider.CUSTOMS_CLEARANCE).orElse(null);
        if (customs == null) return;

        migrateLegacyCustomsClearance(persistentData, customs);
        for (ICustomsClearance.PendingItem pendingItem : customs.takeDueItems(gameTime)) {
            ItemStack item = pendingItem.item();
            if (item.isEmpty()) continue;
            if (!player.addItem(item)) {
                player.drop(item, false);
            }
            player.displayClientMessage(Component.translatable("message.ring_of_the_hundred_curses.customs_clearance.delivered").withStyle(ChatFormatting.GREEN), false);
        }
    }

    private static void migrateLegacyCustomsClearance(CompoundTag persistentData, ICustomsClearance customs) {
        if (!persistentData.contains(CUSTOMS_CLEARANCE_LEGACY_KEY, Tag.TAG_LIST)) return;
        ListTag pendingList = persistentData.getList(CUSTOMS_CLEARANCE_LEGACY_KEY, Tag.TAG_COMPOUND);
        for (int i = 0; i < pendingList.size(); i++) {
            CompoundTag entry = pendingList.getCompound(i);
            ItemStack item = ItemStack.of(entry.getCompound("Item"));
            if (!item.isEmpty()) {
                customs.addPendingItem(item, entry.getLong("DeliveryTime"));
            }
        }
        persistentData.remove(CUSTOMS_CLEARANCE_LEGACY_KEY);
    }

    // 时空紊乱：通过传送门时有概率被传送到目标的任意附近位置（低优先级，确保破裂之门先执行）
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onTimeDistortion(PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        if (!RingUtil.configAndRing(player, getConfig().enableTimeDistortion)) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        if (player.getRandom().nextDouble() >= getConfig().timeDistortionChance) return;
        int maxOffset = getConfig().timeDistortionMaxOffset;
        for (int attempt = 0; attempt < 16; attempt++) {
            double x = player.getX() + (player.getRandom().nextDouble() - 0.5) * 2 * maxOffset;
            double y = Mth.clamp(player.getY() + player.getRandom().nextInt(16) - 8, serverLevel.getMinBuildHeight(), serverLevel.getMinBuildHeight() + serverLevel.getLogicalHeight() - 1);
            double z = player.getZ() + (player.getRandom().nextDouble() - 0.5) * 2 * maxOffset;

            if (player.randomTeleport(x, y, z, true)) {
                player.displayClientMessage(Component.translatable("message.ring_of_the_hundred_curses.time_distortion").withStyle(ChatFormatting.DARK_PURPLE), true);
                CurseAdvancementManager.trigger(player, "time_distortion");
                break;
            }
        }
    }

    // 雷霆引誓：雷雨天更高概率被雷击中
    @SubscribeEvent
    public static void onChunkThunder(ChunkThunderEvent event) {
        Player player = event.getPlayer();
        if (!RingUtil.configAndRing(player, getConfig().enableThunderboundOath)) return;
        ServerLevel level = event.getLevel();
        int rarity = getConfig().thunderboundOathRarity;
        if (rarity <= 0 || level.random.nextInt(rarity) != 0) return;
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(player.position());
            if (level.addFreshEntity(bolt)) {
                CurseAdvancementManager.trigger(player, "thunderbound_oath");
            }
        }
    }

    // 创伤应激：玩家被生物杀死时，记录该生物类型到NBT
    @SubscribeEvent
    public static void onPlayerDeathPTSD(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!RingUtil.configAndRing(player, getConfig().enablePTSD)) return;
        if (event.getSource().getEntity() instanceof LivingEntity killer) {
            ResourceLocation killerTypeKey = EntityType.getKey(killer.getType());
            String killerType = killerTypeKey.toString();
            CompoundTag data = player.getPersistentData();
            ListTag traumaList = data.contains(PTSD_TRAUMA_KEY, Tag.TAG_LIST)
                    ? data.getList(PTSD_TRAUMA_KEY, Tag.TAG_STRING)
                    : new ListTag();
            for (int i = 0; i < traumaList.size(); i++) {
                if (traumaList.getString(i).equals(killerType)) return;
            }
            traumaList.add(StringTag.valueOf(killerType));
            int max = getConfig().ptsdMaxTraumaCount;
            while (traumaList.size() > max) {
                traumaList.remove(0);
            }
            data.put(PTSD_TRAUMA_KEY, traumaList);
        }
    }


    // byd Event名称和类名冲突了
    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        CompoundTag oldData = event.getOriginal().getPersistentData();
        CompoundTag newData = event.getEntity().getPersistentData();
        if (oldData.contains(PTSD_TRAUMA_KEY, Tag.TAG_LIST)) {
            newData.put(PTSD_TRAUMA_KEY, oldData.getList(PTSD_TRAUMA_KEY, Tag.TAG_STRING).copy());
        }
        if (oldData.contains(BALANCED_DIET_KEY, Tag.TAG_COMPOUND)) {
            newData.put(BALANCED_DIET_KEY, oldData.getCompound(BALANCED_DIET_KEY).copy());
        }
        if (oldData.contains(PHANTOM_GIFT_LAST_NIGHT_KEY, Tag.TAG_LONG)) {
            newData.putLong(PHANTOM_GIFT_LAST_NIGHT_KEY, oldData.getLong(PHANTOM_GIFT_LAST_NIGHT_KEY));
        }
        if (oldData.getBoolean(FIRST_RING_GIVEN_KEY)) {
            newData.putBoolean(FIRST_RING_GIVEN_KEY, true);
        }
        event.getOriginal().getCapability(CustomsClearanceProvider.CUSTOMS_CLEARANCE).ifPresent(oldCustoms ->
                event.getEntity().getCapability(CustomsClearanceProvider.CUSTOMS_CLEARANCE)
                        .ifPresent(newCustoms -> newCustoms.copyFrom(oldCustoms))
        );
    }

    // 创伤应激：怪物将玩家设为攻击目标时，检查是否为创伤生物
    @SubscribeEvent
    public static void onPTSDTargeted(LivingChangeTargetEvent event) {
        if (!(event.getNewTarget() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (!RingUtil.configAndRing(player, getConfig().enablePTSD)) return;
        CompoundTag data = player.getPersistentData();
        if (!data.contains(PTSD_TRAUMA_KEY, Tag.TAG_LIST)) return;
        ListTag traumaList = data.getList(PTSD_TRAUMA_KEY, Tag.TAG_STRING);
        String entityType = EntityType.getKey(event.getEntity().getType()).toString();
        for (int i = 0; i < traumaList.size(); i++) {
            if (traumaList.getString(i).equals(entityType)) {
                if (applyPTSDDebuff(player)) {
                    CurseAdvancementManager.trigger(player, "ptsd");
                }
                return;
            }
        }
    }

    private static boolean applyPTSDDebuff(Player player) {
        int duration = getConfig().ptsdDebuffDuration;
        int amplifier = getConfig().ptsdDebuffAmplifier;
        boolean applied = false;
        applied |= player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amplifier, false, true));
        applied |= player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, amplifier, false, true));
        applied |= player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, amplifier, false, true));
        return applied;
    }
}
