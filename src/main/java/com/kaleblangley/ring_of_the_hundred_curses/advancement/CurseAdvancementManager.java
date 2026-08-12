package com.kaleblangley.ring_of_the_hundred_curses.advancement;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfig;
import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.lang.reflect.Field;
import java.util.Set;

/** Awards the one-time advancement associated with a curse when that curse is first used. */
public final class CurseAdvancementManager {
    public static final String CRITERION = "curse_triggered";
    private static final String ADVANCEMENT_PREFIX = "curses/";

    private static final Set<String> CURSE_IDS = Set.of(
            "fragile_life", "world_against", "greedy_eating", "backpack_limit", "full_power",
            "shield_on_the_right", "lack_of_oxygen", "sleepless_nights", "weak_stomach", "weaponless",
            "starry_path", "end_water_ban", "greedy_lock", "heavy_shackles", "weakened_strikes",
            "soul_suppression", "fragile_body", "fragile_armor", "sluggish_hands", "lonely_master",
            "lost_direction", "weak_magic_constitution", "weak_swimmer", "cafeteria_lady", "endless_quiz",
            "friend_or_foe", "incompetent_thief", "blood_and_flesh", "horde_mind", "focus_disturbance",
            "neurological_degeneration", "phantom_gift", "social_paradox", "pressure_disorder",
            "loss_of_synchronicity", "flesh_collapse", "slow_recovery", "weak_wall", "regeneration_ban",
            "potion_conflicts", "no_shelter", "bad_luck", "time_rift", "time_distortion",
            "abandoned_by_poseidon", "depth_charge", "outline_master", "reborn_wrath", "oxygen_deprivation",
            "worm_hoard", "hypocrisy_body", "greedy_tome", "grinding_wear", "water_shackles",
            "misstep_peril", "lavish_taste", "shattered_eye", "draconic_favor", "unfair_trader",
            "exposed_weakness", "justified_combat", "food_coma", "barren_harvest", "slippery_adventure",
            "hostile_flora", "deep_sea_entanglement", "balanced_diet", "united_adversaries", "bleeding_wound",
            "feast_or_famine", "pulmonary_fibrosis", "bargaining_power", "hollow_stomach", "distant_deflection",
            "ptsd", "curse_of_misfortune", "lactose_intolerance", "thunderbound_oath", "lava_sacrifice",
            "dilapidated_warrior", "dodgy_merchant", "fresh_weapon", "befuddled_artisan", "overburdened",
            "shattered_portal", "unbalanced_weapon", "customs_clearance", "overzealous_growth", "clumsy_farmer",
            "mirage", "single_purpose_tools", "muscle_weakness", "rotting_hunger", "overhealing",
            "patterned_assault", "unlit_objects", "ice_rink", "terrible_cook", "deafening", "ending_moment"
    );

    private CurseAdvancementManager() {
    }

    public static void trigger(LivingEntity entity, String curseId) {
        if (!isKnownCurse(curseId)) return;
        if (entity instanceof ServerPlayer serverPlayer) {
            award(serverPlayer, curseId);
            return;
        }
        if (entity.level().isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    com.kaleblangley.ring_of_the_hundred_curses.client.advancement.CurseAdvancementClient.request(curseId));
        }
    }

    public static void award(ServerPlayer player, String curseId) {
        if (!isKnownCurse(curseId)) return;
        Advancement advancement = player.server.getAdvancements().getAdvancement(
                new ResourceLocation(RingOfTheHundredCurses.MODID, ADVANCEMENT_PREFIX + curseId)
        );
        if (advancement != null) {
            player.getAdvancements().award(advancement, CRITERION);
        }
    }

    public static boolean isKnownCurse(String curseId) {
        return curseId != null && CURSE_IDS.contains(curseId);
    }

    /** Used for client requests so a client cannot award an unknown or disabled curse. */
    public static boolean isEnabled(String curseId) {
        if (!isKnownCurse(curseId)) return false;
        String fieldName = "enable" + toConfigSuffix(curseId);
        try {
            Field field = ModConfig.class.getField(fieldName);
            return field.getBoolean(ModConfigManager.getConfig());
        } catch (ReflectiveOperationException exception) {
            RingOfTheHundredCurses.LOGGER.warn("Could not read curse config for advancement: {}", curseId, exception);
            return false;
        }
    }

    private static String toConfigSuffix(String curseId) {
        if (curseId.equals("ptsd")) return "PTSD";
        StringBuilder suffix = new StringBuilder();
        boolean upperNext = true;
        for (int index = 0; index < curseId.length(); index++) {
            char character = curseId.charAt(index);
            if (character == '_') {
                upperNext = true;
            } else {
                suffix.append(upperNext ? Character.toUpperCase(character) : character);
                upperNext = false;
            }
        }
        return suffix.toString();
    }
}
