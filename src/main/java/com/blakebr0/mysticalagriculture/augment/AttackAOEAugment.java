package com.blakebr0.mysticalagriculture.augment;

import com.blakebr0.cucumber.helper.ColorHelper;
import com.blakebr0.mysticalagriculture.api.tinkering.Augment;
import com.blakebr0.mysticalagriculture.api.tinkering.AugmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;
import java.util.List;

public class AttackAOEAugment extends Augment {
    private final int amplifier;

    public AttackAOEAugment(ResourceLocation id, int tier, int amplifier) {
        super(id, tier, EnumSet.of(AugmentType.SWORD), getColor(0xFF0000, tier), getColor(0x700000, tier));
        this.amplifier = amplifier;
    }

    @Override
    public boolean onHitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) attacker;
            if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
                List<LivingEntity> entities = player.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(1.5D * this.amplifier, 0.25D * this.amplifier, 1.5D * this.amplifier));

                for (LivingEntity aoeEntity : entities) {
                    if (aoeEntity != player && aoeEntity != target && !player.isAlliedTo(target)) {
                        aoeEntity.knockback(0.4F, MathHelper.sin(player.yRot * 0.017453292F), -MathHelper.cos(player.yRot * 0.017453292F));
                        aoeEntity.hurt(DamageSource.playerAttack(player), 5.0F + (5.0F * this.amplifier));
                    }
                }

                player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
                player.sweepAttack();
            }

            return true;
        }

        return false;
    }

    private static int getColor(int color, int tier) {
        return ColorHelper.saturate(color, Math.min((float) tier / 5, 1));
    }
}
