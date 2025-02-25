package com.kaleblangley.ring_of_the_hundred_curses.config;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = RingOfTheHundredCurses.MODID)
public class ModConfig implements ConfigData {
    /*
     * 命薄如纸：减少玩家的部分固定生命值
     * Fragile Life: Reduces a portion of the player's base health.
     */
    public boolean enableFragileLife = true;
    public int reducesHealth = 10;

    /*
     * 世界为敌：全部生物对你有敌对性
     * The World Against You: All creatures become hostile towards you.
     */
    public boolean enableWorldAgainst = true;
    public double entityAttackSpeed = 1.2d;


    /*
     * 贪婪吃食：减少食物获得的饥饿值
     * Greedy Eating: Reduces the hunger restoration from food.
     */

    /*
     * 背包受限：堆叠上限减少
     * Backpack Limit: Reduces the stack limit of items.
     */

    /*
     * 竭尽全力：武器必须满蓄力才能使用
     * Full Power: Weapons can only be used when fully charged.
     */

    /*
     * 盾牌在右：副手无法使用盾牌
     * Shield on the Right: Cannot use a shield in the off-hand.
     */

    /*
     * 氧气不足：地下会造成缺氧
     * Lack of Oxygen: Suffocation effect occurs underground.
     */

    /*
     * 彻夜难眠：无法在床上睡觉
     * Sleepless Nights: Cannot sleep in beds.
     */

    /*
     * 脆弱的胃：生肉有debuff
     * Weak Stomach: Raw meat gives debuffs.
     */

    /*
     * 手无寸铁：手不能撸坚硬的方块
     * Weaponless: Cannot mine hard blocks.
     */

    /*
     * 星光大道：光照过低无法跑步
     * Starry Path: Cannot run when light levels are too low.
     */

    /*
     * 末水禁令：末地不能放水
     * End Water Ban: Water cannot be placed in the End.
     */

    /*
     * 贪婪锁链：宝箱中无法开出高级战利品
     * Greedy Lock: Chests cannot give high-level loot.
     */

    /*
     * 沉重枷锁：装备重量降低移速
     * Heavy Shackles: Wearing equipment reduces movement speed.
     */

    /*
     * 削弱打击：对敌人造成伤害更少
     * Weakened Strikes: Deals less damage to enemies.
     */

    /*
     * 灵魂压制：减少经验值的获取
     * Soul Suppression: Reduces experience point gain.
     */

    /*
     * 破碎之躯：摔落伤害无法被减缓
     * Fragile Body: Fall damage cannot be mitigated.
     */

    /*
     * 脆弱护甲：减少护甲给予的盔甲值与韧性
     * Fragile Armor: Reduces armor and toughness given by armor.
     */

    /*
     * 迟钝双手：降低挖掘速度
     * Sluggish Hands: Reduces mining speed.
     */

    /*
     * 孤独之主：无法驯服生物
     * Lonely Master: Cannot tame mobs.
     */

    /*
     * 迷失方向：地图和指南针将无法使用
     * Lost Direction: Maps and compasses become useless.
     */

    /*
     * 弱魔体质：无法使用高等级附魔
     * Weak Magic Constitution: Cannot use high-level enchantments.
     */

    /*
     * 水性弱者：水中游泳速度减慢，溺水受伤按深度增加
     * Weak Swimmer: Reduces swimming speed, and drowning damage increases with depth.
     */

    /*
     * 食堂阿姨：重生锚每次充能填充数量随机
     * Cafeteria Lady: Respawn anchors charge with a random number of uses.
     */

    /*
     * 无效题海：当玩家每天对同一种武器附魔次数过多后，附魔效果的等级下降
     * Endless Quiz: Enchanting the same weapon repeatedly reduces its enchantment level.
     */

    /*
     * 敌我不分：附有忠诚的三叉戟有一定概率会在返回时伤害玩家
     * Friend or Foe: A trident with Loyalty has a chance to damage the player when returning.
     */

    /*
     * 无能窃贼：玩家在掠夺者营地与村庄打开战利品箱时会刷新掠夺者和村民
     * Incompetent Thief: Opening loot chests in Pillager Outposts or Villages spawns Pillagers and Villagers.
     */

    /*
     * 血肉失控：每一次受伤额外受到最大生命值部分百分比的伤害
     * Blood and Flesh: Each time you take damage, you receive additional damage based on a percentage of your maximum health.
     */

    /*
     * 集群意识：击杀怪物后，概率性会原地生成一个同样的怪物
     * Horde Mind: After killing a mob, there is a chance to spawn the same mob in place.
     */

    /*
     * 专注失调：攻击，放置，制作物品概率失败
     * Focus Disturbance: Attacks, placements, and crafting have a chance to fail.
     */

    /*
     * 神经退行：重生后获得负面效果
     * Neurological Degeneration: Negative effects are applied after respawning.
     */

    /*
     * 恐怖实体：敌人的生命值有概率提升
     * Horrific Entity: Enemies may have a chance to gain extra health.
     */

    /*
     * 社会悖论：村民交易价格提升
     * Social Paradox: Villager trade prices are higher.
     */

    /*
     * 气压失序：进入过高或过低的位置，导致移动和跳跃能力减弱
     * Pressure Disorder: Moving and jumping abilities are reduced when in high or low altitudes.
     */

    /*
     * 趋同缺失：生命值降低时同时导致攻击力降低
     * Loss of Synchronicity: Lower health also reduces attack power.
     */

    /*
     * 血肉崩溃：以部分的生命值，饥饿值重生
     * Flesh Collapse: Respawn with a portion of your health and hunger.
     */

    /*
     * 恢复缓慢：你不能自然回血
     * Slow Recovery: You cannot naturally regenerate health.
     */

    /*
     * 羸弱之墙：你举盾时会不断消耗盾牌耐久
     * Weak Wall: Shield durability drains when holding a shield.
     */

    /*
     * 再生禁令：你获得的瞬间恢复效果会延迟生效
     * Regeneration Ban: Instant healing effects you receive will be delayed.
     */

    /*
     * 药效冲突：你每获得一个正面buff，缩短你身上已有的buff的时长，并延长你身上debuff的时长
     * Potion Conflicts: Each time you gain a positive buff, existing buffs shorten while debuffs are extended.
     */

    /*
     * 无处藏身：怪物的近战攻击可以穿透方块进行判定
     * No Shelter: Mobs' melee attacks can hit through blocks.
     */

    /*
     * 霉运附体：挖掘矿物时时运附魔概率不生效
     * Bad Luck: The chance of fortune enchantment not triggering while mining is increased.
     */

    /*
     * 时空破缺：你每次投出末影珍珠都会刷出末影螨来咬你
     * Time Rift: Every time you throw an Ender Pearl, Endermites spawn to attack you.
     */

    /*
     * 时空紊乱：通过传送门时有概率被传送到目标的任意附近位置
     * Time Distortion: There is a chance to be teleported to a random location near the target when using a portal.
     */

    /*
     * 海神遗弃：钓上来的是垃圾概率大幅度提高
     * Abandoned by Poseidon: The chance of fishing up junk is significantly increased.
     */

    /*
     * 描边大师：弓有更大的偏转角
     * Outline Master: Archery has a larger arrow deviation.
     */

    /*
     * 复生怒火：每次打boss死了后boss不仅回满血还会变得更强
     * Reborn Wrath: Every time you die to a boss, the boss not only recovers full health but becomes stronger.
     */

    /*
     * 速行之困：跑步消耗氧气
     * Oxygen Deprivation: Running depletes oxygen.
     */

    /*
     * 蛀虫秘藏：挖掘原石有概率出蠹虫
     * Worm Hoard: Mining stone has a chance to spawn Silverfish.
     */
}
