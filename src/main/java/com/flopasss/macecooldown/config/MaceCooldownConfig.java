package com.flopasss.macecooldown.config;

import java.nio.file.Files;
import java.nio.file.Path;

import com.flopasss.macecooldown.MaceCooldown;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public class MaceCooldownConfig {
    public boolean enabled = true; // The mod is enabled by default
    public int cooldownTicks = 600; // Default cooldown of 30 seconds (600 ticks)
    public boolean onlyPreventSmash = true; // By default only the smash attack is prevented whilst on cooldown
    public boolean onlyApplyOnSmash = true; // By default only the smash attack will apply the cooldown
    public boolean playerCooldownPreference = false; // By default the player cooldown preference functionality is off

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create(); // For JSON serialization
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir()
            .resolve(MaceCooldown.MOD_ID + ".json"); // Config file path

    public static MaceCooldownConfig load() {
        // Return default config if file doesn't exist to load
        if (!Files.exists(CONFIG_PATH)) {
            MaceCooldownConfig defaultConfig = new MaceCooldownConfig();
            defaultConfig.save(); // Save the default config to create the file
            return defaultConfig;
        }

        try {
            // Read the JSON file into a string
            String json = Files.readString(CONFIG_PATH);

            // Deserialize the JSON string into a MaceCooldownConfig variable
            MaceCooldownConfig config = GSON.fromJson(json, MaceCooldownConfig.class);

            // If the config is null, return default config
            if (config == null)
                return new MaceCooldownConfig();

            // Return the loaded config
            return config;
        } catch (Exception e) {
            // Log the error
            MaceCooldown.LOGGER.warn("Failed to load config, using default values", e);

            // Return default config
            return new MaceCooldownConfig();
        }
    }

    public void save() {
        try {
            // Serialize the config object to a JSON string
            String json = GSON.toJson(this);

            // Write the JSON string to the config file
            Files.writeString(CONFIG_PATH, json);
        } catch (Exception e) {
            // Log the error
            MaceCooldown.LOGGER.error("Failed to save config", e);
        }
    }
}
