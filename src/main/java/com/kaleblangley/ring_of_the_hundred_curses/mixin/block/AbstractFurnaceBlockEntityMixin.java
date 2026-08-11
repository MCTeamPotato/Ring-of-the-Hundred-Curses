package com.kaleblangley.ring_of_the_hundred_curses.mixin.block;

import com.kaleblangley.ring_of_the_hundred_curses.event.PlayerEvent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {

    @Redirect(
            method = "burn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/Recipe;assemble(Lnet/minecraft/world/Container;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack ring_of_the_hundred_curses$terribleCook(
            Recipe recipe, Container container, RegistryAccess registryAccess
    ) {
        ItemStack cookedMeal = recipe.assemble(container, registryAccess);
        AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) (Object) this;
        Level level = furnace.getLevel();
        if (level == null || !furnace.getItem(2).isEmpty()) return cookedMeal;
        return PlayerEvent.maybeConvertTerribleCook(level, furnace.getBlockPos(), cookedMeal);
    }
}
