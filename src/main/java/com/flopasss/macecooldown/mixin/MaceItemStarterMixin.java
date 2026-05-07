package com.flopasss.macecooldown.mixin;

import com.flopasss.macecooldown.MaceCooldown;
import com.flopasss.macecooldown.data.MaceCooldownPlayerData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.ItemStack;

@Mixin(MaceItem.class)
public class MaceItemStarterMixin {
	@Inject(method = "hurtEnemy", at = @At("TAIL"))

	private void onHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfo callbackInfo) {
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
		if (MaceCooldown.CONFIG.onlyApplyOnSmash && !(attacker.fallDistance > 1.5 && !attacker.isFallFlying()))
			return;

		// Return if the target is not a player
		if (!(target instanceof Player))
			return;

		// Check if the preferences
		if (MaceCooldown.CONFIG.playerCooldownPreference
				&& (!((MaceCooldownPlayerData) attacker).maceCooldown_hasPreference()
						&& !((MaceCooldownPlayerData) target).maceCooldown_hasPreference()))
			return;

		// Return if the mace is already on cooldown
		if (player.getCooldowns().isOnCooldown(stack))
			return;

		// Start the cooldown
		player.getCooldowns().addCooldown(stack, MaceCooldown.CONFIG.cooldownTicks);
	}
}