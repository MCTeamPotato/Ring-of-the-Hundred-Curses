package com.kaleblangley.ring_of_the_hundred_curses.mixin.block;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

/**
 * 霉运附体：挖掘矿物时时运附魔概率不生效
 * Bad Luck: Fortune enchantment has a chance to not work when mining.
 */
@Mixin(Block.class)
public class BadLuckMixin {

    @Inject(
            method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void ring_of_the_hundred_curses$badLuck(
            BlockState state, ServerLevel level, BlockPos pos,
            @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack tool,
            CallbackInfoReturnable<List<ItemStack>> cir
    ) {
        if (!(entity instanceof Player player)) return;
        if (!RingUtil.configAndRing(player, getConfig().enableBadLuck)) return;

        int fortuneLevel = tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
        if (fortuneLevel <= 0) return;

        if (player.getRandom().nextDouble() >= getConfig().badLuckChance) return;

        // Create tool copy with Fortune removed
        ItemStack toolCopy = tool.copy();
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(toolCopy);
        enchants.remove(Enchantments.BLOCK_FORTUNE);
        EnchantmentHelper.setEnchantments(enchants, toolCopy);

        // Build LootParams with the modified tool (same as vanilla Block.getDrops)
        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, toolCopy)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);

        cir.setReturnValue(state.getDrops(builder));
    }
}
