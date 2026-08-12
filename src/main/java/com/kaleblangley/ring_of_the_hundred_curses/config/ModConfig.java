package com.kaleblangley.ring_of_the_hundred_curses.config;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;


@Config(name = RingOfTheHundredCurses.MODID)
public class ModConfig implements ConfigData {
    /**
     * 命薄如纸：减少玩家的部分固定生命值
     * Fragile Life: Reduces a portion of the player's base health.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableFragileLife = true;
    @ConfigEntry.Gui.Tooltip
    public int reducesHealth = 10;

    /**
     * 世界为敌：全部生物对你有敌对性
     * The World Against You: All creatures become hostile towards you.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableWorldAgainst = true;
    @ConfigEntry.Gui.Tooltip
    public double entityAttackSpeed = 1.2d;
    @ConfigEntry.Gui.Tooltip
    public int entityAttackChange = 50;
    /**
     * Additional follow ranges in the format {@code entity_id=range}, for example
     * {@code minecraft:zombie=32}. Keeping this as strings makes it editable in
     * Cloth Config; Minecraft's Pair type has no no-argument constructor.
     */
    @ConfigEntry.Gui.Tooltip
    public String[] entityFollowRange = {};

    /**
     * 贪婪吃食：减少食物获得的饥饿值
     * Greedy Eating: Reduces the hunger restoration from food.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableGreedyEating = true;
    @ConfigEntry.Gui.Tooltip
    public float hungerReductionPercent = 0.5f;
    /**
     * 背包受限：堆叠上限减少
     * Backpack Limit: Reduces the stack limit of items.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableBackpackLimit = true;
    @ConfigEntry.Gui.Tooltip
    public int maxStackSize = 24;
    /**
     * 竭尽全力：武器必须满蓄力才能使用
     * Full Power: Weapons can only be used when fully charged.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableFullPower = true;

    /**
     * 盾牌在右：副手无法使用盾牌
     * Shield on the Right: Cannot use a shield in the off-hand.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableShieldOnTheRight = true;

    /**
     * 氧气不足：很多地方会造成缺氧
     * Lack of Oxygen: Suffocation effect occurs in many place.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableLackOfOxygen = true;
    @ConfigEntry.Gui.Tooltip
    public double minimumBreathY = 30.0d;
    @ConfigEntry.Gui.Tooltip
    public double maximumBreathY = 130.0d;
    @ConfigEntry.Gui.Tooltip
    public boolean endCanBreath = false;
    @ConfigEntry.Gui.Tooltip
    public boolean netherCanBreath = false;


    /**
     * 彻夜难眠：无法在床上睡觉
     * Sleepless Nights: Cannot sleep in beds.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableSleeplessNights = true;

    /**
     * 脆弱的胃：生肉有debuff
     * Weak Stomach: Raw meat gives debuffs.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableWeakStomach = true;
    @ConfigEntry.Gui.Tooltip
    public String rawMeatDebuffId = "minecraft:hunger";
    @ConfigEntry.Gui.Tooltip
    public int rawMeatDebuffDuration = 1200;
    @ConfigEntry.Gui.Tooltip
    public int rawMeatDebuffAmplifier = 1;

    /**
     * 手无寸铁：手不能撸坚硬的方块
     * Weaponless: Cannot mine hard blocks.
     *
     * @see tags/blocks/always_dig.json
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableWeaponless = true;

    /**
     * 星光大道：光照过低无法跑步
     * Starry Path: Cannot run when light levels are too low.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableStarryPath = true;
    @ConfigEntry.Gui.Tooltip
    public int starryPathMinimumLightLevel = 8; // 可以跑步的最低光照等级 / Minimum light level to allow sprinting

    /**
     * 末水禁令：末地不能放水
     * End Water Ban: Water cannot be placed in the End.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableEndWaterBan = true;

    /**
     * 贪婪锁链：宝箱中无法开出高级战利品
     * Greedy Lock: Chests cannot give high-level loot.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableGreedyLock = true;
    @ConfigEntry.Gui.Tooltip
    public String[] greedyLockJunkItems = { // 替换高级战利品的垃圾物品列表 / List of junk items to replace high-level loot
        "minecraft:cobblestone",
        "minecraft:dirt",
        "minecraft:gravel",
        "minecraft:sand",
        "minecraft:stick",
        "minecraft:rotten_flesh",
        "minecraft:bone",
        "minecraft:string",
        "minecraft:leather",
        "minecraft:feather"
    };
    @ConfigEntry.Gui.Tooltip
    public String[] greedyLockHighValueItems = { // 被认为是高级战利品的物品列表 / List of items considered high-value loot
        "minecraft:diamond",
        "minecraft:emerald",
        "minecraft:gold_ingot",
        "minecraft:iron_ingot",
        "minecraft:enchanted_book",
        "minecraft:diamond_sword",
        "minecraft:diamond_pickaxe",
        "minecraft:diamond_axe",
        "minecraft:diamond_shovel",
        "minecraft:diamond_hoe",
        "minecraft:diamond_helmet",
        "minecraft:diamond_chestplate",
        "minecraft:diamond_leggings",
        "minecraft:diamond_boots",
        "minecraft:golden_apple",
        "minecraft:enchanted_golden_apple",
        "minecraft:netherite_scrap",
        "minecraft:netherite_ingot",
        "minecraft:ancient_debris",
        "minecraft:totem_of_undying",
        "minecraft:elytra",
        "minecraft:shulker_shell",
        "minecraft:nether_star",
        "minecraft:dragon_egg",
        "minecraft:heart_of_the_sea",
        "minecraft:nautilus_shell"
    };

    /**
     * 沉重枷锁：装备重量降低移速
    * Heavy Shackles: Wearing equipment reduces movement speed.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableHeavyShackles = true;
    @ConfigEntry.Gui.Tooltip
    public double heavyShacklesMovementReduction = 0.25; // 移速降低比例 / Movement speed reduction ratio

    /**
     * 削弱打击：对敌人造成伤害更少
    * Weakened Strikes: Deals less damage to enemies.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableWeakenedStrikes = true;
    @ConfigEntry.Gui.Tooltip
    public float weakenedStrikesDamageMultiplier = 0.8f; // 对敌人造成的伤害倍率 / Damage multiplier against enemies

    /**
     * 灵魂压制：减少经验值的获取
     * Soul Suppression: Reduces experience point gain.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableSoulSuppression = true;
    @ConfigEntry.Gui.Tooltip
    public float xpReductionPercent = 0.5f;

    /**
     * 破碎之躯：摔落伤害无法被减缓
     * Fragile Body: Fall damage cannot be mitigated.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableFragileBody = true;

    /**
     * 脆弱护甲：减少护甲给予的盔甲值与韧性
    * Fragile Armor: Reduces armor and toughness given by armor.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableFragileArmor = true;
    @ConfigEntry.Gui.Tooltip
    public float fragileArmorMultiplier = 0.5f; // 护甲与韧性保留倍率 / Armor and toughness multiplier

    /**
     * 迟钝双手：降低挖掘速度
     * Sluggish Hands: Reduces mining speed.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableSluggishHands = true;
    @ConfigEntry.Gui.Tooltip
    public float multiplyRawSpeed = 0.5f;

    /**
     * 孤独之主：无法驯服生物
     * Lonely Master: Cannot tame mobs.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableLonelyMaster = true;

    /**
     * 迷失方向：地图和指南针将无法使用
     * Lost Direction: Maps and compasses become useless.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableLostDirection = true;

    /**
     * 弱魔体质：无法使用高等级附魔
     * Weak Magic Constitution: Cannot use high-level enchantments.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableWeakMagicConstitution = true;
    @ConfigEntry.Gui.Tooltip
    public int weakMagicConstitutionMaxLevel = 2;

    /**
     * 水性弱者：水中游泳速度减慢，溺水受伤按深度增加
     * Weak Swimmer: Reduces swimming speed, and drowning damage increases with depth.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableWeakSwimmer = true;
    @ConfigEntry.Gui.Tooltip
    public float weakSwimmerSwimSpeedMultiplier = 0.5f; // 水中游泳速度倍率 / Swimming speed multiplier in water
    @ConfigEntry.Gui.Tooltip
    public float weakSwimmerDrowningDamagePerBlock = 0.05f; // 每低于海平面一格的额外溺水伤害倍率 / Extra drowning multiplier per block below sea level
    @ConfigEntry.Gui.Tooltip
    public float weakSwimmerMaxDrowningMultiplier = 3.0f; // 溺水伤害倍率上限 / Maximum drowning damage multiplier

    /**
     * 食堂阿姨：重生锚每次充能填充数量随机
     * Cafeteria Lady: Respawn anchors charge with a random number of uses.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableCafeteriaLady = true;
    @ConfigEntry.Gui.Tooltip
    public int cafeteriaLadyMinCost = 1; // 最少消耗萤石数量 / Minimum glowstone cost per charge
    @ConfigEntry.Gui.Tooltip
    public int cafeteriaLadyMaxCost = 4; // 最多消耗萤石数量 / Maximum glowstone cost per charge

    /**
     * 无效题海：当玩家每天对同一种武器附魔次数过多后，附魔效果的等级下降
     * Endless Quiz: Enchanting the same weapon repeatedly reduces its enchantment level.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableEndlessQuiz = true;
    @ConfigEntry.Gui.Tooltip
    public int endlessQuizDailyLimit = 3;
    @ConfigEntry.Gui.Tooltip
    public int endlessQuizLevelReduction = 1;

    /**
     * 敌我不分：附有忠诚的三叉戟有一定概率会在返回时伤害玩家
     * Friend or Foe: A trident with Loyalty has a chance to damage the player when returning.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableFriendOrFoe = true;
    @ConfigEntry.Gui.Tooltip
    public double friendOrFoeChance = 0.3; // 返回时伤害玩家的概率 / Chance to damage the player when returning
    @ConfigEntry.Gui.Tooltip
    public float friendOrFoeDamage = 8.0f; // 造成的伤害值 / Damage dealt to the player

    /**
     * 无能窃贼：玩家在掠夺者营地与村庄打开战利品箱时会刷新掠夺者和村民
     * Incompetent Thief: Opening loot chests in Pillager Outposts or Villages spawns Pillagers and Villagers.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableIncompetentThief = true;
    @ConfigEntry.Gui.Tooltip
    public int incompetentThiefMinSpawn = 1; // 最少生成数量 / Minimum number of mobs to spawn
    @ConfigEntry.Gui.Tooltip
    public int incompetentThiefMaxSpawn = 3; // 最多生成数量 / Maximum number of mobs to spawn

    /**
     * 血肉失控：每一次受伤额外受到最大生命值部分百分比的伤害
    * Blood and Flesh: Each time you take damage, you receive additional damage based on a percentage of your maximum health.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableBloodAndFlesh = true;
    @ConfigEntry.Gui.Tooltip
    public float bloodAndFleshExtraDamagePercent = 0.1f; // 按最大生命值追加的伤害比例 / Extra damage as a percentage of max health

    /**
     * 集群意识：击杀怪物后，概率性会原地生成一个同样的怪物
     * Horde Mind: After killing a mob, there is a chance to spawn the same mob in place.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableHordeMind = true;
    @ConfigEntry.Gui.Tooltip
    public double hordeMindSpawnChance = 0.3; // 生成同样怪物的概率 / Chance to spawn the same mob

    /**
     * 专注失调：攻击，放置，制作物品概率失败
     * Focus Disturbance: Attacks, placements, and crafting have a chance to fail.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableFocusDisturbance = true;
    @ConfigEntry.Gui.Tooltip
    public double focusDisturbanceChance = 0.15;

    /**
     * 神经退行：重生后获得负面效果
     * Neurological Degeneration: Negative effects are applied after respawning.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableNeurologicalDegeneration = true;
    @ConfigEntry.Gui.Tooltip
    public int neurologicalDegenerationMinAmplifier = 0; // 最小等级 / Minimum amplifier level
    @ConfigEntry.Gui.Tooltip
    public int neurologicalDegenerationMaxAmplifier = 1; // 最大等级 / Maximum amplifier level
    @ConfigEntry.Gui.Tooltip
    public int neurologicalDegenerationMinDuration = 400; // 最小时长（tick）/ Minimum duration in ticks (60 seconds)
    @ConfigEntry.Gui.Tooltip
    public int neurologicalDegenerationMaxDuration = 800; // 最大时长（tick）/ Maximum duration in ticks (180 seconds)

    /**
     * 幻翼赠礼：每晚必刷新幻翼，幻翼会空投怪物
     * Phantom Gift: A phantom is spawned every night and drops hostile mobs.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enablePhantomGift = true;
    @ConfigEntry.Gui.Tooltip
    public int phantomGiftAirdropHeight = 12;
    @ConfigEntry.Gui.Tooltip
    public int phantomGiftAirdropCount = 1;
    @ConfigEntry.Gui.Tooltip
    public int phantomGiftAirdropInterval = 200;
    @ConfigEntry.Gui.Tooltip
    public String[] phantomGiftAirdropMobs = {
            "minecraft:zombie",
            "minecraft:skeleton",
            "minecraft:spider",
            "minecraft:creeper"
    };

    /**
     * 社会悖论：村民交易价格提升
     * Social Paradox: Villager trade prices are higher.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableSocialParadox = true;
    @ConfigEntry.Gui.Tooltip
    public float socialParadoxPriceIncreaseRatio = 0.5f; // 价格额外增加比例 / Extra villager trade price ratio

    /**
     * 气压失序：进入过高或过低的位置，导致移动和跳跃能力减弱
     * Pressure Disorder: Moving and jumping abilities are reduced when in high or low altitudes.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enablePressureDisorder = true;
    @ConfigEntry.Gui.Tooltip
    public double pressureDisorderLowY = 0.0; // 低于此Y值开始触发 / Effect starts below this Y level
    @ConfigEntry.Gui.Tooltip
    public double pressureDisorderHighY = 200.0; // 高于此Y值开始触发 / Effect starts above this Y level
    @ConfigEntry.Gui.Tooltip
    public double pressureDisorderMaxSpeedReduction = 0.5; // 最大移速降低比例 / Maximum movement speed reduction ratio
    @ConfigEntry.Gui.Tooltip
    public double pressureDisorderMaxGravityIncrease = 1.0; // 最大重力增加比例（影响跳跃高度）/ Maximum gravity increase ratio (affects jump height)
    @ConfigEntry.Gui.Tooltip
    public double pressureDisorderRange = 64.0; // 从阈值到最大效果的距离 / Distance from threshold to reach maximum effect

    /**
     * 趋同缺失：生命值降低时同时导致攻击力降低
     * Loss of Synchronicity: Lower health also reduces attack power.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableLossOfSynchronicity = true;

    /**
     * 血肉崩溃：以部分的生命值，饥饿值重生
     * Flesh Collapse: Respawn with a portion of your health and hunger.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableFleshCollapse = true;
    @ConfigEntry.Gui.Tooltip
    public float fleshCollapseHealthPercent = 0.5f;
    @ConfigEntry.Gui.Tooltip
    public float fleshCollapseHungerPercent = 0.5f;

    /**
     * 恢复缓慢：你不能自然回血
     * Slow Recovery: You cannot naturally regenerate health.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableSlowRecovery = true;

    /**
     * 羸弱之墙：长按使用盾牌时持续消耗耐久
     * Weak Wall: Shield loses durability while being used continuously.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableWeakWall = true;
    @ConfigEntry.Gui.Tooltip
    public int shieldDurabilityDrainInterval = 20; // 每隔多少tick消耗一次耐久 / Durability drain interval in ticks
    @ConfigEntry.Gui.Tooltip
    public int shieldDurabilityDrainAmount = 1; // 每次消耗的耐久值 / Durability amount consumed each time

    /**
     * 再生禁令：你获得的瞬间恢复效果会延迟生效
     * Regeneration Ban: Instant healing effects you receive will be delayed.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableRegenerationBan = true;
    @ConfigEntry.Gui.Tooltip
    public int regenerationBanDelay = 40;
    @ConfigEntry.Gui.Tooltip
    public float regenerationBanMinimumAmount = 2.0f;

    /**
     * 药效冲突：你每获得一个正面buff，缩短你身上已有的buff的时长，并延长你身上debuff的时长
     * Potion Conflicts: Each time you gain a positive buff, existing buffs shorten while debuffs are extended.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enablePotionConflicts = true;
    @ConfigEntry.Gui.Tooltip
    public float potionConflictsBeneficialDurationMultiplier = 0.5f;
    @ConfigEntry.Gui.Tooltip
    public float potionConflictsHarmfulDurationMultiplier = 1.5f;

    /**
     * 无处藏身：怪物的近战攻击可以穿透方块进行判定
     * No Shelter: Mobs' melee attacks can hit through blocks.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableNoShelter = true;
    @ConfigEntry.Gui.Tooltip
    public double noShelterExtraReach = 2.0; // 额外近战攻击距离（方块），2.0可穿透1-2格厚的墙 / Extra melee reach in blocks, 2.0 penetrates 1-2 block walls

    /**
     * 霉运附体：挖掘矿物时时运附魔概率不生效
     * Bad Luck: The chance of fortune enchantment not triggering while mining is increased.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableBadLuck = true;
    @ConfigEntry.Gui.Tooltip
    public double badLuckChance = 0.5; // 时运不生效的概率 / Chance for Fortune to not apply

    /**
     * 时空破缺：你每次投出末影珍珠都会刷出末影螨来咬你
     * Time Rift: Every time you throw an Ender Pearl, Endermites spawn to attack you.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableTimeRift = true;

    /**
     * 时空紊乱：通过传送门时有概率被传送到目标的任意附近位置
     * Time Distortion: There is a chance to be teleported to a random location near the target when using a portal.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableTimeDistortion = true;
    @ConfigEntry.Gui.Tooltip
    public double timeDistortionChance = 0.5; // 传送偏移的概率 / Chance of being teleported to a random nearby location
    @ConfigEntry.Gui.Tooltip
    public int timeDistortionMaxOffset = 64;  // 最大偏移距离（方块）/ Maximum offset distance in blocks

    /**
     * 海神遗弃：钓上来的是垃圾概率大幅度提高
     * Abandoned by Poseidon: The chance of fishing up junk is significantly increased.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableAbandonedByPoseidon = true;
    @ConfigEntry.Gui.Tooltip
    public float abandonedByPoseidonLuckPenalty = 40.0f; // 降低的幸运值（越高垃圾越多，40≈42%垃圾率）/ Luck penalty applied (higher = more junk, 40 ≈ 42% junk rate)
    
    /**
     * 深水炸弹：钓鱼有概率钓上来坏东西
     * Depth Charge: Fishing has a chance to catch something bad.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableDepthCharge = true;
    @ConfigEntry.Gui.Tooltip
    public double depthChargeTntChance = 0.05; // 钓上TNT的概率 / Chance to fish up TNT
    @ConfigEntry.Gui.Tooltip
    public double depthChargeMobChance = 0.08; // 钓上敌对生物的概率 / Chance to fish up hostile mobs
    @ConfigEntry.Gui.Tooltip
    public String[] depthChargeHostileMobs = { // 可能钓上的敌对生物列表 / List of hostile mobs that can be fished up
        "minecraft:zombie",
        "minecraft:skeleton", 
        "minecraft:spider",
        "minecraft:creeper",
        "minecraft:enderman",
        "minecraft:witch",
        "minecraft:drowned"
    };

    /**
     * 描边大师：弓有更大的偏转角
     * Outline Master: Archery has a larger arrow deviation.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableOutlineMaster = true;
    @ConfigEntry.Gui.Tooltip
    public float outlineMasterInaccuracyIncrease = 0.80f; // 增加的偏转角度

    /**
     * 复生怒火：每次打boss死了后boss不仅回满血还会变得更强
     * Reborn Wrath: Every time you die to a boss, the boss not only recovers full health but becomes stronger.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableRebornWrath = true;
    @ConfigEntry.Gui.Tooltip
    public int rebornWrathMaxStacks = 8;
    @ConfigEntry.Gui.Tooltip
    public float rebornWrathHealthBonusPerDeath = 0.2f;
    @ConfigEntry.Gui.Tooltip
    public float rebornWrathAttackBonusPerDeath = 0.15f;
    @ConfigEntry.Gui.Tooltip
    public String[] rebornWrathBossTargets = {
        "#forge:bosses",
        "minecraft:ender_dragon",
        "minecraft:wither"
    };

    /**
     * 速行之困：跑步消耗氧气
     * Oxygen Deprivation: Running depletes oxygen.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableOxygenDeprivation = true;
    @ConfigEntry.Gui.Tooltip
    public int sprintingAirConsumption = 2; // 跑步时每次呼吸事件消耗的氧气量 / Air consumption per breath event while sprinting

    /**
     * 蛀虫秘藏：挖掘原石有概率出蠹虫
     * Worm Hoard: Mining stone has a chance to spawn Silverfish.
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableWormHoard = true;
    @ConfigEntry.Gui.Tooltip
    public double wormHoardSpawnChance = 0.05; // 挖掘石头时蠹虫生成概率 / Chance to spawn silverfish when mining stone
    @ConfigEntry.Gui.Tooltip
    public int wormHoardMaxSilverfish = 1; // 最多生成蠹虫数量 / Maximum number of silverfish to spawn
    @ConfigEntry.Gui.Tooltip
    public String[] wormHoardTargetBlocks = {
        "minecraft:stone",
        "#forge:stone"
    };

    /**
     * 脆弱身躯：玩家的无敌帧减少
     * Hypocrisy Body: Reduces player's invincibility frames
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableHypocrisyBody = true;
    @ConfigEntry.Gui.Tooltip
    public int fragileBodyMaxInvulnerableTime = 5; // 无敌帧上限（原版为20tick） / Max invulnerable ticks after damage (vanilla is 20)

    /**
     * 贪婪之书：附魔台需要的经验翻倍
     * Greedy Tome: Doubles the experience cost for the enchantment table
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableGreedyTome = true;
    @ConfigEntry.Gui.Tooltip
    public double greedyTomeCostMultiplier = 2.0; // 附魔经验消耗倍率 / Enchantment experience cost multiplier

    /**
     * 打磨损耗：砂轮会折损耐久值
     * Grinding Wear: Grindstone consumes durability
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableGrindingWear = true;
    @ConfigEntry.Gui.Tooltip
    public double grindingWearDurabilityLossPercent = 0.1; // 砂轮折损耐久百分比 / Percentage of max durability lost when using grindstone

    /**
     * 水中束缚：下水减速
     * Water Shackles: Slows movement speed in water
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableWaterShackles = true;
    @ConfigEntry.Gui.Tooltip
    public double waterShacklesSlowdown = 0.5; // 水中移速降低比例，0.5=降低50% / Movement speed reduction ratio in water, 0.5 = 50% reduction

    /**
     * 失足之险：潜行不能阻止走到边缘时坠落
     * Misstep Peril: Sneaking no longer prevents falling off edges
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableMisstepPeril = true;

    /**
     * 爱好奢靡：只有打上盔甲纹饰的盔甲才能被使用
     * Lavish Taste: Only armor with trims can be worn
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableLavishTaste = true;

    /**
     * 碎碎平安：末影之眼一定会碎
     * Shattered Eye: Ender Eyes always break upon use
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableShatteredEye = true;

    /**
     * 龙阳之好：末影龙释放龙息时，玩家脚下一定会出现龙息
     * Draconic Favor: When the Ender Dragon breathes fire, a dragon breath area appears under the player's feet
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableDraconicFavor = true;

    /**
     * 无良卖家：猪灵有概率会取走黄金却不以物易物
     * Unfair Trader: Piglins may take gold without bartering
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableUnfairTrader = true;
    @ConfigEntry.Gui.Tooltip
    public double unfairTraderChance = 0.6; // 猪灵取走黄金不以物易物的概率 / Chance for piglins to take gold without bartering

    /**
     * 被刺高手：玩家潜行时，受到的伤害翻倍
     * Exposed Weakness: Player takes more damage while sneaking
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableExposedWeakness = true;
    @ConfigEntry.Gui.Tooltip
    public float exposedWeaknessDamageMultiplier = 2.0f; // 潜行时受到伤害的倍率 / Damage multiplier when sneaking

    /**
     * 师出有名：玩家不能攻击未攻击自己的生物
     * Justified Combat: Player cannot attack non-hostile creatures first
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableJustifiedCombat = true;

    /**
     * 劲霸男装：吃东西有后摇，在此期间不能进行操作
     * Food Coma: Eating has a delay, during which no actions can be performed
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableFoodComa = true;
    @ConfigEntry.Gui.Tooltip
    public int foodComaDuration = 40;

    /**
     * 颗粒无收：作物的掉落概率改为掉落枯萎的灌木
     * Barren Harvest: Crops now have a chance to drop dead bushes instead
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableBarrenHarvest = true;
    @ConfigEntry.Gui.Tooltip
    public double barrenHarvestChance = 0.2; // 作物掉落枯萎灌木的概率 / Chance for crops to drop dead bushes instead

    /**
     * 滑雪冒险：在寒冷群系，所有方块上施加冰块效果
     * Slippery Adventure: All blocks in cold biomes apply ice physics
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableSlipperyAdventure = true;
    @ConfigEntry.Gui.Tooltip
    public float slipperyAdventureFriction = 0.98f; // 寒冷群系摩擦力（0.6=普通, 0.98=冰, 0.989=蓝冰）/ Friction in cold biomes (0.6=normal, 0.98=ice, 0.989=blue ice)

    /**
     * 疯狂植物：会被植物扎
     * Hostile Flora: Plants can deal damage to the player
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableHostileFlora = true;
    @ConfigEntry.Gui.Tooltip
    public float hostileFloraDamage = 1.0f; // 植物造成的伤害值 / Damage dealt by plants

    /**
     * 深海纠缠：游泳有时间限制，时间过后会下沉
     * Deep Sea Entanglement: Swimming has a time limit; after which the player starts sinking
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableDeepSeaEntanglement = true;
    @ConfigEntry.Gui.Tooltip
    public double deepSeaEntanglementSinkingSpeed = 0.12; // Downward speed after the swimming time is exhausted
    @ConfigEntry.Gui.Tooltip
    public int deepSeaEntanglementSwimTime = 10; // 可以游泳的时间（秒）/ Swimming time limit in seconds
    @ConfigEntry.Gui.Tooltip
    public int deepSeaEntanglementRecoverTime = 10; // 离开水后恢复所需时间（秒）/ Recovery time after leaving water in seconds

    /**
     * 均衡饮食：某一食物食用过多会减少饥饿值回复
     * Balanced Diet: Overconsuming a single type of food reduces its hunger restoration
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableBalancedDiet = true;
    @ConfigEntry.Gui.Tooltip
    public int balancedDietThreshold = 5; // 同一食物食用多少次后开始减少回复 / Number of times eating the same food before penalty starts
    @ConfigEntry.Gui.Tooltip
    public float balancedDietReductionPerExtra = 0.15f; // 每多吃一次额外减少的回复比例 / Nutrition reduction ratio per extra eat beyond threshold
    @ConfigEntry.Gui.Tooltip
    public float balancedDietMaxReduction = 0.8f; // 最大减少比例（0.8=最多只回复20%）/ Maximum reduction ratio (0.8 = at most 20% nutrition remains)

    /**
     * 狼狈为奸：附近对玩家有仇恨的生物越多，玩家受到的伤害越高
     * United Adversaries: The more hostile mobs nearby, the more damage the player takes
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableUnitedAdversaries = true;
    @ConfigEntry.Gui.Tooltip
    public double unitedAdversariesRange = 16.0;
    @ConfigEntry.Gui.Tooltip
    public float unitedAdversariesPerMobBonus = 0.05f;
    @ConfigEntry.Gui.Tooltip
    public float unitedAdversariesMaxBonus = 1.0f;

    /**
     * 创伤开口：受到伤害概率持续掉血
     * Bleeding Wound: Taking damage has a chance to cause continuous health loss
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableBleedingWound = true;
    @ConfigEntry.Gui.Tooltip
    public double bleedingWoundChance = 0.2; // 受伤后触发持续掉血的概率 / Chance to trigger bleeding after taking damage
    @ConfigEntry.Gui.Tooltip
    public int bleedingWoundDuration = 100; // 持续时间（tick）/ Bleeding duration in ticks
    @ConfigEntry.Gui.Tooltip
    public int bleedingWoundInterval = 20; // 掉血间隔（tick）/ Damage interval in ticks
    @ConfigEntry.Gui.Tooltip
    public float bleedingWoundDamage = 1.0f; // 每次掉血伤害 / Damage per bleeding tick

    /**
     * 饥荒盛世：饱食度低于或高于一定值都会获得debuff
     * Feast or Famine: Having too low or too high saturation applies debuffs
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableFeastOrFamine = true;
    @ConfigEntry.Gui.Tooltip
    public int feastOrFamineLowFoodLevel = 6; // 低饥饿阈值 / Low food threshold
    @ConfigEntry.Gui.Tooltip
    public int feastOrFamineHighFoodLevel = 18; // 高饥饿阈值 / High food threshold
    @ConfigEntry.Gui.Tooltip
    public float feastOrFamineLowSaturation = 1.0f; // 低饱和度阈值 / Low saturation threshold
    @ConfigEntry.Gui.Tooltip
    public float feastOrFamineHighSaturation = 10.0f; // 高饱和度阈值 / High saturation threshold
    @ConfigEntry.Gui.Tooltip
    public int feastOrFamineDebuffDuration = 40; // 负面效果持续时间（tick）/ Debuff duration in ticks
    @ConfigEntry.Gui.Tooltip
    public int feastOrFamineDebuffAmplifier = 0; // 负面效果等级 / Debuff amplifier

    /**
     * 肺纤维化：氧气值减少
     * Pulmonary Fibrosis: Reduces maximum oxygen
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enablePulmonaryFibrosis = true;
    @ConfigEntry.Gui.Tooltip
    public int pulmonaryFibrosisAirReduction = 150; // 减少的氧气值，默认减少150（相当于5个气泡）/ Air reduction amount, default 150 (equivalent to 5 bubbles)

    /**
     * 以理服人：交易生物血量越高价格越贵
     * Bargaining Power: The higher the health of a trading entity, the more expensive its trades
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableBargainingPower = true;
    @ConfigEntry.Gui.Tooltip
    public float bargainingPowerPriceMultiplier = 1.0f;
    @ConfigEntry.Gui.Tooltip
    public int bargainingPowerMaxExtraCost = 64;

    /**
     * 饥肠辘辘：减少饱食度上限
     * Hollow Stomach: Reduces maximum saturation
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableHollowStomach = true;
    @ConfigEntry.Gui.Tooltip
    public int hollowStomachMaxHunger = 15; // 饥肠辘辘状态下的最大饥饿值上限 / Maximum hunger level under hollow stomach curse

    /**
     * 远域挡招：为任意添加限伤和远程保护机制
     * Distant Deflection: Introduces universal damage cap and ranged damage resistance
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableDistantDeflection = true;
    @ConfigEntry.Gui.Tooltip
    public float distantDeflectionMaxDamage = 10.0f;
    @ConfigEntry.Gui.Tooltip
    public float distantDeflectionRangedDamageMultiplier = 0.5f;

    /**
     * 创伤应激：如果被一种生物打死了复活之后再遇见这种生物会有debuff
     * PTSD: If the player was previously killed by a mob, encountering it again causes a debuff
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enablePTSD = true;
    @ConfigEntry.Gui.Tooltip
    public int ptsdDebuffDuration = 200; // debuff持续时间（tick）/ Debuff duration in ticks
    @ConfigEntry.Gui.Tooltip
    public int ptsdDebuffAmplifier = 0; // debuff等级 / Debuff amplifier level
    @ConfigEntry.Gui.Tooltip
    public int ptsdMaxTraumaCount = 10; // 最多记录的创伤生物种类数量 / Maximum number of trauma mob types stored

    /**
     * 厄运诅咒：玩家造成的掉落物计算会多次判定，取更差的结果
     * Curse of Misfortune: The player's dropped items are calculated multiple times, taking the worst result
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableCurseOfMisfortune = true;
    @ConfigEntry.Gui.Tooltip
    public int curseOfMisfortuneRolls = 3;

    /**
     * 乳糖不耐：你无法通过牛奶解除负面效果
     * Lactose Intolerance: You cannot remove negative effects with milk
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableLactoseIntolerance = true;

    /**
     * 雷霆引誓：更高概率被雷击中
     * Thunderbound Oath: Significantly increases the chance of being struck by lightning
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableThunderboundOath = true;
    @ConfigEntry.Gui.Tooltip
    public int thunderboundOathRarity = 2000; // 每次区块tick雷击概率为1/此值，越小越频繁 / Lightning chance is 1/N per chunk tick, lower = more frequent

    /**
     * 熔岩献祭：处于下界被攻击会使自身着火
     * Lava Sacrifice: While in the Nether, being attacked will set you on fire
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableLavaSacrifice = true;
    @ConfigEntry.Gui.Tooltip
    public int lavaSacrificeFireDuration = 100; // 燃烧持续时间（tick），默认100tick=5秒 / Fire duration in ticks, default 100 ticks = 5 seconds

    /**
     * 破败战士：穿戴装备的耐久值越低，提供的护甲值越少
     * Dilapidated Warrior: The lower your equipment's durability, the less armor protection it provides
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableDilapidatedWarrior = true;

    /**
     * 掉包奸商：与村民交易完物品后概率被换成其他物品
     * Dodgy Merchant: After completing a trade with a villager, there's a chance the item you receive will be swapped for something else
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableDodgyMerchant = true;
    @ConfigEntry.Gui.Tooltip
    public double dodgyMerchantSwapChance = 0.15; // 掉包概率 / Chance to swap traded items
    @ConfigEntry.Gui.Tooltip
    public String[] dodgyMerchantJunkItems = { // 可替换的垃圾物品列表 / List of junk items for replacement
        "minecraft:rotten_flesh",
        "minecraft:bone",
        "minecraft:string",
        "minecraft:spider_eye",
        "minecraft:gunpowder",
        "minecraft:slime_ball",
        "minecraft:leather",
        "minecraft:feather",
        "minecraft:rabbit_hide",
        "minecraft:phantom_membrane",
        "minecraft:cobblestone",
        "minecraft:dirt",
        "minecraft:gravel",
        "minecraft:sand",
        "minecraft:wheat_seeds",
        "minecraft:beetroot_seeds",
        "minecraft:melon_seeds",
        "minecraft:pumpkin_seeds",
        "minecraft:poisonous_potato",
        "minecraft:dead_bush",
        "minecraft:stick",
        "minecraft:paper",
        "minecraft:clay_ball",
        "minecraft:brick"
    };

    /**
     * 新鲜武器：武器耐久越低伤害越低
     * Fresh Weapon: The lower a weapon's durability, the less damage it deals
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableFreshWeapon = true;

    /**
     * 迷糊工匠：合成的物品概率减少耐久或数量
     * Befuddled Artisan: Crafted items have a chance to spawn with reduced durability or in reduced quantity
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableBefuddledArtisan = true;
    @ConfigEntry.Gui.Tooltip
    public double befuddledArtisanChance = 0.3; // 触发概率 / Trigger chance
    @ConfigEntry.Gui.Tooltip
    public double befuddledArtisanDurabilityLossPercent = 0.5; // 最大耐久损失百分比 / Maximum durability loss percentage
    @ConfigEntry.Gui.Tooltip
    public double befuddledArtisanCountLossPercent = 0.5; // 最大数量减少百分比 / Maximum count reduction percentage

    /**
     * 溢出负重：玩家背包内存放的物品超过一定数量，移动速度会降低，摔落伤害增加
     * Overburdened: When your inventory holds too many items, your movement speed is reduced and fall damage is increased
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableOverburdened = true;
    @ConfigEntry.Gui.Tooltip
    public int overburdenedItemThreshold = 72; // 触发负重的物品数量 / Item count threshold
    @ConfigEntry.Gui.Tooltip
    public double overburdenedMovementReduction = 0.25; // 负重时移速降低比例 / Movement speed reduction while overburdened
    @ConfigEntry.Gui.Tooltip
    public float overburdenedFallDamageMultiplier = 1.5f; // 负重时摔落伤害倍率 / Fall damage multiplier while overburdened

    /**
     * 破裂之门：传送门是一次性的，使用了之后会炸裂
     * Shattered Portal: Portals become one-time use; they explode after being used
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableShatteredPortal = true;

    /**
     * 失衡武器：武器的攻击力在每次攻击时概率减少
     * Unbalanced Weapon: Each time you attack, there's a chance your weapon's attack power will decrease
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableUnbalancedWeapon = true;
    @ConfigEntry.Gui.Tooltip
    public double unbalancedWeaponChance = 0.15; // 攻击伤害降低的概率 / Chance to reduce attack damage
    @ConfigEntry.Gui.Tooltip
    public float unbalancedWeaponDamageMultiplier = 0.5f; // 触发时的伤害倍率 / Damage multiplier when triggered

    /**
     * 海关过境：村民的高级交易需要等待几个 Minecraft 日后才给你交易的物品
     * Customs Clearance: Advanced villager trades require waiting several Minecraft days before the items become available
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableCustomsClearance = true;
    @ConfigEntry.Gui.Tooltip
    public int customsClearanceMinLevel = 3; // 触发海关的最低村民等级（1=新手, 2=学徒, 3=老手, 4=专家, 5=大师） / Minimum villager level to trigger (1=Novice, 2=Apprentice, 3=Journeyman, 4=Expert, 5=Master)
    @ConfigEntry.Gui.Tooltip
    public int customsClearanceWaitDays = 3; // 交易物品延迟交付的天数 / Number of MC days to wait before delivery

    /**
     * 拔苗助长：作物施肥时概率降低生长阶段，施肥越多降低概率越大
     * Overzealous Growth: When fertilizing crops, there's a chance their growth stage will regress—and the more fertilizer you apply, the higher the chance
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableOverzealousGrowth = true;
    @ConfigEntry.Gui.Tooltip
    public double overzealousGrowthBaseChance = 0.1; // 基础降低概率 / Base chance to regress growth stage
    @ConfigEntry.Gui.Tooltip
    public double overzealousGrowthChanceIncrement = 0.15; // 每次施肥增加的降低概率 / Chance increment per fertilizer use
    @ConfigEntry.Gui.Tooltip
    public double overzealousGrowthMaxChance = 0.9; // 最大降低概率 / Maximum regress chance

    /**
     * 笨拙农夫：玩家在耕地上走有几率踩坏田
     * Clumsy Farmer: There's a chance to trample and break farmland when walking over tilled soil
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableClumsyFarmer = true;
    @ConfigEntry.Gui.Tooltip
    public double clumsyFarmerChance = 0.01; // 每tick踩坏耕地的概率 / Chance per tick to trample farmland

    /**
     * 海市蜃楼：概率看到远处出现结构，但并不是真的
     * Mirage: You may occasionally see distant structures that aren't actually there
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableMirage = true;
    @ConfigEntry.Gui.Tooltip
    public int mirageRefreshInterval = 600; // 新幻象的刷新间隔（tick）/ Refresh interval for additional mirages
    @ConfigEntry.Gui.Tooltip
    public int mirageMaxActiveInstances = 3; // 同时存在的幻象数量上限 / Maximum active mirages
    @ConfigEntry.Gui.Tooltip
    public double mirageMinimumSeparation = 32.0; // 幻象之间的最小分离距离 / Minimum distance between mirages
    @ConfigEntry.Gui.Tooltip
    public double mirageSpawnChance = 0.2; // 每次检查生成幻象的概率 / Chance to create a mirage on each check
    @ConfigEntry.Gui.Tooltip
    public int mirageCheckInterval = 100; // 检查间隔（tick）/ Search interval in ticks
    @ConfigEntry.Gui.Tooltip
    public double mirageSearchRadius = 192.0; // 只搜索已加载区块中的结构 / Search radius for loaded structures only
    @ConfigEntry.Gui.Tooltip
    public double miragePlacementMinDistance = 48.0; // 幻象最小距离 / Minimum mirage distance
    @ConfigEntry.Gui.Tooltip
    public double miragePlacementMaxDistance = 128.0; // 幻象最大距离 / Maximum mirage distance
    @ConfigEntry.Gui.Tooltip
    public double mirageAngleMinDegrees = 25.0; // 与真实结构方向的最小偏转角 / Minimum direction offset
    @ConfigEntry.Gui.Tooltip
    public double mirageAngleMaxDegrees = 50.0; // 与真实结构方向的最大偏转角 / Maximum direction offset
    @ConfigEntry.Gui.Tooltip
    public double mirageRetreatDistance = 32.0; // 每次后退的距离 / Distance added on each retreat
    @ConfigEntry.Gui.Tooltip
    public double mirageApproachDistance = 18.0; // 玩家接近幻象的触发距离 / Distance that triggers dissolving
    @ConfigEntry.Gui.Tooltip
    public int mirageMaxDuration = 1200; // 单个幻象最大持续时间（tick）/ Maximum lifetime in ticks
    @ConfigEntry.Gui.Tooltip
    public int mirageDissolveDuration = 30; // 溶解时间（tick）/ Dissolve duration in ticks
    @ConfigEntry.Gui.Tooltip
    public int mirageMaxRetreats = 3; // 最大后退次数 / Maximum retreat count
    @ConfigEntry.Gui.Tooltip
    public int mirageMaxRenderBlocks = 4096; // 单个模板最大渲染方块数 / Maximum blocks per template
    @ConfigEntry.Gui.Tooltip
    public double mirageRenderDistance = 256.0; // 客户端最大渲染距离 / Client render distance

    /**
     * 专事专办：工具只能干对应的事情
     * Single-Purpose Tools: Tools can only perform their designated function
     * @see tags/blocks/axe_dig.json
     * @see tags/blocks/hoe_dig.json
     * @see tags/blocks/pickaxe_dig.json
     * @see tags/blocks/shovel_dig.json
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableSinglePurposeTools = true;

    /**
     * 肌肉无力：使用盾牌抵挡伤害的时候概率会被破盾
     * Muscle Weakness: When blocking damage with a shield, there's a chance your shield will break
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableMuscleWeakness = true;
    @ConfigEntry.Gui.Tooltip
    public int muscleWeaknessChance = 30;

    /**
     * 溃烂饥饿：食物在背包中存放时间过长会变质成腐肉
     * Rotting Hunger: Food stored in your inventory for too long will spoil into rotten flesh
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableRottingHunger = true;
    @ConfigEntry.Gui.Tooltip
    public int rottingHungerExpireTime = 24000; // 食物保鲜时间（tick），默认24000=1个MC日 / Food freshness duration in ticks, default 24000 = 1 MC day

    /**
     * 过度治疗：使用治疗道具时，有概率使玩家短时间内无法再次治疗
     * Overhealing: Using a healing item may cause you to be unable to heal again for a short time
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableOverhealing = true;
    @ConfigEntry.Gui.Tooltip
    public double overhealingChance = 0.3;
    @ConfigEntry.Gui.Tooltip
    public int overhealingCooldown = 100;
    @ConfigEntry.Gui.Tooltip
    public float overhealingMinimumAmount = 2.0f;

    /**
     * 花样出招：玩家要轮流使用不同攻击手段才可对特定生物造成伤害
     * Patterned Assault: You must alternate different attack methods to damage certain creatures
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enablePatternedAssault = true;
    @ConfigEntry.Gui.Tooltip
    public String[] patternedAssaultTargets = {"#forge:bosses", "minecraft:warden"};
    @ConfigEntry.Gui.Tooltip
    public int patternedAssaultResetAfterTicks = 600;

    /**
     * 不亮之物：放置的火把，营火需要手动点燃
     * Unlit Objects: Placed torches and campfires must be manually lit
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableUnlitObjects = true;

    /**
     * 滑冰赛场：雨天方块阻力减小
     * Ice Rink: During rain, block friction is reduced, making surfaces slippery
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableIceRink = true;
    @ConfigEntry.Gui.Tooltip
    public float iceRinkFriction = 0.98f; // 冰的摩擦力值（0.6=普通, 0.98=冰, 0.989=蓝冰）/ Ice friction value (0.6=normal, 0.98=ice, 0.989=blue ice)

    /**
     * 糟糕厨师：饭菜概率变成迷之炖菜（debuff的）
     * Terrible Cook: Cooked meals have a chance to turn into mysterious stew that inflicts a debuff
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableTerribleCook = true;
    @ConfigEntry.Gui.Tooltip
    public double terribleCookChance = 0.2;
    /**
     * Randomly selected debuff IDs for the suspicious stew.
     */
    @ConfigEntry.Gui.Tooltip
    public String[] terribleCookDebuffIds = {};
    @ConfigEntry.Gui.Tooltip
    public int terribleCookDebuffDuration = 200;
    @ConfigEntry.Gui.Tooltip
    public double terribleCookPlayerRange = 16.0;

    /**
     * 震耳欲聋：不一定需要幽匿尖啸体才能召唤监守者
     * Deafening: You don't always need a Sculk Shrieker to summon the Warden
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableDeafening = true;
    @ConfigEntry.Gui.Tooltip
    public boolean deafeningOnlyInDeepDark = true;
    @ConfigEntry.Gui.Tooltip
    public double deafeningSpawnChance = 0.01;
    @ConfigEntry.Gui.Tooltip
    public int deafeningCheckInterval = 200;
    @ConfigEntry.Gui.Tooltip
    public double deafeningSearchRange = 32.0;

    /**
     * 结束时刻：末影龙出现会播放越来越好remix并添加后处理效果
     * Ending Moment: The appearance of the ender dragon will play better and better remixes and add post-processing effects
     */
    @ConfigEntry.Gui.Tooltip
    public boolean enableEndingMoment = true;
    @ConfigEntry.Gui.Tooltip
    public float endingMomentVolume = 1.0f;
    @ConfigEntry.Gui.Tooltip
    public float endingMomentBasePitch = 1.0f;
    @ConfigEntry.Gui.Tooltip
    public float endingMomentPitchIncrease = 0.1f;
    @ConfigEntry.Gui.Tooltip
    public float endingMomentMaxPitch = 2.0f;
    @ConfigEntry.Gui.Tooltip
    public double endingMomentRange = 128.0;
    @ConfigEntry.Gui.Tooltip
    public int endingMomentLoopInterval = 1200;
    @ConfigEntry.Gui.Tooltip
    public int endingMomentDarknessDuration = 80;

}
