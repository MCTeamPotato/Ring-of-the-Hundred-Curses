package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.api.event.EatEvent;
import com.kaleblangley.ring_of_the_hundred_curses.item.CursedRing;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.Containers;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.entity.player.TradeWithVillagerEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModTag;

import java.util.ArrayList;
import java.util.List;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvent {

    private static final List<MobEffect> HARMFUL_EFFECTS;

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
        if (!RingUtil.isTradeOrSpecialContainer(event.getPlayer())) {
            RingUtil.backpackLimitSizeModify(event.getPlayer(), carryItem);
        }
        if (stackedOnItem.is(Items.SHIELD) && event.getSlot().getContainerSlot() == 40 && RingUtil.configAndRing(event.getPlayer(), getConfig().enableShieldOnTheRight)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void pickUpItem(ItemPickupEvent event) {
        if (!RingUtil.isTradeOrSpecialContainer(event.getEntity())) {
            RingUtil.backpackLimitSizeModify(event.getEntity(), event.getStack());
        }
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
            if (ItemStack.isSameItemSameTags(slotStack, originalResult) &&
                slotStack.getCount() == originalResult.getCount()) {
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
        return !(stack.getItem() instanceof CursedRing) &&
                !stack.is(Items.NETHERITE_INGOT) &&
                !stack.is(Items.DIAMOND) &&
                !stack.is(Items.EMERALD);
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

    /**
     * 深水炸弹：在钓鱼位置生成TNT，并让它像被钓上来一样飞向玩家
     */
    private static void spawnTntAtPosition(Vec3 position, Level level, Player player) {
        PrimedTnt tnt = new PrimedTnt(level, position.x, position.y, position.z, player);
        // 设置较短的引爆时间，增加危险性
        tnt.setFuse(40); // 2秒后爆炸，给飞行时间
        level.addFreshEntity(tnt);
        
        // 计算从钓鱼位置到玩家的方向向量
        Vec3 playerPos = player.position().add(0, 1, 0); // 玩家胸部高度
        Vec3 direction = playerPos.subtract(position).normalize();
        
        // 设置TNT朝向玩家飞行，模拟被钓上来的效果
        double speed = 0.8; // 飞行速度
        tnt.setDeltaMovement(
            direction.x * speed,
            Math.max(0.3, direction.y * speed + 0.2), // 确保有向上的分量
            direction.z * speed
        );
    }

    /**
     * 深水炸弹：在钓鱼位置生成敌对生物，并让它像被钓上来一样飞向玩家
     */
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
                    // 在钓鱼位置生成生物
                    monster.setPos(position.x, position.y, position.z);
                    monster.setTarget(player); // 让生物主动攻击玩家
                    level.addFreshEntity(monster);
                    
                    // 计算从钓鱼位置到玩家的方向向量
                    Vec3 playerPos = player.position().add(0, 1, 0); // 玩家胸部高度
                    Vec3 direction = playerPos.subtract(position).normalize();
                    
                    // 让生物朝向玩家飞行，模拟被钓上来的效果
                    double speed = 0.6; // 生物飞行速度稍慢一些
                    monster.setDeltaMovement(
                        direction.x * speed,
                        Math.max(0.2, direction.y * speed + 0.15), // 确保有向上的分量
                        direction.z * speed
                    );
                    
                    // 让生物在着陆后有短暂的无敌时间，避免摔死
                    monster.invulnerableTime = 20; // 1秒无敌时间
                }
            }
        } catch (Exception e) {
            RingOfTheHundredCurses.LOGGER.warn("Invalid mob in depth charge config: {}", randomMobId);
        }
    }
}