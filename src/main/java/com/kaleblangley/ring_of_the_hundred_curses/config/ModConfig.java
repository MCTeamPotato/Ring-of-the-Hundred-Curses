package com.kaleblangley.ring_of_the_hundred_curses.config;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.mojang.datafixers.util.Pair;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.List;

@Config(name = RingOfTheHundredCurses.MODID)
public class ModConfig implements ConfigData {
    /**
     * 命薄如纸：减少玩家的部分固定生命值
     * Fragile Life: Reduces a portion of the player's base health.
     */
    public boolean enableFragileLife = true;
    public int reducesHealth = 10;

    /**
     * 世界为敌：全部生物对你有敌对性
     * The World Against You: All creatures become hostile towards you.
     */
    public boolean enableWorldAgainst = true;
    public double entityAttackSpeed = 1.2d;
    public int entityAttackChange = 50;
    public Pair<String, Double>[] entityFollowRange = new Pair[]{};

    /**
     * 贪婪吃食：减少食物获得的饥饿值
     * Greedy Eating: Reduces the hunger restoration from food.
     */
    public boolean enableGreedyEating = true;
    public float hungerReductionPercent = 0.5f;
    /**
     * 背包受限：堆叠上限减少
     * Backpack Limit: Reduces the stack limit of items.
     */
    public boolean enableBackpackLimit = true;
    public int maxStackSize = 24;
    /**
     * 竭尽全力：武器必须满蓄力才能使用
     * Full Power: Weapons can only be used when fully charged.
     */
    public boolean enableFullPower = true;

    /**
     * 盾牌在右：副手无法使用盾牌
     * Shield on the Right: Cannot use a shield in the off-hand.
     */
    public boolean enableShieldOnTheRight = true;

    /**
     * 氧气不足：很多地方会造成缺氧
     * Lack of Oxygen: Suffocation effect occurs in many place.
     */
    public boolean enableLackOfOxygen = true;
    public double minimumBreathY = 30.0d;
    public double maximumBreathY = 130.0d;
    public boolean endCanBreath = false;
    public boolean netherCanBreath = false;


    /**
     * 彻夜难眠：无法在床上睡觉
     * Sleepless Nights: Cannot sleep in beds.
     */
    public boolean enableSleeplessNights = true;

    /**
     * 脆弱的胃：生肉有debuff
     * Weak Stomach: Raw meat gives debuffs.
     */
    public boolean enableWeakStomach = true;
    public String rawMeatDebuffId = "minecraft:hunger";
    public int rawMeatDebuffDuration = 1200;
    public int rawMeatDebuffAmplifier = 1;

    /**
     * 手无寸铁：手不能撸坚硬的方块
     * Weaponless: Cannot mine hard blocks.
     *
     * @see tags/blocks/always_dig.json
     */
    public boolean enableWeaponless = true;

    /**
     * 星光大道：光照过低无法跑步
     * Starry Path: Cannot run when light levels are too low.
     */
    public boolean enableStarryPath = true;

    /**
     * 末水禁令：末地不能放水
     * End Water Ban: Water cannot be placed in the End.
     */
    public boolean enableEndWaterBan = true;

    /**
     * 贪婪锁链：宝箱中无法开出高级战利品
     * Greedy Lock: Chests cannot give high-level loot.
     */
    public boolean enableGreedyLock = true;

    /**
     * 沉重枷锁：装备重量降低移速
     * Heavy Shackles: Wearing equipment reduces movement speed.
     */
    public boolean enableHeavyShackles = true;

    /**
     * 削弱打击：对敌人造成伤害更少
     * Weakened Strikes: Deals less damage to enemies.
     */
    public boolean enableWeakenedStrikes = true;

    /**
     * 灵魂压制：减少经验值的获取
     * Soul Suppression: Reduces experience point gain.
     */
    public boolean enableSoulSuppression = true;

    /**
     * 破碎之躯：摔落伤害无法被减缓
     * Fragile Body: Fall damage cannot be mitigated.
     */
    public boolean enableFragileBody = true;

    /**
     * 脆弱护甲：减少护甲给予的盔甲值与韧性
     * Fragile Armor: Reduces armor and toughness given by armor.
     */
    public boolean enableFragileArmor = true;

    /**
     * 迟钝双手：降低挖掘速度
     * Sluggish Hands: Reduces mining speed.
     */
    public boolean enableSluggishHands = true;
    public float multiplyRawSpeed = 0.5f;

    /**
     * 孤独之主：无法驯服生物
     * Lonely Master: Cannot tame mobs.
     */
    public boolean enableLonelyMaster = true;

    /**
     * 迷失方向：地图和指南针将无法使用
     * Lost Direction: Maps and compasses become useless.
     */
    public boolean enableLostDirection = true;

    /**
     * 弱魔体质：无法使用高等级附魔
     * Weak Magic Constitution: Cannot use high-level enchantments.
     */
    public boolean enableWeakMagicConstitution = true;

    /**
     * 水性弱者：水中游泳速度减慢，溺水受伤按深度增加
     * Weak Swimmer: Reduces swimming speed, and drowning damage increases with depth.
     */
    public boolean enableWeakSwimmer = true;

    /**
     * 食堂阿姨：重生锚每次充能填充数量随机
     * Cafeteria Lady: Respawn anchors charge with a random number of uses.
     */
    public boolean enableCafeteriaLady = true;

    /**
     * 无效题海：当玩家每天对同一种武器附魔次数过多后，附魔效果的等级下降
     * Endless Quiz: Enchanting the same weapon repeatedly reduces its enchantment level.
     */
    public boolean enableEndlessQuiz = true;

    /**
     * 敌我不分：附有忠诚的三叉戟有一定概率会在返回时伤害玩家
     * Friend or Foe: A trident with Loyalty has a chance to damage the player when returning.
     */
    public boolean enableFriendOrFoe = true;

    /**
     * 无能窃贼：玩家在掠夺者营地与村庄打开战利品箱时会刷新掠夺者和村民
     * Incompetent Thief: Opening loot chests in Pillager Outposts or Villages spawns Pillagers and Villagers.
     */
    public boolean enableIncompetentThief = true;

    /**
     * 血肉失控：每一次受伤额外受到最大生命值部分百分比的伤害
     * Blood and Flesh: Each time you take damage, you receive additional damage based on a percentage of your maximum health.
     */
    public boolean enableBloodAndFlesh = true;

    /**
     * 集群意识：击杀怪物后，概率性会原地生成一个同样的怪物
     * Horde Mind: After killing a mob, there is a chance to spawn the same mob in place.
     */
    public boolean enableHordeMind = true;

    /**
     * 专注失调：攻击，放置，制作物品概率失败
     * Focus Disturbance: Attacks, placements, and crafting have a chance to fail.
     */
    public boolean enableFocusDisturbance = true;

    /**
     * 神经退行：重生后获得负面效果
     * Neurological Degeneration: Negative effects are applied after respawning.
     */
    public boolean enableNeurologicalDegeneration = true;

    /**
     * 恐怖实体：敌人的生命值有概率提升
     * Horrific Entity: Enemies may have a chance to gain extra health.
     */
    public boolean enableHorrificEntity = true;

    /**
     * 社会悖论：村民交易价格提升
     * Social Paradox: Villager trade prices are higher.
     */
    public boolean enableSocialParadox = true;

    /**
     * 气压失序：进入过高或过低的位置，导致移动和跳跃能力减弱
     * Pressure Disorder: Moving and jumping abilities are reduced when in high or low altitudes.
     */
    public boolean enablePressureDisorder = true;

    /**
     * 趋同缺失：生命值降低时同时导致攻击力降低
     * Loss of Synchronicity: Lower health also reduces attack power.
     */
    public boolean enableLossOfSynchronicity = true;

    /**
     * 血肉崩溃：以部分的生命值，饥饿值重生
     * Flesh Collapse: Respawn with a portion of your health and hunger.
     */
    public boolean enableFleshCollapse = true;

    /**
     * 恢复缓慢：你不能自然回血
     * Slow Recovery: You cannot naturally regenerate health.
     */
    public boolean enableSlowRecovery = true;

    /**
     * 羸弱之墙：你举盾时会不断消耗盾牌耐久
     * Weak Wall: Shield durability drains when holding a shield.
     */
    public boolean enableWeakWall = true;

    /**
     * 再生禁令：你获得的瞬间恢复效果会延迟生效
     * Regeneration Ban: Instant healing effects you receive will be delayed.
     */
    public boolean enableRegenerationBan = true;

    /**
     * 药效冲突：你每获得一个正面buff，缩短你身上已有的buff的时长，并延长你身上debuff的时长
     * Potion Conflicts: Each time you gain a positive buff, existing buffs shorten while debuffs are extended.
     */
    public boolean enablePotionConflicts = true;

    /**
     * 无处藏身：怪物的近战攻击可以穿透方块进行判定
     * No Shelter: Mobs' melee attacks can hit through blocks.
     */
    public boolean enableNoShelter = true;

    /**
     * 霉运附体：挖掘矿物时时运附魔概率不生效
     * Bad Luck: The chance of fortune enchantment not triggering while mining is increased.
     */
    public boolean enableBadLuck = true;

    /**
     * 时空破缺：你每次投出末影珍珠都会刷出末影螨来咬你
     * Time Rift: Every time you throw an Ender Pearl, Endermites spawn to attack you.
     */
    public boolean enableTimeRift = true;

    /**
     * 时空紊乱：通过传送门时有概率被传送到目标的任意附近位置
     * Time Distortion: There is a chance to be teleported to a random location near the target when using a portal.
     */
    public boolean enableTimeDistortion = true;

    /**
     * 海神遗弃：钓上来的是垃圾概率大幅度提高
     * Abandoned by Poseidon: The chance of fishing up junk is significantly increased.
     */
    public boolean enableAbandonedByPoseidon = true;

    /**
     * 描边大师：弓有更大的偏转角
     * Outline Master: Archery has a larger arrow deviation.
     */
    public boolean enableOutlineMaster = true;

    /**
     * 复生怒火：每次打boss死了后boss不仅回满血还会变得更强
     * Reborn Wrath: Every time you die to a boss, the boss not only recovers full health but becomes stronger.
     */
    public boolean enableRebornWrath = true;

    /**
     * 速行之困：跑步消耗氧气
     * Oxygen Deprivation: Running depletes oxygen.
     */
    public boolean enableOxygenDeprivation = true;

    /**
     * 蛀虫秘藏：挖掘原石有概率出蠹虫
     * Worm Hoard: Mining stone has a chance to spawn Silverfish.
     */
    public boolean enableWormHoard = true;

    /**
     * 虚伪身躯：玩家的无敌帧减少
     * Hypocrisy Body: Reduces player's invincibility frames
     */
    public boolean enableHypocrisyBody = true;

    /**
     * 贪婪之书：附魔台需要的经验翻倍
     * Greedy Tome: Doubles the experience cost for the enchantment table
     */
    public boolean enableGreedyTome = true;

    /**
     * 打磨损耗：砂轮会折损耐久值
     * Grinding Wear: Grindstone consumes durability
     */
    public boolean enableGrindingWear = true;

    /**
     * 水中束缚：下水减速
     * Water Shackles: Slows movement speed in water
     */
    public boolean enableWaterShackles = true;

    /**
     * 失足之险：潜行不能阻止走到边缘时坠落
     * Misstep Peril: Sneaking no longer prevents falling off edges
     */
    public boolean enableMisstepPeril = true;

    /**
     * 爱好奢靡：只有打上盔甲纹饰的盔甲才能被使用
     * Lavish Taste: Only armor with trims can be worn
     */
    public boolean enableLavishTaste = true;

    /**
     * 碎碎平安：末影之眼一定会碎
     * Shattered Eye: Ender Eyes always break upon use
     */
    public boolean enableShatteredEye = true;

    /**
     * 龙阳之好：末影龙释放龙息时，玩家脚下一定会出现龙息
     * Draconic Favor: When the Ender Dragon breathes fire, a dragon breath area appears under the player's feet
     */
    public boolean enableDraconicFavor = true;

    /**
     * 无良卖家：猪灵有概率会取走黄金却不以物易物
     * Unfair Trader: Piglins may take gold without bartering
     */
    public boolean enableUnfairTrader = true;

    /**
     * 被刺高手：玩家潜行时，受到的伤害翻倍
     * Exposed Weakness: Player takes more damage while sneaking
     */
    public boolean enableExposedWeakness = true;

    /**
     * 师出有名：玩家不能攻击未攻击自己的生物
     * Justified Combat: Player cannot attack non-hostile creatures first
     */
    public boolean enableJustifiedCombat = true;

    /**
     * 劲霸男装：吃东西有后摇，在此期间不能进行操作
     * Food Coma: Eating has a delay, during which no actions can be performed
     */
    public boolean enableFoodComa = true;

    /**
     * 颗粒无收：作物的掉落概率改为掉落枯萎的灌木
     * Barren Harvest: Crops now have a chance to drop dead bushes instead
     */
    public boolean enableBarrenHarvest = true;

    /**
     * 滑雪冒险：在寒冷群系，所有方块上施加冰块效果
     * Slippery Adventure: All blocks in cold biomes apply ice physics
     */
    public boolean enableSlipperyAdventure = true;

    /**
     * 疯狂植物：会被植物扎
     * Hostile Flora: Plants can deal damage to the player
     */
    public boolean enableHostileFlora = true;

    /**
     * 深海纠缠：游泳有时间限制，时间过后会下沉
     * Deep Sea Entanglement: Swimming has a time limit; after which the player starts sinking
     */
    public boolean enableDeepSeaEntanglement = true;

    /**
     * 均衡饮食：某一食物食用过多会减少饥饿值回复
     * Balanced Diet: Overconsuming a single type of food reduces its hunger restoration
     */
    public boolean enableBalancedDiet = true;

    /**
     * 狼狈为奸：附近对玩家有仇恨的生物越多，玩家受到的伤害越高
     * United Adversaries: The more hostile mobs nearby, the more damage the player takes
     */
    public boolean enableUnitedAdversaries = true;

    /**
     * 创伤开口：受到伤害概率持续掉血
     * Bleeding Wound: Taking damage has a chance to cause continuous health loss
     */
    public boolean enableBleedingWound = true;

    /**
     * 饥荒盛世：饱食度低于或高于一定值都会获得debuff
     * Feast or Famine: Having too low or too high saturation applies debuffs
     */
    public boolean enableFeastOrFamine = true;

    /**
     * 肺纤维化：氧气值减少
     * Pulmonary Fibrosis: Reduces maximum oxygen
     */
    public boolean enablePulmonaryFibrosis = true;

    /**
     * 以理服人：交易生物血量越高价格越贵
     * Bargaining Power: The higher the health of a trading entity, the more expensive its trades
     */
    public boolean enableBargainingPower = true;

    /**
     * 饥肠辘辘：减少饱食度上限
     * Hollow Stomach: Reduces maximum saturation
     */
    public boolean enableHollowStomach = true;

    /**
     * 远域挡招：为任意添加限伤和远程保护机制
     * Distant Deflection: Introduces universal damage cap and ranged damage resistance
     */
    public boolean enableDistantDeflection = true;

    /**
     * 创伤应激：如果被一种生物打死了复活之后再遇见这种生物会有debuff
     * PTSD: If the player was previously killed by a mob, encountering it again causes a debuff
     */
    public boolean enablePTSD = true;

    /**
     * 厄运诅咒：玩家造成的掉落物计算会多次判定，取更差的结果
     * Curse of Misfortune: The player's dropped items are calculated multiple times, taking the worst result
     */
    public boolean enableCurseOfMisfortune = true;

    /**
     * 乳糖不耐：你无法通过牛奶解除负面效果
     * Lactose Intolerance: You cannot remove negative effects with milk
     */
    public boolean enableLactoseIntolerance = true;

    /**
     * 雷霆引誓：更高概率被雷击中
     * Thunderbound Oath: Significantly increases the chance of being struck by lightning
     */
    public boolean enableThunderboundOath = true;

    /**
     * 熔岩献祭：处于下界被攻击会使自身着火
     * Lava Sacrifice: While in the Nether, being attacked will set you on fire
     */
    public boolean enableLavaSacrifice = true;

    /**
     * 破败战士：穿戴装备的耐久值越低，提供的护甲值越少
     * Dilapidated Warrior: The lower your equipment’s durability, the less armor protection it provides
     */
    public boolean enableDilapidatedWarrior = true;

    /**
     * 掉包奸商：与村民交易完物品后概率被换成其他物品
     * Dodgy Merchant: After completing a trade with a villager, there’s a chance the item you receive will be swapped for something else
     */
    public boolean enableDodgyMerchant = true;

    /**
     * 新鲜武器：武器耐久越低伤害越低
     * Fresh Weapon: The lower a weapon’s durability, the less damage it deals
     */
    public boolean enableFreshWeapon = true;

    /**
     * 迷糊工匠：合成的物品概率减少耐久或数量
     * Befuddled Artisan: Crafted items have a chance to spawn with reduced durability or in reduced quantity
     */
    public boolean enableBefuddledArtisan = true;

    /**
     * 溢出负重：玩家背包内存放的物品超过一定数量，移动速度会降低，摔落伤害增加
     * Overburdened: When your inventory holds too many items, your movement speed is reduced and fall damage is increased
     */
    public boolean enableOverburdened = true;

    /**
     * 破裂之门：传送门是一次性的，使用了之后会炸裂
     * Shattered Portal: Portals become one-time use; they explode after being used
     */
    public boolean enableShatteredPortal = true;

    /**
     * 失衡武器：武器的攻击力在每次攻击时概率减少
     * Unbalanced Weapon: Each time you attack, there’s a chance your weapon’s attack power will decrease
     */
    public boolean enableUnbalancedWeapon = true;

    /**
     * 海关过境：村民的高级交易需要等待几个 Minecraft 日后才给你交易的物品
     * Customs Clearance: Advanced villager trades require waiting several Minecraft days before the items become available
     */
    public boolean enableCustomsClearance = true;

    /**
     * 拔苗助长：作物施肥时概率降低生长阶段，施肥越多降低概率越大
     * Overzealous Growth: When fertilizing crops, there’s a chance their growth stage will regress—and the more fertilizer you apply, the higher the chance
     */
    public boolean enableOverzealousGrowth = true;

    /**
     * 笨拙农夫：玩家在耕地上走有几率踩坏田
     * Clumsy Farmer: There’s a chance to trample and break farmland when walking over tilled soil
     */
    public boolean enableClumsyFarmer = true;

    /**
     * 海市蜃楼：概率看到远处出现结构，但并不是真的
     * Mirage: You may occasionally see distant structures that aren’t actually there
     */
    public boolean enableMirage = true;

    /**
     * 专事专办：工具只能干对应的事情
     * Single-Purpose Tools: Tools can only perform their designated function
     * @see tags/blocks/axe_dig.json
     * @see tags/blocks/hoe_dig.json
     * @see tags/blocks/pickaxe_dig.json
     * @see tags/blocks/shovel_dig.json
     */
    public boolean enableSinglePurposeTools = true;

    /**
     * 肌肉无力：使用盾牌抵挡伤害的时候概率会被破盾
     * Muscle Weakness: When blocking damage with a shield, there’s a chance your shield will break
     */
    public boolean enableMuscleWeakness = true;

    /**
     * 溃烂饥饿：食物在背包中存放时间过长会变质成腐肉
     * Rotting Hunger: Food stored in your inventory for too long will spoil into rotten flesh
     */
    public boolean enableRottingHunger = true;

    /**
     * 过度治疗：使用治疗道具时，有概率使玩家短时间内无法再次治疗
     * Overhealing: Using a healing item may cause you to be unable to heal again for a short time
     */
    public boolean enableOverhealing = true;

    /**
     * 花样出招：玩家要轮流使用不同攻击手段才可对特定生物造成伤害
     * Patterned Assault: You must alternate different attack methods to damage certain creatures
     */
    public boolean enablePatternedAssault = true;

    /**
     * 不亮之物：放置的火把，营火需要手动点燃
     * Unlit Objects: Placed torches and campfires must be manually lit
     */
    public boolean enableUnlitObjects = true;

    /**
     * 滑冰赛场：雨天方块阻力减小
     * Ice Rink: During rain, block friction is reduced, making surfaces slippery
     */
    public boolean enableIceRink = true;

    /**
     * 糟糕厨师：饭菜概率变成迷之炖菜（debuff的）
     * Terrible Cook: Cooked meals have a chance to turn into mysterious stew that inflicts a debuff
     */
    public boolean enableTerribleCook = true;

    /**
     * 震耳欲聋：不一定需要幽匿尖啸体才能召唤监守者
     * Deafening: You don’t always need a Sculk Shrieker to summon the Warden
     */
    public boolean enableDeafening = true;

    /**
     * 话痨戒指：偶尔会听到戒指的话语
     * Chatterbox Ring: Occasionally you’ll hear the ring itself speaking to you
     */
    public boolean enableChatterboxRing = true;

}
