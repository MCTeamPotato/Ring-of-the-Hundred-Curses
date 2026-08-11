package com.kaleblangley.ring_of_the_hundred_curses.mixin.block;

import com.kaleblangley.ring_of_the_hundred_curses.event.PlayerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CampfireBlockEntity.class)
public class CampfireBlockEntityMixin {

    @Redirect(
            method = "cookTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"
            )
    )
    private static void ring_of_the_hundred_curses$terribleCook(
            Level level, double x, double y, double z, ItemStack result
    ) {
        ItemStack converted = PlayerEvent.maybeConvertTerribleCook(
                level, BlockPos.containing(x, y, z), result
        );
        Containers.dropItemStack(level, x, y, z, converted);
    }
}
