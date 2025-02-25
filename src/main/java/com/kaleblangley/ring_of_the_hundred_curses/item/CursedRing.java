package com.kaleblangley.ring_of_the_hundred_curses.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.ChunkEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CursedRing extends Item implements ICurioItem {
    public CursedRing() {
        super(new Properties().stacksTo(1).defaultDurability(0));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        ICurioItem.super.onEquip(slotContext, prevStack, stack);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        ICurioItem.super.onUnequip(slotContext, newStack, stack);
    }


    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        LivingEntity livingEntity = slotContext.entity();
        if (RingUtil.isEquipRing(livingEntity) && livingEntity instanceof ServerPlayer serverPlayer){
            serverPlayer.sendSystemMessage(Component.translatable("message.ring_of_the_hundred_curses.same"));
            return false;
        }
        return ICurioItem.super.canEquip(slotContext, stack);
    }

//    @Override
//    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
//        return false;
//    }

    @Override
    public ICurio.@NotNull DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        HashMultimap<Attribute, AttributeModifier> attributes = HashMultimap.create();
        AttributeModifier modifier = new AttributeModifier(UUID.fromString("3f2c4a78-91b5-4d3f-9e8a-7c6e5f1b2d4a"), "reducesHealth", -ModConfigManager.getConfig().reducesHealth, AttributeModifier.Operation.ADDITION);

        if (ModConfigManager.getConfig().enableFragileLife) attributes.put(Attributes.MAX_HEALTH, modifier);
        return attributes;
    }

    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltips, ItemStack stack) {
        return new ArrayList<>();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> componentList, @NotNull TooltipFlag tooltipFlag) {}

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {return new ArrayList<>();}
}
