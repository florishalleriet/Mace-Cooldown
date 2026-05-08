package com.flopasss.macecooldown.command;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import com.flopasss.macecooldown.MaceCooldown;
import com.flopasss.macecooldown.data.MaceCooldownPlayerData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class MaceCooldownCommand {

    public static void register(
        CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
            literal("maceCooldown")
                // Cooldown length
                .then(
                    literal("duration")
                        .requires(
                            Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)
                        )
                        .then(
                            argument("ticks", IntegerArgumentType.integer(0))
                                .suggests((context, builder) -> {
                                    // Provide tab-completion suggestions
                                    builder.suggest(
                                        MaceCooldown.CONFIG.cooldownTicks
                                    );
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    int ticks = IntegerArgumentType.getInteger(
                                        context,
                                        "ticks"
                                    );

                                    MaceCooldown.CONFIG.cooldownTicks = ticks;
                                    MaceCooldown.CONFIG.save();

                                    context
                                        .getSource()
                                        .sendSuccess(
                                            () ->
                                                Component.literal(
                                                    "The duration is now " +
                                                        ticks +
                                                        " ticks"
                                                ),
                                            true
                                        );
                                    return 1;
                                })
                        )
                )
                // Player preference
                .then(
                    literal("preference")
                        .then(
                            literal("set").then(
                                Commands.argument(
                                    "boolean",
                                    BoolArgumentType.bool()
                                ).executes(context -> {
                                    boolean bool = BoolArgumentType.getBool(
                                        context,
                                        "boolean"
                                    );

                                    if (
                                        context
                                                .getSource()
                                                .getEntity() instanceof
                                            Player player
                                    ) {
                                        (
                                            (MaceCooldownPlayerData) player
                                        ).maceCooldown_setPreference(bool);

                                        context
                                            .getSource()
                                            .sendSuccess(
                                                () ->
                                                    Component.literal(
                                                        "Mace Cooldown preference set to: " +
                                                            (bool
                                                                ? "§aENABLED"
                                                                : "§cDISABLED")
                                                    ),
                                                false
                                            );
                                    } else {
                                        context
                                            .getSource()
                                            .sendFailure(
                                                Component.literal(
                                                    "This command must be run by a player"
                                                )
                                            );
                                    }

                                    return 1;
                                })
                            )
                        )
                        .then(
                            literal("info").then(
                                Commands.argument(
                                    "player",
                                    EntityArgument.player()
                                ).executes(context -> {
                                    Player target = EntityArgument.getPlayer(
                                        context,
                                        "player"
                                    );

                                    MaceCooldownPlayerData data =
                                        (MaceCooldownPlayerData) target;

                                    context
                                        .getSource()
                                        .sendSuccess(
                                            () ->
                                                Component.literal(
                                                    target
                                                            .getName()
                                                            .getString() +
                                                        "'s Mace Cooldown preference: " +
                                                        (data.maceCooldown_hasPreference()
                                                            ? "§aENABLED"
                                                            : "§cDISABLED")
                                                ),
                                            false
                                        );

                                    return 1;
                                })
                            )
                        )
                )
                // Config boolean toggles
                .then(
                    createConfigToggle(
                        "enabled",
                        val -> MaceCooldown.CONFIG.enabled = val,
                        val ->
                            "The mod has been " + (val ? "enabled" : "disabled")
                    )
                )
                .then(
                    createConfigToggle(
                        "onlyPreventSmash",
                        val -> MaceCooldown.CONFIG.onlyPreventSmash = val,
                        val ->
                            (val ? "Only smash" : "All mace") +
                            " attacks are prevented whilst on cooldown"
                    )
                )
                .then(
                    createConfigToggle(
                        "onlyApplyOnSmash",
                        val -> MaceCooldown.CONFIG.onlyApplyOnSmash = val,
                        val ->
                            (val ? "Only smash" : "All mace") +
                            " attacks will apply the cooldown"
                    )
                )
                .then(
                    createConfigToggle(
                        "playerCooldownPreference",
                        val ->
                            MaceCooldown.CONFIG.playerCooldownPreference = val,
                        val ->
                            "The player cooldown preference functionality has been " +
                            (val ? "enabled" : "disabled")
                    )
                )
        );
    }

    private static LiteralArgumentBuilder<
        CommandSourceStack
    > createConfigToggle(
        String name,
        Consumer<Boolean> setter,
        Function<Boolean, String> messageFactory
    ) {
        return Commands.literal(name)
            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(
                Commands.argument("boolean", BoolArgumentType.bool()).executes(
                    context -> {
                        boolean bool = BoolArgumentType.getBool(
                            context,
                            "boolean"
                        );

                        // Update config via the setter
                        setter.accept(bool);
                        MaceCooldown.CONFIG.save();

                        // Send feedback using the factory
                        context
                            .getSource()
                            .sendSuccess(
                                () ->
                                    Component.literal(
                                        messageFactory.apply(bool)
                                    ),
                                true
                            );
                        return 1;
                    }
                )
            );
    }
}
