package com.flopasss.macecooldown.mixin;

import com.flopasss.macecooldown.data.MaceCooldownPlayerData;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerDataMixin implements MaceCooldownPlayerData {
    @Unique
    private static final String NBT_KEY = "maceCooldownPreference";

    @Unique
    private boolean maceCooldownPreference = true; // Enabled by default

    public boolean maceCooldown_hasPreference() {
        return this.maceCooldownPreference;
    }

    public void maceCooldown_setPreference(boolean bool) {
        this.maceCooldownPreference = bool;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void onSave(ValueOutput nbt, CallbackInfo callbackInfo) {
        nbt.putBoolean(NBT_KEY, this.maceCooldownPreference);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void onLoad(ValueInput nbt, CallbackInfo callbackInfo) {
        this.maceCooldownPreference = nbt.getBooleanOr(NBT_KEY, true);
    }
}
