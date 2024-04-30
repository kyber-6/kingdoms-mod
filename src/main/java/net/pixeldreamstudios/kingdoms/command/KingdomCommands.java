package net.pixeldreamstudios.kingdoms.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.pixeldreamstudios.kingdoms.Kingdoms;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class KingdomCommands {
    public static void init() {
        registerSetKingdomCommand();
        registerShowKingdomCommand();
    }

    private static void registerShowKingdomCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("kingdom")
                .then(literal("show")
                        .then(argument("player", EntityArgument.player())
                                .executes(context -> {
                                    // get player
                                    ServerPlayer serverPlayer = EntityArgument.getPlayer(context, "player");

                                    // get team
                                    int team = Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(serverPlayer).getTeam();

                                    // get team component
                                    final Component teamName;
                                    if (team == 0) {
                                        teamName = Component.literal("No Kingdom");
                                    } else if (team == 1) {
                                        teamName = Component.translatable("team."+ Kingdoms.MOD_ID +  ".thesium");
                                    } else if (team == 2) {
                                        teamName = Component.translatable("team."+ Kingdoms.MOD_ID +  ".krulath");
                                    }
                                    else {
                                        teamName = Component.literal("ILLEGAL VALUE ASSIGNED FOR KINGDOM");
                                    }

                                    context.getSource().sendSuccess(() -> Component.literal("Player " + serverPlayer.getScoreboardName() + " is part of the " + teamName.getString() + " kingdom."), true);
                                    return 1;
                                })))
        ));
    }

    private static void registerSetKingdomCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("kingdom").requires(source -> source.hasPermission(2))
                        .then(literal("set")
                                .then(argument("player", EntityArgument.player())
                                        .then(argument("team", IntegerArgumentType.integer(0, 2))
                                                .executes(context -> {
                                                    // get player
                                                    ServerPlayer serverPlayer = EntityArgument.getPlayer(context, "player");

                                                    // get team
                                                    int team = IntegerArgumentType.getInteger(context, "team");

											        // get team component
                                                    final Component teamName;
                                                    if (team == 0) {
                                                        teamName = Component.literal("No Kingdom");
                                                    } else if (team == 1) {
                                                        teamName = Component.translatable("team."+ Kingdoms.MOD_ID +  ".thesium");
                                                    } else if (team == 2) {
                                                        teamName = Component.translatable("team."+ Kingdoms.MOD_ID +  ".krulath");
                                                    }
                                                    else {
                                                        teamName = Component.literal("ILLEGAL VALUE ASSIGNED FOR KINGDOM");
                                                    }

                                                    // change player team
                                                    Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(serverPlayer).setTeam(team);

                                                    context.getSource().sendSuccess(() -> Component.literal("Player " + serverPlayer.getScoreboardName() + " now belongs to the " + teamName.getString() + " kingdom."), true);
                                                    return 1;
                                                }))))
        ));
    }
}
