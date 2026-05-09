package com.flopasss.macecooldown;

import com.flopasss.macecooldown.command.MaceCooldownCommand;
import com.flopasss.macecooldown.config.MaceCooldownConfig;
import com.flopasss.macecooldown.event.RespawnEventHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaceCooldown implements ModInitializer {

    public static final String MOD_ID = "mace-cooldown";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MaceCooldownConfig CONFIG;

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Load the config when the mod initializes
        CONFIG = MaceCooldownConfig.load();

        // Register the command
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) ->
                MaceCooldownCommand.register(dispatcher)
        );

        // Register respawn event handler
        RespawnEventHandler.register();

        // Log a message to indicate that the mod has been initialized
        LOGGER.info("Flopasss Mace Cooldown initialized!");
    }
}
