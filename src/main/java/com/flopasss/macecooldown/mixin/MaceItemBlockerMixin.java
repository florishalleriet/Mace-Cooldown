package com.flopasss.macecooldown.mixin;

import com.flopasss.macecooldown.MaceCooldown;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MaceItem.class)
public class MaceItemBlockerMixin {
	@Inject(at = @At("HEAD"), method = "canSmashAttack")

	private static void interceptCanSmashAttack(LivingEntity attacker, CallbackInfoReturnable<Boolean> callbackInfo) {
		// Return if the mod is disabled
		if (!MaceCooldown.CONFIG.enabled)
			return;

		// Return if the cooldown is 0 or lower
		if (MaceCooldown.CONFIG.cooldownTicks <= 0)
			return;

		// Return if the attacker is not a player
		if (!(attacker instanceof Player player))
			return;

		// Return if the player did not meet the smash requirements
		if (MaceCooldown.CONFIG.onlyPreventSmash && !(attacker.fallDistance > 1.5 && !attacker.isFallFlying()))
			return;

		// Get the mace in the player's main hand
		ItemStack mace = player.getMainHandItem();
		// Return if the item in the player's main hand is not on cooldown
		if (!player.getCooldowns().isOnCooldown(mace))
			return;

		// Cancel the injected method
		callbackInfo.setReturnValue(false);
	}
}