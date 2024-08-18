package net.pixeldreamstudios.kingdoms.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.pixeldreamstudios.kingdoms.Kingdoms;
import net.pixeldreamstudios.kingdoms.cardinalcomponents.TeamComponent;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class KingdomCommands {
    public static void init() {
        registerSetKingdomCommand();
        registerShowKingdomCommand();
        registerSetKingCommand();  // Register the /kingdom king command
    }

    private static void registerShowKingdomCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("kingdom")
                .then(literal("show")
                        .then(argument("player", EntityArgument.player())
                                .executes(context -> {
                                    ServerPlayer serverPlayer = EntityArgument.getPlayer(context, "player");
                                    int team = Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(serverPlayer).getTeam();

                                    final Component teamName;
                                    if (team == Kingdoms.NO_KINGDOM) {
                                        teamName = Component.literal("No Kingdom");
                                    } else if (team == Kingdoms.THESIUM_KINGDOM) {
                                        teamName = Component.translatable("team."+ Kingdoms.MOD_ID +  ".thesium");
                                    } else if (team == Kingdoms.KRULATH_KINGDOM) {
                                        teamName = Component.translatable("team."+ Kingdoms.MOD_ID +  ".krulath");
                                    } else {
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
                                            ServerPlayer serverPlayer = EntityArgument.getPlayer(context, "player");
                                            int team = IntegerArgumentType.getInteger(context, "team");

                                            final Component teamName;
                                            if (team == Kingdoms.NO_KINGDOM) {
                                                teamName = Component.literal("No Kingdom");
                                            } else if (team == Kingdoms.THESIUM_KINGDOM) {
                                                teamName = Component.translatable("team."+ Kingdoms.MOD_ID +  ".thesium");
                                            } else if (team == Kingdoms.KRULATH_KINGDOM) {
                                                teamName = Component.translatable("team."+ Kingdoms.MOD_ID +  ".krulath");
                                            } else {
                                                teamName = Component.literal("ILLEGAL VALUE ASSIGNED FOR KINGDOM");
                                            }

                                            TeamComponent teamComponent = Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(serverPlayer);
                                            teamComponent.setTeam(team);

                                            context.getSource().sendSuccess(() -> Component.literal("Player " + serverPlayer.getScoreboardName() + " now belongs to the " + teamName.getString() + " kingdom."), true);
                                            return 1;
                                        }))))
        ));
    }
    private static void registerSetKingCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("kingdom").requires(source -> source.hasPermission(2))
                .then(literal("king")
                        .then(argument("player", EntityArgument.player())
                                .executes(context -> {
                                    ServerPlayer serverPlayer = EntityArgument.getPlayer(context, "player");
                                    int team = Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(serverPlayer).getTeam();
                                    if (team == Kingdoms.NO_KINGDOM) {
                                        context.getSource().sendFailure(Component.literal("Player " + serverPlayer.getScoreboardName() + " is not part of any kingdom."));
                                        return 0;
                                    }
                                    TeamComponent teamComponent = Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(serverPlayer);
                                    teamComponent.setKing(true);
                                    String kingdomName = (team == Kingdoms.THESIUM_KINGDOM) ? "Thesium" : "Krul'ath";
                                    Component kingMessage = Component.literal(serverPlayer.getScoreboardName())
                                            .append(" is now the king of the ")
                                            .append(Component.literal(kingdomName).withStyle(ChatFormatting.GOLD))
                                            .append(" Kingdom!");
                                    context.getSource().sendSuccess(() -> kingMessage, true);

                                    return 1;
                                }))
                )));
    }


}
