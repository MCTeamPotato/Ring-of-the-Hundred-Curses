package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.api.event.ChunkThunderEvent;
import com.kaleblangley.ring_of_the_hundred_curses.api.event.EatEvent;
import com.kaleblangley.ring_of_the_hundred_curses.api.event.StepOnBlockEvent;
import com.kaleblangley.ring_of_the_hundred_curses.item.CursedRing;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.Containers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.entity.player.TradeWithVillagerEvent;
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
import java.util.List;
import java.util.UUID;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvent {

    private static final List<MobEffect> HARMFUL_EFFECTS;
    private static final UUID WATER_SHACKLES_UUID = UUID.fromString("a3d2b4c1-8f7e-4d6a-9b5c-1e2f3a4b5c6d");

    static {
        List<MobEffect> effects = new java.util.ArrayList<>();
        for (MobEffect effect : ForgeRegistries.MOB_EFFECTS) {
            if (effect.getCategory() == MobEffectCategory.HARMFUL && effect != MobEffects.HARM && effect != MobEffects.HEAL && effect != MobEffects.LEVITATION) {
                effects.add(effect);
            }
        }
        HARMFUL_EFFECTS = List.copyOf(effects);
    }

    @SubscribeEvent
    public static void onEatFood(EatEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (RingUtil.configAndRing(player, getConfig().enableHollowStomach)) {
                int maxHunger = getConfig().hollowStomachMaxHunger;
                FoodData foodData = player.getFoodData();
                int currentHunger = foodData.getFoodLevel();
                int nutritionToAdd = event.getNutrition();
                if (currentHunger >= maxHunger) {
                    event.setNutrition(0);
                } else if (currentHunger + nutritionToAdd > maxHunger) {
                    event.setNutrition(maxHunger - currentHunger);
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
        if (event.getEntity() instanceof Player) {
            Player player = event.getEntity();
            if (RingUtil.configAndRing(player, getConfig().enableNeurologicalDegeneration)) {
                applyRandomHarmfulEffect(player);
            }
        }
    }

    @SubscribeEvent
    public static void playerBreakEvent(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player != null && RingUtil.configAndRing(player, getConfig().enableWormHoard)) {
            BlockState state = event.getState();
            Level level = player.level();
            if (!level.isClientSide && isWormHoardTarget(state.getBlock())) {
                if (level.random.nextDouble() < getConfig().wormHoardSpawnChance) {
                    spawnSilverfishAtPosition(event.getPos(), level, player);
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
            spawnTntAtPosition(hookPos, level, player);
            return;
        }
        if (level.random.nextDouble() < getConfig().depthChargeMobChance) {
            spawnHostileMobAtPosition(hookPos, level, player);
        }
    }

    @SubscribeEvent
    public static void onVillagerTrade(TradeWithVillagerEvent event) {
        Player player = event.getEntity();
        if (!RingUtil.configAndRing(player, getConfig().enableDodgyMerchant)) {
            return;
        }
        Level level = player.level();
        if (level.isClientSide) {
            return;
        }
        double swapChance = getConfig().dodgyMerchantSwapChance;
        if (level.random.nextDouble() >= swapChance) {
            return;
        }
        ItemStack originalResult = event.getMerchantOffer().getResult();
        if (canBeSwappedItem(originalResult)) {
            swapTradeResult(player, originalResult);
        }
    }

    private static void swapTradeResult(Player player, ItemStack originalResult) {
        ItemStack randomItem = getDodgyMerchantRandomItem(player);
        randomItem.setCount(Math.min(originalResult.getCount(), randomItem.getMaxStackSize()));
        for (int i = player.getInventory().getContainerSize() - 1; i >= 0; i--) {
            ItemStack slotStack = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameTags(slotStack, originalResult) && slotStack.getCount() == originalResult.getCount()) {
                player.getInventory().setItem(i, randomItem);
                player.containerMenu.broadcastChanges();
                break;
            }
        }
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

    private static void applyRandomHarmfulEffect(Player player) {
        if (HARMFUL_EFFECTS.isEmpty()) {
            return;
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
        player.addEffect(effectInstance);
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

    private static void spawnSilverfishAtPosition(BlockPos blockPos, Level level, Player player) {
        int maxSilverfish = getConfig().wormHoardMaxSilverfish;
        int numToSpawn = 1 + level.random.nextInt(maxSilverfish);
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
            level.addFreshEntity(silverfish);
        }
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
        Item randomItem = junkItems.get(level.random.nextInt(junkItems.size()));
        int count = 1 + level.random.nextInt(3);
        return new ItemStack(randomItem, count);
    }

    private static void spawnTntAtPosition(Vec3 position, Level level, Player player) {
        PrimedTnt tnt = new PrimedTnt(level, position.x, position.y, position.z, player);
        tnt.setFuse(40);
        level.addFreshEntity(tnt);
        Vec3 playerPos = player.position().add(0, 1, 0);
        Vec3 direction = playerPos.subtract(position).normalize();
        double speed = 0.8;
        tnt.setDeltaMovement(direction.x * speed, Math.max(0.3, direction.y * speed + 0.2), direction.z * speed);
    }

    private static void spawnHostileMobAtPosition(Vec3 position, Level level, Player player) {
        String[] mobStrings = getConfig().depthChargeHostileMobs;
        if (mobStrings.length == 0) return;
        String randomMobId = mobStrings[level.random.nextInt(mobStrings.length)];
        try {
            ResourceLocation mobLocation = new ResourceLocation(randomMobId);
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(mobLocation);
            if (entityType != null) {
                LivingEntity mob = (LivingEntity) entityType.create(level);
                if (mob instanceof Monster monster) {
                    monster.setPos(position.x, position.y, position.z);
                    monster.setTarget(player);
                    level.addFreshEntity(monster);
                    Vec3 playerPos = player.position().add(0, 1, 0);
                    Vec3 direction = playerPos.subtract(position).normalize();
                    double speed = 0.6;
                    monster.setDeltaMovement(direction.x * speed, Math.max(0.2, direction.y * speed + 0.15), direction.z * speed);
                    monster.invulnerableTime = 20;
                }
            }
        } catch (Exception e) {
            RingOfTheHundredCurses.LOGGER.warn("Invalid mob in depth charge config: {}", randomMobId);
        }
    }

    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        Player player = event.getEntity();
        if (RingUtil.configAndRing(player, getConfig().enableSoulSuppression)) {
            int reduced = (int) (event.getAmount() * (1.0f - getConfig().xpReductionPercent));
            event.setAmount(Math.max(reduced, 0));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide) return;
        long gameTime = level.getGameTime();
        if (RingUtil.configAndRing(player, getConfig().enableFragileBody)) {
            int max = getConfig().fragileBodyMaxInvulnerableTime;
            if (player.invulnerableTime > max) {
                player.invulnerableTime = max;
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
                }
            }
        }
        if (gameTime % 20 == 0) {
            checkCustomsClearanceDelivery(player, level.getDayTime());
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
        } else if (!shouldApply && existing != null) {
            speedAttr.removeModifier(WATER_SHACKLES_UUID);
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
        }
    }

    @SubscribeEvent
    public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
        Level level = event.getLevel();
        Player nearestPlayer = level.getNearestPlayer(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), 8.0, false);
        if (nearestPlayer == null) return;
        if (!RingUtil.configAndRing(nearestPlayer, getConfig().enableGreedyTome)) return;
        int newLevel = (int) (event.getEnchantLevel() * getConfig().greedyTomeCostMultiplier);
        event.setEnchantLevel(Math.min(newLevel, 30));
    }

    private static final String OVERZEALOUS_GROWTH_KEY = "OverzealousGrowthCounts";

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
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (!RingUtil.configAndRing(player, getConfig().enableUnlitObjects)) return;
        BlockState placedState = event.getPlacedBlock();
        Block placedBlock = placedState.getBlock();
        BlockPos pos = event.getPos();
        Level level = (Level) event.getLevel();
        if (placedBlock == Blocks.TORCH) {
            level.setBlock(pos, ModBlock.EXTINGUISHED_TORCH.get().defaultBlockState(), 3);
        } else if (placedBlock == Blocks.WALL_TORCH) {
            BlockState extinguishedState = ModBlock.EXTINGUISHED_WALL_TORCH.get().defaultBlockState().setValue(WallTorchBlock.FACING, placedState.getValue(WallTorchBlock.FACING));
            level.setBlock(pos, extinguishedState, 3);
        } else if (placedBlock instanceof CampfireBlock) {
            BlockState replacedState = event.getBlockSnapshot().getReplacedBlock();
            boolean isNewPlacement = !(replacedState.getBlock() instanceof CampfireBlock);
            if (isNewPlacement && placedState.getValue(CampfireBlock.LIT)) {
                level.setBlock(pos, placedState.setValue(CampfireBlock.LIT, false), 3);
            }
        }
    }

    private static final String SWIM_TIME_KEY = "DeepSeaEntanglementSwimTick";

    @SubscribeEvent
    public static void onDeepSeaEntanglementTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide) return;
        if (!RingUtil.configAndRing(player, getConfig().enableDeepSeaEntanglement)) return;
        CompoundTag data = player.getPersistentData();
        int swimTicks = data.getInt(SWIM_TIME_KEY);
        int maxSwimTicks = getConfig().deepSeaEntanglementSwimTime * 20;
        int recoverTicks = getConfig().deepSeaEntanglementRecoverTime * 20;
        if (player.isInWater()) {
            swimTicks++;
            data.putInt(SWIM_TIME_KEY, swimTicks);
            if (swimTicks >= maxSwimTicks) {
                Vec3 motion = player.getDeltaMovement();
                double ySpeed = Math.min(motion.y, 0) - 0.08;
                player.setDeltaMovement(motion.x * 0.7, ySpeed, motion.z * 0.7);
                player.setSwimming(false);
                if (swimTicks % 20 == 0) {
                    player.displayClientMessage(Component.translatable("message.ring_of_the_hundred_curses.deep_sea_entanglement.sinking").withStyle(ChatFormatting.RED), true);
                }
            } else {
                int remainSeconds = (maxSwimTicks - swimTicks) / 20;
                if (swimTicks % 20 == 0) {
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
        destroyPortalAt(serverLevel, player.blockPosition());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        if (!RingUtil.configAndRing(player, getConfig().enableShatteredPortal)) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        destroyPortalAt(serverLevel, player.blockPosition());
        player.displayClientMessage(Component.translatable("message.ring_of_the_hundred_curses.shattered_portal").withStyle(ChatFormatting.RED), true);
    }

    private static void destroyPortalAt(ServerLevel level, BlockPos center) {
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
        if (portalBlocks.isEmpty()) return;
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
    }

    // 海关过境：高级村民交易需要等待几天才能拿到物品
    private static final String CUSTOMS_CLEARANCE_KEY = "CustomsClearancePending";

    @SubscribeEvent
    public static void onCustomsClearanceTrade(TradeWithVillagerEvent event) {
        Player player = event.getEntity();
        if (!RingUtil.configAndRing(player, getConfig().enableCustomsClearance)) return;
        Level level = player.level();
        if (level.isClientSide) return;
        if (!(event.getAbstractVillager() instanceof Villager villager)) return;
        int villagerLevel = villager.getVillagerData().getLevel();
        if (villagerLevel < getConfig().customsClearanceMinLevel) return;
        ItemStack result = event.getMerchantOffer().getResult();
        if (result.isEmpty()) return;
        boolean removed = false;
        for (int i = player.getInventory().getContainerSize() - 1; i >= 0; i--) {
            ItemStack slotStack = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameTags(slotStack, result) && slotStack.getCount() == result.getCount()) {
                player.getInventory().setItem(i, ItemStack.EMPTY);
                player.containerMenu.broadcastChanges();
                removed = true;
                break;
            }
        }
        if (!removed) return;
        long deliveryTime = level.getDayTime() + (long) getConfig().customsClearanceWaitDays * 24000L;
        CompoundTag persistentData = player.getPersistentData();
        ListTag pendingList = persistentData.contains(CUSTOMS_CLEARANCE_KEY, Tag.TAG_LIST) ? persistentData.getList(CUSTOMS_CLEARANCE_KEY, Tag.TAG_COMPOUND) : new ListTag();
        CompoundTag entry = new CompoundTag();
        entry.put("Item", result.save(new CompoundTag()));
        entry.putLong("DeliveryTime", deliveryTime);
        pendingList.add(entry);
        persistentData.put(CUSTOMS_CLEARANCE_KEY, pendingList);
        int waitDays = getConfig().customsClearanceWaitDays;
        player.displayClientMessage(Component.translatable("message.ring_of_the_hundred_curses.customs_clearance.held", waitDays).withStyle(ChatFormatting.YELLOW), true);
    }

    private static void checkCustomsClearanceDelivery(Player player, long gameTime) {
        CompoundTag persistentData = player.getPersistentData();
        if (!persistentData.contains(CUSTOMS_CLEARANCE_KEY, Tag.TAG_LIST)) return;
        ListTag pendingList = persistentData.getList(CUSTOMS_CLEARANCE_KEY, Tag.TAG_COMPOUND);
        if (pendingList.isEmpty()) return;
        ListTag remaining = new ListTag();
        for (int i = 0; i < pendingList.size(); i++) {
            CompoundTag entry = pendingList.getCompound(i);
            long deliveryTime = entry.getLong("DeliveryTime");
            if (gameTime >= deliveryTime) {
                ItemStack item = ItemStack.of(entry.getCompound("Item"));
                if (!item.isEmpty()) {
                    if (!player.addItem(item)) {
                        player.drop(item, false);
                    }
                    player.displayClientMessage(Component.translatable("message.ring_of_the_hundred_curses.customs_clearance.delivered").withStyle(ChatFormatting.GREEN), false);
                }
            } else {
                remaining.add(entry);
            }
        }
        persistentData.put(CUSTOMS_CLEARANCE_KEY, remaining);
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
            level.addFreshEntity(bolt);
        }
    }

}