package com.flopasss.macecooldown.command;

import com.flopasss.macecooldown.MaceCooldown;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.Commands.argument;

public class MaceCooldownCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("maceCooldown")

                // Cooldown length
                .then(literal("length").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(argument("ticks", IntegerArgumentType.integer(0)).suggests((context, builder) -> {
                            // Provide tab-completion suggestions
                            builder.suggest(MaceCooldown.CONFIG.cooldownTicks);
                            return builder.buildFuture();
                        }).executes(ctx -> {
                            int ticks = IntegerArgumentType.getInteger(ctx, "ticks");

                            MaceCooldown.CONFIG.cooldownTicks = ticks;
                            MaceCooldown.CONFIG.save();

                            ctx.getSource().sendSuccess(
                                    () -> Component.literal("Length: " + ticks + " ticks"),
                                    true);
                            return 1;
                        })))

                // Toggle the mod on/off
                .then(literal("enabled").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(argument("boolean", StringArgumentType.word()).suggests((context, builder) -> {
                            // Provide tab-completion suggestions
                            builder.suggest("true");
                            builder.suggest("false");
                            return builder.buildFuture();
                        }).executes(context -> {
                            // Parse the input string into a boolean
                            String enabledStr = StringArgumentType.getString(context, "boolean");
                            boolean enabled = Boolean.parseBoolean(enabledStr);

                            // Update config value and save
                            MaceCooldown.CONFIG.enabled = enabled;
                            MaceCooldown.CONFIG.save();

                            // Send feedback to the command executor
                            context.getSource()
                                    .sendSuccess(() -> Component.literal("Enabled: " + enabled),
                                            true);
                            return 1;
                        }))));
    }
}
