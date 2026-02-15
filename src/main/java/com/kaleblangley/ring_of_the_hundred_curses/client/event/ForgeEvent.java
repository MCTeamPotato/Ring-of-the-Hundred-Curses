package com.kaleblangley.ring_of_the_hundred_curses.client.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeEvent {

    @SubscribeEvent
    public static void rottingHungerTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!getConfig().enableRottingHunger || !stack.isEdible() || stack.is(Items.ROTTEN_FLESH)) return;

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("RotHungerTick")) return;

        long startTime = tag.getLong("RotHungerTick");
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null) return;

        long gameTime = clientLevel.getGameTime();
        int expireTime = getConfig().rottingHungerExpireTime;
        long remaining = expireTime - (gameTime - startTime);
        if (remaining < 0) remaining = 0;

        int totalSeconds = (int) (remaining / 20);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);

        ChatFormatting color;
        float ratio = (float) remaining / expireTime;
        if (ratio > 0.5f) {
            color = ChatFormatting.GREEN;
        } else if (ratio > 0.2f) {
            color = ChatFormatting.YELLOW;
        } else {
            color = ChatFormatting.RED;
        }

        List<Component> toolTips = event.getToolTip();
        toolTips.add(Component.translatable("message.ring_of_the_hundred_curses.freshness",
                Component.literal(timeStr).withStyle(color)));
    }
}
