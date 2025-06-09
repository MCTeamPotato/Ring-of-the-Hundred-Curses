package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModTag;

import java.util.List;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvent {
    
    private static final List<MobEffect> HARMFUL_EFFECTS;
    
    static {
        List<MobEffect> effects = new java.util.ArrayList<>();
        for (MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
            if (effect.getCategory() == MobEffectCategory.HARMFUL && 
                effect != MobEffects.HARM && effect != MobEffects.HEAL && effect != MobEffects.LEVITATION) {
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
            Player player = (Player) event.getEntity();
            if (RingUtil.configAndRing(player, getConfig().enableNeurologicalDegeneration)) {
                applyRandomHarmfulEffect(player);
            }
        }
    }

    @SubscribeEvent
    public static void wormHoardEffect(BlockEvent.BreakEvent event) {
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
        int amplifier = player.level().random.nextInt(3);
        int duration = 1200 + player.level().random.nextInt(2401);
        MobEffectInstance effectInstance = new MobEffectInstance(randomEffect, duration, amplifier);
        player.addEffect(effectInstance);
    }

    private static boolean isWormHoardTarget(Block block) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
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
                } catch (Exception e) {
                    continue;
                }
            } else {
                if (target.equals(blockIdString)) {
                    return true;
                }
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
                if (!level.getBlockState(testPos).isCollisionShapeFullBlock(level, testPos) && 
                    !level.getBlockState(testPos.above()).isCollisionShapeFullBlock(level, testPos.above())) {
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
} 