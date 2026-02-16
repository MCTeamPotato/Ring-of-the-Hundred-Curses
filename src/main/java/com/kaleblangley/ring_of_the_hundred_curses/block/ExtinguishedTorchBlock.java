package com.kaleblangley.ring_of_the_hundred_curses.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.particles.ParticleTypes;

public class ExtinguishedTorchBlock extends TorchBlock {

    public ExtinguishedTorchBlock(Properties pProperties) {
        super(pProperties, ParticleTypes.SMOKE);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (stack.getItem() instanceof FlintAndSteelItem || stack.getItem() instanceof FireChargeItem) {
            if (!pLevel.isClientSide) {
                pLevel.setBlock(pPos, Blocks.TORCH.defaultBlockState(), 11);
                pLevel.playSound(null, pPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, pLevel.getRandom().nextFloat() * 0.4F + 0.8F);
                pLevel.gameEvent(pPlayer, GameEvent.BLOCK_CHANGE, pPos);
                if (stack.getItem() instanceof FlintAndSteelItem) {
                    stack.hurtAndBreak(1, pPlayer, (p) -> p.broadcastBreakEvent(pHand));
                } else {
                    if (!pPlayer.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
