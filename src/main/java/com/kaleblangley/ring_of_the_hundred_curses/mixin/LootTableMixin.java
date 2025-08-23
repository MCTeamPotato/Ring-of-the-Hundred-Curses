package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(LootTable.class)
public class LootTableMixin {

    @Inject(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;",
            at = @At("RETURN"), cancellable = true)
    private void modifyLootForGreedyLock(LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        Player player = getPlayerFromContext(context);
        if (player == null || !RingUtil.configAndRing(player, ModConfigManager.getConfig().enableGreedyLock)) {
            return;
        }
        ObjectArrayList<ItemStack> originalLoot = cir.getReturnValue();
        ObjectArrayList<ItemStack> modifiedLoot = new ObjectArrayList<>();
        List<String> highValueItems = Arrays.asList(ModConfigManager.getConfig().greedyLockHighValueItems);
        for (ItemStack stack : originalLoot) {
            if (isHighValueItem(stack, highValueItems)) {
                ItemStack junkItem = getRandomJunkItem(context);
                if (junkItem != null) {
                    junkItem.setCount(Math.max(1, stack.getCount()));
                    modifiedLoot.add(junkItem);
                } else {
                    modifiedLoot.add(stack);
                }
            } else {
                modifiedLoot.add(stack);
            }
        }

        cir.setReturnValue(modifiedLoot);
    }

    private Player getPlayerFromContext(LootContext context) {
        if (context.hasParam(LootContextParams.LAST_DAMAGE_PLAYER)) {
            return context.getParam(LootContextParams.LAST_DAMAGE_PLAYER);
        }
        if (context.hasParam(LootContextParams.ORIGIN)) {
            var origin = context.getParam(LootContextParams.ORIGIN);
            return context.getLevel().getNearestPlayer(
                    origin.x, origin.y, origin.z, 16.0,
                    player -> RingUtil.isEquipRing((LivingEntity) player)
            );
        }

        return null;
    }

    private boolean isHighValueItem(ItemStack stack, List<String> highValueItems) {
        if (stack.isEmpty()) return false;
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) return false;
        return highValueItems.contains(itemId.toString());
    }

    private ItemStack getRandomJunkItem(LootContext context) {
        String[] junkItemStrings = ModConfigManager.getConfig().greedyLockJunkItems;
        if (junkItemStrings.length == 0) return null;
        List<Item> junkItems = new ArrayList<>();
        for (String itemString : junkItemStrings) {
            try {
                ResourceLocation itemLocation = new ResourceLocation(itemString);
                Item item = ForgeRegistries.ITEMS.getValue(itemLocation);
                if (item != null && item != Items.AIR) {
                    junkItems.add(item);
                }
            } catch (Exception ignored) {
            }
        }
        if (junkItems.isEmpty()) return null;
        Item randomItem = junkItems.get(context.getRandom().nextInt(junkItems.size()));
        int count = 1 + context.getRandom().nextInt(3);
        return new ItemStack(randomItem, count);
    }
}
