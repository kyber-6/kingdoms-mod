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

//											// get team component
                                                    final Component teamName;
                                                    if (team == 0) {
                                                        teamName = Component.literal("No Team");
                                                    } else if (team == 1) {
                                                        teamName = Component.literal("Thesium");
                                                    } else if (team == 2) {
                                                        teamName = Component.literal("Krul\'ath");
                                                    }
                                                    else {
                                                        teamName = Component.literal("ILLEGAL VALUE ASSIGNED FOR TEAM");
                                                    }

                                                    // change player team
                                                    Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(serverPlayer).setTeam(team);
//											Minecraft.getInstance().player.getServer().sendSystemMessage(Component.literal("kingdom " + Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(serverPlayer).getTeam()));

                                                    context.getSource().sendSuccess(() -> Component.literal("Player " + serverPlayer.getScoreboardName() + " now belongs to the " + teamName.getString() + " kingdom."), true);
//											context.getSource().sendSuccess(() -> Component.literal("Called foo with bar"), false);
                                                    return 1;
                                                }))))
        ));
    }
}
