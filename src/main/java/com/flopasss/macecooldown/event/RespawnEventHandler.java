package com.flopasss.macecooldown.event;

import com.flopasss.macecooldown.data.MaceCooldownPlayerData;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class RespawnEventHandler {

    public static void register() {
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            // Copy the preference from the old player to the new player
            MaceCooldownPlayerData oldData = (MaceCooldownPlayerData) oldPlayer;
            MaceCooldownPlayerData newData = (MaceCooldownPlayerData) newPlayer;

            newData.maceCooldown_setPreference(
                oldData.maceCooldown_hasPreference()
            );
        });
    }
}
